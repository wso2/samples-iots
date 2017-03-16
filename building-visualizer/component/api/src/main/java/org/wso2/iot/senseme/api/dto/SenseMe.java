package org.wso2.iot.senseme.api.dto;

public class SenseMe {

    private String deviceId;
    private String deviceName;
    private String xCord;
    private String yCord;
    private String floorNumber;
    private String buildingId;
    private String buildingName;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
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

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }
}
