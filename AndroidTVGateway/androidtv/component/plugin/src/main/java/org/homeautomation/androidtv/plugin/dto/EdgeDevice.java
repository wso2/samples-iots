package org.homeautomation.androidtv.plugin.dto;

public class EdgeDevice {

    private String gatewayId;
    private String edgeDeviceSerial;
    private String edgeDeviceName;

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getEdgeDeviceSerial() {
        return edgeDeviceSerial;
    }

    public void setEdgeDeviceSerial(String edgeDeviceSerial) {
        this.edgeDeviceSerial = edgeDeviceSerial;
    }

    public String getEdgeDeviceName() {
        return edgeDeviceName;
    }

    public void setEdgeDeviceName(String edgeDeviceName) {
        this.edgeDeviceName = edgeDeviceName;
    }
}
