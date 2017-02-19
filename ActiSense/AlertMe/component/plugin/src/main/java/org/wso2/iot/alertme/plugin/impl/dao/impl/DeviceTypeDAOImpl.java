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

package org.wso2.iot.alertme.plugin.impl.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.wso2.iot.alertme.plugin.constants.DeviceTypeConstants;
import org.wso2.iot.alertme.plugin.exception.DeviceMgtPluginException;
import org.wso2.iot.alertme.plugin.impl.dao.DeviceTypeDAO;
import org.wso2.iot.alertme.plugin.impl.util.DeviceTypeUtils;

import org.wso2.carbon.device.mgt.common.Device;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements IotDeviceDAO for alertme Devices.
 */
public class DeviceTypeDAOImpl {

    private static final Log log = LogFactory.getLog(DeviceTypeDAOImpl.class);

    public Device getDevice(String deviceId) throws DeviceMgtPluginException {
        Connection conn = null;
        PreparedStatement stmt = null;
        Device iotDevice = null;
        ResultSet resultSet = null;
        try {
            conn = DeviceTypeDAO.getConnection();
            String selectDBQuery =
                    "SELECT alertme_DEVICE_ID, DEVICE_NAME" +
                            " FROM alertme_DEVICE WHERE alertme_DEVICE_ID = ?";
            stmt = conn.prepareStatement(selectDBQuery);
            stmt.setString(1, deviceId);
            resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                iotDevice = new Device();
                iotDevice.setName(resultSet.getString(
                        DeviceTypeConstants.DEVICE_PLUGIN_DEVICE_NAME));
                if (log.isDebugEnabled()) {
                    log.debug("alertme device " + deviceId + " data has been fetched from " +
                            "alertme database.");
                }
            }
        } catch (SQLException e) {
            String msg = "Error occurred while fetching alertme device : '" + deviceId + "'";
            log.error(msg, e);
            throw new DeviceMgtPluginException(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, resultSet);
            DeviceTypeDAO.closeConnection();
        }
        return iotDevice;
    }

    public boolean addDevice(Device device) throws DeviceMgtPluginException {
        boolean status = false;
        Connection conn;
        PreparedStatement stmt = null;
        try {
            conn = DeviceTypeDAO.getConnection();
            String createDBQuery =
                    "INSERT INTO alertme_DEVICE(alertme_DEVICE_ID, DEVICE_NAME) VALUES (?, ?)";
            stmt = conn.prepareStatement(createDBQuery);
            stmt.setString(1, device.getDeviceIdentifier());
            stmt.setString(2, device.getName());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                status = true;
                if (log.isDebugEnabled()) {
                    log.debug("alertme device " + device.getDeviceIdentifier() + " data has been" +
                            " added to the alertme database.");
                }
            }
        } catch (SQLException e) {
            String msg = "Error occurred while adding the alertme device '" +
                    device.getDeviceIdentifier() + "' to the alertme db.";
            log.error(msg, e);
            throw new DeviceMgtPluginException(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, null);
        }
        return status;
    }

    /**
     * When a  mapping is added, if it exists it is updated.
     * @param senseMeDeviceId
     * @param alertMeDeviceId
     * @param policy
     * @return
     * @throws DeviceMgtPluginException
     */
    public boolean addDeviceMapping(String senseMeDeviceId, String alertMeDeviceId, String policy) throws
                                                                                   DeviceMgtPluginException {
        boolean status = false;
        Connection conn;
        PreparedStatement stmt = null;
        try {
            conn = DeviceTypeDAO.getConnection();
            String createDBQuery = "MERGE INTO SENSE_ALERT_MAPPINGS KEY(senseme_DEVICE_ID, alertme_DEVICE_ID) " +
                                   "VALUES (?, ?, ?);";
            stmt = conn.prepareStatement(createDBQuery);
            stmt.setString(1, senseMeDeviceId);
            stmt.setString(2, alertMeDeviceId);
            stmt.setString(3, policy);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                status = true;
                if (log.isDebugEnabled()) {
                    log.debug("senseme device " + senseMeDeviceId + " and " + alertMeDeviceId + " mapping added to DB.");
                }
            }

        } catch (SQLException e) {
            String msg = "Error occurred while adding the senseme " + senseMeDeviceId + " and  alertme "
                         + alertMeDeviceId + " mapping added to DB.";
            log.error(msg, e);
            throw new DeviceMgtPluginException(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, null);
        }
        return status;
    }

    /**
     * Distance policy included as a property per retrieved device.
     * @param alertMe
     * @return
     * @throws DeviceMgtPluginException
     */
    public List<Device> retrieveDeviceMappings(Device alertMe) throws
                                                               DeviceMgtPluginException {
        boolean status = false;
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        Device device;
        List<Device> deviceMappings = new ArrayList<Device>();
        try {
            conn = DeviceTypeDAO.getConnection();
            String checkMappingQuery = "SELECT * FROM SENSE_ALERT_MAPPINGS where " +
                                       "alertme_DEVICE_ID = ?";
            stmt = conn.prepareStatement(checkMappingQuery);
            stmt.setString(1, alertMe.getDeviceIdentifier());
            resultSet = stmt.executeQuery();
            List<Device.Property> props = new ArrayList<>();
            while (resultSet.next()) {
                device = new Device();
                device.setDeviceIdentifier(resultSet.getString(DeviceTypeConstants.DEVICE_PLUGIN_DEVICE_ID));
                Device.Property prop = new Device.Property();
                prop.setName("distance");
                prop.setValue(resultSet.getString("policy"));
                props.add(prop);
                device.setProperties(props);
                deviceMappings.add(device);
            }
        } catch (SQLException e) {
            String msg = "Error occurred while retrieving device mappings for alertme ID "+
                         alertMe.getDeviceIdentifier();
            log.error(msg, e);
            throw new DeviceMgtPluginException(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, null);
        }
        return deviceMappings;
    }

    public boolean updateDevice(Device device) throws DeviceMgtPluginException {
        boolean status = false;
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DeviceTypeDAO.getConnection();
            String updateDBQuery =
                    "UPDATE alertme_DEVICE SET  DEVICE_NAME = ? WHERE alertme_DEVICE_ID = ?";
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
                    log.debug("alertme device " + device.getDeviceIdentifier() + " data has been" +
                            " modified.");
                }
            }
        } catch (SQLException e) {
            String msg = "Error occurred while modifying the alertme device '" +
                    device.getDeviceIdentifier() + "' data.";
            log.error(msg, e);
            throw new DeviceMgtPluginException(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, null);
        }
        return status;
    }

    public boolean deleteDevice(String deviceId) throws DeviceMgtPluginException {
        boolean status = false;
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DeviceTypeDAO.getConnection();
            String deleteDBQuery =
                    "DELETE FROM alertme_DEVICE WHERE alertme_DEVICE_ID = ?";
            stmt = conn.prepareStatement(deleteDBQuery);
            stmt.setString(1, deviceId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                status = true;
                if (log.isDebugEnabled()) {
                    log.debug("alertme device " + deviceId + " data has deleted" +
                            " from the alertme database.");
                }
            }
        } catch (SQLException e) {
            String msg = "Error occurred while deleting alertme device " + deviceId;
            log.error(msg, e);
            throw new DeviceMgtPluginException(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, null);
        }
        return status;
    }

    public List<Device> getAllDevices() throws DeviceMgtPluginException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        Device device;
        List<Device> iotDevices = new ArrayList<>();
        try {
            conn = DeviceTypeDAO.getConnection();
            String selectDBQuery =
                    "SELECT alertme_DEVICE_ID, DEVICE_NAME " +
                            "FROM alertme_DEVICE";
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
                log.debug("All alertme device details have fetched from alertme database.");
            }
            return iotDevices;
        } catch (SQLException e) {
            String msg = "Error occurred while fetching all alertme device data'";
            log.error(msg, e);
            throw new DeviceMgtPluginException(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, resultSet);
            DeviceTypeDAO.closeConnection();
        }
    }
}
