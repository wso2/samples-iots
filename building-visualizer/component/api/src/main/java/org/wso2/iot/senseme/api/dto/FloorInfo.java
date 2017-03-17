package org.wso2.iot.senseme.api.dto;

/**
 * Created by lasantha on 3/17/17.
 */
public class FloorInfo {

    private int buildingId;
    private int floorNum;
    private String xCords;
    private String yCords;

    public int getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }

    public int getFloorNum() {
        return floorNum;
    }

    public void setFloorNum(int floorNum) {
        this.floorNum = floorNum;
    }

    public String getxCords() {
        return xCords;
    }

    public void setxCords(String xCords) {
        this.xCords = xCords;
    }

    public String getyCords() {
        return yCords;
    }

    public void setyCords(String yCords) {
        this.yCords = yCords;
    }
}
