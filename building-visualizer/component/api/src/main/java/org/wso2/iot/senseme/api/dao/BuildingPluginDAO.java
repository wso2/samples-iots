/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.iot.senseme.api.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.iot.senseme.api.dto.BuildingInfo;
import org.wso2.iot.senseme.api.dto.FloorInfo;
import org.wso2.iot.senseme.api.util.DeviceTypeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implements CRUD operations for Buildings.
 */
public class BuildingPluginDAO {

    private static final Log log = LogFactory.getLog(BuildingPluginDAO.class);
    private BuildingDAOHandler buildingDAOHandler;

    public BuildingPluginDAO(BuildingDAOHandler buildingDAOHandler) {
        this.buildingDAOHandler = buildingDAOHandler;
    }

    public int addBuilding(BuildingInfo building)  {
        int buildingId=0;
        Connection conn = null;
        PreparedStatement stmt = null;

        try {

            conn = BuildingDAOHandler.getConnection();
            String createDBQuery = "INSERT INTO building(buildingName,owner,lng,lat,numOfFloors)" +
                    " VALUES (?, ?, ?, ?, ?)";

            stmt = conn.prepareStatement(createDBQuery);
            stmt.setString(1, building.getBuildingName());
            stmt.setString(2, building.getOwner());
            stmt.setString(3, building.getLongitude());
            stmt.setString(4, building.getLatitude());
            stmt.setInt(5, building.getNumFloors());

            int rows = stmt.executeUpdate();
            if (rows == 1) {
                buildingId = getBuildingId(building.getBuildingName());
            }
        } catch (SQLException e) {
            String msg = "SQL Exception";
            log.error(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, null,conn);
        }
        return buildingId;
    }

    public boolean addFloor(FloorInfo floor){
        boolean status = false;
        Connection conn = null;
        PreparedStatement stmt = null;

        try {

            conn = BuildingDAOHandler.getConnection();
            String createDBQuery = "INSERT INTO floor(FLOORNUM,BUILDINGID,XCORDS,YCORDS)" +
                    " VALUES (?, ?, ?, ?)";

            stmt = conn.prepareStatement(createDBQuery);
            stmt.setInt(1,floor.getFloorNum());
            stmt.setInt(2,floor.getBuildingId());
            stmt.setString(3,floor.getxCords());
            stmt.setString(4,floor.getyCords());

            int rows = stmt.executeUpdate();
            if (rows >0) {
                status=true;
            }

        } catch (SQLException e) {
            String msg = "SQL Exception";
            log.error(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, null,conn);
        }
        return status;
    }

    public int getBuildingId(String buildingName){

        int buildingId = 0;
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = BuildingDAOHandler.getConnection();
            String selectDBQuery = "SELECT buildingId FROM building "+
                    "WHERE buildingName=?" ;
            stmt = conn.prepareStatement(selectDBQuery);
            stmt.setString(1, buildingName);

            ResultSet rows = stmt.executeQuery();
            while (rows.next()) {
                buildingId = rows.getInt("buildingId");
                if (log.isDebugEnabled()) {
                    log.debug("BUilding Id "+buildingId+" for "+buildingName);
                }
            }
        } catch (SQLException e) {
            String msg = "SQL Exception";
            log.error(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, null,conn);
        }
        return buildingId;
    }

    public boolean updateBuildingImage(int buildingId, byte[] imageBytes){

        boolean status = false;
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = BuildingDAOHandler.getConnection();
            String updateDBQuery = "UPDATE building "
                    + "SET image = ? "
                    + "WHERE buildingId=?";
            stmt = conn.prepareStatement(updateDBQuery);
            stmt.setBytes(1, imageBytes);
            stmt.setInt(2, buildingId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                status = true;
                if (log.isDebugEnabled()) {
                    log.debug("Image updated for BUilding Id "+buildingId);
                }
            }
        } catch (SQLException e) {
            String msg = "SQL Exception";
            log.error(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, null,conn);
        }
        return status;
    }

    public boolean updateFloorPlan(int buildingId, int floorId, byte[] imageBytes){

        boolean status = false;
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = BuildingDAOHandler.getConnection();
            String updateDBQuery = "UPDATE floor "
                    + "SET image = ? "
                    + "WHERE buildingId=? "+ "AND floorId=? ";
            stmt = conn.prepareStatement(updateDBQuery);
            stmt.setBytes(1, imageBytes);
            stmt.setInt(2, buildingId);
            stmt.setInt(3, floorId);


            int rows = stmt.executeUpdate();
            if (rows > 0) {
                status = true;
                if (log.isDebugEnabled()) {
                    log.debug("Image updated for BUilding Id "+buildingId);
                }
            }
        } catch (SQLException e) {
            String msg = "SQL Exception";
            log.error(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, null,conn);
        }
        return status;
    }

    public File getBuildingImage(int buildingId){

        boolean status = false;
        Connection conn = null;
        PreparedStatement stmt = null;
        FileOutputStream fos = null;
        File file = null;

        try {
            conn = BuildingDAOHandler.getConnection();
            String updateDBQuery = "SELECT image FROM building WHERE buildingId=?";
            stmt = conn.prepareStatement(updateDBQuery);
            stmt.setInt(1, buildingId);

            ResultSet rows = stmt.executeQuery();

            file = new File("image");
            fos = new FileOutputStream(file);

            while (rows.next()) {
                InputStream input = rows.getBinaryStream("image");
                byte[] buffer = new byte[1024];
                while (input.read(buffer) > 0) {
                    fos.write(buffer);
                }
            }
        } catch (SQLException e) {
            String msg = "SQL Exception";
            log.error(msg, e);
        } catch (IOException e){
            String msg = "I/O Exception";
            log.error(msg, e);
        }finally {

            DeviceTypeUtils.cleanupResources(stmt, null,conn);

            if (fos != null){
                try{
                    fos.close();
                }catch (IOException e){
                    log.error(e.getMessage());
                }
            }
        }
        return file;
    }
}