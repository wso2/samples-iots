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

package org.wso2.iot.smartCity.api.dto;


import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.EnrolmentInfo;
import org.wso2.iot.smartCity.api.util.APIUtil;

import java.util.List;

public class PlaceDevices {

    private String deviceId;
    private String xCord;
    private String yCord;
    private String deviceType;
    private String placeId;
    private String status;

    public PlaceDevices(){}

    public PlaceDevices(Device device) {
        deviceId = device.getDeviceIdentifier();
        status = device.getEnrolmentInfo().getStatus().toString();

        List<Device.Property> propertyList = device.getProperties();
        for (Device.Property property : propertyList) {
            switch (property.getName()) {
                case "xCoordinate":  xCord = property.getValue();
                    break;
                case "yCoordinate":  yCord = property.getValue();
                    break;
                case "deviceType":  deviceType = property.getValue();
                    break;
                case "placeId":  placeId = property.getValue();
                    break;
                case "lastKnown":
                    if (device.getEnrolmentInfo().getStatus() == EnrolmentInfo.Status.ACTIVE) {
                        if (property.getValue() != null) {
                            long timestamp = Long.parseLong(property.getValue());
                            if ((System.currentTimeMillis() - timestamp) / 1000 > 3600) {
                                status = "FAULT";
                            }
                        }
                    }
                    break;

            }
        }
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getxCord() {
        return xCord;
    }

    public void setxCord(String xCord) {
        this.xCord = xCord;
    }

    public String getyCord() {
        return yCord;
    }

    public void setyCord(String yCord) {
        this.yCord = yCord;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
