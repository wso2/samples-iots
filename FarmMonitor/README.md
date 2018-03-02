#Configuration

Before you begin make sure that Java SE 8u144 (Use this exact version to avoid complications),
Maven and Apache Tomcat are installed.

First you need to start the WSO2 IoT Server’s broker, core, and analytics profile

cd <IOTS_HOME>/bin 
------Linux/Mac OS/Solaris ----------
./broker.sh
./iot-server.sh
./analytics.sh
  
-----Windows-----------
broker.bat
iot-server.bat
Analytics.bat

#Registering device type via UIs

1).First go to https://localhost:9443/devicemgt/
2).Sign in to the WSO2 Device Management Console using admin as the username and admin as the password.
3).Click  > DEVICE TYPE MANAGEMENT > CREATE DEVICE TYPE.
4).Enter the following details to add a new device type and click Add Device Type.

tempf=double
humidity=double
dewptf=double
windchillf=double
winddir=double
windspeedmph=double
windgustmph=double
rainin=double
dailyrainin=double
weeklyrainin=double
monthlyrainin=double
yearlyrainin=double
solarradiation=double
UV=double
indoortempf=double
indoorhumidity=double
baromin=double
lowbatt=double
dateutc=String
softwaretype=String
action=String
realtime=bool
rtfreq=bool

#Building and Deploying the app without an ide

1).Clone the weather station source code from https://github.com/wso2/samples-iots. 
2).Then go to Samples-iots -> WeatherStation.
3).Build using mvn clean install.
4).Go to Samples-iots -> WeatherStation -> web-app -> target and copy the weatherstation-portal.war to apache-tomcat-8.5.23 -> webapps.
5).Start the Tomcat server and confirm the startup by going to  http://localhost:8080/
6).Then go to http://127.0.0.1:8080/weatherstation-portal/ and login using admin as the username and admin as the password.

#Building and Deploying the app with an ide
1).Clone the weather station source code from https://github.com/wso2/samples-iots. 
2).Now open the project using an ide. And go to edit configurations  and add a new configuration tomcat->local.
3).Next add artifact org.wso2.iot.weatherstation.portal:war to the configuration.
4).Now just run the project using the ide.


#User guide

1).First enter username and password to login . 

2).Now you will arrive at the devices page. Here you can see the details of the weather stations enrolled. You can 
go to the dashboard of each device by clicking on the button in the right corner of each row. 

3).You can enroll a weather station instance via the App. To enroll a weather station instance click ADD WEATHER
 STATION. Enter the relevant details in the following popup modal  and click ADD to add the device.Then a json 
 file will be downloaded automatically. 

4).There is a regex search bar in the sidebar which will let you find the device you need by name.

5).You can have all the devices in a map view by selecting the map view tab .

6).On the map view you can click on each marker to get the latest details of the station and also you can go 
to the dashboard of the device by clicking on the button in the marker.

7).Once you click the button to view dashboard in the map view or table view you will be redirected to the 
 real time analytics page where the current updates will be shown real time.

8).Next you can check the historical data of the device by clicking the historical tab.Here there is a date 
range picker where you can chose the range of dates in which you need the records. There are some already 
defined ranges or you can give a custom date range.

9).Finally you can click on the Dashboard tab to get a dashboard of the device.You click on each graph to expand 
them to the whole page.



