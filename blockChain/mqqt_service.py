import paho.mqtt.client as mqtt
import json
from blockChain import Blockchain
from miner import mine_block_parallel

bc = Blockchain()

def on_message(client, userdata, msg):
    print(f"Prejet dogodek: {msg.topic}")
    # Ko pride MQTT sporočilo, zaženemo rudarjenje z vsebino sporočila
    new_block = mine_block_parallel(bc, "Miner_Node", 4, 0, 1, data=msg.payload.decode())
    if bc.add_block(new_block):
        print(f"Dogodek zapisan v blok {new_block.index}")

client = mqtt.Client()
client.on_message = on_message
client.connect("127.0.0.1", 1883)
client.subscribe("mobilegarden/events")
client.loop_forever()