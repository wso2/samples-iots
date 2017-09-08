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

package org.homeautomation.androidtv.api.constants;

/**
 * Holds constants for AndroidTV API.
 */
public class AndroidTVConstants {

    public static final String DEVICE_TYPE = "androidtv";
    public static final String DEVICE_PLUGIN_DEVICE_NAME = "DEVICE_NAME";
    public static final String DEVICE_PLUGIN_DEVICE_SERIAL = "SERIAL";
    public static final String DEVICE_PLUGIN_DEVICE_ID = "androidtv_DEVICE_ID";

    public static final String DATA_SOURCE_NAME = "jdbc/androidtvDM_DB";
    public static final String DEVICE_TYPE_PROVIDER_DOMAIN = "carbon.super";

    //mqtt transport related constants
    public static final String MQTT_PORT = "\\{mqtt.broker.port\\}";
    public static final String MQTT_BROKER_HOST = "\\{mqtt.broker.host\\}";
    public static final String CARBON_CONFIG_PORT_OFFSET = "Ports.Offset";
    public static final String DEFAULT_CARBON_LOCAL_IP_PROPERTY = "carbon.local.ip";
    public static final int CARBON_DEFAULT_PORT_OFFSET = 0;
    public static final int DEFAULT_MQTT_PORT = 1883;

    public static final String MQTT_ADAPTER_TOPIC_PROPERTY_NAME = "mqtt.adapter.topic";

    public static final String LOCALHOST = "localhost";
    public static final String CONFIG_TYPE = "general";
    public static final String DEFAULT_ENDPOINT = "tcp://${mqtt.broker.host}:${mqtt.broker.port}";

    public static final String SCOPE = "scope";

    public static final String PERM_ENROLL_ANDROID_TV = "/permission/admin/device-mgt/devices/enroll/android-tv";
    public static final String PERM_OWNING_DEVICE_VIEW = "/permission/admin/device-mgt/devices/owning-device/view";

    public static final String ROLE_NAME = "internal/devicemgt-user";

    //sensor events summarized table name for temperature
    public static final String SENSOR_TYPE1 = "TEMP";
    public static final String SENSOR_TYPE2 = "HUMIDITY";
    public static final String SENSOR_TYPE3 = "DOOR";
    public static final String SENSOR_TYPE4 = "WINDOW";
    public static final String SENSOR_TYPE5 = "AC";
    public static final String SENSOR_TYPE1_EVENT_TABLE = "ORG_WSO2_IOT_DEVICES_TEMP";
    public static final String SENSOR_TYPE2_EVENT_TABLE = "ORG_WSO2_IOT_DEVICES_HUMIDITY";
    public static final String SENSOR_TYPE3_EVENT_TABLE = "ORG_WSO2_IOT_DEVICES_DOOR";
    public static final String SENSOR_TYPE4_EVENT_TABLE = "ORG_WSO2_IOT_DEVICES_WINDOW";
    public static final String SENSOR_TYPE5_EVENT_TABLE = "ORG_WSO2_IOT_DEVICES_AC";

}
