#include <FS.h>
#include <ArduinoJson.h>
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include "PubSubClient.h"
#include <Keypad_I2C.h>
#include <Keypad.h>
#include <Wire.h>
#include <rBase64.h>
#include <DHT.h>

#define SWITCH D0
#define IRPIN D4
#define MDPIN D5
#define RELAY D6


//-------------------------------------------------------------------------------------------

#define DHTPIN D3
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE); 

//-------------------------------------------------------------------------------------------

#define I2CADDR 0x27

const byte ROWS = 4;
const byte COLS = 3;

char keys[ROWS][COLS] = {
  {'1','2','3'},
  {'4','5','6'},
  {'7','8','9'},
  {'*','0','#'}
};

byte rowPins[ROWS] = {0, 1, 2, 3};
byte colPins[COLS] = {4, 5, 6};

Keypad_I2C myKeypad = Keypad_I2C(makeKeymap(keys), rowPins, colPins, ROWS, COLS, I2CADDR);

//-------------------------------------------------------------------------------------------

const char* ssid = "Dialog 4G";
const char* password = "G8GQ0YBQ822";

char* type = "locker";
char* deviceId = "123456";
char* clientId = "dp2xTSSQE6zvDgwEQcJQ9LHSccwa";
char* clientSecret = "KoFbhegtxWdz9xUHrgNZOH9iH9Ya";
char* accessToken = "e7a14d70-a9f8-3708-820b-d525ec453dff";
char* refreshToken = "bbd032a9-3996-3cf5-a851-bf2f78b1e22a";

const char* tokenEndpoint = "http://192.168.8.101:8280/token";
const char* mqttserver = "192.168.8.101";
const int mqttport = 1886;

char publishTopic[100];
char subscribeTopic[100];
char ack[100];
char input[4];
float humidity;
float temperature;

static char masterKey[5];

long set = 0;
int len;
int r = 0;
boolean Open = true;

void Dht(){
  humidity = dht.readHumidity();
  temperature = dht.readTemperature();
}

bool IR(){
  int val = digitalRead(IRPIN);
  if (val == 1){
    return false;
  }
  else{
    return true;
  }
}

bool MD(){
  int val = digitalRead(MDPIN);
  if (val == 1){
    return false;
  }
  else{
    return true;
  }
}

bool SW(){
  int val = digitalRead(SWITCH);
  if (val == 1){
    return false;
  }
  else{
    return true;
  }
}

void Unlock(){
  long t = millis();
  digitalWrite(RELAY,HIGH);
  while(millis()-t > 2000){
    if (millis()-t > 2000){
      digitalWrite(RELAY,LOW);
    }
  }
}

void topics(){
  snprintf(publishTopic,100,"carbon.super/%s/%s/events",type,deviceId);
  snprintf(subscribeTopic,100,"carbon.super/%s/%s/operation/#",type,deviceId);
  snprintf(ack,100,"carbon.super/%s/%s/operation/profile/",type,deviceId);
  String s = String(ack);
  len = s.length();
}

void loadMaster(){
  File master = SPIFFS.open("/master.json", "r");
  StaticJsonBuffer<200> jsonBuffer;
  if (!master) {
    Serial.println("Master profile doesn't exist yet. Create it..");
  } 
  else {
    size_t size = master.size();
    std::unique_ptr<char[]> buf(new char[size]);
    master.readBytes(buf.get(), size);
    JsonObject& json = jsonBuffer.parseObject(buf.get());
    strcpy(masterKey,(const char*)json["masterKey"]);
  }
  master.close();
}

void storeKey(String key){
  StaticJsonBuffer<200> jsonBuffer;
  JsonObject& json = jsonBuffer.createObject();
  json["masterKey"] = key;
  File master = SPIFFS.open("/master.json", "w");
  json.printTo(master);
  master.close();
  loadMaster(); 
}

void getConfig(){
  SPIFFS.begin();
//  Serial.println("SPIFFS opened");
//  
//  File f = SPIFFS.open("/f.json", "r");
//  StaticJsonBuffer<200> jsonBuffer;
//  if (!f) {
//    Serial.println("File doesn't exist yet. Create it..");
//  } 
//  else {
//    size_t size = f.size();
//    if (size > 1024) {
//      Serial.println("Config file size is too large");
//    }
//    else{
//      std::unique_ptr<char[]> buf(new char[size]);
//      f.readBytes(buf.get(), size);
//      JsonObject& json = jsonBuffer.parseObject(buf.get());
//      strcpy(type,(const char*)json["type"]);
//      strcpy(deviceId,(const char*)json["deviceId"]);
//      strcpy(clientId,(const char*)json["clientId"]);
//      strcpy(clientSecret,(const char*)json["clientSecret"]);
//      strcpy(accessToken,(const char*)json["accessToken"]);
//      strcpy(refreshToken,(const char*)json["refreshToken"]);
//    }
//  }
//  f.close();
  loadMaster();
}

void loadTokens(){
  File tokens = SPIFFS.open("/tokens.json", "r");
  StaticJsonBuffer<200> jsonBuffer;
  if (!tokens) {
    Serial.println("File doesn't exist yet. Continue with default tokens.");
  } 
  else {
    size_t size = tokens.size();
    std::unique_ptr<char[]> buf(new char[size]);
    tokens.readBytes(buf.get(), size);
    JsonObject& json = jsonBuffer.parseObject(buf.get());
    strcpy(accessToken,(const char*)json["access_token"]);
    strcpy(refreshToken,(const char*)json["refresh_token"]);
  }
  tokens.close(); 
}

void getTokens(){
  
   HTTPClient http;
   http.begin(tokenEndpoint);
   http.addHeader("Authorization", "Basic "+rbase64.encode(String(clientId)+":"+String(clientSecret)));
   http.addHeader("Content-Type", "application/x-www-form-urlencoded");
  
   char data[150];
   snprintf(data,150,"grant_type=refresh_token&refresh_token=%s&scope=PRODUCTION",refreshToken);
   int httpCode = http.POST(String(data));
     
   String payload = http.getString();
   Serial.println(payload);

   if (httpCode == 200) {
     StaticJsonBuffer<1024> jsonBuffer_1;
     StaticJsonBuffer<200> jsonBuffer_2;
     JsonObject& root_1 = jsonBuffer_1.parseObject(payload);
     JsonObject& root_2 = jsonBuffer_2.createObject();

     root_2["access_token"]  = root_1["access_token"];
     root_2["refresh_token"] = root_1["refresh_token"];
     String access = root_1["access_token"];

     Serial.print("new access token :");
     Serial.println(access);

     strcpy(accessToken,(const char*)root_2["access_token"]);
     strcpy(refreshToken,(const char*)root_2["refresh_token"]);

     File tokens = SPIFFS.open("/tokens.json", "w");
     root_2.printTo(tokens);
     tokens.close(); 
   }
   else{
     Serial.println("error_1");
   }
   http.end();
  
}

void Wifi(){
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
}

WiFiClient espClient;
PubSubClient client(espClient);

void Message(String attempt){
  char payday[250];
  Dht();
  snprintf(payday,250,"{\"temperature\":\"%f\",\"humidity\":\"%f\",\"metal\":\"%b\",\"occupancy\":\"%b\",\"open\":\"%b\",\"attempt\":\"%s\"}",temperature,humidity,MD(),IR(),SW(),attempt.c_str());
  client.publish(publishTopic,payday);
}

void callback(char* topic, byte* payload, int length){
  String msg;
  for (int i = 0; i < length; i++){
    msg += (char)payload[i];
  }
  Serial.println(msg);
  String Topic = String(topic);
  String topicPart = Topic.substring(len,Topic.length());
  int index = topicPart.indexOf('/');
  String operationId =  Topic.substring(len,len+index);
  Serial.println(topicPart);
  Serial.println(topic);
  Serial.println(operationId);

  if (operationId == "lock_released"){
    Open = !Open;
  }
  else if(operationId == "lock_code"){
    storeKey(msg);
  }
  else{
    Serial.println("default");
  }
}

void setup() {
  Serial.begin(115200);
  Serial.println("Starting...");
  delay(10);
  Wifi();
  getConfig();
  loadTokens();
  topics();
  pinMode(IRPIN,INPUT);
  pinMode(MDPIN,INPUT);
  pinMode(SWITCH,INPUT);
  pinMode(RELAY,OUTPUT);
  myKeypad.begin();
  myKeypad.addEventListener(keypadEvent);
  client.setServer(mqttserver,mqttport);
  client.setCallback(callback);
  if (client.connect(deviceId,accessToken,"")) {
    client.subscribe(subscribeTopic);
  }
  else{
    Serial.println("Error");
  }
}

void reconnect() {
  Serial.print("Attempting MQTT connection...");
  if (client.connect(deviceId, accessToken,"")) {
    Serial.println("MQTT Client Connected again");
    client.subscribe(subscribeTopic);
  } 
  else {
    int state = client.state();
    Serial.print("failed, rc=");
    Serial.println(state);
    if (state == 4){
      getTokens();
    }
    else{
      Serial.println("Try again in 5 seconds");
      delay(5000);
    } 
  }
}

void Check() {
  Serial.println();
  if (input[0]==masterKey[0] && input[1]==masterKey[1] && input[2]==masterKey[2] && input[3]==masterKey[3]){
    Serial.print("Unlocked");
    Unlock();
    Message("succes");
    Serial.println();
  }
  else{
    Serial.println("Locked");
    Message("denied");
  }
  r = 0; 
}

void loop() {
  long now = millis();
  if (!client.connected()) {
    reconnect();
    return;
  }
  char key = myKeypad.getKey();
  client.loop();
}

void keypadEvent(KeypadEvent key){
  switch (myKeypad.getState()){
    case PRESSED:
      if (Open && r<4 && key != '*' && key != '#'){
        input[r] = key;
        r++;
        Serial.print(key);
        Serial.print(" ");
      }
      if (r == 4){
        Check();
      }
      break;
      
    case HOLD:
      if (key == '*'){
        r = 0;
        Serial.println();
        Serial.println("CLEAR");
      }
      break;
  }
}

