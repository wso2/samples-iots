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

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is the API which is used to control and manage building data
 */
@Path("building")
public class BuildingServiceImpl implements BuildingService {

    private static final String KEY_TYPE = "PRODUCTION";
    private static Log log = LogFactory.getLog(BuildingServiceImpl.class);
    BuildingPluginDAOManager manager = new BuildingPluginDAOManager();
    BuildingPluginDAO building = manager.getDeviceDAO();

    @POST
    @Produces("application/json")
    @Override
    public Response addBuilding(BuildingInfo building){
        try {
            int id;
            building.setOwner(PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername());
            id = this.building.addBuilding(building);
            if (id!=0){
                return Response.status(Response.Status.OK).entity(id).build();
            }else{
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
            }
        }catch (Exception e){
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }
    }

    @POST
    @Path("/{buildingId}/upload")
    @Consumes("multipart/form-data")
    @Produces("application/json")
    @Override
    public Response uploadBuildingImage(@PathParam("buildingId") int buildingId, InputStream fileInputStream, Attachment fileDetail){

        boolean status = false ;

        try {
            String fileName = fileDetail.getContentDisposition().getParameter("filename");
            byte[] imageBytes = IOUtils.toByteArray(fileInputStream);
            status = building.updateBuildingImage(buildingId, imageBytes);

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
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }
    }

    @POST
    @Path("/{buildingId}")
    @Produces("application/json")
    @Override
    public Response addFloor(@PathParam("buildingId") int buildingId, FloorInfo floor){
        try {
            boolean status;
            floor.setBuildingId(buildingId);
            status = this.building.addFloor(floor);
            if (status){
                return Response.status(Response.Status.OK).entity(floor.getFloorNum()).build();
            }else{
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
            }
        }catch (Exception e){
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        }
    }

    @Override
    @Path("/{buildingId}/download")
    @GET
    @Produces("image/*")
    public Response downloadImage(@PathParam("buildingId") int buildingId) {
        try {

            File file = building.getBuildingImage(buildingId);

            if (file!=null){
                Response.ResponseBuilder response = Response.ok((Object) file);
                response.status(Response.Status.OK);
                response.type("image/*");
                response.header("Content-Disposition",
                        "attachment; filename=image_from_server.jpg");
                return response.build();

            }else{
                Response.ResponseBuilder response = Response.status(500);
                return response.build();
            }

        } catch (IllegalArgumentException ex) {
            return Response.status(400).entity(ex.getMessage()).build();//bad request
        }
    }

    @POST
    @Path("/{buildingId}/{floorId}/upload")
    @Consumes("multipart/form-data")
    @Produces("application/json")
    @Override
    public Response uploadFloorPlan(@PathParam("buildingId") int buildingId,@PathParam("floorId") int floorId, InputStream fileInputStream, Attachment fileDetail){

        boolean status = false ;

        try {
            String fileName = fileDetail.getContentDisposition().getParameter("filename");
            byte[] imageBytes = IOUtils.toByteArray(fileInputStream);
            status = building.updateFloorPlan(buildingId,floorId, imageBytes);

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