/**
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
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
**/
#include <DHT.h>
#include <DHT_U.h>
#include <ESP8266SSDP.h>
#include <ESP8266SSDP.h>
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include "PubSubClient.h"
#include "FS.h"

#define TRIGGER  5 //D1
#define ECHO     4 //D2
#define INDICATOR_LED 0 //D3
#define DHT11_PIN  14 //D5 

#define DHTTYPE DHT11

DHT dht(DHT11_PIN, DHTTYPE, 30);

// Update these with values suitable for your network.

const char* ssid = "WSO2-Restricted";
const char* password = "LKvene8xIOT";

int pulse_time = 0;
int trash_level = -1;
int height_to_full = 50;
int height_to_sensor = 60;
boolean client_connected = false;
boolean client_connection_pending = false;
int waiting_count = 0;

//const char* gateway = "http://192.168.43.237:8280";
const char* mqtt_server = "${IP}";      // "192.168.43.237";
const int mqtt_port = ${MQTT_PORT};    //1883;
const char* tenant_domain = "carbon.super";
const char* owner = "${DEVICE_OWNER}";
const char* device_id = "${DEVICE_ID}"; 

const char* accessToken = "${DEVICE_TOKEN}";
char msg[150];
char publishTopic[100];
char subscribedTopic[100];
int count = 0;
long distance = 0;
long lastDataSend = 0;
long lastDistanceCheck = 0;
int lastDistance = -1;
unsigned long initialTimeStamp = 0;

void setup_wifi() {
  digitalWrite(INDICATOR_LED, HIGH);
  delay(1000);
  // We start by connecting to a WiFi network
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }  
  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
  digitalWrite(INDICATOR_LED, LOW);
}

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  String payloadData = (String)(char*)payload;
  height_to_full = payloadData.substring(payloadData.indexOf(':')+1,payloadData.indexOf(',')).toInt();
  height_to_sensor = payloadData.substring(payloadData.indexOf(',')+1,payloadData.length()).toInt();
  save_config(height_to_full,height_to_sensor);
  read_config(); 
}

void save_config(int height_to_full,int height_to_sensor){ 
  File configFile = SPIFFS.open("/configData", "w");
    if (!configFile) {
      Serial.println("Failed to open config data file for writing");
    }else{
      configFile.print(height_to_full);
      configFile.print(",");
      configFile.print(height_to_sensor);
      configFile.print("$");
      configFile.close();
      Serial.println();
      Serial.print("Config updated");
    }
}

void read_config(){
  File configFile = SPIFFS.open("/configData", "r");
    if (!configFile) {
      Serial.println("Failed to open config data file for reaidng");
    }else {
      int index = 0;
      char dataH[36];
      while (configFile.available()) {
        dataH[index++] = (char) configFile.read();
      }
      configFile.close();
      Serial.print("\nLoaded configurations: ");
      String heights = dataH;
      height_to_full = heights.substring(0,heights.indexOf(',')).toInt();
      height_to_sensor = heights.substring(heights.indexOf(',')+1,heights.indexOf('$')).toInt();
      Serial.print("height_to_full : ");
      Serial.print(height_to_full);
      Serial.print(" height_to_sensor : ");
      Serial.println(height_to_sensor);      
    }
}

WiFiClient espClient;
PubSubClient client(mqtt_server, mqtt_port, callback, espClient);

void setup() {
  pinMode(TRIGGER, OUTPUT);
  pinMode(ECHO, INPUT);
  pinMode(INDICATOR_LED, OUTPUT);
  digitalWrite(ECHO, LOW);
  digitalWrite(INDICATOR_LED, LOW);
  
  Serial.begin(115200);
  
  setup_wifi();
  dht.begin();  

  if (!SPIFFS.begin()) {
    Serial.println("Failed to mount file system");
    return;
  }
  read_config();
  
  Serial.print("\nConnecting MQTT client using access token: ");
  Serial.println(accessToken);
  snprintf (subscribedTopic, 100, "%s/garbagebin/%s/command", tenant_domain, device_id);
  if (client.connect(device_id, accessToken, "")) {
    client.subscribe(subscribedTopic);
    Serial.println("MQTT Client Connected");
    Serial.println(subscribedTopic);
  } else {
    Serial.println("Error while connecting with MQTT server.");
  }
}

void reconnect() {
  // Loop until we're reconnected
  Serial.print("Attempting MQTT connection...");
  // Attempt to connect
  if (client.connect(device_id, accessToken, "")) {
    client.subscribe(subscribedTopic);
    Serial.println("MQTT Client Connected again");
  } else {
    Serial.print("failed, rc=");
    Serial.print(client.state());
    Serial.println(" try again in 5 seconds");
    // Wait 5 seconds before retrying
    delay(5000);
  }
}

long measureDistance() {
  long duration, distance;
  digitalWrite(TRIGGER, LOW);
  delayMicroseconds(2);

  digitalWrite(TRIGGER, HIGH);
  delayMicroseconds(10);

  digitalWrite(TRIGGER, LOW);
  duration = pulseIn(ECHO, HIGH);
  distance = (duration/2) / 29.1;
  return distance;
}

void loop() {
  
  if (!client.connected()) {
    reconnect();
    return;
  }
  client.loop();

  long now = millis();
  int distanceDelta = 0;
  if (now - lastDistanceCheck > 2000) {
    distance = measureDistance();
    distanceDelta = distance - lastDistance;
    lastDistanceCheck = now;
  }
  
  if (now - lastDataSend > 3000 ) {
    
    float temperature = dht.readTemperature();
    float humidity = dht.readHumidity();  
    int garbage_level = (height_to_sensor - distance) * 100 / height_to_full;

    if( garbage_level > 100 ){
       digitalWrite(INDICATOR_LED, HIGH);
    } else {
       digitalWrite(INDICATOR_LED, LOW);
    }
  
    if( temperature < 500 || humidity < 500 ) {      
      if( garbage_level < 0 ){
        garbage_level = 0;
      } else if ( garbage_level > 100 ){
        garbage_level = 100;
      }
      lastDataSend = now;
      long _timeStamp = initialTimeStamp + (now / 1000);
      snprintf (msg, 150, "{event:{metaData:{owner:\"%s\",deviceId:\"%s\"},payloadData:{garbagelevel:\"%ld.0\",temperature:\"%d.0\",humidity:\"%d.0\"}}}",
      owner, device_id,garbage_level,(int)temperature,(int)humidity);
      snprintf (publishTopic, 100, "%s/garbagebin/%s/data", tenant_domain, device_id);
      client.publish(publishTopic, msg);
      Serial.print("Publish message: ");
      Serial.println(msg);
      Serial.println(publishTopic);
    } else{
      Serial.println("Failed to read from DHT sensor");
      delay(2000);
    }
  }
}
