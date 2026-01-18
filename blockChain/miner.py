import threading
import time
from datetime import datetime
from block import Block

found_event = threading.Event()
found_block_result = None

def mine_thread(index, timestamp_iso, data, prev_hash, difficulty, start_nonce, step, miner_name, timestamp_obj):
    global found_block_result
    nonce = start_nonce
    
    base_hash_obj = Block.get_hash_object(index, timestamp_iso, data, prev_hash, difficulty)
    target = '0' * difficulty
    
    while not found_event.is_set():
        h = base_hash_obj.copy()
        h.update(str(nonce).encode())
        hash_val = h.hexdigest()
        
        if hash_val.startswith(target):
            found_block_result = Block(index, data, timestamp_obj, hash_val, prev_hash, difficulty, nonce, miner_name)
            found_event.set()
            break
        nonce += step

def mine_block_parallel(blockchain, miner_name, num_threads, rank, size):
    global found_event, found_block_result
    last_block = blockchain.get_last_block()
    index = last_block.index + 1
    difficulty = blockchain.get_difficulty()
    timestamp = datetime.now()
    
    found_event.clear()
    found_block_result = None
    threads = []

    total_threads = size * num_threads

    for i in range(num_threads):
        global_thread_id = (rank * num_threads) + i
        t = threading.Thread(target=mine_thread, args=(
            index, timestamp.isoformat(), f"Blok {index}", last_block.hash, 
            difficulty, global_thread_id, total_threads, miner_name, timestamp
        ))
        threads.append(t)
        t.start()

    while not found_event.is_set():
        time.sleep(0.01)

    for t in threads: t.join()
    return found_block_result