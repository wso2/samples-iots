#include <SoftwareSerial.h>
#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <DHT_U.h>
#include <SPI.h>
#include <MFRC522.h>

#define DHTTYPE           DHT11
#define WINDOWPIN         4
#define ACPIN             5
#define DHTPIN            6
#define DOOR_LOCK         7
#define LED_LIGHT         8

DHT dht(DHTPIN, DHTTYPE);
SoftwareSerial XBee(2, 3); // RX, TX
uint8_t successRead;    // Variable integer to keep if we have Successful Read from Reader
byte readCard[4];   // Stores scanned ID read from RFID Module
int lastId = 0;
char msg[100];
boolean isCardIn = false;
boolean isBulbOn = false;

// Create MFRC522 instance.
constexpr uint8_t RST_PIN = 9;     // Configurable, see typical pin layout above
constexpr uint8_t SS_PIN = 10;     // Configurable, see typical pin layout above

MFRC522 mfrc522(SS_PIN, RST_PIN);

void setup() {
  XBee.begin(9600);
  Serial.begin(9600);
  dht.begin();
  pinMode(WINDOWPIN, INPUT);
  pinMode(ACPIN, INPUT);
  pinMode(LED_LIGHT, OUTPUT);
  pinMode(DOOR_LOCK, OUTPUT);
  digitalWrite(DOOR_LOCK, HIGH);
  SPI.begin();      // Init SPI bus
  mfrc522.PCD_Init();   // Init MFRC522
  mfrc522.PCD_DumpVersionToSerial();  // Show details of PCD - MFRC522 Card Reader details
}

void loop() {
  if (XBee.available()) {
    String incomingMsg = XBee.readString();
    Serial.println(incomingMsg);
    if (incomingMsg == "LON\r") {
      digitalWrite(LED_LIGHT, HIGH);
      XBee.print("{\"a\":\"LON\"}\r");
      isBulbOn = true;
    } else if (incomingMsg == "LOFF\r") {
      digitalWrite(LED_LIGHT, LOW);
      XBee.print("{\"a\":\"LOFF\"}\r");
      isBulbOn = false;
    }else if (incomingMsg == "DOPEN\r") {
      digitalWrite(DOOR_LOCK, LOW);
      XBee.print("{\"a\":\"DOPEN\"}\r");
    } else if (incomingMsg == "DCLOSE\r") {
      digitalWrite(DOOR_LOCK, HIGH);
      XBee.print("{\"a\":\"DCLOSE\"}\r");
    } else if (incomingMsg == "D\r") {
      syncNode();
    }
  }
  if (getID()) {
    if (!lastId) {
      lastId = 1;
      digitalWrite(DOOR_LOCK, LOW);
      isCardIn = true;
    }    
  } else if (lastId) {
    lastId = 0;
    digitalWrite(DOOR_LOCK, HIGH);
    isCardIn = true;    
  }
}

void syncNode() {
  float h = dht.readHumidity();
  float t = dht.readTemperature();
  int window = digitalRead(WINDOWPIN);
  int ac = digitalRead(ACPIN);
  char syncmsg[100];
  sprintf (syncmsg, "{\"a\":\"DATA\",\"p\":{\"t\":%d,\"h\":%d,\"a\":%d,\"w\":%d,\"k\":%d,\"l\":%d}}\r", (int) t, (int) h, ac, window, isCardIn, isBulbOn);
  XBee.print(syncmsg);
  Serial.println(syncmsg);
}

uint8_t getID() {
  // Getting ready for Reading PICCs
  if ( ! mfrc522.PICC_IsNewCardPresent()) { //If a new PICC placed to RFID reader continue
    return 0;
  }
  if ( ! mfrc522.PICC_ReadCardSerial()) {   //Since a PICC placed get Serial and continue
    return 0;
  }
  // There are Mifare PICCs which have 4 byte or 7 byte UID care if you use 7 byte PICC
  // I think we should assume every PICC as they have 4 byte UID
  // Until we support 7 byte PICCs
  Serial.println(F("Scanned PICC's UID:"));
  for ( uint8_t i = 0; i < 4; i++) {  //
    readCard[i] = mfrc522.uid.uidByte[i];
    Serial.print(readCard[i], HEX);
  }
  Serial.println("");
  mfrc522.PCD_Reset(); // Stop reading
  delay(2000);
  mfrc522.PCD_Init();    // Init MFRC522
  return 1;
}

