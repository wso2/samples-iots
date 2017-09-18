#Floor Analytics Demo
This sample is designed to monitor the different buildings using various sensors (i.e. temperature, motion, etc.) and
 to generate the alerts whenever the un-usual behavior is detected. This sample is capable of supporting real-time 
 analytics and batch analytics. Following are some sample real life scenarios, where we could make use of this.
 - Monitoring the temperature level of ATMs remotely to make sure the proper temperature level is optimum to support 
 the functionality of it.
 - Monitoring the pattern of usage in a building to configure the AC, light on/off settings to get the optimal energy
  usage.   
##Instructions
- Move this sample (i.e. building-visualizer directory) to <IoTS_HOME>/samples/ folder
- Copy building-visualizer-deployer.xml to <IoTS_HOME>/samples/
- Run mvn clean install -f building-visualizer-deployer.xml to deploy sample
