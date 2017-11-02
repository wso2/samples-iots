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
#define DLED D4
#define MDPIN D5
#define RELAY D6
#define IRPIN D7

//-------------------------------------------------------------------------------------------

#define DHTPIN D3
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);

//-------------------------------------------------------------------------------------------

#define I2CADDR 0x27

const byte ROWS = 4;
const byte COLS = 3;

char keys[ROWS][COLS] = {
  {'1', '2', '3'},
  {'4', '5', '6'},
  {'7', '8', '9'},
  {'*', '0', '#'}
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
char ackTopic[100];
char input[4];
long humidity;
long temperature;

static char masterKey[4];

int r = 0;
boolean isAllowOpen = true;
boolean isCodeExpired = false;
long lastSyncTime = 0;
int _lastIR, _lastMD, _lastSW;
char user[45];

void Dht() {
  int _humidity = dht.readHumidity();
  int _temperature = dht.readTemperature();
  if (_humidity < 100) {
    humidity = _humidity;
  }
  if (_temperature < 100) {
    temperature = _temperature;
  }
}

int IR() {
  if (digitalRead(IRPIN) == 1) {
    return 0;
  } else {
    return 1;
  }
}

int MD() {
  return digitalRead(MDPIN);
}

int SW() {
  return digitalRead(SWITCH);
}

void topics() {
  snprintf(publishTopic, 100, "carbon.super/%s/%s/events", type, deviceId);
  snprintf(subscribeTopic, 100, "carbon.super/%s/%s/operation/#", type, deviceId);
  snprintf(ackTopic, 100, "carbon.super/%s/%s/update/operation", type, deviceId);
}

void loadMaster() {
  File master = SPIFFS.open("/master.json", "r");
  StaticJsonBuffer<200> jsonBuffer;
  if (!master) {
    Serial.println("Master profile doesn't exist yet. Create it..");
  } else {
    size_t size = master.size();
    std::unique_ptr<char[]> buf(new char[size]);
    master.readBytes(buf.get(), size);
    JsonObject& json = jsonBuffer.parseObject(buf.get());
    char key[size];
    strcpy(key, (const char*)json["masterKey"]);
    String _masterKey = getStringPart(key, 0, ',');
    String _user = getStringPart(key, 1, ',');
    strcpy(masterKey, _masterKey.c_str());
    strcpy(user, _user.c_str());
  }
  master.close();
  isCodeExpired = false;
}

void storeKey(String key) {
  StaticJsonBuffer<200> jsonBuffer;
  JsonObject& json = jsonBuffer.createObject();
  json["masterKey"] = key;
  File master = SPIFFS.open("/master.json", "w");
  json.printTo(master);
  master.close();
  loadMaster();
}

void getConfig() {
  SPIFFS.begin();
  loadMaster();
}

void loadTokens() {
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
    strcpy(accessToken, (const char*)json["access_token"]);
    strcpy(refreshToken, (const char*)json["refresh_token"]);
  }
  tokens.close();
}

void getTokens() {

  HTTPClient http;
  http.begin(tokenEndpoint);
  http.addHeader("Authorization", "Basic " + rbase64.encode(String(clientId) + ":" + String(clientSecret)));
  http.addHeader("Content-Type", "application/x-www-form-urlencoded");

  char data[150];
  snprintf(data, 150, "grant_type=refresh_token&refresh_token=%s&scope=PRODUCTION", refreshToken);
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

    strcpy(accessToken, (const char*)root_2["access_token"]);
    strcpy(refreshToken, (const char*)root_2["refresh_token"]);

    File tokens = SPIFFS.open("/tokens.json", "w");
    root_2.printTo(tokens);
    tokens.close();
  }
  else {
    Serial.println("error_1");
  }
  http.end();

}

void Wifi() {
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

void Message(char* attempt) {
  char payload[250];
  Dht();
  _lastIR = IR();
  _lastMD = MD();
  _lastSW = SW();
  snprintf(payload, 250, "{\"temperature\":%ld.0,\"humidity\":%ld.0,\"metal\":%s,\"occupancy\":%s,\"open\":%s,\"attempt\":\"{\\\"status\\\":\\\"%s\\\",\\\"user\\\":\\\"%s\\\"}\"}", temperature, humidity, _lastMD == 0 ? "true" : "false", _lastIR == 1 ? "true" : "false", _lastSW == 1 ? "true" : "false", attempt, user);
  client.publish(publishTopic, payload);
  Serial.println(payload);
}

String getStringPart(String original, int partIndex, char separator) {
  String part = original;
  int currentPart = 0;
  while (currentPart <= partIndex) {
    int index = original.indexOf(separator);
    if (index > 0) {
      part = original.substring(0, index);
      original = original.substring(index + 1);
      currentPart++;
    } else if (currentPart == partIndex) {
      return original;
    } else {
      break;
    }
  }
  if (partIndex > currentPart) {
    return "";
  }
  return part;
}

void callback(char* topic, byte* payload, int length) {
  String msg;
  for (int i = 0; i < length; i++) {
    msg += (char)payload[i];
  }

  String incomingTopic = String(topic);
  String opCode = getStringPart(incomingTopic, 5, '/');
  String opId = getStringPart(incomingTopic, 6, '/');
  Serial.print("Operation Code: ");
  Serial.println(opCode);
  Serial.print("Operation Id: ");
  Serial.println(opId);
  char* status;
  char message[100];
  if (opCode == "lock_code") {
    storeKey(msg);
    status = "COMPLETED";
    snprintf(message, 100, "Lock code set for user %s", user);
  } else if (opCode == "allow_open") {
    isAllowOpen = (msg == "true");
    digitalWrite(DLED, isAllowOpen ? LOW : HIGH);
    status = "COMPLETED";
    snprintf(message, 100, "Lock is configured%s to open with code for user %s", !isAllowOpen ? " not" : "", user);
  } else {
    Serial.println("Unknown operation");
    status = "ERROR";
    strcpy(message, "Unknown Operation");
  }
  char opResponse[250];
  snprintf(opResponse, 250, "{\"id\": %s,\"status\": \"%s\", \"operationResponse\": \"%s\"}", opId.c_str(), status, message);
  Serial.println(opResponse);
  client.publish(ackTopic, opResponse);
}

void setup() {
  Serial.begin(115200);
  Serial.println("Starting...");
  delay(10);
  Wifi();
  getConfig();
  loadTokens();
  topics();
  pinMode(IRPIN, INPUT);
  pinMode(MDPIN, INPUT);
  pinMode(SWITCH, INPUT);
  pinMode(RELAY, OUTPUT);
  pinMode(DLED, OUTPUT);
  digitalWrite(DLED, LOW);
  digitalWrite(RELAY, HIGH);
  myKeypad.begin();
  myKeypad.addEventListener(keypadEvent);
  client.setServer(mqttserver, mqttport);
  client.setCallback(callback);
  if (client.connect(deviceId, accessToken, "")) {
    client.subscribe(subscribeTopic);
  } else {
    Serial.println("Error");
  }
}

void reconnect() {
  Serial.print("Attempting MQTT connection...");
  if (client.connect(deviceId, accessToken, "")) {
    Serial.println("MQTT Client Connected again");
    client.subscribe(subscribeTopic);
  }
  else {
    int state = client.state();
    Serial.print("failed, rc=");
    Serial.println(state);
    if (state == 4) {
      getTokens();
    }
    else {
      Serial.println("Try again in 5 seconds");
      delay(5000);
    }
  }
}

void Check() {
  Serial.println();
  if (isAllowOpen && !isCodeExpired && input[0] == masterKey[0] && input[1] == masterKey[1] && input[2] == masterKey[2] && input[3] == masterKey[3]) {
    isCodeExpired = true;
    Serial.println("Unlocking...");
    digitalWrite(RELAY, LOW);
    Message("Success");
    delay(5000);
    digitalWrite(RELAY, HIGH);
  } else if (!isAllowOpen) {
    Message("Blocked");
  } else if (isCodeExpired) {
    digitalWrite(DLED, HIGH);
    Message("Expired");
    delay(1000);
    digitalWrite(DLED, LOW);
    delay(1000);
    digitalWrite(DLED, HIGH);
  } else {
    digitalWrite(DLED, HIGH);
    Message("Denied");
  }
  r = 0;
  if (isAllowOpen) {
    delay(1000);
    digitalWrite(DLED, LOW);
  }
}

void loop() {
  long now = millis();
  if (!client.connected()) {
    reconnect();
    return;
  }
  char key = myKeypad.getKey();
  client.loop();
  if ((_lastIR != IR() || _lastMD != MD() || _lastSW != SW()) && now - lastSyncTime > 2000 || now - lastSyncTime > 60000) {
    lastSyncTime = now;
    Message("None");
  }
}

void keypadEvent(KeypadEvent key) {
  switch (myKeypad.getState()) {
    case PRESSED:
      if (r < 4 && key != '*' && key != '#') {
        input[r] = key;
        r++;
        Serial.print(key);
        Serial.print(" ");
      }
      if (r == 4) {
        Check();
      }
      break;

    case HOLD:
      if (key == '*') {
        r = 0;
        Serial.println();
        Serial.println("CLEAR");
      }
      break;
  }
}
