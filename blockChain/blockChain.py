# blockchain.py
import threading
import json
from datetime import datetime, timedelta
from block import Block
import config

class Blockchain:
    def __init__(self):
        self.chain = []
        self.lock = threading.Lock()
        self.create_genesis_block()

    def create_genesis_block(self):
        timestamp = datetime.now()
        genesis_block = Block(
            index=0,
            data="Genesis Block",
            timestamp=timestamp,
            hash_val="0",
            prev_hash="0",
            difficulty=config.DEFAULT_DIFFICULTY,
            nonce=0,
            miner="System"
        )
        genesis_block.hash = Block.calculate_hash(
            0, timestamp.isoformat(), "Genesis Block", "0", config.DEFAULT_DIFFICULTY, 0
        )
        self.chain.append(genesis_block)

    def get_last_block(self):
        with self.lock:
            return self.chain[-1] if self.chain else None

    def get_difficulty(self):
        last_block = self.get_last_block()
        if not last_block:
            return config.DEFAULT_DIFFICULTY
        
        if (last_block.index + 1) % config.DIFFICULTY_ADJUSTMENT_INTERVAL != 0:
            return last_block.difficulty
        
        adjustment_index = max(0, len(self.chain) - config.DIFFICULTY_ADJUSTMENT_INTERVAL)
        adjustment_block = self.chain[adjustment_index]
        
        expected_time = config.BLOCK_GENERATION_INTERVAL * config.DIFFICULTY_ADJUSTMENT_INTERVAL
        time_diff = (last_block.timestamp - adjustment_block.timestamp).total_seconds()
        
        if time_diff < (expected_time / 2):
            print(f"[INFO] Povečujem težavnost na {last_block.difficulty + 1}")
            return last_block.difficulty + 1
        elif time_diff > (expected_time * 2):
            new_diff = max(1, last_block.difficulty - 1)
            print(f"[INFO] Zmanjšujem težavnost na {new_diff}")
            return new_diff
        else:
            return last_block.difficulty

    def get_cumulative_difficulty(self):
        return sum([2 ** b.difficulty for b in self.chain])

    def add_block(self, block):
        with self.lock:
            if self.is_valid_new_block(block, self.chain[-1]):
                self.chain.append(block)
                return True
            return False

    def is_valid_new_block(self, new_block, prev_block):
        if new_block.index != prev_block.index + 1:
            return False
        if new_block.prev_hash != prev_block.hash:
            return False
        if not new_block.hash.startswith('0' * new_block.difficulty):
             return False
        
        recalculated = Block.calculate_hash(
            new_block.index, new_block.timestamp.isoformat(), new_block.data,
            new_block.prev_hash, new_block.difficulty, new_block.nonce
        )
        if new_block.hash != recalculated:
            return False

        now = datetime.now()
        if new_block.timestamp > now + timedelta(minutes=1):
            return False
        if new_block.timestamp < prev_block.timestamp - timedelta(minutes=1):
            return False

        return True

    def is_valid_chain(self, chain_to_validate):
        for i in range(1, len(chain_to_validate)):
            if not self.is_valid_new_block(chain_to_validate[i], chain_to_validate[i-1]):
                return False
        return True

    def replace_chain(self, new_chain):
        with self.lock:
            if not self.is_valid_chain(new_chain):
                return
            
            current_cum_diff = self.get_cumulative_difficulty()
            new_cum_diff = sum([2 ** b.difficulty for b in new_chain])

            if new_cum_diff > current_cum_diff:
                print(f"[INFO] Zamenjujem verigo. Nova težavnost: {new_cum_diff} > Stara: {current_cum_diff}")
                self.chain = new_chain
            else:
                print("[INFO] Ohranjam trenutno verigo.")

    def to_json(self):
        with self.lock:
            return json.dumps([b.to_dict() for b in self.chain])