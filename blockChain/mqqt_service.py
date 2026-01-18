import paho.mqtt.client as mqtt
import json
from blockChain import Blockchain
from miner import mine_block_parallel

bc = Blockchain()

def on_connect(client, userdata, flags, rc, properties=None):
    if rc == 0:
        print("Povezan z brokerjem! Čakam na dogodke...")
        client.subscribe("mobilegarden/events")
    else:
        print(f"Napaka pri povezavi: {rc}")

def on_message(client, userdata, msg):
    print(f"Prejet dogodek na temi {msg.topic}")
    try:
        payload = msg.payload.decode()
        print(f"Vsebina: {payload}")

        new_block = mine_block_parallel(bc, "Miner_Node", 4, 0, 1, data=payload)

        if bc.add_block(new_block):
            print(f"USPEH: Blok {new_block.index} dodan v verigo.")
            print(f"Hash: {new_block.hash}")
    except Exception as e:
        print(f"Napaka pri obdelavi: {e}")

client = mqtt.Client(callback_api_version=mqtt.CallbackAPIVersion.VERSION2)
client.on_connect = on_connect
client.on_message = on_message

print("Zaganjam MQTT poslušalca...")
client.connect("localhost", 1883, 60)
client.loop_forever()