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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.iot.alertme.plugin.impl.util;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.wso2.carbon.apimgt.application.extension.APIManagementProviderService;
import org.wso2.carbon.apimgt.application.extension.dto.ApiApplicationKey;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.identity.jwt.client.extension.JWTClient;
import org.wso2.carbon.identity.jwt.client.extension.dto.AccessTokenInfo;
import org.wso2.carbon.identity.jwt.client.extension.exception.JWTClientException;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.iot.alertme.plugin.impl.dao.DeviceTypeDAO;
import org.wso2.iot.alertme.plugin.impl.dto.Event;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class GetTokenThread implements Runnable {

    private static Log log = LogFactory.getLog(GetTokenThread.class);

    private static final DeviceTypeDAO deviceTypeDAO = new DeviceTypeDAO();

    private static ApiApplicationKey apiApplicationKey;
    private static final String KEY_TYPE = "PRODUCTION";

    public void run() {
        initSubscriber();
    }

    public static void initSubscriber() {
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME, true);

        final MqttClient client;
        String accessToken = null;
        MqttConnectOptions options = new MqttConnectOptions();
        //TODO:: Use constant strings
        options.setWill("senseme/disconnection", "Connection-Lost".getBytes(StandardCharsets.UTF_8), 2, true);

        String[] tags = {"sense_me"};
        accessToken = generateAccessToken("alertme-event-receiver-" + UUID.randomUUID().toString(), tags);

        if(accessToken != null) {
            options.setUserName(accessToken);
            options.setPassword("".toCharArray());
            options.setCleanSession(false);
        }
        try {
            client = new MqttClient("tcp://localhost:1886", "SenseMeSubscription", null);
            //TODO:: Need to check for debug
            log.info("MQTT subscriber was created with ClientID : " + "SenseMeSubscription");
            client.setCallback(new MqttCallback() {

                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Gson gson = new Gson();
                    Event event = gson.fromJson(message.toString(), Event.class);
                    String deviceID = event.getMetaData().getDeviceId();
                    int ultraSonicReading = event.getPayloadData().getULTRASONIC();
                    Device device = new Device();
                    device.setDeviceIdentifier(deviceID);
                    String payload = "{\n" + "\t\"activate\": true\n" + "}";

                    List<Device> mappedDevice = deviceTypeDAO.getDeviceTypeDAO()
                            .retrieveDeviceMappings
                            (device);
                    for(Device deviceEntry : mappedDevice){
                        int distance = Integer.parseInt(deviceEntry.getProperties().get(0)
                                .getValue());
                        if(ultraSonicReading < distance){
                            client.publish("carbon.super/alertme/"+deviceEntry.getDeviceIdentifier()+"/notify",
                                    payload.getBytes
                                    (StandardCharsets.UTF_8), 0,
                                    false);
                        }
                    }
                }

                public void connectionLost(Throwable cause) {
                    log.info("delivery lost");
                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                    log.info("delivery complete");
                }

            });
            client.connect(options);
            client.subscribe("carbon.super/senseme/+/ULTRASONIC");
        } catch (MqttException ex) {
            //TODO:: Remove unnecessary formatting and print exception
            String errorMsg = "MQTT Client Error\n" + "\tReason:  " + ex.getReasonCode() +
                    "\n\tMessage: " + ex.getMessage() + "\n\tLocalMsg: " +
                    ex.getLocalizedMessage() + "\n\tCause: " + ex.getCause() +
                    "\n\tException: " + ex;
            log.error(errorMsg);
            //TODO:: Throw the error out
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }

    }

    private static String generateAccessToken(String applicationName, String[] tags) {
        if (apiApplicationKey == null) {
            String applicationUsername = null;
            boolean amUp = false;

            while (!amUp) {
                try {
                    applicationUsername = PrivilegedCarbonContext.getThreadLocalCarbonContext().getUserRealm().getRealmConfiguration()
                            .getAdminUserName();
                } catch (UserStoreException e) {
                    log.error("Error while getting username");
                }
                APIManagementProviderService apiManagementProviderService = APIUtil.getAPIManagementProviderService();
                try {
                    apiApplicationKey = apiManagementProviderService.generateAndRetrieveApplicationKeys(
                            applicationName, tags, KEY_TYPE, applicationUsername, true, "360000");
                    amUp = true;
                } catch (Exception e) {
                    log.error("Error while retrieving application keys.AM Is not up yet");
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e1) {
                        log.error("Token Retrieval operation halting failed");
                    }
                }
            }
        }
        try {
            JWTClient jwtClient = APIUtil.getJWTClientManagerService().getJWTClient();
            String scopes = " device_" + "ABC";
            AccessTokenInfo accessTokenInfo = jwtClient.getAccessToken(apiApplicationKey.getConsumerKey(), apiApplicationKey.getConsumerSecret(), "admin", scopes);
            return accessTokenInfo.getAccessToken();
        } catch (JWTClientException e) {
            e.printStackTrace();
        }
        return null;
    }
}
