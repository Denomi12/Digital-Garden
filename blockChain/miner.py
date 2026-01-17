# miner.py
from datetime import datetime
from block import Block

def mine_block(blockchain, miner_name):
    last_block = blockchain.get_last_block()
    index = last_block.index + 1
    prev_hash = last_block.hash
    difficulty = blockchain.get_difficulty()
    
    data = f"Blok {index} (Miner: {miner_name})"
    timestamp = datetime.now()
    nonce = 0
    
    print(f"[{miner_name}] Rudarim blok {index} (Diff: {difficulty})...")

    while True:
        current_last = blockchain.get_last_block()
        if current_last.index >= index:
             print(f"[{miner_name}] Blok {index} je bil medtem že najden.")
             return None

        timestamp_iso = timestamp.isoformat()
        hash_val = Block.calculate_hash(index, timestamp_iso, data, prev_hash, difficulty, nonce)
        
        if hash_val.startswith('0' * difficulty):
            new_block = Block(index, data, timestamp, hash_val, prev_hash, difficulty, nonce, miner_name)
            if blockchain.add_block(new_block):
                print(f"[{miner_name}] NAJDEN BLOK! Index: {index}, Hash: {hash_val}")
                return new_block
            else:
                print(f"[{miner_name}] Blok zavrnjen.")
                return None
        
        nonce += 1