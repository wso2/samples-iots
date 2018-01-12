/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.iot.sample.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.iot.sample.constants.DeviceConstants;
import org.wso2.iot.sample.mqtt.MQTTHandler;
import org.wso2.iot.sample.mqtt.MessageReceivedCallback;
import org.wso2.iot.sample.mqtt.transport.TransportHandlerException;
import org.wso2.iot.sample.util.LocalRegistry;
import org.wso2.iot.sample.util.dto.Operation;

public class DeviceManagementService extends Service {

    private static final String TAG = DeviceManagementService.class.getSimpleName();
    private final IBinder myBinder = new DeviceManagementBinder();
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
                Intent broadcast = new Intent();
                broadcast.putExtra(DeviceConstants.CHECKOUT_OPERATION, operation);
                broadcast.setAction(DeviceConstants.OPERATION_BROADCAST_ACTION);
                sendBroadcast(broadcast);
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

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public void publishMessage(String payload) throws TransportHandlerException {
        String topic = mqttHandler.getPublishTopic();
        mqttHandler.publishDeviceData(topic, payload);
    }

    public void publishOperationResponse(int operationId, String payload) throws TransportHandlerException {
        // carbon.super/mobile_client/807393bab81898c6/update/operation
        String topic = DeviceConstants.TENANT_ID + "/" + DeviceConstants.DEVICE_TYPE + "/" +
                LocalRegistry.getDeviceId(getApplicationContext()) + "/update/operation";

        // {"id": 1,"status": "COMPLETED", "operationResponse": "this is my response"}
        JSONObject operationResponse = new JSONObject();
        try {
            operationResponse.put("id", operationId);
            operationResponse.put("status", "COMPLETED");
            operationResponse.put("operationResponse", payload);
            mqttHandler.publishDeviceData(topic, operationResponse.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public class DeviceManagementBinder extends Binder {
        public DeviceManagementService getService() {
            return DeviceManagementService.this;
        }
    }

}
