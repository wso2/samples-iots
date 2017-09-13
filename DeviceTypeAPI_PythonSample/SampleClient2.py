'''
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
'''

import importer

importer.installMissingPackages()
import base64
import subprocess
import json
import threading
import random
import sys
import paho.mqtt.client as mqtt

global Client

print('\n')
print('----------------------------------------------------')
print('|                   MQTT SAMPLE 2                  |')
print('----------------------------------------------------')

# Reading config.json
try:
    config = json.loads(open('Configuration/config.json').read())
except:
    print "Cannot read the configuration file"
    sys.exit(0)

DEVICE_ID = config['deviceId']
DEVICE_TYPE = config['type']
ACCESS_TOKEN = config['accessToken']
MQTT_GATEWAY = config['mqttGateway'].split(":")
MQTT_IP = MQTT_GATEWAY[1].replace('//', '')
MQTT_PORT = int(MQTT_GATEWAY[2])

'''
    Using clientId and clientSecret to generate new access token
    Since we are generating access token each time, no need of a refresh token 
'''

CLIENT_ID = config['clientId']
CLIENT_SECRET = config['clientSecret']

encodedCredentials = base64.b64encode(CLIENT_ID + ':' + CLIENT_SECRET)

bash_com = 'curl -k -d "grant_type=password&username=admin&password=admin&scope=perm:admin:device-type perm:device-types:events perm:device-types:events:view perm:device-types:types perm:devices:operations" -H "Authorization: Basic ' + \
           encodedCredentials + '" -H "Content-Type: application/x-www-form-urlencoded" https://' + MQTT_IP + ':8243/token'
subprocess.Popen(bash_com, shell=True)
output = subprocess.check_output(['bash', '-c', bash_com])
output = json.loads(output)

print("New access token : " + output['access_token'])
print("New refresh token : " + output['refresh_token'])

# Updating access token
ACCESS_TOKEN = output['access_token']

# Change this appropiately
SENSOR_NAME = "temperature"

# Publish and subscribe topics
publishTopic = 'carbon.super/' + DEVICE_TYPE + '/' + DEVICE_ID + '/events'
subscribeTopic = 'carbon.super/' + DEVICE_TYPE + '/' + DEVICE_ID + '/operation/#'


def on_connect(client, userdata, flags, rc):
    print("Connected with result code " + str(rc))
    client.subscribe(subscribeTopic)
    # If connection successful start publishing data
    if rc == 0:
        publishTempData()


def on_message(client, userdata, msg):
    print("Message Received: " + str(msg.payload))


# Publish random temp data between 10-100
def publishData():
    num = random.randint(10, 100)
    message = '{"' + SENSOR_NAME + '":' + str(num) + '}'
    print("Publish Data: " + message)
    Client.publish(publishTopic, message)


def publishTempData():
    publishData()
    threading.Timer(5.0, publishTempData).start()


client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message
client.username_pw_set(ACCESS_TOKEN, password="")
client.connect(MQTT_IP, MQTT_PORT, 60, bind_address="")
Client = client
client.loop_forever()
