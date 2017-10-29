#include <FS.h>
#include <ArduinoJson.h>
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <PubSubClient.h>
#include <Keypad.h>
#include <rBase64.h>

const byte ROWS = 4;
const byte COLS = 3;

char keys[ROWS][COLS] = {
  {'1','2','3'},
  {'4','5','6'},
  {'7','8','9'},
  {'*','0','#'}
};

byte colPins[COLS] = {D3, D2, D1};
byte rowPins[ROWS] = {D7, D6, D5, D4};

Keypad myKeypad = Keypad(makeKeymap(keys), rowPins, colPins, ROWS, COLS);

const char* ssid = "Shehan";
const char* password = "123456789";

char type[32];
char deviceId[32];
char clientId[32];
char clientSecret[32];
char accessToken[64];
char refreshToken[64];

const char* mqttserver = "192.168.43.247";
const int mqttport = 1886;

char publishTopic[100];
char subscribeTopic[100];
char ack[100];
char input[4];

static char masterKey[5];
static char guestKey[5];

long set = 0;
int len;
int r = 0;

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

void loadGuest(){
  File guest = SPIFFS.open("/guest.json", "r");
  StaticJsonBuffer<200> jsonBuffer;
  if (!guest) {
    Serial.println("Guest profile doesn't exist yet. Create it..");
  } 
  else {
    size_t size = guest.size();
    std::unique_ptr<char[]> buf(new char[size]);
    guest.readBytes(buf.get(), size);
    JsonObject& json = jsonBuffer.parseObject(buf.get());
    strcpy(guestKey,(const char*)json["guestKey"]);
  }
  guest.close();
}

void storeKey(String msg){
  int index = msg.indexOf('\n');
  String accountType = msg.substring(0,index);
  Serial.println(accountType);
  String key = msg.substring(index+1);
  StaticJsonBuffer<200> jsonBuffer;
  JsonObject& json = jsonBuffer.createObject();
  if (accountType == "master"){
    json["masterKey"] = key;
    File master = SPIFFS.open("/master.json", "w");
    json.printTo(master);
    master.close();
    loadMaster(); 
  }
  else if(accountType == "guest"){
    json["guestKey"] = key;
    File guest = SPIFFS.open("/guest.json", "w");
    json.printTo(guest);
    guest.close();
    loadGuest();
  }
}

void getConfig(){
  SPIFFS.begin();
  Serial.println("SPIFFS opened");
  
  File f = SPIFFS.open("/f.json", "r");
  StaticJsonBuffer<200> jsonBuffer;
  if (!f) {
    Serial.println("File doesn't exist yet. Create it..");
  } 
  else {
    size_t size = f.size();
    if (size > 1024) {
      Serial.println("Config file size is too large");
    }
    else{
      std::unique_ptr<char[]> buf(new char[size]);
      f.readBytes(buf.get(), size);
      JsonObject& json = jsonBuffer.parseObject(buf.get());
      strcpy(type,(const char*)json["type"]);
      strcpy(deviceId,(const char*)json["deviceId"]);
      strcpy(clientId,(const char*)json["clientId"]);
      strcpy(clientSecret,(const char*)json["clientSecret"]);
      strcpy(accessToken,(const char*)json["accessToken"]);
      strcpy(refreshToken,(const char*)json["refreshToken"]);
    }
  }
  f.close();
  loadMaster();
  loadGuest();
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
   http.begin("http://192.168.43.247:8280/token");
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

void callback(char* topic, byte* payload, int length){
  String msg;
  for (int i = 0; i < length; i++){
    msg += (char)payload[i];
  }
  Serial.println(msg);
  int operationId =  (String(topic).substring(len,len+1)).toInt();
  Serial.println(topic);
  Serial.println(operationId);
  switch (operationId){
    case 2:
      client.publish(publishTopic,"{\"Temperature\":50,\"Status\":\"Unlock\"}");
      break;
    case 3:
      storeKey(msg);
      break;
    default:
      Serial.println("default");
      break;
    
  }
}

void setup() {
  Serial.begin(115200);
  delay(10);
  Wifi();
  getConfig();
  loadTokens();
  topics();
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
    Serial.print("Unlocked by Master Key");
    Serial.println();
  }
  else if (input[0]==guestKey[0] && input[1]==guestKey[1] && input[2]==guestKey[2] && input[3]==guestKey[3]){
    Serial.print("Unlocked by Guest Key");
    Serial.println();
  }
  else{
    Serial.println("Locked");
  }
  r = 0; 
}

void loop() {
  long now = millis();
  if (!client.connected()) {
    reconnect();
    return;
  }
  char myKey = myKeypad.getKey();
  if (myKey != NULL){
    input[r] = myKey;
    r++;
    Serial.print(myKey);
    Serial.print(" ");
    if (r == 4){
      Check();    
    }
  }
  client.loop();
}

