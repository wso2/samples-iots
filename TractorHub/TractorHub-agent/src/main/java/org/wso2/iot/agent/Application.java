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
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.iot.agent.analytics.SiddhiEngine;
import org.wso2.iot.agent.operation.dto.Operation;
import org.wso2.iot.agent.simulation.EventSimulator;
import org.wso2.iot.agent.transport.MQTTHandler;
import org.wso2.iot.agent.transport.OauthHttpClient;
import org.wso2.iot.agent.transport.TokenHandler;
import org.wso2.iot.agent.transport.TransportHandlerException;
import org.wso2.iot.agent.transport.dto.AccessTokenInfo;
import org.wso2.iot.agent.transport.dto.ApiApplicationKey;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.callback.QueryCallback;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static org.apache.commons.codec.CharEncoding.UTF_8;

public class Application {

    public static final String AGENT_VERSION = "v1.1.0";

    private static final double LATITUDE = 6.927079;
    private static final double LONGITUDE = 79.861244;

    private static final Log log = LogFactory.getLog(Application.class);
    private static final String CONFIG_FILE = "config.json";
    private static final String SIDDHI_FILE = "plan.siddhiql";
    private static final String UPGRADE_ZIP = "upgrade.zip";
    private static final String UPGRADE_INFO_FILE = "upgrade.info";

    private static Application application;
    private MQTTHandler mqttHandler;
    private TokenHandler tokenHandler;
    private SiddhiEngine siddhiEngine;
    private JSONObject configData;
    private String mqttEndpoint;
    private String httpEndpoint;
    private String deviceId;
    private String deviceType;
    private String tenantDomain;

    private Application() {
        initConfigs();
        initTransport();
        initSiddhiEngine();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                cleanUpFirmwareUpgrades();
                updatedProperties();
            }
        }, 5000);
    }

    public static void main(String[] args) {
        application = new Application();
        EventSimulator simulator = new EventSimulator(application.siddhiEngine);
        simulator.start();
    }

    private void initSiddhiEngine() {
        String executionPlan = "define stream agentEventStream (EngineTemp double, humidity double, " +
                               "tractorSpeed double, loadWeight double, soilMoisture double, illumination double, " +
                               "fuelUsage double, engineidle bool, raining bool, temperature double);" +
                               "@info(name = 'publish_query') " +
                               "from agentEventStream " +
                               "select EngineTemp, humidity, tractorSpeed, loadWeight, soilMoisture, illumination, " +
                               "fuelUsage, engineidle, raining, temperature " +
                               "insert into publishEvents; " +
                               "from every e1=agentEventStream, e2=agentEventStream[e2.EngineTemp > e1.EngineTemp + 5 " +
                               "and e1.EngineTemp > 100] " +
                               "select str:concat(time:currentTimestamp(), ' Engine temperature increasing rapidly. " +
                               "Current: ', e2.EngineTemp) as alert " +
                               "insert into alertEvents;" +
                               "@info(name = 'alert_query') " +
                               "from alertEvents " +
                               "select alert " +
                               "insert into alertStream;";

        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(SIDDHI_FILE), Charset.forName(UTF_8)))) {
            StringBuilder fileContents = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                fileContents.append(line);
                line = bufferedReader.readLine();
            }
            executionPlan = fileContents.toString();
        } catch (FileNotFoundException e) {
            log.warn("Siddhi execution plan file not found. Using default pass though plan.");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        siddhiEngine = new SiddhiEngine(executionPlan);
        siddhiEngine.addQueryCallback("publish_query", new QueryCallback() {
            @Override
            public void receive(long timestamp, Event[] inEvents, Event[] removeEvents) {
                if (inEvents == null) {
                    return;
                }
                for (Event event : inEvents) {
                    application.mqttHandler.publishMessage(tenantDomain + "/" + deviceType + "/" + deviceId + "/events",
                                                           "{\"EngineTemp\":" + event.getData(0) +
                                                           ",\"humidity\":" + event.getData(1) +
                                                           ",\"tractorSpeed\":" + event.getData(2) +
                                                           ",\"loadWeight\":" + event.getData(3) +
                                                           ",\"soilMoisture\":" + event.getData(4) +
                                                           ",\"illumination\":" + event.getData(5) +
                                                           ",\"fuelUsage\":" + event.getData(6) +
                                                           ",\"engineidle\":" + event.getData(7) +
                                                           ",\"raining\":" + event.getData(8) +
                                                           ",\"temperature\":" + event.getData(9) +
                                                           ",\"timestamp\":" + Calendar.getInstance().getTimeInMillis() + "}");
                }
            }
        });
    }

    private void initTransport() {
        try {
            mqttHandler = new MQTTHandler(mqttEndpoint, tenantDomain, deviceType, deviceId, tokenHandler,
                                          operation -> {
                                              switch (operation.getCode()) {
                                                  case "EXEC_PLAN":
                                                      updateSiddhiQuery(operation);
                                                      break;
                                                  case "FIRMWARE_UPGRADE":
                                                      upgradeFirmware(operation);
                                                      break;
                                                  default:
                                                      String message = "Unknown operation code: " + operation.getCode();
                                                      log.warn(message);
                                                      operation.setStatus(Operation.Status.ERROR);
                                                      operation.setOperationResponse(message);
                                              }
                                          });
        } catch (TransportHandlerException e) {
            log.error("Error occurred when creating MQTT Client.", e);
            System.exit(1);
        }
    }

    private void cleanUpFirmwareUpgrades() {
        File upgradeInfo = new File(UPGRADE_INFO_FILE);
        if (upgradeInfo.exists()) {
            try {
                String operationId = new String(Files.readAllBytes(upgradeInfo.toPath()));
                org.json.JSONObject responseObj = new org.json.JSONObject();
                responseObj.put("id", Integer.parseInt(operationId));
                responseObj.put("status", Operation.Status.COMPLETED);
                responseObj.put("operationResponse", "Upgraded to " + AGENT_VERSION);
                mqttHandler.publishMessage(tenantDomain + "/" + deviceType + "/" + deviceId +
                                           "/update/operation", responseObj.toString());
            } catch (Exception e) {
                log.error("Unable to process firmware upgrade info file.", e);
            } finally {
                log.info("Upgrade info removed: " + upgradeInfo.delete());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void initConfigs() {
        ApiApplicationKey apiApplicationKey = new ApiApplicationKey();
        AccessTokenInfo accessTokenInfo = new AccessTokenInfo();
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(CONFIG_FILE), Charset.forName(UTF_8)))) {
            JSONParser parser = new JSONParser();
            configData = (JSONObject) parser.parse(bufferedReader);
            apiApplicationKey.setClientId(configData.get("clientId").toString());
            apiApplicationKey.setClientSecret(configData.get("clientSecret").toString());
            accessTokenInfo.setAccessToken(configData.get("accessToken").toString());
            accessTokenInfo.setRefreshToken(configData.get("refreshToken").toString());
            mqttEndpoint = configData.get("mqttGateway").toString();
            httpEndpoint = configData.get("httpGateway").toString();
            deviceId = configData.get("deviceId").toString();
            deviceType = configData.get("type").toString();
            tenantDomain = "carbon.super";
        } catch (IOException | ParseException e) {
            log.error("Error occurred when reading device details from json file.", e);
        }

        tokenHandler = new TokenHandler(httpEndpoint + "/token", accessTokenInfo, apiApplicationKey,
                                        updatedTokenInfo -> {
                                            configData.put("accessToken", updatedTokenInfo.getAccessToken());
                                            configData.put("refreshToken", updatedTokenInfo.getRefreshToken());
                                            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                                                    new FileOutputStream(CONFIG_FILE), UTF_8))) {
                                                writer.write(configData.toJSONString());
                                                writer.close();
                                            } catch (IOException e) {
                                                log.error("Error occurred when writing device details to " +
                                                          "config json file.", e);
                                            }
                                        });
    }

    private void updatedProperties() {
        OauthHttpClient client = new OauthHttpClient(tokenHandler);
        HttpPut httpPut = new HttpPut(this.httpEndpoint + "/api/device-mgt/v1.0/device/agent/properties/" +
                                      this.deviceType + "/" + this.deviceId);
        httpPut.setHeader("Content-Type", "application/json");

        JSONArray properties = new JSONArray();

        org.json.JSONObject firmwareVersion = new org.json.JSONObject();
        firmwareVersion.put("name", "FirmwareVersion");
        firmwareVersion.put("value", AGENT_VERSION);
        properties.put(firmwareVersion);

        org.json.JSONObject latitude = new org.json.JSONObject();
        latitude.put("name", "FarmLatitude");
        latitude.put("value", String.valueOf(LATITUDE));
        properties.put(latitude);

        org.json.JSONObject longitude = new org.json.JSONObject();
        longitude.put("name", "FarmLongitude");
        longitude.put("value", String.valueOf(LONGITUDE));
        properties.put(longitude);

        StringEntity tokenEPPayload = new StringEntity(properties.toString(), UTF_8);
        httpPut.setEntity(tokenEPPayload);

        try {
            HttpResponse response = client.execute(httpPut);
            log.info("Device properties update response: " + response.getStatusLine());
        } catch (IOException e) {
            log.error("Cannot update device properties due to " + e.getMessage(), e);
        }
    }

    private void updateSiddhiQuery(Operation operation) {
        String siddhiQuery = operation.getPayload();
        siddhiEngine.updateExecutionPlan(siddhiQuery);
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(SIDDHI_FILE), UTF_8))) {
            writer.write(siddhiQuery);
            writer.close();
            operation.setStatus(Operation.Status.COMPLETED);
        } catch (IOException e) {
            String message = "Error occurred when writing device details to config json file.";
            log.error(message, e);
            operation.setOperationResponse(message);
            operation.setStatus(Operation.Status.ERROR);
        }
    }

    private void upgradeFirmware(Operation operation) {
        try {
            URL upgradeFile = new URL(operation.getPayload());
            try (InputStream in = upgradeFile.openStream()) {
                Files.copy(in, new File(UPGRADE_ZIP).toPath(), StandardCopyOption.REPLACE_EXISTING);
                operation.setStatus(Operation.Status.IN_PROGRESS);
            }
            Files.write(new File(UPGRADE_INFO_FILE).toPath(), String.valueOf(operation.getId()).getBytes(),
                        StandardOpenOption.CREATE);
            Runtime.getRuntime().exec("sh start.sh > upgrade.log");
            System.exit(0);
        } catch (Exception e) {
            log.error("Error occurred when upgrading firmware.", e);
            operation.setOperationResponse(e.getMessage());
            operation.setStatus(Operation.Status.ERROR);
        }
    }

}
