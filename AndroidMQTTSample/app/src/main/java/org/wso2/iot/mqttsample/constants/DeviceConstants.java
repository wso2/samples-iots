/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */
package org.wso2.iot.mqttsample.constants;

/**
 * This hold constants related to android_sense.
 */
public class DeviceConstants {
    public final static int MQTT_PORT = 1886;
    public final static String TENANT_ID = "carbon.super";
    public final static String DEVICE_TYPE = "sample";
    public final static String DEVICE_API_CONTEXT = "/api/device-mgt/v1.0/device/agent";
    public final static String API_APPLICATION_REGISTRATION_CONTEXT = "/api-application-registration";

    public final static String PUBLISH_TOPIC_SUFFIX = "/events";
    public final static String SUBSCRIBE_TOPIC_SUFFIX = "/operation/#";

    public final class Request {
        public final static String REQUEST_SUCCESSFUL = "200";
    }
}
