package org.wso2.iot.alertme.plugin.impl.dto;

/**
 * Created by ruwan on 2/16/17.
 */
public class MetaData {
    String owner;
    String deviceType;
    String deviceId;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
