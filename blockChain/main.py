import time
import os
from mpi4py import MPI
from blockChain import Blockchain
from miner import mine_block_parallel, found_event

def main():
    comm = MPI.COMM_WORLD
    rank = comm.Get_rank()
    size = comm.Get_size()
    
    bc = Blockchain()
    miner_name = f"Vozlisce_{rank}"
    num_threads = os.cpu_count() or 4
    
    TARGET_BLOCKS = 40
    
    if rank == 0:
        print(f"--- Zagon testiranja: {size} vozlišč, {num_threads} niti/vozlišče ---")
        print(f"{'Blok':<5} | {'Čas rudarjenja (s)':<20} | {'Rudar':<12}")
        print("-" * 45)
        total_start_time = time.time()

    try:
        while len(bc.chain) <= TARGET_BLOCKS:
            comm.Barrier()
            block_start = time.time()
            
            new_block = mine_block_parallel(bc, miner_name, num_threads, rank, size)
            found_blocks = comm.gather(new_block, root=0)

            if rank == 0:
                block_end = time.time()
                for block in filter(None, found_blocks):
                    if bc.add_block(block):
                        print(f"{block.index:<5} | {block_end - block_start:<20.4f} | {block.miner:<12}")
                        break
                current_chain = bc.chain
            else:
                current_chain = None

            current_chain = comm.bcast(current_chain, root=0)
            if rank != 0:
                bc.replace_chain(current_chain)
                found_event.set() 

    except KeyboardInterrupt:
        pass
    finally:
        if rank == 0:
            total_duration = time.time() - total_start_time
            print("-" * 45)
            print(f"SKUPNI ČAS ZA {TARGET_BLOCKS} BLOKOV: {total_duration:.2f} sekund")
            print(f"Povprečen čas na blok: {total_duration/TARGET_BLOCKS:.2f} sekund")

if __name__ == "__main__":
    main()