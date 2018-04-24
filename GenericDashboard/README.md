# Generic Dashboard

A generic dashboard which can be customised to work with any device type created on the WSO2 Iot server.

### Prerequisites

Java SE 8u144  
Maven  
Apache Tomcat   
WSO2 IOT server  


### Creating a new device type

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

Now register a device type can be registerd following the below steps.  

1).First go to https://localhost:9443/devicemgt/  
2).Sign in to the WSO2 Device Management Console using admin as the username and admin as the password.  
3).Click  > DEVICE TYPE MANAGEMENT > CREATE DEVICE TYPE.  
4).Enter the details needed to create a new device type and click Add Device Type.  

### Building and Deploying the app without an ide  
  
1).Clone the samples-iots repository from https://github.com/wso2/samples-iots.   
2).Then go to Samples-iots -> GenericDashboard.  
3).Then Build the web applicaiton using mvn clean install.  
4).Go to Samples-iots -> GenericDashboard -> web-app -> target and copy the dashboard-portal.war to apache-tomcat-8.5.23 -> webapps.  
5).Start the Tomcat server and confirm the startup by going to  http://localhost:8080/  
6).Then go to http://127.0.0.1:8080/dashboard-portal/ and login using admin as the username and admin as the password.  

  
## Building and Deploying the app with an ide  
  
1).Clone the weather station source code from https://github.com/wso2/samples-iots.   
2).Now open the project using an ide. And go to edit configurations  and add a new configuration tomcat->local.  
3).Next add artifact org.wso2.iot.dashboard.portal:war to the configuration.  
4).Now run the project using the ide.  
   
## Setting up the web app to suit your device type  

1). In the devices.jsp page you need to change the following variables according to your device type. An example is given below.  
    
    var deviceType="weatherStation";  // The exact name of your device type
      
    var typepParameter1="temperature";  // The exact name of the event attribute  you need to diplay  
    var displayName1="Temperature";     // The name you wish to display the even attribute under  
    var units1="&#8451";                // The units of the event attribute this is celcius in HTML unicode  
    
    var typeParameter2="humidity";     
    var displayName2="Humidity";    
    var units2="%";    
  
    var typeParameter3="winddir";    
    var displayName3="Wind Direction";    
    var units3="&#176";    
      
 2). Next in the details.jsp page you need to change the following variables according to your device type. An example is given below.    
    
    var deviceType="weatherstation";  // The exact name of your device type  

    var typepParameter1="temperature";  // The exact name of the event attribute  you need to diplay    
    var displayName1="Temperature";     // The name you wish to display the even attribute under    
    var units1="&#8451";                // The units of the event attribute this is celcius in HTML unicode    
  
    var typeParameter2="humidity";  
    var displayName2="Humidity";  
    var units2="%";  
  
    var typeParameter3="winddir";  
    var displayName3="Wind Direction";  
    var units3="&#176";  
  
    var typeParameter4="dewptf";  
    var displayName4="Dew Point";  
    var units4="&#8451";  
  
    var typeParameter5="windspeedmph";  
    var displayName5="Wind Speed";  
    var units5="<Strong> mph</Strong>";  
  
    var typeParameter6="rainin";  
    var displayName6="Raining";  
    var units6="&#176";  
  
    var typeParameter7="solarradiation";  
    var displayName7="Solar Radiation";   
    var units7="<Strong> mmpH</Strong>";  
  
    var typeParameter8="UV";  
    var displayName8="Ultra Violet";  
    var units8="<Strong> milliwatts</Strong>";  
  
    var typeParameter9="baromin";  
    var displayName9="Baromin";  
    var units9="<Strong> pascal</Strong>";  

  
## User guide  

1).First enter username and password to login . 

2).Now you will arrive at the devices page. Here you can see the details of the devices enrolled. You can 
go to the dashboard of each device by clicking the row of the device. 

3).To enroll a device instance click the ADD DEVICE button. Enter the relevant details in the following popup modal  and click ADD.Then a json file will be downloaded automatically with the relevent information of the device. 

4).There is a regex search bar in the sidebar which will let you find the device you need by name.

5).You can have all the enrolled devices in a map view by selecting the map view tab .

6).On the map view you can get the latest updates of the device and also you can go 
to the dashboard of the device by clicking each popup.

7).When you enter the Dashboardyou will be redirected to the real time analytics page where the current updates will be shown real time on each graph.

8)Historical data of the device cen be viewed by clicking the historical tab.Here there is a date 
range picker where you can chose the range of dates in which you need the records. There are some already 
defined ranges or you can give a custom date range.

9)Both tabs above can be expanded to fit the screen by hiding the side bar by clicking the arrow on the top left.



