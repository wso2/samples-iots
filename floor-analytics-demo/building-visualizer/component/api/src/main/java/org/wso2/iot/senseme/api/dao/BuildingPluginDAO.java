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
import java.util.ArrayList;
import java.util.List;

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
            String createDBQuery = "INSERT INTO building(buildingName, owner, lng, lat, numOfFloors)" +
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
            DeviceTypeUtils.cleanupResources(stmt, null);
        }
        return buildingId;
    }

    public List<BuildingInfo> getAllBuildings() {
        List<BuildingInfo> buildingList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = BuildingDAOHandler.getConnection();
            String getAllBuildingsQuery = "SELECT * FROM building";
            stmt = conn.prepareStatement(getAllBuildingsQuery);

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                BuildingInfo building = new BuildingInfo();
                building.setBuildingId(resultSet.getInt("BUILDINGID"));
                building.setOwner(resultSet.getString("OWNER"));
                building.setBuildingName(resultSet.getString("BUILDINGNAME"));
                building.setLatitude(resultSet.getString("LNG"));
                building.setLongitude(resultSet.getString("LAT"));
                building.setNumFloors(resultSet.getInt("NUMOFFLOORS"));
                buildingList.add(building);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DeviceTypeUtils.cleanupResources(stmt, null);
        }

        return buildingList;
    }

    public BuildingInfo updateBuilding(BuildingInfo building) {

        Connection conn = null;
        PreparedStatement stmt = null;
        BuildingInfo buildingInfo;

        try {
            conn = BuildingDAOHandler.getConnection();
            String updateBuildingQuery = "UPDATE building SET buildingName = ?, lng = ?, lat = ? WHERE buildingId = ?";
            stmt = conn.prepareStatement(updateBuildingQuery);
            stmt.setString(1, building.getBuildingName());
            stmt.setString(2, building.getLongitude());
            stmt.setString(3, building.getLatitude());
            stmt.setInt(4, building.getBuildingId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            buildingInfo = getBuilding(building.getBuildingId());
            DeviceTypeUtils.cleanupResources(stmt, null);
        }

        return buildingInfo;

    }

    public BuildingInfo getBuilding(int buildingId) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = BuildingDAOHandler.getConnection();
            String getAllBuildingsQuery = "SELECT * FROM building where BUILDINGID = ?";
            stmt = conn.prepareStatement(getAllBuildingsQuery);
            stmt.setInt(1, buildingId);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                BuildingInfo building = new BuildingInfo();
                building.setBuildingId(resultSet.getInt("BUILDINGID"));
                building.setOwner(resultSet.getString("OWNER"));
                building.setBuildingName(resultSet.getString("BUILDINGNAME"));
                building.setLatitude(resultSet.getString("LNG"));
                building.setLongitude(resultSet.getString("LAT"));
                building.setNumFloors(resultSet.getInt("NUMOFFLOORS"));
                return building;
            }

        } catch (SQLException e) {
            log.error("failed to retireve building details for building id " + buildingId);
            return null;
        }finally {
            DeviceTypeUtils.cleanupResources(stmt, null);
        }
        return null;
    }

    public boolean addFloor(FloorInfo floor){
        boolean status = false;
        Connection conn = null;
        PreparedStatement stmt = null;

        try {

            conn = BuildingDAOHandler.getConnection();
          
            String createDBQuery = "INSERT INTO floor(FLOORNUM,BUILDINGID)" +
                    " VALUES (?, ?)";


            stmt = conn.prepareStatement(createDBQuery);
            stmt.setInt(1,floor.getFloorNum());
            stmt.setInt(2,floor.getBuildingId());

            int rows = stmt.executeUpdate();
            if (rows >0) {
                status=true;
            }

        } catch (SQLException e) {
            String msg = "SQL Exception";
            log.error(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, null);
        }
        return status;
    }

    public int getBuildingId(String buildingName){

        int buildingId = 0;
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = BuildingDAOHandler.getConnection();
            String selectDBQuery = "SELECT buildingId FROM building WHERE buildingName=?" ;
            stmt = conn.prepareStatement(selectDBQuery);
            stmt.setString(1, buildingName);

            ResultSet rows = stmt.executeQuery();
            while (rows.next()) {
                buildingId = rows.getInt("buildingId");
                if (log.isDebugEnabled()) {
                    log.debug("Building Id " + buildingId + " for " + buildingName);
                }
            }
        } catch (SQLException e) {
            String msg = "SQL Exception";
            log.error(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, null);
        }
        return buildingId;
    }


    public BuildingInfo getBuildingData(int buildingId){

        Connection conn = null;
        PreparedStatement stmt = null;
        BuildingInfo building = new BuildingInfo();

        try {
            conn = BuildingDAOHandler.getConnection();
            String selectDBQuery = "SELECT * FROM building WHERE buildingId=?" ;
            stmt = conn.prepareStatement(selectDBQuery);
            stmt.setInt(1, buildingId);

            ResultSet rows = stmt.executeQuery();

            while (rows.next()) {
                building.setBuildingId(rows.getInt("BUILDINGID"));
                building.setOwner(rows.getString("OWNER"));
                building.setBuildingName(rows.getString("BUILDINGNAME"));
                building.setLatitude(rows.getString("LNG"));
                building.setLongitude(rows.getString("LAT"));
                building.setNumFloors(rows.getInt("NUMOFFLOORS"));
            }
        } catch (SQLException e) {
            String msg = "SQL Exception";
            log.error(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, null,conn);
        }
        return building;
    }

    public boolean updateFloorPlan(int buildingId, int floorId, byte[] imageBytes){

        boolean status = false;
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = BuildingDAOHandler.getConnection();
            String updateDBQuery = "UPDATE floor SET image = ? WHERE buildingId=? AND floorId=? ";
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
            DeviceTypeUtils.cleanupResources(stmt, null);
        }
        return status;
    }

    public boolean insertFloorDetails(int buildingId, int floorId, byte[] imageBytes){

        boolean status = false;
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = BuildingDAOHandler.getConnection();
            String insertDBQuery = "INSERT INTO floor(FLOORNUM,BUILDINGID,IMAGE)" +
                    " VALUES (?,?,?)";
            stmt = conn.prepareStatement(insertDBQuery);
            stmt.setInt(1, floorId);
            stmt.setInt(2, buildingId);
            stmt.setBytes(3, imageBytes);



            int rows = stmt.executeUpdate();
            if (rows > 0) {
                status = true;
                if (log.isDebugEnabled()) {
                    log.debug("Floor updated in "+buildingId+" floor "+floorId);
                }
            }
        } catch (SQLException e) {
            String msg = "SQL Exception";
            log.error(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, null);
        }
        return status;
    }

    public File getFloorPlan(int buildingId, int floorId){

        boolean status = false;
        Connection conn = null;
        PreparedStatement stmt = null;
        FileOutputStream fos = null;
        File file = null;

        try {
            conn = BuildingDAOHandler.getConnection();
            String updateDBQuery = "SELECT image FROM floor WHERE buildingId=? "+"And FLOORNUM=?";
            stmt = conn.prepareStatement(updateDBQuery);
            stmt.setInt(1, buildingId);
            stmt.setInt(2, floorId);
            ResultSet rows = stmt.executeQuery();
            if (rows.next()) {
                file = new File("image");
                fos = new FileOutputStream(file);
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

            DeviceTypeUtils.cleanupResources(stmt, null);

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

    public List<Integer> getAvailableFloors(int buildingId) {
        List<Integer> floorIds = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;

        try {
            conn = BuildingDAOHandler.getConnection();
            String query = "SELECT FLOORNUM, image FROM floor WHERE buildingId = ?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, buildingId);
            resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                if (resultSet.getBinaryStream("image") != null) {
                    floorIds.add(resultSet.getInt("FLOORNUM"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, resultSet);
        }
        return floorIds;
    }

    public boolean removeBuilding(int buildingId) {
        boolean check = false;
        Connection conn;
        PreparedStatement stmt = null;

        try {
            conn = BuildingDAOHandler.getConnection();
            String query = "DELETE from building WHERE buildingId=?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, buildingId);
            check = stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, null);
        }
        return check;
    }
}