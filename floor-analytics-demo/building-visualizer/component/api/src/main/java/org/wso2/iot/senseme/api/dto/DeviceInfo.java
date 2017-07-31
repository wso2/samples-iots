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
package org.wso2.iot.senseme.api.dto;

public class DeviceInfo {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getActiveDevices() {
        return activeDevices;
    }

    public void setActiveDevices(int activeDevices) {
        this.activeDevices = activeDevices;
    }

    public int getFaultDevices() {
        return faultDevices;
    }

    public void setFaultDevices(int faultDevices) {
        this.faultDevices = faultDevices;
    }

    public int getInactiveDevices() {
        return inactiveDevices;
    }

    public void setInactiveDevices(int inactiveDevices) {
        this.inactiveDevices = inactiveDevices;
    }

    public int getTotalDevices() {
        return totalDevices;
    }

    public void setTotalDevices(int totalDevices) {
        this.totalDevices = totalDevices;
    }

    private int activeDevices = 0;
    private int faultDevices = 0;
    private int inactiveDevices = 0;
    private int totalDevices = 0;

    public DeviceInfo(String id) {
        this.id = id;
    }

    public void increaseActive() {
        activeDevices++;
        totalDevices++;
    }

    public void increaseFault() {
        faultDevices++;
        totalDevices++;
    }

    public void increaseInactive() {
        inactiveDevices++;
        totalDevices++;
    }

    public void decreaseActive() {
        activeDevices--;
        totalDevices--;
    }

    public void decreaseFault() {
        faultDevices--;
        totalDevices--;
    }

    public void decreaseInactive() {
        inactiveDevices--;
        totalDevices--;
    }
}
