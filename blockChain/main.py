import socket
import threading
import json
import time
from datetime import datetime
import hashlib

DIFFICULTY = 3
STD_MSG_SIZE = 1024

# -------- Block / Blockchain --------
def generate_hash(index, timestamp, data, prev_hash, difficulty, nonce):
    raw = f"{index}{timestamp}{data}{prev_hash}{difficulty}{nonce}"
    return hashlib.sha256(raw.encode()).hexdigest()

def is_valid_hash(hash_str, difficulty):
    return hash_str.startswith('0' * difficulty)

class Block:
    def __init__(self, index, data, timestamp, hash_val, prev_hash, difficulty, nonce, miner):
        self.index = index
        self.data = data
        self.timestamp = timestamp
        self.hash = hash_val
        self.prev_hash = prev_hash
        self.difficulty = difficulty
        self.nonce = nonce
        self.miner = miner

    def to_dict(self):
        return {
            'index': self.index,
            'data': self.data,
            'timestamp': self.timestamp.isoformat(),
            'hash': self.hash,
            'prev_hash': self.prev_hash,
            'difficulty': self.difficulty,
            'nonce': self.nonce,
            'miner': self.miner
        }

    @staticmethod
    def from_dict(d):
        return Block(d['index'], d['data'], datetime.fromisoformat(d['timestamp']),
                     d['hash'], d['prev_hash'], d['difficulty'], d['nonce'], d['miner'])

class Blockchain:
    def __init__(self):
        self.chain = []
        self.lock = threading.Lock()

    def add_block(self, block):
        with self.lock:
            self.chain.append(block)

    def get_last_block(self):
        return self.chain[-1] if self.chain else None

    def get_last_hash(self):
        return self.chain[-1].hash if self.chain else "0"

    def get_last_index(self):
        return self.chain[-1].index if self.chain else -1

    def to_json(self):
        with self.lock:
            return json.dumps([b.to_dict() for b in self.chain])

    def replace_chain(self, new_chain):
        with self.lock:
            if len(new_chain) > len(self.chain):
                self.chain = new_chain

# -------- P2P funkcije --------
def send_chain(sock, blockchain):
    try:
        sock.sendall(blockchain.to_json().encode())
    except:
        pass

def receive_chain(sock):
    try:
        data = sock.recv(STD_MSG_SIZE)
        if not data:
            return None
        chain_list = json.loads(data.decode())
        return [Block.from_dict(b) for b in chain_list]
    except:
        return None

# -------- Miner --------
def mine_block(blockchain, miner_name):
    last_index = blockchain.get_last_index()
    last_hash = blockchain.get_last_hash()
    index = last_index + 1
    data = f"Blok {index}"
    timestamp = datetime.now()
    nonce = 0

    while True:
        hash_val = generate_hash(index, timestamp.isoformat(), data, last_hash, DIFFICULTY, nonce)
        if is_valid_hash(hash_val, DIFFICULTY):
            new_block = Block(index, data, timestamp, hash_val, last_hash, DIFFICULTY, nonce, miner_name)
            blockchain.add_block(new_block)
            print(f"[{miner_name}] Rudarjen blok: {new_block.to_dict()}")
            return new_block
        nonce += 1

# -------- Strežnik za peerje --------
def server_thread(blockchain, host, port):
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.bind((host, port))
    server.listen()
    print(f"[SERVER] Poslušam na {host}:{port}")
    while True:
        conn, addr = server.accept()
        threading.Thread(target=handle_client, args=(blockchain, conn), daemon=True).start()

def handle_client(blockchain, conn):
    while True:
        new_chain = receive_chain(conn)
        if new_chain:
            blockchain.replace_chain(new_chain)

# -------- Peer connect --------
def connect_to_peer(host, port):
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect((host, port))
    return sock

# -------- Glavni program --------
if __name__ == "__main__":
    my_name = input("Ime vozlišča: ")
    my_port = int(input("Moj port: "))
    peer_ports = input("Peer porti (ločeni z vejico, prazno če ni): ")
    peer_ports = [int(p.strip()) for p in peer_ports.split(",") if p.strip()]

    blockchain = Blockchain()

    # Začni strežnik
    threading.Thread(target=server_thread, args=(blockchain, '127.0.0.1', my_port), daemon=True).start()
    time.sleep(1)

    # Poveži se na peerje
    peer_socks = []
    for p in peer_ports:
        try:
            sock = connect_to_peer('127.0.0.1', p)
            peer_socks.append(sock)
            print(f"Povezan na peer port {p}")
        except:
            print(f"Ni mogoče povezati na {p}")

    # Miner thread
    def miner():
        while True:
            new_block = mine_block(blockchain, my_name)
            for sock in peer_socks:
                send_chain(sock, blockchain)
            time.sleep(1)

    threading.Thread(target=miner, daemon=True).start()

    while True:
        time.sleep(1)