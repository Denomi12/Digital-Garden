# main.py
import time
import threading
from blockChain import Blockchain
from miner import mine_block

def main():
    print("--- Zagon Blockchaina ---")
    
    bc = Blockchain()
    miner_name = "Vozlisce_A"

    try:
        while True:
            new_block = mine_block(bc, miner_name)
            
            if new_block:
                time.sleep(1)
            else:
                time.sleep(0.5)

    except KeyboardInterrupt:
        print("\nUstavljanje...")
        print("Končna veriga:")
        print(bc.to_json())

if __name__ == "__main__":
    main()