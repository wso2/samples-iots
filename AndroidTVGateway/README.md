# Android TV Plugin
WSO2 IoT server plugin and agent for Android TV devices.

## Instructions
- Copy 'androidtv' directory and samples-deployer.xml to <IoTS_HOME>/samples directory.
- Overwrite exiting samples-deployer.xml in <IoTS_HOME>/samples directory.
- Run mvn clean install -f samples-deployer.xml to install the plugin
- Start broker, core and analytics.
- Login in to https://localhost:9443/devicemgt using 'admin' as user name and password.
