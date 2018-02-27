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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.wso2.iot.agent.dto.AccessTokenInfo;
import org.wso2.iot.agent.dto.ApiApplicationKey;
import org.wso2.iot.agent.dto.Operation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import static org.apache.commons.codec.CharEncoding.UTF_8;

public class Application {

    private static final Log log = LogFactory.getLog(Application.class);
    private static final String CONFIG_FILE = "config.json";
    private static final String SIDDHI_FILE = "plan.siddhiql";

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

    @SuppressWarnings("unchecked")
    private Application() {
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
                log.error("Error occurred when writing device details to config json file.", e);
            }
        });
        try {
            mqttHandler = new MQTTHandler(mqttEndpoint, tenantDomain, deviceType, deviceId, tokenHandler,
                                          operation -> {
                switch (operation.getCode()) {
                    case "update-config":
                        updateSiddhiQuery(operation);
                        break;
                    case "upgrade-firmware":
                        //TODO: Implement firmware upgrade flow
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
        }
        String executionPlan = "" +
                               "define stream agentEventStream (symbol string, price long, volume long);" +
                               "" +
                               "@info(name = 'query1') " +
                               "from agentEventStream " +
                               "select symbol, price, volume as totalCount " +
                               "insert into Output;";

        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(SIDDHI_FILE), Charset.forName(UTF_8)))) {
            StringBuilder fileContents = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                fileContents.append(line);
                line = bufferedReader.readLine();
            }
            executionPlan = fileContents.toString();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        siddhiEngine = new SiddhiEngine(mqttHandler, executionPlan);
    }

    private void updateSiddhiQuery(Operation operation) {
        String siddhiQuery = operation.getPayload();
        siddhiEngine.updateExecutionPlan(siddhiQuery);
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(SIDDHI_FILE), UTF_8))) {
            writer.write(siddhiQuery);
            writer.close();
        } catch (IOException e) {
            log.error("Error occurred when writing device details to config json file.", e);
        }
        operation.setStatus(Operation.Status.COMPLETED);
    }

    public static void main(String[] args) {
        init();
    }

    public static void init() {
        application = new Application();
    }

    public static Application getInstance() {
        return application;
    }

    public MQTTHandler getMqttHandler() {
        return mqttHandler;
    }

    public TokenHandler getTokenHandler() {
        return tokenHandler;
    }

    public SiddhiEngine getSiddhiEngine() {
        return siddhiEngine;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getTenantDomain() {
        return tenantDomain;
    }
}