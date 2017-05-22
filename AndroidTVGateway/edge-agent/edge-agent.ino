#include <SoftwareSerial.h>
#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <DHT_U.h>

#define DHTPIN            6
#define DHTTYPE           DHT11
#define LED_LIGHT         13
#define DOOR_LOCK         7

DHT dht(DHTPIN, DHTTYPE);
SoftwareSerial XBee(2, 3); // RX, TX

void setup() {
  XBee.begin(9600);
  Serial.begin(9600);
  dht.begin();
  pinMode(LED_LIGHT, OUTPUT);
  pinMode(DOOR_LOCK, OUTPUT);
  digitalWrite(DOOR_LOCK, HIGH);
  XBee.print("Device Connected\r");
}

void loop() {
  if (XBee.available()) {
    String msg = XBee.readString();
    Serial.println(msg);
    if (msg == "LON\r") {
      digitalWrite(LED_LIGHT, HIGH);
      XBee.print("Light ON\r");
    } else if (msg == "LOFF\r") {
      digitalWrite(LED_LIGHT, LOW);
      XBee.print("Light OFF\r");
    }else if (msg == "DOPEN\r") {
      digitalWrite(DOOR_LOCK, LOW);
      XBee.print("Door Lock Open\r");
    } else if (msg == "DCLOSE\r") {
      digitalWrite(DOOR_LOCK, HIGH);
      XBee.print("Door Lock Close\r");
    } else if (msg == "D\r") {
      int h = dht.readHumidity();
      int t = dht.readTemperature(); 
      XBee.print("Humidity: ");
      XBee.print(h);
      XBee.print("%  ");
      XBee.print("Temperature: ");
      XBee.print(t);
      XBee.print("C\r");     
    }
  }
}
