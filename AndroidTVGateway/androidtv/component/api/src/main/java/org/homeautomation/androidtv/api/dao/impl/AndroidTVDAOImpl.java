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

package org.homeautomation.androidtv.api.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.homeautomation.androidtv.api.constants.AndroidTVConstants;
import org.homeautomation.androidtv.api.dao.AndroidTVDAO;
import org.homeautomation.androidtv.api.dao.AndroidTVDAOFactory;
import org.homeautomation.androidtv.api.dao.AndroidTVDAOUtil;
import org.homeautomation.androidtv.api.dto.EdgeDevice;
import org.homeautomation.androidtv.api.exception.AndroidTVDAOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements AndroidTVDAO for Android TV Devices.
 */
public class AndroidTVDAOImpl implements AndroidTVDAO {

    private static final Log log = LogFactory.getLog(AndroidTVDAOImpl.class);

    @Override
    public List<EdgeDevice> getAllEdgeDevices(String gatewayId) throws AndroidTVDAOException {
        Connection conn;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        EdgeDevice edgeDevice;
        List<EdgeDevice> edgeDevices = new ArrayList<>();
        try {
            conn = AndroidTVDAOFactory.getConnection();
            String selectDBQuery =
                    "SELECT androidtv_DEVICE_ID, SERIAL, DEVICE_NAME " +
                    "FROM edge_DEVICE WHERE androidtv_DEVICE_ID = ?";
            stmt = conn.prepareStatement(selectDBQuery);
            stmt.setString(1, gatewayId);
            resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                edgeDevice = new EdgeDevice();
                edgeDevice.setGatewayId(resultSet.getString(AndroidTVConstants.DEVICE_PLUGIN_DEVICE_ID));
                edgeDevice.setEdgeDeviceName(resultSet.getString(AndroidTVConstants.DEVICE_PLUGIN_DEVICE_NAME));
                edgeDevice.setEdgeDeviceSerial(resultSet.getString(AndroidTVConstants.DEVICE_PLUGIN_DEVICE_SERIAL));
                edgeDevices.add(edgeDevice);
            }
            if (log.isDebugEnabled()) {
                log.debug("All androidtv device details have fetched from androidtv database.");
            }
            return edgeDevices;
        } catch (SQLException e) {
            String msg = "Error occurred while fetching all androidtv device data'";
            log.error(msg, e);
            throw new AndroidTVDAOException(msg, e);
        } finally {
            AndroidTVDAOUtil.cleanupResources(stmt, resultSet);
        }
    }

    @Override
    public void addEdgeDevice(EdgeDevice edgeDevice) throws AndroidTVDAOException {
        Connection conn;
        PreparedStatement stmt = null;
        try {
            conn = AndroidTVDAOFactory.getConnection();
            String createDBQuery =
                    "INSERT INTO edge_DEVICE(androidtv_DEVICE_ID, SERIAL, DEVICE_NAME) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(createDBQuery);
            stmt.setString(1, edgeDevice.getGatewayId());
            stmt.setString(2, edgeDevice.getEdgeDeviceSerial());
            stmt.setString(3, edgeDevice.getEdgeDeviceName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            String msg = "Error occurred while adding the edge device '" +
                         edgeDevice.getEdgeDeviceSerial() + "' to the androidtv db.";
            log.error(msg, e);
            throw new AndroidTVDAOException(msg, e);
        } finally {
            AndroidTVDAOUtil.cleanupResources(stmt, null);
        }
    }

    @Override
    public void removeEdgeDevice(String serial) throws AndroidTVDAOException {
        Connection conn;
        PreparedStatement stmt = null;
        try {
            conn = AndroidTVDAOFactory.getConnection();
            String deleteDBQuery =
                    "DELETE FROM edge_DEVICE WHERE SERIAL = ?";
            stmt = conn.prepareStatement(deleteDBQuery);
            stmt.setString(1, serial);
            stmt.executeUpdate();
        } catch (SQLException e) {
            String msg = "Error occurred while deleting edge device " + serial;
            log.error(msg, e);
            throw new AndroidTVDAOException(msg, e);
        } finally {
            AndroidTVDAOUtil.cleanupResources(stmt, null);
        }
    }
}
