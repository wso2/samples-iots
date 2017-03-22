package org.wso2.iot.senseme.api.dto;


import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.iot.senseme.api.util.APIUtil;

import java.util.List;

public class SenseMe {

    private String deviceId;
    private String xCord;
    private String yCord;
    private String floorNumber;
    private String buildingId;
    private String status;

    public SenseMe(){}

    public SenseMe(Device device) {
        deviceId = device.getDeviceIdentifier();
        status = device.getEnrolmentInfo().getStatus().toString();
        List<Device.Property> propertyList = device.getProperties();
        for (Device.Property property : propertyList) {
            switch (property.getName()) {
                case "xCoordinate":  xCord = property.getValue();
                    break;
                case "yCoordinate":  yCord = property.getValue();
                    break;
                case "floorId":  floorNumber = property.getValue();
                    break;
                case "buildingId":  buildingId = property.getValue();
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

    public String getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(String floorNumber) {
        this.floorNumber = floorNumber;
    }

    public String getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
