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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.iot.agent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.json.JSONObject;
import org.wso2.iot.agent.dto.AccessTokenInfo;
import org.wso2.iot.agent.dto.Operation;

import java.nio.charset.Charset;

import static org.apache.commons.codec.CharEncoding.UTF_8;

public class MQTTHandler {

    private static final Log log = LogFactory.getLog(MQTTHandler.class);

    private static final int MQTT_QOS = 1;

    private MqttClient mqttClient = null;
    private OperationListener operationListener;
    private boolean isClientConnected = false;
    private String tenant;
    private String type;
    private String deviceId;

    MQTTHandler(String brokerUrl, String tenant, String type, String deviceId, TokenHandler tokenHandler, OperationListener operationListener)
            throws TransportHandlerException {
        this.tenant = tenant;
        this.type = type;
        this.deviceId = deviceId;
        this.operationListener = operationListener;

        try {
            mqttClient = new MqttClient(brokerUrl, deviceId, new MqttDefaultFilePersistence());
        } catch (MqttException e) {
            String message = "Error occurred when creating MQTT Client.";
            log.error(message, e);
            throw new TransportHandlerException(message, e);
        }

        connectWithBroker(tokenHandler);

        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) { //Called when the client lost the connection to the broker
                log.warn("Connection lost. Reattempting to connect.");
                isClientConnected = false;
                connectWithBroker(tokenHandler);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                log.info("Message arrived from : " + topic);

                String payload = new String(message.getPayload(), Charset.forName(UTF_8));
                log.info("Message: " + payload);
                handleIncomingMessage(topic, payload);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {//Called when a outgoing publish is complete
                log.info("Delivery completed. " + token);
            }
        });
    }

    private void connectWithBroker(TokenHandler tokenHandler) {
        Runnable connectionThread = () -> {
            MqttConnectOptions connOpts = new MqttConnectOptions();
            AccessTokenInfo accessTokenInfo;
            try {
                accessTokenInfo = tokenHandler.renewTokens();
            } catch (TokenRenewalException e) {
                log.error("Error occurred while renewing tokens. Using tokens from config.", e);
                accessTokenInfo = tokenHandler.getAccessTokenInfo();
            }
            connOpts.setUserName(accessTokenInfo.getAccessToken());
            connOpts.setPassword("".toCharArray());
            connOpts.setKeepAliveInterval(120);
            connOpts.setCleanSession(false);
            while (!isClientConnected) {
                try {
                    mqttClient.connect(connOpts);
                } catch (MqttException e) {
                    log.error("Error occurred when connecting with MQTT Server.", e);
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignore) {
                    Thread.currentThread().interrupt();
                    break;
                }
                isClientConnected = mqttClient.isConnected();
            }
            log.info("Agent connected with the broker.");
            String operationTopic = tenant + "/" + type + "/" + deviceId + "/operation/#";
            try {
                mqttClient.subscribe(operationTopic, MQTT_QOS);
            } catch (Exception e) {
                log.error("Error occurred when subscribing with Operation topic '" + operationTopic + "'.", e);
            }
            log.info("MQTT client connected and subscribed for operation topic: " + operationTopic);
        };
        new Thread(connectionThread).start();
    }

    private void handleIncomingMessage(String topic, String message){
        Runnable messageHandler = () -> {
            String operationSpecifier = topic.replace(tenant + "/" + type + "/" + deviceId + "/operation/", "");
            String[] operationParams = operationSpecifier.split("/");

            Operation operation = new Operation();
            String operationResponse = "";
            try {
                operation.setId(Integer.parseInt(operationParams[2]));
                operation.setType(Operation.Type.valueOf(operationParams[0].toUpperCase()));
                operation.setCode(operationParams[1]);
                operation.setPayload(message);
                operationListener.onOperationReceived(operation);
            } catch (Exception e) {
                operation.setStatus(Operation.Status.ERROR);
                operation.setOperationResponse("Exception: " + e.getMessage());
                log.error(operationResponse, e);
            } finally {
                JSONObject responseObj = new JSONObject();
                responseObj.put("id", operation.getId());
                responseObj.put("status", operation.getStatus());
                responseObj.put("operationResponse", operation.getOperationResponse());
                publishMessage(tenant + "/" + type + "/" + deviceId + "/update/operation", responseObj.toString());
            }
        };
        new Thread(messageHandler).start();
    }

    public void publishMessage(String topic, String payload) {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(payload.getBytes(Charset.forName(UTF_8)));
        mqttMessage.setQos(MQTT_QOS);
        mqttMessage.setRetained(false);
        try {
            mqttClient.publish(topic, mqttMessage);
        } catch (MqttException e) {
            log.error("Error occurred when publishing message.", e);
        }
        log.info("Published to topic: " + topic + "\nPayload: " + payload);
    }
}
