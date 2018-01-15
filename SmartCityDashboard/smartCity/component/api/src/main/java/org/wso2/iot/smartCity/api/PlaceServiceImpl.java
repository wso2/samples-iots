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

package org.wso2.iot.smartCity.api;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.analytics.dataservice.commons.SortByField;
import org.wso2.carbon.analytics.dataservice.commons.SortType;
import org.wso2.carbon.analytics.datasource.commons.exception.AnalyticsException;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.*;
import org.wso2.carbon.device.mgt.common.group.mgt.DeviceGroup;
import org.wso2.carbon.device.mgt.common.group.mgt.GroupAlreadyExistException;
import org.wso2.carbon.device.mgt.common.group.mgt.GroupManagementException;
import org.wso2.carbon.device.mgt.common.group.mgt.RoleDoesNotExistException;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.core.operation.mgt.ProfileOperation;
import org.wso2.carbon.device.mgt.core.service.GroupManagementProviderService;
import org.wso2.carbon.user.api.Permission;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;

import org.wso2.iot.smartCity.api.constants.DeviceTypeConstants;
import org.wso2.iot.smartCity.api.dao.PlacePluginDAO;
import org.wso2.iot.smartCity.api.dao.PlacePluginDAOManager;
import org.wso2.iot.smartCity.api.dto.*;
import org.wso2.iot.smartCity.api.exception.DeviceTypeException;
import org.wso2.iot.smartCity.api.util.APIUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This is the API which is used to control and manage place data
 */
@Path("place")
@SuppressWarnings("NonJaxWsWebServices")
public class PlaceServiceImpl implements PlaceService {

    private static Log log = LogFactory.getLog(PlaceServiceImpl.class);
    private PlacePluginDAOManager placeDAOManager = new PlacePluginDAOManager();
    private PlacePluginDAO placeDAO = placeDAOManager.getDeviceDAO();

    @POST
    @Produces("application/json")
    @Override
    public Response addPlace(PlaceInfo place) {
        try {
            int id;
            placeDAOManager.getPlaceDAOHandler().beginTransaction();
            id = this.placeDAO.addPlace(place);
            placeDAOManager.getPlaceDAOHandler().commitTransaction();
            if (id != 0) {
                String placeRole = String.format(DeviceTypeConstants.PLACE_ROLE, id);
                String placeGroupName = String.format(DeviceTypeConstants.PLACE_GROUP_NAME, id);
                APIUtil.addRolesForPlaces(placeRole);
                APIUtil.createAndAddGroups(placeGroupName, placeRole, "Group for the " + id);
                return Response.status(Response.Status.OK).entity(id).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
            }
        } catch (UserStoreException e) {
            log.error("Cannot add the place " + place.getPlaceId(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } catch (DeviceTypeException e) {
            log.error("Cannot create the group for the place " + place.getPlaceId(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } finally {
            placeDAOManager.getPlaceDAOHandler().closeConnection();
        }
    }

    @GET
    @Produces("application/json")
    @Override
    public Response getRegisteredPlaces() {
        try {
            GroupManagementProviderService groupManagementProviderService = APIUtil.getGroupManagementProviderService();
            placeDAOManager.getPlaceDAOHandler().openConnection();
            List<PlaceInfo> placeList = this.placeDAO.getAllPlaces();
            List<DeviceGroup> userGroups = groupManagementProviderService
                    .getGroups(PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername());
            List<PlaceInfo> authorizedPlaces = new ArrayList<>();

            if (isAdmin()) {
                authorizedPlaces = placeList;
            } else {
                for (PlaceInfo place : placeList) {
                    String placeGroupName = String
                            .format(DeviceTypeConstants.PLACE_GROUP_NAME, place.getPlaceId());
                    if (isUserAuthorizedToGroup(userGroups, placeGroupName)) {
                        authorizedPlaces.add(place);
                    }
                }
            }
            return Response.status(Response.Status.OK).entity(authorizedPlaces).build();

        } catch (Exception e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } finally {
            placeDAOManager.getPlaceDAOHandler().closeConnection();
        }
    }

    @POST
    @Path("/update")
    @Produces("application/json")
    @Consumes("application/json")
    @Override
    public Response updatePlace(PlaceInfo placeInfo) {
        PlaceInfo place;
        try {
            String placeGroupName = String.format(DeviceTypeConstants.PLACE_GROUP_NAME, placeInfo.getPlaceId());
            GroupManagementProviderService groupManagementProviderService = APIUtil.getGroupManagementProviderService();
            List<DeviceGroup> userGroups = groupManagementProviderService
                    .getGroups(PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername()) ;

            if (isAdmin() || isUserAuthorizedToGroup(userGroups, placeGroupName)) {
                placeDAOManager.getPlaceDAOHandler().openConnection();
                placeDAOManager.getPlaceDAOHandler().beginTransaction();
                place = this.placeDAO.updatePlace(placeInfo);
                placeDAOManager.getPlaceDAOHandler().commitTransaction();

                if (place != null) {
                    return Response.status(Response.Status.OK).entity(place).build();
                } else {
                    return Response.status(Response.Status.NOT_FOUND).entity("Relevant Place with the id " +
                            placeInfo.getPlaceId() + " is not found.").build();
                }
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized to view group " +
                        placeGroupName + " for Place " + placeInfo.getPlaceId()).build();
            }
        } catch (SQLException e) {
            placeDAOManager.getPlaceDAOHandler().rollbackTransaction();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (GroupManagementException e) {
            log.error("Error checking group level authorizations for updating the Place " + placeInfo
                    .getPlaceId(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getCause()).build();

        } finally {
            placeDAOManager.getPlaceDAOHandler().closeConnection();
        }
    }


    @GET
    @Path("/{placeId}")
    @Produces("application/json")
    @Override
    public Response getRegisteredPlace(@PathParam("placeId") int placeId) {
        try {
            String placeGroupName = String.format(DeviceTypeConstants.PLACE_GROUP_NAME, placeId);
            GroupManagementProviderService groupManagementProviderService = APIUtil.getGroupManagementProviderService();
            List<DeviceGroup> userGroups = groupManagementProviderService
                    .getGroups(PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername());
            if (isAdmin() || isUserAuthorizedToGroup(userGroups, placeGroupName)) {
                placeDAOManager.getPlaceDAOHandler().openConnection();
                PlaceInfo placeInfo = this.placeDAO.getPlace(placeId);
                if (placeInfo == null) {
                    return Response.status(Response.Status.NO_CONTENT).build();
                }
                return Response.status(Response.Status.OK).entity(placeInfo).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } finally {
            placeDAOManager.getPlaceDAOHandler().closeConnection();
        }
    }

    @Override
    @Path("/{placeId}/getPlan")
    @GET
    @Produces("image/*")
    public Response getPlacePlan(@PathParam("placeId") int placeId) {
        try {
            String placeGroupName = String.format(DeviceTypeConstants.PLACE_GROUP_NAME, placeId);
            GroupManagementProviderService groupManagementProviderService = APIUtil.getGroupManagementProviderService();
            List<DeviceGroup> userGroups = groupManagementProviderService
                    .getGroups(PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername());

            if (isAdmin() || isUserAuthorizedToGroup(userGroups, placeGroupName)) {
                placeDAOManager.getPlaceDAOHandler().openConnection();
                File file = placeDAO.getPlacePlan(placeId);
                if (file != null) {
                    Response.ResponseBuilder response = Response.ok(file);
                    response.status(Response.Status.OK);
                    response.type("image/*");
                    response.header("Content-Disposition", "attachment; filename=image_from_server.jpg");
                    return response.build();
                } else {
                    Response.ResponseBuilder response = Response.status(Response.Status.NO_CONTENT);
                    return response.build();
                }
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } catch (GroupManagementException e) {
            log.error("Error while getting the group details in building " + placeId);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } finally {
            placeDAOManager.getPlaceDAOHandler().closeConnection();
        }
    }

    @POST
    @Path("/{placeId}/addPlan")
    @Consumes("multipart/form-data")
    @Produces("application/json")
    @Override
    public Response addImage(@PathParam("placeId") int placeId,
            InputStream fileInputStream, Attachment fileDetail) {

        boolean status;
        try {
            String placeGroupName = String.format(DeviceTypeConstants.PLACE_GROUP_NAME, placeId);
            GroupManagementProviderService groupManagementProviderService = APIUtil.getGroupManagementProviderService();
            List<DeviceGroup> userGroups = groupManagementProviderService
                    .getGroups(PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername()) ;
            byte[] imageBytes = IOUtils.toByteArray(fileInputStream);

            if (isAdmin() || isUserAuthorizedToGroup(userGroups, placeGroupName)) {
                placeDAOManager.getPlaceDAOHandler().openConnection();
                placeDAOManager.getPlaceDAOHandler().beginTransaction();
                status = this.placeDAO.updatePlacePlan(placeId, imageBytes);
                placeDAOManager.getPlaceDAOHandler().commitTransaction();

                if (status) {
                    return Response.status(Response.Status.OK.getStatusCode()).build();
                } else {
                    return Response.status(Response.Status.NOT_FOUND).entity("Relevant Place with the id " +
                            placeId + " is not found.").build();
                }
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized to view group " +
                        placeGroupName + " for Place " + placeId).build();
            }
        } catch (IOException e) {
            log.error("Error occured while adding image " + placeId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } catch (SQLException e) {
            placeDAOManager.getPlaceDAOHandler().rollbackTransaction();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (GroupManagementException e) {
            log.error("Error checking group level authorizations for updating the Place " + placeId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getCause()).build();

        } finally {
            placeDAOManager.getPlaceDAOHandler().closeConnection();
        }
    }

    @Path("/test")
    @GET
    @Produces("application/json")
    @Override
    public Response test() {
        try {
            String msg = "API works well";
            return Response.status(Response.Status.OK.getStatusCode()).entity(msg).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(e.getMessage())
                    .build();
        }
    }


    @Override
    @POST
    @Path("/remove/{placeId}")
    public Response removePlace(int placeId) {
        boolean res;
        GroupManagementProviderService groupManagementProviderService;
        List<DeviceGroup> userGroups = null;

        try {
            groupManagementProviderService = APIUtil.getGroupManagementProviderService();
            userGroups = groupManagementProviderService
                    .getGroups(PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername());
        } catch (GroupManagementException e) {
            log.error("Error while checking group level permissions before deleting the place " + placeId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        try {
            if (isAdmin() || isUserAuthorizedToGroup(userGroups,
                    String.format(DeviceTypeConstants.PLACE_GROUP_NAME, placeId))) {
                placeDAOManager.getPlaceDAOHandler().openConnection();
                placeDAOManager.getPlaceDAOHandler().beginTransaction();
                res = placeDAO.removePlace(placeId);
                placeDAOManager.getPlaceDAOHandler().commitTransaction();
                return res ?
                        Response.status(Response.Status.OK).build() :
                        Response.status(Response.Status.OK).entity("Could not delete the Place.").build();
            } else {
                Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } catch (SQLException e) {
            placeDAOManager.getPlaceDAOHandler().rollbackTransaction();
            log.error("Error while performing database operations while deleting the Place " + placeId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } finally {
            placeDAOManager.getPlaceDAOHandler().closeConnection();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }


    @Override
    @Path("/isExistingPlace/{placeId}")
    @GET
    @Produces("application/json")
    public Response isExistingPlace(@PathParam("placeId") int placeId) {
        try {
            placeDAOManager.getPlaceDAOHandler().openConnection();
            PlaceInfo placeInfo = this.placeDAO.getPlace(placeId);

            if (placeInfo != null) {
                String msg= "Place exists";
                return Response.status(Response.Status.OK).entity(msg).build();
            } else {
                String msg= "Place does not exist";
                return Response.status(Response.Status.NOT_FOUND).entity(msg).build();
            }
        } catch (SQLException e) {
            log.error("Error occured while checking whether a place with the id " + placeId + " exist.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getCause()).build();
        }

    }


    @Override
    @Path("/devices")
    @GET
    @Produces("application/json")
    public Response getDevicesForUser() {
        try {
            GroupManagementProviderService groupManagementProviderService = APIUtil.getGroupManagementProviderService();
            List<DeviceInfo> deviceInfos = new ArrayList<>();
            placeDAOManager.getPlaceDAOHandler().openConnection();
            List<PlaceInfo> placeList = this.placeDAO.getAllPlaces();
            List<DeviceGroup> userGroups = groupManagementProviderService
                    .getGroups(PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername()) ;
            for (PlaceInfo placeId : placeList) {
                String groupName = String.format(DeviceTypeConstants.PLACE_GROUP_NAME, placeId.getPlaceId());

                if (isAdmin() || isUserAuthorizedToGroup(userGroups, groupName)) {
                    DeviceGroup DeviceGroup = APIUtil.getGroupManagementProviderService().getGroup(groupName);
                    if (DeviceGroup != null) {
                        DeviceInfo deviceInfo = new DeviceInfo("" + placeId.getPlaceId());
                        List<Device> devices = APIUtil.getGroupManagementProviderService()
                                .getDevices(DeviceGroup.getGroupId(), 0, 1000);
                        for (Device device : devices) {

                            device = APIUtil.getDeviceManagementService()
                                    .getDevice(new DeviceIdentifier(device.getDeviceIdentifier(), device.getType()));
                            List<Device.Property> propertyList = device.getProperties();
                            if (device.getEnrolmentInfo().getStatus() == EnrolmentInfo.Status.ACTIVE) {
                                deviceInfo.increaseActive();
                                for (Device.Property property : propertyList) {
                                    switch (property.getName()) {
                                    case "lastKnown":
                                        if (property.getValue() != null) {
                                            long timestamp = Long.parseLong(property.getValue());
                                            if ((System.currentTimeMillis() - timestamp) / 1000 > 3600) {
                                                deviceInfo.increaseFault();
                                                deviceInfo.decreaseActive();
                                            }
                                        }

                                    }
                                }
                            } else {
                                deviceInfo.increaseInactive();
                            }
                        }
                        deviceInfos.add(deviceInfo);
                    }
                }
            }
            if (deviceInfos.size() > 0) {
                return Response.status(Response.Status.OK).entity(deviceInfos).build();
            } else {
                return Response.status(Response.Status.NO_CONTENT).entity(deviceInfos).build();
            }
        } catch (GroupManagementException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(e.getMessage())
                    .build();
        } catch (DeviceManagementException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(e.getMessage())
                    .build();
        } catch (SQLException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(e.getMessage())
                    .build();
        } finally {
            placeDAOManager.getPlaceDAOHandler().closeConnection();
        }
    }

    @Override
    @Path("/{placeId}/devices")
    @GET
    @Produces("application/json")
    public Response getDevicesForPlace(@PathParam("placeId") int placeId) {
        try {
            List<DeviceInfo> deviceInfos = new ArrayList<>();


                String groupName = String.format(DeviceTypeConstants.PLACE_GROUP_NAME, placeId);
                DeviceGroup placeDeviceGroup = APIUtil.getGroupManagementProviderService().getGroup(groupName);
                if (placeDeviceGroup != null) {
                    DeviceInfo deviceInfo = new DeviceInfo("" + placeId);
                    List<Device> devices = APIUtil.getGroupManagementProviderService()
                            .getDevices(placeDeviceGroup.getGroupId(), 0, 1000);
                    for (Device device : devices) {

                        device = APIUtil.getDeviceManagementService()
                                .getDevice(new DeviceIdentifier(device.getDeviceIdentifier(), device.getType()));
                        List<Device.Property> propertyList = device.getProperties();
                        if (device.getEnrolmentInfo().getStatus() == EnrolmentInfo.Status.ACTIVE) {
                            deviceInfo.increaseActive();
                            for (Device.Property property : propertyList) {
                                switch (property.getName()) {
                                case "lastKnown":
                                    if (property.getValue() != null) {
                                        long timestamp = Long.parseLong(property.getValue());
                                        if ((System.currentTimeMillis() - timestamp) / 1000 > 3600) {
                                            deviceInfo.increaseFault();
                                            deviceInfo.decreaseActive();
                                        }
                                    }

                                }
                            }
                        } else {
                            deviceInfo.increaseInactive();
                        }
                    }
                    deviceInfos.add(deviceInfo);
                }

            if (deviceInfos.size() > 0) {
                return Response.status(Response.Status.OK).entity(deviceInfos).build();
            } else {
                return Response.status(Response.Status.NO_CONTENT).entity(deviceInfos).build();
            }
        } catch (GroupManagementException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(e.getMessage())
                    .build();
        } catch (DeviceManagementException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(e.getMessage())
                    .build();
        }
    }


    @Override
    @Path("/{placeId}/devicesDetails")
    @GET
    @Produces("application/json")
    public Response getDevicesForPlaceDetails(@PathParam("placeId") int placeId) {
        try {
            String groupName = String.format(DeviceTypeConstants.PLACE_GROUP_NAME, placeId);
            GroupManagementProviderService groupManagementProviderService = APIUtil.getGroupManagementProviderService();
            DeviceGroup placeDeviceGroup = groupManagementProviderService.getGroup(groupName);
            List<PlaceDevices> placeDevices = new ArrayList<>();
            if (placeDeviceGroup == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            List<DeviceGroup> userGroups = groupManagementProviderService
                    .getGroups(PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername());
            if (isAdmin() || isUserAuthorizedToGroup(userGroups, groupName)) {

                List<Device> devices = APIUtil.getGroupManagementProviderService()
                        .getDevices(placeDeviceGroup.getGroupId(), 0, 1000);
                for (Device device : devices) {

                    device = APIUtil.getDeviceManagementService()
                            .getDevice(new DeviceIdentifier(device.getDeviceIdentifier(), device.getType()));
                    placeDevices.add(new PlaceDevices(device));
                }
                return Response.status(Response.Status.OK).entity(placeDevices).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } catch (GroupManagementException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(e.getMessage())
                    .build();
        } catch (DeviceManagementException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(e.getMessage())
                    .build();
        }
    }

    /**
     * Whether user is authorized to view a group.
     *
     * @param userGroups User Groups
     * @param groupName  Group Name
     * @return true if the user is authorized view the particular group unless false.
     */
    private boolean isUserAuthorizedToGroup(List<DeviceGroup> userGroups, String groupName) {
        for (DeviceGroup userGroup : userGroups) {
            if (userGroup.getName().equals(groupName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * To check whether current user is admin
     *
     * @return true if current user is admin, otherwise returns false;
     */
    private boolean isAdmin() {
        try {
            return APIUtil.getAuthorizationManager()
                    .isUserAuthorized(PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername(),
                            CarbonConstants.UI_ADMIN_PERMISSION_COLLECTION, CarbonConstants.UI_PERMISSION_ACTION);

        } catch (UserStoreException e) {
            log.error(e);
            return false;
        }
    }



 }