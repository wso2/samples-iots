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

#include <ESP8266WiFi.h>
#include "PubSubClient.h"
#include "FS.h"

#define TRIGGER  5
#define ECHO     4
#define TEST_LED 0
#define PIR_OUT  2

// Update these with values suitable for your network.
const char* ssid = "<ADD YOUR WiFi SSID>";
const char* password = "<ADD YOUR WiFi PASSWORD>";

const char* mqtt_server = "{MQTT_SERVER}";
const int port = {MQTT_PORT};
const char* tenant_domain = "{TENANT_DOMAIN}";
const char* owner = "{DEVICE_OWNER}";
const char* device_id = "{DEVICE_ID}";

const char* accessToken;
const char* refreshToken = "{DEVICE_REFRESH_TOKEN}";
long lastMsg = 0;
char msg[120];
char publishTopic[100];
char subscribedTopic[100];
int count = 0;

bool loadConfig() {
  File configFile = SPIFFS.open("/config.json", "r");
  if (!configFile) {
    Serial.println("Failed to open config file");
    return false;
  }

  size_t size = configFile.size();
  if (size > 1024) {
    Serial.println("Config file size is too large");
    return false;
  }

  String conf = configFile.readString();
  refreshToken = conf.c_str();

  // Real world application would store these values in some variables for
  // later use.

  Serial.print("Loaded refreshToken: ");
  Serial.println(refreshToken);
  return true;
}

bool saveConfig() {
  File configFile = SPIFFS.open("/config.json", "w");
  if (!configFile) {
    Serial.println("Failed to open config file for writing");
    return false;
  }

  configFile.println(refreshToken);
  return true;
}

void setup_wifi() {
  digitalWrite(TEST_LED, HIGH);
  delay(10);
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
  digitalWrite(TEST_LED, LOW);
}

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();

  // Switch on the LED if an 1 was received as first character
  if ((char)payload[0] == '1') {
    digitalWrite(TEST_LED, HIGH);
    count = 5;
  }
}

WiFiClient espClient;
PubSubClient client(mqtt_server, port, callback, espClient);

void setup() {
  pinMode(TRIGGER, OUTPUT);
  pinMode(ECHO, INPUT);
  pinMode(TEST_LED, OUTPUT);
  pinMode(PIR_OUT, INPUT);
  Serial.begin(115200);
  setup_wifi();

  accessToken = refreshToken;
  snprintf (subscribedTopic, 100, "%s/senseme/%s/COMMAND", tenant_domain, device_id);
      
  if (client.connect(device_id, accessToken, "")) {
    client.subscribe(subscribedTopic);
    Serial.println("MQTT Client Connected");
    Serial.println(subscribedTopic);
  } else {
    Serial.println("Error while connecting with MQTT server.");
  }

  if (!SPIFFS.begin()) {
    Serial.println("Failed to mount file system");
    return;
  }

  if (!loadConfig()) {
    Serial.println("Failed to load config");
  } else {
    Serial.println("Config loaded");
  }

  saveConfig();
}

void reconnect() {
  // Loop until we're reconnected
  while (!client.connected()) {
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

int isMoving() {
  return digitalRead(PIR_OUT);
}

void loop() {

  if (!client.connected()) {
    reconnect();
  }
  client.loop();

  long now = millis();
  if (now - lastMsg > 2000) {
    lastMsg = now;
    
    snprintf (msg, 120, "{event:{metaData:{owner:\"%s\",deviceType:\"senseme\",deviceId:\"%s\"},payloadData:{ULTRASONIC:%ld}}}", owner, device_id, measureDistance());
    snprintf (publishTopic, 100, "%s/senseme/%s/ULTRASONIC", tenant_domain, device_id);
    client.publish(publishTopic, msg);
    Serial.print("Publish message: ");
    Serial.println(msg);
    Serial.println(publishTopic);
    
    snprintf (msg, 120, "{event:{metaData:{owner:\"%s\",deviceType:\"senseme\",deviceId:\"%s\"},payloadData:{PIR:%ld}}}", owner, device_id, isMoving());    
    snprintf (publishTopic, 100, "%s/senseme/%s/PIR", tenant_domain, device_id);
    client.publish(publishTopic, msg);
    Serial.print("Publish message: ");
    Serial.println(msg);
    Serial.println(publishTopic);
    
    if (count == 0) {
      digitalWrite(TEST_LED, LOW);
    } else {
      count--;
    }
  }
}

