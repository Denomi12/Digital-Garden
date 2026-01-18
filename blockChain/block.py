import hashlib
from datetime import datetime

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

    @staticmethod
    def calculate_hash(index, timestamp_iso, data, prev_hash, difficulty, nonce):
        raw = f"{index}{timestamp_iso}{data}{prev_hash}{difficulty}{nonce}"
        return hashlib.sha256(raw.encode()).hexdigest()

    @staticmethod
    def get_hash_object(index, timestamp_iso, data, prev_hash, difficulty):
        prefix = f"{index}{timestamp_iso}{data}{prev_hash}{difficulty}"
        h = hashlib.sha256()
        h.update(prefix.encode())
        return h

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
        return Block(
            d['index'],
            d['data'],
            datetime.fromisoformat(d['timestamp']),
            d['hash'],
            d['prev_hash'],
            d['difficulty'],
            d['nonce'],
            d['miner']
        )