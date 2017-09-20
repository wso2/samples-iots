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

package org.homeautomation.garbagebin.api;

/**
 * Device type specific constants.
 */
public class DeviceTypeConstants {

    public final static String DEVICE_TYPE = "garbagebin";

    //sensor events summarized table name
    public final static String STREAM_TEMPERATURE = "temperature";
    public final static String STREAM_HUMIDITY = "humidity";
    public final static String STREAM_GARBAGELEVEL = "garbagelevel";
    public final static String TEMPERATURE_EVENT_TABLE = "DEVICE_TEMPERATURE_SUMMARY";
    public final static String HUMIDITY_EVENT_TABLE = "DEVICE_HUMIDITY_SUMMARY";
    public final static String GARBAGELEVEL_EVENT_TABLE = "DEVICE_GARBAGELEVEL_SUMMARY";
}
