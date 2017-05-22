/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.homeautomation.androidtv.plugin.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.homeautomation.androidtv.plugin.dto.EdgeDevice;
import org.homeautomation.androidtv.plugin.impl.dao.DeviceTypeDAO;
import org.homeautomation.androidtv.plugin.exception.DeviceMgtPluginException;
import org.homeautomation.androidtv.plugin.impl.feature.DeviceTypeFeatureManager;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.DeviceManager;
import org.wso2.carbon.device.mgt.common.EnrolmentInfo;
import org.wso2.carbon.device.mgt.common.FeatureManager;
import org.wso2.carbon.device.mgt.common.configuration.mgt.PlatformConfiguration;
import org.wso2.carbon.device.mgt.common.license.mgt.License;
import org.wso2.carbon.device.mgt.common.license.mgt.LicenseManagementException;

import java.util.ArrayList;
import java.util.List;


/**
 * This represents the androidtv implementation of DeviceManagerService.
 */
public class DeviceTypeManager implements DeviceManager {

    private static final Log log = LogFactory.getLog(DeviceTypeManager.class);
    private static final DeviceTypeDAO deviceTypeDAO = new DeviceTypeDAO();
    private FeatureManager featureManager = new DeviceTypeFeatureManager();

    @Override
    public FeatureManager getFeatureManager() {
        return featureManager;
    }

    @Override
    public boolean saveConfiguration(PlatformConfiguration platformConfiguration) throws DeviceManagementException {
        return false;
    }

    @Override
    public PlatformConfiguration getConfiguration() throws DeviceManagementException {
        return null;
    }

    @Override
    public boolean enrollDevice(Device device) throws DeviceManagementException {
        boolean status;
        try {
            if (log.isDebugEnabled()) {
                log.debug("Enrolling a new androidtv device : " + device.getDeviceIdentifier());
            }
            DeviceTypeDAO.beginTransaction();
            status = deviceTypeDAO.getDeviceTypeDAO().addDevice(device);
            DeviceTypeDAO.commitTransaction();
        } catch (DeviceMgtPluginException e) {
            try {
                DeviceTypeDAO.rollbackTransaction();
            } catch (DeviceMgtPluginException iotDAOEx) {
                log.warn("Error occurred while roll back the device enrol transaction :" + device.toString(), iotDAOEx);
            }
            String msg = "Error while enrolling the androidtv device : " + device.getDeviceIdentifier();
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
        return status;
    }

    @Override
    public boolean modifyEnrollment(Device device) throws DeviceManagementException {
        boolean status;
        try {
            if (log.isDebugEnabled()) {
                log.debug("Modifying the androidtv device enrollment data");
            }
            DeviceTypeDAO.beginTransaction();
            status = deviceTypeDAO.getDeviceTypeDAO().updateDevice(device);
            DeviceTypeDAO.commitTransaction();
        } catch (DeviceMgtPluginException e) {
            try {
                DeviceTypeDAO.rollbackTransaction();
            } catch (DeviceMgtPluginException iotDAOEx) {
                String msg = "Error occurred while roll back the update device transaction :" + device.toString();
                log.warn(msg, iotDAOEx);
            }
            String msg = "Error while updating the enrollment of the androidtv device : " +
                    device.getDeviceIdentifier();
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
        return status;
    }

    @Override
    public boolean disenrollDevice(DeviceIdentifier deviceId) throws DeviceManagementException {
        boolean status;
        try {
            if (log.isDebugEnabled()) {
                log.debug("Dis-enrolling androidtv device : " + deviceId);
            }
            DeviceTypeDAO.beginTransaction();
            status = deviceTypeDAO.getDeviceTypeDAO().deleteDevice(deviceId.getId());
            DeviceTypeDAO.commitTransaction();
        } catch (DeviceMgtPluginException e) {
            try {
                DeviceTypeDAO.rollbackTransaction();
            } catch (DeviceMgtPluginException iotDAOEx) {
                String msg = "Error occurred while roll back the device dis enrol transaction :" + deviceId.toString();
                log.warn(msg, iotDAOEx);
            }
            String msg = "Error while removing the androidtv device : " + deviceId.getId();
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
        return status;
    }

    @Override
    public boolean isEnrolled(DeviceIdentifier deviceId) throws DeviceManagementException {
        boolean isEnrolled = false;
        try {
            if (log.isDebugEnabled()) {
                log.debug("Checking the enrollment of androidtv device : " + deviceId.getId());
            }
            Device iotDevice =
                    deviceTypeDAO.getDeviceTypeDAO().getDevice(deviceId.getId());
            if (iotDevice != null) {
                isEnrolled = true;
            }
        } catch (DeviceMgtPluginException e) {
            String msg = "Error while checking the enrollment status of androidtv device : " +
                    deviceId.getId();
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
        return isEnrolled;
    }

    @Override
    public boolean isActive(DeviceIdentifier deviceId) throws DeviceManagementException {
        return true;
    }

    @Override
    public boolean setActive(DeviceIdentifier deviceId, boolean status)
            throws DeviceManagementException {
        return true;
    }

    @Override
    public Device getDevice(DeviceIdentifier deviceId) throws DeviceManagementException {
        Device device;
        try {
            if (log.isDebugEnabled()) {
                log.debug("Getting the details of androidtv device : " + deviceId.getId());
            }
            device = deviceTypeDAO.getDeviceTypeDAO().getDevice(deviceId.getId());
        } catch (DeviceMgtPluginException e) {
            String msg = "Error while fetching the androidtv device : " + deviceId.getId();
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
        return device;
    }

    @Override
    public boolean setOwnership(DeviceIdentifier deviceId, String ownershipType)
            throws DeviceManagementException {
        return true;
    }

    public boolean isClaimable(DeviceIdentifier deviceIdentifier) throws DeviceManagementException {
        return false;
    }

    @Override
    public boolean setStatus(DeviceIdentifier deviceId, String currentOwner,
                             EnrolmentInfo.Status status) throws DeviceManagementException {
        return false;
    }

    @Override
    public License getLicense(String s) throws LicenseManagementException {
        return null;
    }

    @Override
    public void addLicense(License license) throws LicenseManagementException {

    }

    @Override
    public boolean requireDeviceAuthorization() {
        return true;
    }

    @Override
    public boolean updateDeviceInfo(DeviceIdentifier deviceIdentifier, Device device) throws DeviceManagementException {
        boolean status;
        try {
            if (log.isDebugEnabled()) {
                log.debug("updating the details of androidtv device : " + deviceIdentifier);
            }
            DeviceTypeDAO.beginTransaction();
            status = deviceTypeDAO.getDeviceTypeDAO().updateDevice(device);
            DeviceTypeDAO.commitTransaction();
        } catch (DeviceMgtPluginException e) {
            try {
                DeviceTypeDAO.rollbackTransaction();
            } catch (DeviceMgtPluginException iotDAOEx) {
                String msg = "Error occurred while roll back the update device info transaction :" + device.toString();
                log.warn(msg, iotDAOEx);
            }
            String msg =
                    "Error while updating the androidtv device : " + deviceIdentifier;
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
        return status;
    }

    @Override
    public List<Device> getAllDevices() throws DeviceManagementException {
        List<Device> devices;
        try {
            if (log.isDebugEnabled()) {
                log.debug("Fetching the details of all androidtv devices");
            }
            devices = deviceTypeDAO.getDeviceTypeDAO().getAllDevices();
        } catch (DeviceMgtPluginException e) {
            String msg = "Error while fetching all androidtv devices.";
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
        return devices;
    }

    public void addEdgeDevice(EdgeDevice edgeDevice) throws DeviceManagementException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Adding a new edge device : " + edgeDevice.getEdgeDeviceSerial());
            }
            DeviceTypeDAO.beginTransaction();
            deviceTypeDAO.getDeviceTypeDAO().addEdgeDevice(edgeDevice);
            DeviceTypeDAO.commitTransaction();
        } catch (DeviceMgtPluginException e) {
            try {
                DeviceTypeDAO.rollbackTransaction();
            } catch (DeviceMgtPluginException iotDAOEx) {
                log.warn("Error occurred while roll back the edge device adding transaction", iotDAOEx);
            }
            String msg = "Error while adding edge device : " + edgeDevice.getEdgeDeviceName();
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
    }

    public void removeEdgeDevice(String serial) throws DeviceManagementException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("removing edge device : " + serial);
            }
            DeviceTypeDAO.beginTransaction();
            deviceTypeDAO.getDeviceTypeDAO().removeEdgeDevice(serial);
            DeviceTypeDAO.commitTransaction();
        } catch (DeviceMgtPluginException e) {
            try {
                DeviceTypeDAO.rollbackTransaction();
            } catch (DeviceMgtPluginException iotDAOEx) {
                log.warn("Error occurred while roll back the edge device removing transaction", iotDAOEx);
            }
            String msg = "Error while removing edge device : " + serial;
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
    }

    public List<EdgeDevice> getAllEdgeDevices(String gatewayId) throws DeviceManagementException {
        List<EdgeDevice> edgeDevices;
        try {
            if (log.isDebugEnabled()) {
                log.debug("Fetching the details of all edge devices");
            }
            edgeDevices = deviceTypeDAO.getDeviceTypeDAO().getAllEdgeDevices(gatewayId);
        } catch (DeviceMgtPluginException e) {
            String msg = "Error while fetching all androidtv devices.";
            log.error(msg, e);
            throw new DeviceManagementException(msg, e);
        }
        return edgeDevices;
    }
}
