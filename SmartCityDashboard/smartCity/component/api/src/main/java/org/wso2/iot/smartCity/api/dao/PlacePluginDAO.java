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

package org.wso2.iot.smartCity.api.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.iot.smartCity.api.dto.PlaceInfo;
import org.wso2.iot.smartCity.api.util.DeviceTypeUtils;

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
 * Implements CRUD operations for Places.
 */
public class PlacePluginDAO {

    private static final Log log = LogFactory.getLog(PlacePluginDAO.class);
    private PlaceDAOHandler placeDAOHandler;

    public PlacePluginDAO(PlaceDAOHandler placeDAOHandler) {
        this.placeDAOHandler = placeDAOHandler;
    }

    public int addPlace(PlaceInfo place)  {
        int placeId=0;
        Connection conn = null;
        PreparedStatement stmt = null;

        try {

            conn = PlaceDAOHandler.getConnection();
            String createDBQuery = "INSERT INTO places(placeName, lng, lat)" +
                    " VALUES (?, ?, ?)";

            stmt = conn.prepareStatement(createDBQuery);
            stmt.setString(1, place.getPlaceName());
            stmt.setString(2, place.getLongitude());
            stmt.setString(3, place.getLatitude());

            int rows = stmt.executeUpdate();
            if (rows == 1) {
                placeId = getPlaceId(place.getPlaceName());
            }
        } catch (SQLException e) {
            String msg = "SQL Exception";
            log.error(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, null);
        }
        return placeId;
    }

    public List<PlaceInfo> getAllPlaces() {
        List<PlaceInfo> placeList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = PlaceDAOHandler.getConnection();
            String getAllPlacesQuery = "SELECT placeId, placeName, lng, lat FROM places";
            stmt = conn.prepareStatement(getAllPlacesQuery);

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                PlaceInfo place = new PlaceInfo();
                place.setPlaceId(resultSet.getInt("PLACEID"));
                place.setPlaceName(resultSet.getString("PLACENAME"));
                place.setLongitude(resultSet.getString("LNG"));
                place.setLatitude(resultSet.getString("LAT"));
                placeList.add(place);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DeviceTypeUtils.cleanupResources(stmt, null);
        }

        return placeList;
    }

    public PlaceInfo updatePlace(PlaceInfo place) {

        Connection conn = null;
        PreparedStatement stmt = null;
        PlaceInfo placeInfo;

        try {
            conn = PlaceDAOHandler.getConnection();
            String updatePlaceQuery = "UPDATE places SET placeName = ?, lng = ?, lat = ? WHERE placeId = ?";
            stmt = conn.prepareStatement(updatePlaceQuery);
            stmt.setString(1, place.getPlaceName());
            stmt.setString(2, place.getLongitude());
            stmt.setString(3, place.getLatitude());
            stmt.setInt(4, place.getPlaceId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            placeInfo = getPlace(place.getPlaceId());
            DeviceTypeUtils.cleanupResources(stmt, null);
        }

        return placeInfo;

    }

    public PlaceInfo getPlace(int placeId) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = PlaceDAOHandler.getConnection();
            String getAllPlacesQuery = "SELECT * FROM places where PLACEID = ?";
            stmt = conn.prepareStatement(getAllPlacesQuery);
            stmt.setInt(1, placeId);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                PlaceInfo place = new PlaceInfo();
                place.setPlaceId(resultSet.getInt("PLACEID"));
                place.setPlaceName(resultSet.getString("PLACENAME"));
                place.setLongitude(resultSet.getString("LNG"));
                place.setLatitude(resultSet.getString("LAT"));
                return place;
            }

        } catch (SQLException e) {
            log.error("failed to retireve place details for place id " + placeId);
            return null;
        }finally {
            DeviceTypeUtils.cleanupResources(stmt, null);
        }
        return null;
    }

    public int getPlaceId(String placeName){

        int placeId = 0;
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = PlaceDAOHandler.getConnection();
            String selectDBQuery = "SELECT placeId FROM places WHERE placeName=?" ;
            stmt = conn.prepareStatement(selectDBQuery);
            stmt.setString(1, placeName);

            ResultSet rows = stmt.executeQuery();
            while (rows.next()) {
                placeId = rows.getInt("placeId");
                if (log.isDebugEnabled()) {
                    log.debug("Place Id " + placeId + " for " + placeName);
                }
            }
        } catch (SQLException e) {
            String msg = "SQL Exception";
            log.error(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, null);
        }
        return placeId;
    }


    public PlaceInfo getPlaceData(int placeId){

        Connection conn = null;
        PreparedStatement stmt = null;
        PlaceInfo place = new PlaceInfo();

        try {
            conn = PlaceDAOHandler.getConnection();
            String selectDBQuery = "SELECT * FROM places WHERE placeId=?" ;
            stmt = conn.prepareStatement(selectDBQuery);
            stmt.setInt(1, placeId);

            ResultSet rows = stmt.executeQuery();

            while (rows.next()) {
                place.setPlaceId(rows.getInt("PLACEID"));
                place.setPlaceName(rows.getString("PLACENAME"));
                place.setLongitude(rows.getString("LNG"));
                place.setLatitude(rows.getString("LAT"));
            }
        } catch (SQLException e) {
            String msg = "SQL Exception";
            log.error(msg, e);
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, null,conn);
        }
        return place;
    }

    public boolean updatePlacePlan(int placeId, byte[] imageBytes){

        boolean status = false;
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = PlaceDAOHandler.getConnection();
            String updateDBQuery = "UPDATE places SET image = ? WHERE placeId=?";
            stmt = conn.prepareStatement(updateDBQuery);
            stmt.setBytes(1, imageBytes);
            stmt.setInt(2, placeId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                status = true;
                if (log.isDebugEnabled()) {
                    log.debug("Image updated for place Id "+placeId);
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

    public File getPlacePlan(int placeId){

        boolean status = false;
        Connection conn = null;
        PreparedStatement stmt = null;
        FileOutputStream fos = null;
        File file = null;

        try {
            conn = PlaceDAOHandler.getConnection();
            String updateDBQuery = "SELECT image FROM places WHERE placeId=?";
            stmt = conn.prepareStatement(updateDBQuery);
            stmt.setInt(1, placeId);
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

    public boolean removePlace(int placeId) {
        boolean check = false;
        Connection conn;
        PreparedStatement stmt = null;

        try {
            conn = PlaceDAOHandler.getConnection();
            String query = "DELETE from places WHERE placeId=?";
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, placeId);
            check = stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DeviceTypeUtils.cleanupResources(stmt, null);
        }
        return check;
    }
}