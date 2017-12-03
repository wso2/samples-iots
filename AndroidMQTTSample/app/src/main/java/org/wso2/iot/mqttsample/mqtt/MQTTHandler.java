/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.iot.mqttsample.mqtt;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.wso2.iot.mqttsample.constants.DeviceConstants;
import org.wso2.iot.mqttsample.mqtt.transport.MQTTTransportHandler;
import org.wso2.iot.mqttsample.mqtt.transport.TransportHandlerException;
import org.wso2.iot.mqttsample.util.APIClient;
import org.wso2.iot.mqttsample.util.LocalRegistry;
import org.wso2.iot.mqttsample.util.dto.Operation;

/**
 * This is an example for the use of the MQTT capabilities provided by the IoT-Server. This example depicts the use
 * of MQTT Transport for the Android Sense device-type. This class extends the abstract class
 * "MQTTTransportHandler". "MQTTTransportHandler" consists of the MQTT client specific functionality and implements
 * the "TransportHandler" interface. The actual functionality related to the "TransportHandler" interface is
 * implemented here, in this concrete class. Whilst the abstract class "MQTTTransportHandler" is intended to provide
 * the common MQTT functionality, this class (which is its extension) provides the implementation specific to the
 * MQTT communication of the Device-Type (Android Sense) in concern.
 * <p/>
 * Hence, the methods of this class are implementation of the "TransportHandler" interface which handles the device
 * specific logic to connect-to, publish-to, process-incoming-messages-from and disconnect-from the MQTT broker
 * listed in the configurations.
 */
public class MQTTHandler extends MQTTTransportHandler {

    private static final String TAG = MQTTHandler.class.getSimpleName();

    private MessageReceivedCallback messageReceivedCallback;
    private String topicPrefix;
    private String publishTopic;
    private Context context;

    /**
     * Default constructor for the MQTTHandler.
     */
    public MQTTHandler(Context context, MessageReceivedCallback messageReceivedCallback) {
        super(context);
        this.context = context;
        this.messageReceivedCallback = messageReceivedCallback;
        this.topicPrefix = DeviceConstants.TENANT_ID + "/" + DeviceConstants.DEVICE_TYPE + "/" +
                LocalRegistry.getDeviceId(context);
        this.publishTopic = topicPrefix + DeviceConstants.PUBLISH_TOPIC_SUFFIX;
    }

    /**
     * {@inheritDoc}
     * AndroidSense device-type specific implementation to connect to the MQTT broker and subscribe to a topic.
     * This method is called to initiate a MQTT communication.
     */
    @Override
    public void connect() {

        Runnable connector = new Runnable() {
            public void run() {
                APIClient client = new APIClient(context);
                while (!isConnected()) {
                    client.renewToken();
                    setUsernameAndPassword(LocalRegistry.getAccessToken(context), "");
                    try {
                        connectToQueue();
                    } catch (TransportHandlerException e) {
                        Log.e(TAG, "Connection to MQTT Broker at: " + mqttBrokerEndPoint + " failed", e);
                        try {
                            Thread.sleep(timeoutInterval);
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                            Log.e(TAG, "MQTT-Connector: Thread Sleep Interrupt Exception.", ex);
                        }
                    }

                    try {
                        subscribeToQueue();
                    } catch (TransportHandlerException e) {
                        Log.w(TAG, "Subscription to MQTT Broker at: " + mqttBrokerEndPoint + " failed", e);
                    }
                }
            }
        };

        Thread connectorThread = new Thread(connector);
        connectorThread.start();
    }


    /**
     * {@inheritDoc}
     * AndroidSense device-type specific implementation to process incoming messages. This is the specific
     * method signature of the overloaded "processIncomingMessage" method that gets called from the messageArrived()
     * callback of the "MQTTTransportHandler".
     */
    @Override
    public void processIncomingMessage(MqttMessage mqttMessage, String... messageParams) {
        if (messageParams.length != 0) {
            // owner and the deviceId are extracted from the MQTT topic to which the message was received.
            // <Topic> = [ServerName/Owner/DeviceType/DeviceId/#]
            String topic = messageParams[0];
            String[] topicParams = topic.split("/");
            String deviceId = topicParams[2];

            Log.d(TAG, "Received MQTT message for: [DEVICE.ID-" + deviceId + "]");

            String msg = mqttMessage.toString();
            Log.d(TAG, "MQTT: Received Message [" + msg + "] topic: [" + topic + "]");

            String operationSpecifier = topic.replace(this.topicPrefix + "/operation/", "");
            String operationParams[] = operationSpecifier.split("/");

            try {
                Operation operation = new Operation();
                operation.setType(Operation.Type.valueOf(operationParams[0].toUpperCase()));
                operation.setCode(operationParams[1]);
                operation.setId(Integer.parseInt(operationParams[2]));
                operation.setPayload(msg);
                messageReceivedCallback.onMessageReceived(operation);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage(), e);
            }
        } else {
            String errorMsg =
                    "MQTT message [" + mqttMessage.toString() + "] was received without the" +
                            " topic information.";
            Log.w(TAG, errorMsg);
        }
    }

    /**
     * {@inheritDoc}
     * AndroidSense device-type specific implementation to publish data to the device. This method calls the
     * {@link #publishToQueue(String, MqttMessage)} method of the "MQTTTransportHandler" class.
     */
    @Override
    public void publishDeviceData(String... publishData) throws TransportHandlerException {
        if (publishData.length < 1 || publishData.length > 2) {
            String errorMsg = "Incorrect number of arguments received to SEND-MQTT Message. " +
                    "Need to be [content, topic*] topic is optional";
            Log.e(TAG, errorMsg);
            throw new TransportHandlerException(errorMsg);
        }
        if (publishData.length == 2) {
            publishToQueue(publishData[1], publishData[0]);
        }
    }


    /**
     * {@inheritDoc}
     * Android Sense device-type specific implementation to disconnect from the MQTT broker.
     */
    @Override
    public void disconnect() {
        Runnable stopConnection = new Runnable() {
            public void run() {
                while (isConnected()) {
                    try {
                        closeConnection();
                    } catch (MqttException e) {
                        Log.w(TAG, "Unable to 'STOP' MQTT connection at broker at: "
                                + mqttBrokerEndPoint
                                + " for device-type - " + DeviceConstants.DEVICE_TYPE, e);

                        try {
                            Thread.sleep(timeoutInterval);
                        } catch (InterruptedException e1) {
                            Thread.currentThread().interrupt();
                            Log.e(TAG, "MQTT-Terminator: Thread Sleep Interrupt Exception " +
                                    "at device-type - " +
                                    DeviceConstants.DEVICE_TYPE, e1);
                        }
                    }
                }
            }
        };

        Thread terminatorThread = new Thread(stopConnection);
        terminatorThread.start();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void publishDeviceData() {
        // nothing to do
    }

    @Override
    public void publishDeviceData(MqttMessage publishData) throws TransportHandlerException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processIncomingMessage() {
        // nothing to do
    }

    @Override
    public void processIncomingMessage(MqttMessage message) throws TransportHandlerException {

    }

    public String getPublishTopic() {
        return publishTopic;
    }
}

