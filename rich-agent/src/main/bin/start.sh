#!/bin/bash
file="temp.jar"
if [ -f "$file" ]
then
    echo "Upgrading firmware..." >> agent.log
    mv agent.jar agent.jar.bkp
    mv temp.jar agent.jar
    sleep 5
fi
java -jar agent.jar