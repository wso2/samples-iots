#!/bin/bash
echo "" >> agent.log
echo "------------------------------" >> agent.log
upgradeFile="upgrade.zip"
if [ -f "$upgradeFile" ]
then
    echo "Upgrading firmware..." >> agent.log
    sleep 5
    timestamp=$(date +%s)
    backup="backup"
    backupLocation="$backup/$timestamp/"
    if [! -d "$backup" ]
    then
        mkdir "$backup"
    fi
    mkdir "$backupLocation"
    mv agent.jar "$backupLocation"
    mv libs/ "$backupLocation"
    mv start.sh "$backupLocation"
    unzip "$upgradeFile"
    mv agent/* .
    mv agent/libs .
    rm "$upgradeFile"
    echo "Firmware upgraded..." >> agent.log
    sleep 2
    ./start.sh &
else
    java -cp agent.jar:libs/* org.wso2.iot.agent.Application
fi