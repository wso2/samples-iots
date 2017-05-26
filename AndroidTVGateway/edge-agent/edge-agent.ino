#include <SoftwareSerial.h>
#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <DHT_U.h>
#include <SPI.h>
#include <MFRC522.h>

#define DHTPIN            6
#define DHTTYPE           DHT11
#define LED_LIGHT         8
#define DOOR_LOCK         7

DHT dht(DHTPIN, DHTTYPE);
SoftwareSerial XBee(2, 3); // RX, TX
uint8_t successRead;    // Variable integer to keep if we have Successful Read from Reader
byte readCard[4];   // Stores scanned ID read from RFID Module
int lastId = 0;

// Create MFRC522 instance.
constexpr uint8_t RST_PIN = 9;     // Configurable, see typical pin layout above
constexpr uint8_t SS_PIN = 10;     // Configurable, see typical pin layout above

MFRC522 mfrc522(SS_PIN, RST_PIN);

void setup() {
  XBee.begin(9600);
  Serial.begin(9600);
  dht.begin();
  pinMode(LED_LIGHT, OUTPUT);
  pinMode(DOOR_LOCK, OUTPUT);
  digitalWrite(DOOR_LOCK, HIGH);
  SPI.begin();      // Init SPI bus
  mfrc522.PCD_Init();   // Init MFRC522
  mfrc522.PCD_DumpVersionToSerial();  // Show details of PCD - MFRC522 Card Reader details
}

void loop() {
  if (XBee.available()) {
    String msg = XBee.readString();
    Serial.println(msg);
    if (msg == "LON\r") {
      digitalWrite(LED_LIGHT, HIGH);
      XBee.print("{\"a\":\"LON\",\"p\":\"OK\"}\r");
    } else if (msg == "LOFF\r") {
      digitalWrite(LED_LIGHT, LOW);
      XBee.print("{\"a\":\"LOFF\",\"p\":\"OK\"}\r");
    }else if (msg == "DOPEN\r") {
      digitalWrite(DOOR_LOCK, LOW);
      XBee.print("{\"a\":\"DOPEN\",\"p\":\"OK\"}\r");
    } else if (msg == "DCLOSE\r") {
      digitalWrite(DOOR_LOCK, HIGH);
      XBee.print("{\"a\":\"DCLOSE\",\"p\":\"OK\"}\r");
    } else if (msg == "D\r") {
      syncNode();
    }
  }
  if (getID()) {
    if (!lastId) {
      lastId = 1;
      char msg[100];
      sprintf (msg, "{\"a\":\"CI\",\"p\":\"%02x%02x%02x%02x\"}\r", readCard[0], readCard[1], readCard[2], readCard[3]);
      XBee.print(msg);
      Serial.println(msg);
    }    
  } else if (lastId) {
    lastId = 0;
    XBee.print("{\"a\":\"CO\"}\r");
    Serial.println("CARDOUT");    
  }
}

void syncNode() {
  float h = dht.readHumidity();
  float t = dht.readTemperature();
  char msg[100];
  sprintf (msg, "{\"a\":\"TEMP\",\"p\":{\"t\":%d,\"h\":%d}}\r", (int) t, (int) h);
  XBee.print(msg);
  Serial.println(msg);
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

