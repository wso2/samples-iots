
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
#include <ESP8266HTTPClient.h>
#include <ESP8266WiFi.h>
#include <Esp.h>
#include <WiFiClient.h>
#include <WiFiClientSecure.h>
#include <WiFiServer.h>
#include <WiFiUdp.h>
#include "PubSubClient.h"
#include "FS.h"

#define GAS  5 //D1
#define LIGHT     4 //D2
#define DHT11_PIN 0 //D3
#define PIR_OUT  14 //D5

#define DHTTYPE DHT11

DHT dht(DHT11_PIN, DHTTYPE, 30); 

unsigned long previousMillis = 0;        // will store last temp was read
const long interval = 2000;              // interval at which to read sensor
long startDelay = millis();
// Update these with values suitable for your network.
//const char* ssid = "linksys";
//const char* password = "linksys@abcdf@12345";
//const char* gateway = "http://192.168.1.103:8280";
//const char* mqtt_server = "192.168.1.103";


const char* ssid = "...";
const char* password = "....";

const char* gateway = "http://192.168.57.100:8280";
const char* mqtt_server = "192.168.57.100";
const int mqtt_port = 1886;
const char* tenant_domain = "carbon.super";
char device_id[100] ;

char apiKey[200];
char accessToken[100];
char refreshToken[100];
char msg[150];
char publishTopic[100];
char subscribedTopic[100];
int count = 0;
bool isTesting = false;
long distance = 0;
long lastDistanceMsg = 0;
long lastMovementMsg = 0;
long lastDistanceCheck = 0;
long lastTest = 0;
int lastDistance = -1;
int lastMovement = -1;
unsigned long initialTimeStamp = 0;
boolean registered = false;
boolean mqttConnected = false;
int isMoving = 0;

void setup_wifi() {
  delay(10);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
  }
  Serial.println(WiFi.localIP());
}

void callback(char* topic, byte* payload, unsigned int length) {

  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
}

boolean registerme() {
  Serial.print("trying to register the device: ");
  HTTPClient http;    //Declare object of class HTTPClient
  char tokenEP[100];
  snprintf (tokenEP, 100, "%s/senseme/device/enrollme?deviceId=%s", gateway, device_id);
  Serial.println(tokenEP);
  http.begin(tokenEP);      //Specify request destination
  http.addHeader("content-type", "application/json");  //Specify content-type header

  char payload[200];
  snprintf (payload, 200, "payload");
  Serial.println(payload);
  int httpCode = http.POST(payload);   //Send the request
  String response = http.getString();  //Get the response payload
  http.end();  //Close connection

  Serial.println(response);
  if (httpCode > 0) {
    if (httpCode == 200) {
      int commaIndex = response.indexOf(',');
      if (commaIndex > 0 && response.length() > 30) {
        String at = response.substring(13, commaIndex);
        Serial.print("accessToken: ");
        Serial.println(at);
        char __at[at.length() + 1];
        at.toCharArray(__at, sizeof(__at));
        String response2 = response.substring(commaIndex + 1, response.length());
        commaIndex = response2.indexOf(',');
        String rt = response2.substring(14, commaIndex);
        Serial.print("refreshToken: ");
        Serial.println(rt);
        //commaIndex = response2.indexOf(',');
        String appKey = response2.substring(commaIndex + 9, response2.length());
        Serial.print("appKey: ");
        Serial.println(appKey);
        char __rt[rt.length() + 1];
        char __appKey[appKey.length() + 1];
        rt.toCharArray(__rt, sizeof(__rt));
        appKey.toCharArray(__appKey, sizeof(__appKey));
        Serial.print("\nUpdated refresh token and app key: ");
        Serial.println(__rt);
        Serial.println(__appKey);
        memcpy(refreshToken,__rt, 36);
        memcpy(apiKey,__appKey, appKey.length() + 1);
        memcpy(accessToken,__at, at.length() + 1);
        Serial.println("test");
        Serial.println(apiKey);
        Serial.println(accessToken);
        Serial.println(refreshToken);
      
        return true;
      }else{
        Serial.println("\nUsing hardcoded access token");
        return false;
      }
    } else {
      return false;  
    }
  }
}

void loadAccessTokenFromRefreshToken() {
  Serial.print("\nLoaded refreshToken: ");
  Serial.println(refreshToken);

  Serial.print("\nLoaded appKey: ");
  Serial.println(apiKey);

  HTTPClient http;    //Declare object of class HTTPClient
  char tokenEP[100];
  snprintf (tokenEP, 100, "%s/oauth2wrapper/token", gateway);
  Serial.println(tokenEP);
  http.begin(tokenEP);      //Specify request destination
  http.addHeader("content-type", "application/x-www-form-urlencoded");  //Specify content-type header
  http.addHeader("Authorization", apiKey);

  char payload[200];
  snprintf (payload, 200, "grant_type=refresh_token&refresh_token=%s", refreshToken);
  Serial.println(payload);
  int httpCode = http.POST(payload);   //Send the request
  String response = http.getString();  //Get the response payload
  http.end();  //Close connection
  Serial.println("Refresh Request");
  Serial.println(response);
  Serial.println(httpCode);
  if (httpCode > 0) {
    if (httpCode == 200) {
      int commaIndex = response.indexOf(',');
      String at = response.substring(13, commaIndex);
      char __at[at.length() + 1];
      at.toCharArray(__at, sizeof(__at));
      String response2 = response.substring(commaIndex + 1, response.length());
      commaIndex = response2.indexOf(',');
      String rt = response2.substring(14, commaIndex);
      char __rt[rt.length() + 1];
      rt.toCharArray(__rt, sizeof(__rt));
      Serial.print("\nUpdated refresh token: ");
      Serial.println(__rt);
      memcpy(refreshToken,__rt, rt.length() + 1);
      memcpy(accessToken,__at, at.length() + 1);
      Serial.println("test from refreshToken");
      Serial.println(apiKey);
      Serial.println(accessToken);
      Serial.println(refreshToken);
    } else {
      registerme();
    }
  }
}

void syncTime(String _timeStampString){
  Serial.print("\n_timeStampString: ");
  Serial.println(_timeStampString);
  char _timeStamp[11];
  _timeStampString.toCharArray(_timeStamp, 11);
  Serial.print("\n_timeStamp: ");
  Serial.println(_timeStamp);
  initialTimeStamp = atol(_timeStamp);
  Serial.print("\nSynced time: ");
  Serial.println(initialTimeStamp);
}

WiFiClient espClient;
PubSubClient client(mqtt_server, mqtt_port, callback, espClient);

void setup() {
  dht.begin();
  setup_wifi();
  snprintf (device_id, 100, "%i", ESP.getChipId());
  pinMode(GAS, INPUT);
  pinMode(LIGHT, INPUT);
  pinMode(PIR_OUT, INPUT);

  Serial.begin(115200);

  Serial.print("device id :");
  Serial.println(device_id);
  if (!SPIFFS.begin()) {
    Serial.println("Failed to mount file system");
    return;
  }
  startDelay = millis();
  delay(2000);
}

void reconnect() {
  // Loop until we're reconnected
  Serial.print("Attempting MQTT connection...");
  // Attempt to connect
  loadAccessTokenFromRefreshToken();
  Serial.print("\nConnecting MQTT client using access token: ");
  Serial.println(accessToken);
  if (client.connect(device_id, accessToken, "")) {
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
  if (!registered) {
    registered = registerme();
    if (!registered) {
      delay(10000);
      Serial.println("device not registered.. retrying");
       return;
     } else {
        Serial.print("\nConnecting MQTT client using access token: ");
        Serial.println(accessToken);
        if (!mqttConnected) {
          if (client.connect(device_id, accessToken, "")) {
            Serial.println("MQTT Client Connected");
            mqttConnected = true;
          } else {
            Serial.println("Error while connecting with MQTT server.");
          }
        }
     
     }
    
   }

  if (!client.connected()) {
    reconnect();
    return;
  }
  client.loop();
  int isMovingTemp = digitalRead(PIR_OUT);
  if (isMovingTemp == 1) {
    isMoving = 1;
  }
  
  if (millis() - startDelay > interval) {
    float temperature = dht.readTemperature();
    float humidity = dht.readHumidity();
    int airQuality = 0;//digitalRead(GAS);
    int light = analogRead(A0);
    
    snprintf (msg, 150, "{\"event\":{\"payloadData\":{\"deviceId\":\"%s\", \"temperature\":%d.0 , \"motion\":%ld.0, \"humidity\":%d.0  , \"airQuality\":%ld.0, \"light\":%ld.0}}}"
    , device_id, (int)temperature, isMoving, (int)humidity, airQuality, light);
    snprintf (publishTopic, 100, "%s/senseme/%s", tenant_domain, device_id);
    if (temperature <= 100 && humidity <= 100 ) {
      client.publish(publishTopic, msg);
    }
    isMoving = 0;
    Serial.println(msg);
    
    startDelay = millis();
  }
}
