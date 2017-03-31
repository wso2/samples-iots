package org.wso2.iot.senseme.api.dto;

import java.util.HashMap;

public class QueryObject {

    private String entity; //Building or floor
    private HashMap<String, String> sensorStatus; // Sensor and respective status.
    private long from; // Typically 1 hour
    private long to;

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public HashMap<String, String> getSensorStatus() {
        return sensorStatus;
    }

    public void setSensorStatus(HashMap<String, String> sensorStatus) {
        this.sensorStatus = sensorStatus;
    }

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getTo() {
        return to;
    }

    public void setTo(long to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "GIVE ME THE " + entity + "/s WHERE " + sensorStatus.toString() + " FROM " + from + " TO " + to;
    }
}
