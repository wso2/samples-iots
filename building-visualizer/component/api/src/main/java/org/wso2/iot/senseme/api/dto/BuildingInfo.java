package org.wso2.iot.senseme.api.dto;

public class BuildingInfo {

    private int buildingId;
    private String buildingName;
    private String owner;
    private String longitude;
    private String latitude;
    private int numFloors;

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public int getNumFloors() {
        return numFloors;
    }

    public void setNumFloors(int numFloors) {
        this.numFloors = numFloors;
    }

    public String getBuildingName() {

        return buildingName;
    }

    public int getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }
}
