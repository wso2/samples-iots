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
import com.google.gson.GsonBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;
import org.wso2.carbon.apimgt.application.extension.APIManagementProviderService;
import org.wso2.carbon.apimgt.application.extension.dto.ApiApplicationKey;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.InvalidDeviceException;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.core.operation.mgt.CommandOperation;
import org.wso2.carbon.identity.jwt.client.extension.JWTClient;
import org.wso2.carbon.identity.jwt.client.extension.dto.AccessTokenInfo;
import org.wso2.carbon.identity.jwt.client.extension.exception.JWTClientException;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.iot.alertme.plugin.constants.DeviceTypeConstants;
import org.wso2.iot.alertme.plugin.exception.DeviceMgtPluginException;
import org.wso2.iot.alertme.plugin.impl.dao.DeviceTypeDAO;
import org.wso2.iot.alertme.plugin.impl.dto.DeviceMapping;
import org.wso2.iot.alertme.plugin.impl.dto.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class GetTokenThread implements Runnable {

    private static Log log = LogFactory.getLog(GetTokenThread.class);

    private static final DeviceTypeDAO deviceTypeDAO = new DeviceTypeDAO();

    private static ApiApplicationKey apiApplicationKey;
    private static final String KEY_TYPE = "PRODUCTION";

    public static void initSubscriber() {
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME, true);

        MqttConnectOptions options = new MqttConnectOptions();

        String[] tags = {"senseme"};
        String accessToken = generateAccessToken("alertme-event-receiver", tags);

        if(accessToken != null) {
            options.setUserName(accessToken);
            options.setPassword("".toCharArray());
            options.setCleanSession(false);
        }
        try {
            MqttClient client = new MqttClient("tcp://localhost:1886", "SenseMeSubscription", null);
            if(log.isDebugEnabled()){
                log.debug("MQTT subscriber was created with ClientID : " + "SenseMeSubscription");
            }
            client.setCallback(new MqttCallback() {

                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String msgString = message.toString();
                    JSONObject eventObject = new JSONObject(msgString);
                    Gson gson = new GsonBuilder().create();
                    Event event = gson.fromJson(eventObject.getJSONObject("event").toString(), Event.class);
                    String senseMeId = event.getMetaData().getDeviceId();
                    if (msgString.contains("ULTRASONIC")) {
                        sendSoundAlert(event, senseMeId);
                    } else if (msgString.contains("PIR")) {
                        sendLedAlert(event, senseMeId);
                    } else {
                        log.warn("Unknown message: " + message.toString());
                    }
                }

                public void connectionLost(Throwable cause) {
                    log.info("delivery lost. Re initializing subscriber...");
                    initSubscriber();
                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                    log.info("delivery complete");
                }

            });
            client.connect(options);
            client.subscribe(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME + "/senseme/+/+");
        } catch (MqttException ex) {
            log.error(ex);
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }

    }

    private static void sendLedAlert(Event event, String senseMeId)
            throws OperationManagementException, InvalidDeviceException, DeviceMgtPluginException {
        int pirReading = Math.round(event.getPayloadData().getPIR());
        List<DeviceMapping> deviceMappings = deviceTypeDAO.getDeviceTypeDAO().retrieveDeviceMappings(senseMeId);
        for (DeviceMapping deviceMapping : deviceMappings) {
            if (pirReading == 1) {
                String alertmsg = "LED:" + deviceMapping.getDuration() + ";";
                String publishTopic = deviceMapping.getTenantDomain() + "/" + DeviceTypeConstants.DEVICE_TYPE
                                      + "/" + deviceMapping.getAlertMeId() + "/alert";
                publishMessage(deviceMapping.getAlertMeId(), alertmsg, publishTopic, deviceMapping.getTenantDomain());
            }
        }
    }

    private static void sendSoundAlert(Event event, String senseMeId)
            throws OperationManagementException, InvalidDeviceException, DeviceMgtPluginException {
        int ultraSonicReading = Math.round(event.getPayloadData().getULTRASONIC());
        List<DeviceMapping> deviceMappings = deviceTypeDAO.getDeviceTypeDAO().retrieveDeviceMappings(senseMeId);
        for (DeviceMapping deviceMapping : deviceMappings) {
            if (ultraSonicReading < deviceMapping.getDistance()) {
                String alertmsg = "SOUND:" + deviceMapping.getDuration() + ";";
                String publishTopic = deviceMapping.getTenantDomain() + "/" + DeviceTypeConstants.DEVICE_TYPE
                                      + "/" + deviceMapping.getAlertMeId() + "/alert";
                publishMessage(deviceMapping.getAlertMeId(), alertmsg, publishTopic, deviceMapping.getTenantDomain());
            }
        }
    }

    private static void publishMessage(String alertMeId, String alertmsg, String publishTopic, String tenantDomain) {
        if (log.isDebugEnabled()){
            log.debug("Topic: " + publishTopic);
            log.debug("payload: " + alertmsg);
        }

        try {
            Operation commandOp = new CommandOperation();
            commandOp.setCode("alert");
            commandOp.setType(Operation.Type.COMMAND);
            commandOp.setEnabled(true);
            commandOp.setPayLoad(alertmsg);

            Properties props = new Properties();
            props.setProperty("mqtt.adapter.topic", publishTopic);
            commandOp.setProperties(props);

            List<DeviceIdentifier> deviceIdentifiers = new ArrayList<>();
            deviceIdentifiers.add(new DeviceIdentifier(alertMeId,
                                                       DeviceTypeConstants.DEVICE_TYPE));
            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(tenantDomain, true);
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setUsername("admin");
            APIUtil.getDeviceManagementService()
                    .addOperation(DeviceTypeConstants.DEVICE_TYPE, commandOp, deviceIdentifiers);
            PrivilegedCarbonContext.endTenantFlow();
        } catch (OperationManagementException | InvalidDeviceException e) {
            log.error("Publishing to topic " + publishTopic + " failed", e);
        }
    }

    public void run() {
        try {
            Thread.sleep(15000); //Wait for APIM start
        } catch (InterruptedException e) {
            log.error("Waiting failed", e);
        }
        initSubscriber();
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
                    log.error("Error while getting username", e);
                }
                APIManagementProviderService apiManagementProviderService = APIUtil.getAPIManagementProviderService();
                try {
                    apiApplicationKey = apiManagementProviderService.generateAndRetrieveApplicationKeys(
                            applicationName + "-" + UUID.randomUUID().toString(), tags, KEY_TYPE, applicationUsername, true, "360000");
                    amUp = true;
                } catch (Exception e) {
                    log.error("Error while retrieving application keys. ", e);
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e1) {
                        log.error("Token Retrieval operation halting failed", e1);
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
            log.error("Error while generating access tokens.", e);
        }
        return null;
    }
}
