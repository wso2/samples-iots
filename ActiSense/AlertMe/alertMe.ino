#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <ESP8266WiFi.h>

const char* ssid = "TP-LINK_EB7F32";
const int motorPin = 12; //corresponds to D6
const int bulbPin = 13; //corresponds to D7
const int ON = 1;
const int OFF = 0;
const char* password = "80246008";
const String creds = "admin:admin";
const String tokenEP = "http://192.168.0.103:8280/oauth2wrapper/introspect";
const String cntType = "application/x-www-form-urlencoded";
const String tokenhdr = "token=08a8c552-b515-399d-a05f-d3139de4a316";

// Create an instance of the server
// specify the port to listen on as an argument
WiFiServer server(80);

void setup() {
  Serial.begin(115200);
  delay(10);

  // prepare GPIO2
  //pinMode(2, OUTPUT); //default led
  //digitalWrite(2, 0); //default led

  pinMode(bulbPin, OUTPUT);
  digitalWrite(bulbPin, 0);

  pinMode(motorPin, OUTPUT);
  digitalWrite(motorPin, 0);

  // Connect to WiFi network
  Serial.println();
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

  // Start the server
  server.begin();
  Serial.println("Server started");

  // Print the IP address
  Serial.println(WiFi.localIP());
}


String callKeyM()
{
  if(WiFi.status()== WL_CONNECTED){   //Check WiFi connection status
    
    HTTPClient http;    //Declare object of class HTTPClient
    
    http.begin(tokenEP);      //Specify request destination
    http.addHeader("content-type", cntType);  //Specify content-type header
    http.addHeader("user", creds);
    
    int httpCode = http.POST(tokenhdr);   //Send the request
    String payload = http.getString();    //Get the response payload
    
    http.end();  //Close connection
    return payload;
  }else{
    Serial.println("Error in WiFi connection");   
  }
}

void loop() {  
  // Check if a client has connected
  WiFiClient client = server.available();
  if (!client) {
    return;
  }

  // Wait until the client sends some data
  Serial.println("new client");
  while(!client.available()){
    delay(1);
  }

  // Read the first line of the request
  String req = client.readStringUntil('\r');
  Serial.println(req);
  client.flush();

  // Match the request
  int val;
  String outcome;
  if (req.indexOf("/bulb/0") != -1){
    outcome = callKeyM();
    if(outcome == "true"){
      digitalWrite(bulbPin, OFF);
    }
  }
  else if (req.indexOf("/bulb/1") != -1){
    outcome = callKeyM();
    if(outcome == "true"){
      digitalWrite(bulbPin, ON);
    }
  }else if (req.indexOf("/motor/1") != -1){
    outcome = callKeyM();
    if(outcome == "true"){
      digitalWrite(motorPin, ON);
    }
  }else if (req.indexOf("/motor/0") != -1){
    outcome = callKeyM();
    if(outcome == "true"){
      digitalWrite(motorPin, OFF);
    }
  }else {
    Serial.println("invalid request");
    client.stop();
    return;
  }

   Serial.println("$$$ Outcome :" +outcome);

// Set GPIO2 according to the request
//  if(outcome == "true"){
//    digitalWrite(2, val); //default led
//    digitalWrite(13, val);
//  }

  char temp[400];
  int sec = millis() / 1000;
  int min = sec / 60;
  int hr = min / 60;

  snprintf ( temp, 400,
  
"HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n<!DOCTYPE HTML>\r\n<html>\
  <head>\
    <meta http-equiv='refresh' content='5'/>\
    <title>ESP8266 DEMO</title>\
    <style>\
      body { background-color: #cccccc; font-family: Arial, Helvetica, Sans-Serif; Color: #000088; }\
    </style>\
  </head>\
  <body>\
    <h1>GPIO is now : %02d</h1>\
    <p>Uptime: %02d:%02d:%02d</p>\
  </body>\
</html>",

    val, hr, min % 60, sec % 60
  );
  client.print (temp);
  
  delay(1);
  Serial.println("Client disonnected");

  // The client will actually be disconnected
  // when the function returns and 'client' object is detroyed
}

