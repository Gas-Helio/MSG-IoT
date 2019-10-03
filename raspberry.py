import paho.mqtt.client as paho

def on_subscribe(client, userdata, mid, granted_qos):
    print('Subscribed: '+str(mid)+' '+str(granted_qos))

def on_message(client, userdata, msg):
    print(str(msg.payload).split("'")[1])

client = paho.Client()
client.on_subscribe = on_subscribe
client.on_message = on_message
client.connect('10.180.14.80', 1883)
client.subscribe('#', qos=1)

while True:
    client.loop()
