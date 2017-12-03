/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.iot.mqttsample.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.wso2.iot.mqttsample.mqtt.MQTTHandler;
import org.wso2.iot.mqttsample.mqtt.MessageReceivedCallback;
import org.wso2.iot.mqttsample.mqtt.transport.TransportHandlerException;
import org.wso2.iot.mqttsample.util.dto.Operation;

public class DeviceManagementService extends Service {

    private MQTTHandler mqttHandler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
       mqttHandler = new MQTTHandler(this, new MessageReceivedCallback() {
            @Override
            public void onMessageReceived(Operation operation) throws JSONException {
                //TODO: Handle incoming operations here
            }
        });
       mqttHandler.connect();
    }

    @Override
    public void onDestroy() {
        if (mqttHandler != null && mqttHandler.isConnected()) {
            mqttHandler.disconnect();
        }
        mqttHandler = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //TODO: Use thhis method to publish events
    private void publishMessage(String payload) throws TransportHandlerException {
        String topic = mqttHandler.getPublishTopic();
        mqttHandler.publishDeviceData(topic, payload);
    }

}
