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
#include <ESP8266HTTPClient.h>
#include "PubSubClient.h"
#include "FS.h"

#define SWITCH  5
#define INDICATOR_LED 13
#define BUZZER 12

// Update these with values suitable for your network.
const char* ssid = "TP-LINK_EB7F32";
const char* password = "80246008";

const char* gateway = "{GATEWAY}";
const char* mqtt_server = "{MQTT_SERVER}";
const int mqtt_port = {MQTT_PORT};
const char* tenant_domain = "{TENANT_DOMAIN}";
const char* owner = "{DEVICE_OWNER}";
const char* device_id = "{DEVICE_ID}";

const char* apiKey = "Basic {API_APPLICATION_KEY}";
const char* accessToken = "{DEVICE_TOKEN}";
const char* refreshToken = "{DEVICE_REFRESH_TOKEN}";
char msg[150];
char publishTopic[100];
char subscribedTopic[100];
long lastMsg = 0;
long lastTick = 0;
int lastWorn = -1;
int ledCount = 0;
int soundCount = 0;
bool isLED = false;
bool isSound = false;

void setup_wifi() {
  digitalWrite(INDICATOR_LED, HIGH);
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
  digitalWrite(INDICATOR_LED, LOW);
}

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  char msg[length];
  for (int i = 0; i < length; i++) {
    msg[i] = (char)payload[i];
  }
  Serial.println(msg);

  String cmd(msg);
  int terminatorIndex = cmd.indexOf(';');
  String _payload = cmd.substring(0, terminatorIndex);
  int separatorIndex = _payload.indexOf(':');
  String action = _payload.substring(0, separatorIndex);
  String duration = _payload.substring(separatorIndex + 1, payload.length());

  Serial.println(action);
  Serial.println(duration);

  if (action == "LED") {
    isLED = true;
    digitalWrite(INDICATOR_LED, HIGH);
    ledCount = duration.toInt();
  }else if (action == "SOUND"){
    isSound = true;
    digitalWrite(BUZZER, HIGH);
    soundCount = duration.toInt();
  }
}

String getAccessToken() {
  File configFile = SPIFFS.open("/config", "r");
  char __refreshToken[36];
  if (!configFile) {
    memcpy(__refreshToken, refreshToken, 36);
    Serial.println("Config file not available. Using defalt coonfigs");
  }else{
    int index = 0;
    while (configFile.available()) {
      __refreshToken[index++] = (char) configFile.read();
      if (index > 36) {
        break;
      }
    }
    configFile.close();
  }

  Serial.print("\nLoaded refreshToken: ");
  Serial.println(__refreshToken);

  HTTPClient http;    //Declare object of class HTTPClient
  char tokenEP[100];
  snprintf (tokenEP, 100, "%s/oauth2wrapper/token", gateway);
  Serial.println(tokenEP);
  http.begin(tokenEP);      //Specify request destination
  http.addHeader("content-type", "application/x-www-form-urlencoded");  //Specify content-type header
  http.addHeader("Authorization", apiKey);

  char payload[200];
  snprintf (payload, 200, "grant_type=refresh_token&refresh_token=%s", __refreshToken);
  Serial.println(payload);
  int httpCode = http.POST(payload);   //Send the request
  String response = http.getString();  //Get the response payload
  http.end();  //Close connection

  Serial.println(response);

  int commaIndex = response.indexOf(',');
  if (commaIndex > 0 && response.length() > 30) {
    String at = response.substring(13, commaIndex);
    String rt = response.substring(commaIndex + 15, response.length());
    char __rt[rt.length() + 1];
    rt.toCharArray(__rt, sizeof(__rt));
    Serial.print("\nUpdated refresh token: ");
    Serial.println(__rt);
    File configFile = SPIFFS.open("/config", "w");
    if (!configFile) {
      Serial.println("Failed to open config file for writing");
    }else{
      configFile.println(__rt);
      configFile.close();
      Serial.println("Config updated");
    }
    return at;
  }else{
    Serial.println("\nUsing hardcoded access token");
    return accessToken;
  }
}

WiFiClient espClient;
PubSubClient client(mqtt_server, mqtt_port, callback, espClient);

void setup() {
  pinMode(SWITCH, INPUT);
  pinMode(INDICATOR_LED, OUTPUT);
  pinMode(BUZZER, OUTPUT);
  
  digitalWrite(INDICATOR_LED, LOW);
  digitalWrite(BUZZER, LOW);
  
  Serial.begin(115200);
  setup_wifi();

  if (!SPIFFS.begin()) {
    Serial.println("Failed to mount file system");
    return;
  }

  String __at = getAccessToken();
  char __accessToken[__at.length() + 1];
  __at.toCharArray(__accessToken, sizeof(__accessToken));
  Serial.print("\nConnecting MQTT client using access token: ");
  Serial.println(__accessToken);

  snprintf (subscribedTopic, 100, "%s/alertme/%s/alert", tenant_domain, device_id);
  if (client.connect(device_id, __accessToken, "")) {
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

void loop() {
  if (Serial.available()) {
    String cmd = Serial.readString();
    if (cmd == "WIPE") {
      SPIFFS.remove("/config");
      Serial.println("Configurations removed!");
      ESP.reset();
    }
    if (cmd == "RESET") {
      ESP.reset();
    }
  }

  if (!client.connected()) {
    reconnect();
    return;
  }
  client.loop();

  bool isUpdated = false;
  int isWorn = digitalRead(SWITCH);
  if (isWorn != lastWorn) {
    isUpdated = true;
  }

  long now = millis();
  if (now - lastMsg > 60000 || isUpdated) {
    lastMsg = now;
    snprintf (msg, 150, "{\"event\":{\"metaData\":{\"owner\":\"%s\",\"deviceType\":\"alertme\",\"deviceId\":\"%s\",\"time\":%ld},\"payloadData\":{\"worndetector\":%ld}}}", owner, device_id, now, isWorn);
    snprintf (publishTopic, 100, "%s/alertme/%s/worndetector", tenant_domain, device_id);
    client.publish(publishTopic, msg);
    Serial.print("Publish message: ");
    Serial.println(msg);
    Serial.println(publishTopic);
    lastWorn = isWorn;
  }

  if (now - lastTick > 1000) {
    lastTick = now;
    if (isLED) {
      if (ledCount == 0) {
        isLED = false;
        digitalWrite(INDICATOR_LED, LOW);
      } else {
        ledCount--;
      }
    }
    if (isSound) {
      if (soundCount == 0) {
        isSound = false;
        digitalWrite(BUZZER, LOW);
      } else {
        soundCount--;
      }
    }
  }
}

