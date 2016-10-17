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

package org.homeautomation.garbagebin.plugin.impl.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.homeautomation.garbagebin.plugin.constants.DeviceTypeConstants;
import org.homeautomation.garbagebin.plugin.impl.dao.DeviceTypeDAO;
import org.homeautomation.garbagebin.plugin.exception.DeviceMgtPluginException;
import org.homeautomation.garbagebin.plugin.impl.util.DeviceTypeUtils;

import org.wso2.carbon.device.mgt.common.Device;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements IotDeviceDAO for garbagebin Devices.
 */
public class DeviceTypeDAOImpl {


    private static final Log log = LogFactory.getLog(DeviceTypeDAOImpl.class);

    /**
     * Get device from the database.
     *
     * @param deviceId to retive the device.
     * @return device object.
     * @throws DeviceMgtPluginException
     */
    public Device getDevice(String deviceId) throws DeviceMgtPluginException {
        Connection conn;
        PreparedStatement stmt = null;
        Device iotDevice = null;
        ResultSet resultSet = null;
        try {
            conn = DeviceTypeDAO.getConnection();
            String selectDBQuery =
                    "SELECT garbagebin_DEVICE_ID, DEVICE_NAME FROM garbagebin_DEVICE WHERE garbagebin_DEVICE_ID = ?";
            stmt = conn.prepareStatement(selectDBQuery);
            stmt.setString(1, deviceId);
            resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                iotDevice = new Device();
                iotDevice.setName(resultSet.getString(
                        DeviceTypeConstants.DEVICE_PLUGIN_DEVICE_NAME));
                if (log.isDebugEnabled()) {
                    log.debug("garbagebin device " + deviceId + " data has been fetched from " +
                            "garbagebin database.");
                }
            }
        } catch (SQLException e) {
            String msg = "Error occurred while fetching garbagebin device : '" + deviceId + "'";
            log.error(msg, e);
            throw new DeviceMgtPluginException(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, resultSet);
            DeviceTypeDAO.closeConnection();
        }
        return iotDevice;
    }

    /**
     * Add new device.
     *
     * @param device to be added.
     * @return added or not.
     * @throws DeviceMgtPluginException
     */
    public boolean addDevice(Device device) throws DeviceMgtPluginException {
        boolean status = false;
        Connection conn;
        PreparedStatement stmt = null;
        try {
            conn = DeviceTypeDAO.getConnection();
            String createDBQuery =
                    "INSERT INTO garbagebin_DEVICE(garbagebin_DEVICE_ID, DEVICE_NAME) VALUES (?, ?)";
            stmt = conn.prepareStatement(createDBQuery);
            stmt.setString(1, device.getDeviceIdentifier());
            stmt.setString(2, device.getName());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                status = true;
                if (log.isDebugEnabled()) {
                    log.debug("garbagebin device " + device.getDeviceIdentifier() + " data has been" +
                            " added to the garbagebin database.");
                }
            }
        } catch (SQLException e) {
            String msg = "Error occurred while adding the garbagebin device '" +
                    device.getDeviceIdentifier() + "' to the garbagebin db.";
            log.error(msg, e);
            throw new DeviceMgtPluginException(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, null);
        }
        return status;
    }

    /**
     * Update existing device.
     *
     * @param device to be updated.
     * @return updated or not.
     * @throws DeviceMgtPluginException
     */
    public boolean updateDevice(Device device) throws DeviceMgtPluginException {
        boolean status = false;
        Connection conn;
        PreparedStatement stmt = null;
        try {
            conn = DeviceTypeDAO.getConnection();
            String updateDBQuery =
                    "UPDATE garbagebin_DEVICE SET  DEVICE_NAME = ? WHERE garbagebin_DEVICE_ID = ?";
            stmt = conn.prepareStatement(updateDBQuery);
            if (device.getProperties() == null) {
                device.setProperties(new ArrayList<Device.Property>());
            }
            stmt.setString(1, device.getName());
            stmt.setString(2, device.getDeviceIdentifier());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                status = true;
                if (log.isDebugEnabled()) {
                    log.debug("garbagebin device " + device.getDeviceIdentifier() + " data has been" +
                            " modified.");
                }
            }
        } catch (SQLException e) {
            String msg = "Error occurred while modifying the garbagebin device '" +
                    device.getDeviceIdentifier() + "' data.";
            log.error(msg, e);
            throw new DeviceMgtPluginException(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, null);
        }
        return status;
    }

    /**
     * Delete device.
     *
     * @param deviceId of the device to be deleted.
     * @return deleted or not.
     * @throws DeviceMgtPluginException
     */
    public boolean deleteDevice(String deviceId) throws DeviceMgtPluginException {
        boolean status = false;
        Connection conn;
        PreparedStatement stmt = null;
        try {
            conn = DeviceTypeDAO.getConnection();
            String deleteDBQuery = "DELETE FROM garbagebin_DEVICE WHERE garbagebin_DEVICE_ID = ?";
            stmt = conn.prepareStatement(deleteDBQuery);
            stmt.setString(1, deviceId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                status = true;
                if (log.isDebugEnabled()) {
                    log.debug("garbagebin device " + deviceId + " data has deleted" +
                            " from the garbagebin database.");
                }
            }
        } catch (SQLException e) {
            String msg = "Error occurred while deleting garbagebin device " + deviceId;
            log.error(msg, e);
            throw new DeviceMgtPluginException(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, null);
        }
        return status;
    }

    /**
     * Get all garbagebin devices.
     *
     * @return list of devices.
     * @throws DeviceMgtPluginException
     */
    public List<Device> getAllDevices() throws DeviceMgtPluginException {
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        Device device;
        List<Device> iotDevices = new ArrayList<>();
        try {
            conn = DeviceTypeDAO.getConnection();
            String selectDBQuery =
                    "SELECT garbagebin_DEVICE_ID, DEVICE_NAME " +
                            "FROM garbagebin_DEVICE";
            stmt = conn.prepareStatement(selectDBQuery);
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                device = new Device();
                device.setDeviceIdentifier(resultSet.getString(DeviceTypeConstants.DEVICE_PLUGIN_DEVICE_ID));
                device.setName(resultSet.getString(DeviceTypeConstants.DEVICE_PLUGIN_DEVICE_NAME));
                List<Device.Property> propertyList = new ArrayList<>();
                device.setProperties(propertyList);
            }
            if (log.isDebugEnabled()) {
                log.debug("All garbagebin device details have fetched from garbagebin database.");
            }
            return iotDevices;
        } catch (SQLException e) {
            String msg = "Error occurred while fetching all garbagebin device data'";
            log.error(msg, e);
            throw new DeviceMgtPluginException(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, resultSet);
            DeviceTypeDAO.closeConnection();
        }
    }
}
