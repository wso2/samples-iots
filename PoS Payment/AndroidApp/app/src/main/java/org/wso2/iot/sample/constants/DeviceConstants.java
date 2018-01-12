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

package org.wso2.iot.sample.constants;

/**
 * This hold constants related to android_sense.
 */
public class DeviceConstants {
    public static final int MQTT_PORT = 1886;
    public static final String TENANT_ID = "carbon.super";
    public static final String DEVICE_TYPE = "mobile_client";
    public static final String DEVICE_API_CONTEXT = "/api/device-mgt/v1.0/device/agent";
    public static final String API_APPLICATION_REGISTRATION_CONTEXT = "/api-application-registration";

    public static final String PUBLISH_TOPIC_SUFFIX = "/events";
    public static final String SUBSCRIBE_TOPIC_SUFFIX = "/operation/#";

    public static final String CHECKOUT_OPERATION = "checkout_operation";
    public static final String SELECTED_ITEMS = "selected_items";
    public static final String SCANNED_QR = "scanned_qr";
    public static final String POS_ID = "pos_id";
    public static final String OPERATION_BROADCAST_ACTION = "org.wso2.iot.operation.received.ACTION";

    public final class Request {
        public static final String REQUEST_SUCCESSFUL = "200";
    }
}
