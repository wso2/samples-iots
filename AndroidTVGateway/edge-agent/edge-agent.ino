#include <SoftwareSerial.h>
#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <DHT_U.h>

#define DHTPIN            6
#define DHTTYPE           DHT11
#define LED               13

DHT dht(DHTPIN, DHTTYPE);
SoftwareSerial XBee(2, 3); // RX, TX

void setup() {
  XBee.begin(9600);
  Serial.begin(9600);
  dht.begin();
  pinMode(LED, OUTPUT);
}

void loop() {
  if (XBee.available()) {
    char a = XBee.read();
    Serial.write(a);
    if (a == 'H') {
      digitalWrite(LED, HIGH);
      XBee.print("OK\r");
    } else if (a == 'L') {
      digitalWrite(LED, LOW);
      XBee.print("OK\r");
    } else if (a == 'D') {
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
