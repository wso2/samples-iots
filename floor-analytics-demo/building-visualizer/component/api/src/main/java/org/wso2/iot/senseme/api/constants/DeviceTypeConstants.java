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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.iot.senseme.api.constants;

import org.wso2.carbon.utils.CarbonUtils;
import java.io.File;

/**
 * Device type specific constants which includes all transport protocols configurations,
 * stream definition and device specific dome constants
 */
public class DeviceTypeConstants {
    public final static String DEVICE_TYPE = "senseme";
    public final static String BUILDING_GROUP_NAME = "BUILDING-%s";
    public final static String FLOOR_GROUP_NAME = "BUILDING-%s-FLOOR-%s";
    public final static String BUILDING_ID = "buildingId";
    public final static String FLOOR_ID = "floorId";
    public final static String X_COORDINATE = "xCoordinate";
    public final static String Y_COORDINATE = "yCoordinate";

    public static final String MOTION_EVENT_TABLE = "DEVICE_MOTION_SUMMARY";
    public static final String LIGHT_EVENT_TABLE = "DEVICE_LIGHT_SUMMARY";
    public static final String TEMPERATURE_EVENT_TABLE = "DEVICE_TEMPERATURE_SUMMARY";
    public static final String HUMIDITY_EVENT_TABLE = "DEVICE_HUMIDITY_SUMMARY";
    public static final String ALERT_NOTIFICATIONS = "ORG_WSO2_FLOOR_ALERTNOTIFICATIONS";

    public static final String DATA_SOURCE_NAME = "jdbc/WSO2_FLOOR_ANALYTICS";
    public final static String SENSOR_TYPE_MOTION = "motion";
    public final static String SENSOR_TYPE_LIGHT = "light";
    public final static String SENSOR_TYPE_TEMPERATURE = "temperature";
    public final static String SENSOR_TYPE_HUMIDITY = "humidity";

    public static final String BUILDING_ROLE = "building-%s";
    public static final String FLOOR_ROLE = "building-%s-floor-%s";
    public static final String REALTIME_ANALYTICS_PERMISSION = "/permission/admin/device-mgt/realtime_analytics";

    public static final String FLOOR_DEVICE_TABLE = "0RG_WSO2_FLOOR_DEVICE_SENSORSTREAM";
    public static final String FLOOR_SUMMARIZED_DEVICE_TABLE = "0RG_WSO2_FLOOR_SUMMARIZED_DEVICE_FLOOR_SENSORSTREAM";
    public static final String FLOOR_SUMMARIZED1HR_DEVICE_TABLE = "0RG_WSO2_FLOOR_SUMMARIZED1HR_DEVICE_FLOOR_SENSORSTREAM";
    public static final String FLOOR_SUMMARIZED3HR_DEVICE_TABLE = "0RG_WSO2_FLOOR_SUMMARIZED3HR_DEVICE_FLOOR_SENSORSTREAM";
    public static final String FLOOR_SUMMARIZED6HR_DEVICE_TABLE = "0RG_WSO2_FLOOR_SUMMARIZED6HR_DEVICE_FLOOR_SENSORSTREAM";


}

