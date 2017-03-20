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

package org.wso2.iot.senseme.api;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.iot.senseme.api.dao.BuildingPluginDAO;
import org.wso2.iot.senseme.api.dao.BuildingPluginDAOManager;
import org.wso2.iot.senseme.api.dto.BuildingInfo;
import org.wso2.iot.senseme.api.dto.FloorInfo;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

/**
 * This is the API which is used to control and manage building data
 */
@Path("building")
public class BuildingServiceImpl implements BuildingService {

    private static final String KEY_TYPE = "PRODUCTION";
    private static Log log = LogFactory.getLog(BuildingServiceImpl.class);
    BuildingPluginDAOManager buildingDAOManager = new BuildingPluginDAOManager();
    BuildingPluginDAO buildingDAO = buildingDAOManager.getDeviceDAO();

    @POST
    @Produces("application/json")
    @Override
    public Response addBuilding(BuildingInfo building){
        try {
            int id;
            building.setOwner(PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername());
            buildingDAOManager.getBuildingDAOHandler().beginTransaction();
            id = this.buildingDAO.addBuilding(building);
            buildingDAOManager.getBuildingDAOHandler().commitTransaction();
            if (id!=0){
                return Response.status(Response.Status.OK).entity(id).build();
            }else{
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
            }
        }catch (Exception e){
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } finally {
            buildingDAOManager.getBuildingDAOHandler().closeConnection();
        }
    }

    @GET
    @Produces("application/json")
    @Override
    public Response getRegisteredBuildings(){
        try {
            buildingDAOManager.getBuildingDAOHandler().openConnection();
            List<BuildingInfo> buildingList = this.buildingDAO.getAllBuildings();
            return Response.status(Response.Status.OK).entity(buildingList).build();

        }catch (Exception e){
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } finally {
            buildingDAOManager.getBuildingDAOHandler().closeConnection();
        }
    }

    @POST
    @Path("/update")
    @Produces("application/json")
    @Consumes("application/json")
    @Override
    public Response updateBuilding(BuildingInfo buildingInfo) {
        BuildingInfo building = null;
        try {
            buildingDAOManager.getBuildingDAOHandler().openConnection();
            buildingDAOManager.getBuildingDAOHandler().beginTransaction();
            building = this.buildingDAO.updateBuilding(buildingInfo);
            buildingDAOManager.getBuildingDAOHandler().commitTransaction();

            if (building != null) {
                return Response.status(Response.Status.OK).entity(building).build();
            }
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (SQLException e) {
            e.printStackTrace();
            buildingDAOManager.getBuildingDAOHandler().rollbackTransaction();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } finally {
            buildingDAOManager.getBuildingDAOHandler().closeConnection();
        }
    }

    @GET
    @Path("/{buildingId}")
    @Produces("application/json")
    @Override
    public Response getRegisteredBuildings(@PathParam("buildingId") int buildingId){
        try {
            buildingDAOManager.getBuildingDAOHandler().openConnection();
            BuildingInfo buildingInfo = this.buildingDAO.getBuilding(buildingId);
            if (buildingInfo == null) {
                return Response.status(Response.Status.NO_CONTENT).entity(buildingInfo).build();
            }
            return Response.status(Response.Status.OK).entity(buildingInfo).build();

        }catch (Exception e){
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } finally {
            buildingDAOManager.getBuildingDAOHandler().closeConnection();
        }
    }

    @Override
    @Path("/{buildingId}/{floorId}")
    @GET
    @Produces("image/*")
    public Response getFloorPlan(@PathParam("buildingId") int buildingId, @PathParam("floorId") int floorId) {
        try {
            buildingDAOManager.getBuildingDAOHandler().openConnection();
            File file = buildingDAO.getFloorPlan(buildingId, floorId);
            if (file != null) {
                Response.ResponseBuilder response = Response.ok((Object) file);
                response.status(Response.Status.OK);
                response.type("image/*");
                response.header("Content-Disposition",
                                "attachment; filename=image_from_server.jpg");
                return response.build();
            } else {
                Response.ResponseBuilder response = Response.status(Response.Status.NO_CONTENT);
                return response.build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } finally {
            buildingDAOManager.getBuildingDAOHandler().closeConnection();
        }
    }

    @POST
    @Path("/{buildingId}/{floorId}")
    @Consumes("multipart/form-data")
    @Produces("application/json")
    @Override
    public Response addFloor(@PathParam("buildingId") int buildingId, @PathParam("floorId") int floorId, InputStream fileInputStream, Attachment fileDetail){

        boolean status = false ;

        try {
            String fileName = fileDetail.getContentDisposition().getParameter("filename");

            FloorInfo floor = new FloorInfo();
            floor.setBuildingId(buildingId);
            floor.setFloorNum(floorId);

            byte[] imageBytes = IOUtils.toByteArray(fileInputStream);
            buildingDAOManager.getBuildingDAOHandler().openConnection();
            status = buildingDAO.insertFloorDetails(buildingId,floorId, imageBytes);

            if (status){
                return Response.status(Response.Status.OK.getStatusCode()).build();
            }else{
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
            }
        }catch (IOException e){
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }
        catch (Exception e){
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } finally {
            buildingDAOManager.getBuildingDAOHandler().closeConnection();
        }
    }

    @Path("/test")
    @GET
    @Produces("application/text")
    @Override
    public Response test(){
        try {
            String msg = "API works well";
            return Response.status(Response.Status.OK.getStatusCode()).entity(msg).build();
        }catch (Exception e){
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(e.getMessage()).build();        }
    }
}