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
import org.wso2.carbon.CarbonConstants;
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
import org.wso2.iot.senseme.api.constants.DeviceTypeConstants;
import org.wso2.iot.senseme.api.dao.BuildingPluginDAO;
import org.wso2.iot.senseme.api.dao.BuildingPluginDAOManager;
import org.wso2.iot.senseme.api.dto.*;
import org.wso2.iot.senseme.api.exception.DeviceTypeException;
import org.wso2.iot.senseme.api.util.APIUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is the API which is used to control and manage building data
 */
@Path("building")
@SuppressWarnings("NonJaxWsWebServices")
public class BuildingServiceImpl implements BuildingService {

    private static Log log = LogFactory.getLog(BuildingServiceImpl.class);
    private BuildingPluginDAOManager buildingDAOManager = new BuildingPluginDAOManager();
    private BuildingPluginDAO buildingDAO = buildingDAOManager.getDeviceDAO();
    private static final String ANDROID_DEVICE_TYPE = "android";
    private static final String SENSEME_DEVICE_TYPE = "senseme";
    private static final String NOTIFICATION = "NOTIFICATION";


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
            if (id != 0) {
                String buildingRole = String.format(DeviceTypeConstants.BUILDING_ROLE, id);
                String buildingGroupName = String.format(DeviceTypeConstants.BUILDING_GROUP_NAME, id);
                addRolesForBuildingsAndFloors(buildingRole);
                createAndAddGroups(buildingGroupName, buildingRole, "Group for the " + id);
                return Response.status(Response.Status.OK).entity(id).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
            }
        } catch (UserStoreException e) {
            log.error("Cannot add the building " + building.getBuildingId(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } catch (DeviceTypeException e) {
            log.error("Cannot create the group for the building " + building.getBuildingId(), e);
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
            GroupManagementProviderService groupManagementProviderService = APIUtil.getGroupManagementProviderService();
            buildingDAOManager.getBuildingDAOHandler().openConnection();
            List<BuildingInfo> buildingList = this.buildingDAO.getAllBuildings();
            List<DeviceGroup> userGroups = groupManagementProviderService
                    .getGroups(PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername()) ;
            List<BuildingInfo> authorizedBuildings = new ArrayList<>();

            for (BuildingInfo building : buildingList) {
                String buildingGroupName = String
                        .format(DeviceTypeConstants.BUILDING_GROUP_NAME, building.getBuildingId());
                if (isUserAuthorizedToGroup(userGroups, buildingGroupName)) {
                    authorizedBuildings.add(building);
                }
            }
            return Response.status(Response.Status.OK).entity(authorizedBuildings).build();

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
        BuildingInfo building;
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
    @Path("/{buildingId}/floors")
    @Produces("application/json")
    @Override
    public Response getAvailableFloors(@PathParam("buildingId") int buildingId) {
        try {

            buildingDAOManager.getBuildingDAOHandler().openConnection();
            List<Integer> floorNums = buildingDAO.getAvailableFloors(buildingId);
            return Response.status(Response.Status.OK).entity(floorNums).build();
        } catch (SQLException e) {
            log.error(e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } finally {
            buildingDAOManager.getBuildingDAOHandler().closeConnection();
        }
    }

    @GET
    @Path("/{buildingId}")
    @Produces("application/json")
    @Override
    public Response getRegisteredBuilding(@PathParam("buildingId") int buildingId){
        try {
            String buildingGroupName = String.format(DeviceTypeConstants.BUILDING_GROUP_NAME, buildingId);
            GroupManagementProviderService groupManagementProviderService = APIUtil.getGroupManagementProviderService();
            List<DeviceGroup> userGroups = groupManagementProviderService
                    .getGroups(PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername()) ;
            if (isUserAuthorizedToGroup(userGroups, buildingGroupName)) {
                buildingDAOManager.getBuildingDAOHandler().openConnection();
                BuildingInfo buildingInfo = this.buildingDAO.getBuilding(buildingId);
                if (buildingInfo == null) {
                    return Response.status(Response.Status.NO_CONTENT).build();
                }
                return Response.status(Response.Status.OK).entity(buildingInfo).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
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
            String floorGroupName = String.format(DeviceTypeConstants.FLOOR_GROUP_NAME, buildingId, floorId);
            GroupManagementProviderService groupManagementProviderService = APIUtil.getGroupManagementProviderService();
            List<DeviceGroup> userGroups = groupManagementProviderService
                    .getGroups(PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername()) ;

            if (isUserAuthorizedToGroup(userGroups, floorGroupName)) {
                buildingDAOManager.getBuildingDAOHandler().openConnection();
                File file = buildingDAO.getFloorPlan(buildingId, floorId);
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
            log.error("Error while getting the group details for the floor " + floorId + " in building " + buildingId);
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
        boolean status;

        try {
            FloorInfo floor = new FloorInfo();
            floor.setBuildingId(buildingId);
            floor.setFloorNum(floorId);

            byte[] imageBytes = IOUtils.toByteArray(fileInputStream);
            buildingDAOManager.getBuildingDAOHandler().openConnection();
            buildingDAOManager.getBuildingDAOHandler().beginTransaction();
            status = buildingDAO.insertFloorDetails(buildingId, floorId, imageBytes);
            buildingDAOManager.getBuildingDAOHandler().commitTransaction();

            if (status) {
                String floorRole = String.format(DeviceTypeConstants.FLOOR_ROLE, buildingId, floorId);
                String floorGroupName = String.format(DeviceTypeConstants.FLOOR_GROUP_NAME, buildingId, floorId);
                addRolesForBuildingsAndFloors(floorRole);
                createAndAddGroups(floorGroupName, floorRole,
                        "Group for floor " + floorId + " in the building " + buildingId);
                return Response.status(Response.Status.OK.getStatusCode()).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
            }
        } catch (IOException e) {
            log.error("Error occured while adding floor " + floorId + " to building " + buildingId, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } catch (SQLException e) {
            log.error("Cannot add the floor " + floorId + " in building " + buildingId + ". SQL exceptions has "
                    + "occurred.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } catch (UserStoreException e) {
            log.error("Cannot add the floor " + floorId + " in building " + buildingId + ". Use store exceptions has"
                    + " occurred.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } catch (DeviceTypeException e) {
            log.error("Cannot create group for the floor " + floorId + " in building " + buildingId + ". Group "
                    + "already exists.", e);
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

    @Override
    @Path("/{buildingId}/{floorId}/devices")
    @GET
    @Produces("application/json")
    public Response getDevices(@PathParam("buildingId") int buildingId, @PathParam("floorId") int floorId) {
        try {
            String groupName = String.format(DeviceTypeConstants.FLOOR_GROUP_NAME, buildingId, floorId);
            DeviceGroup floorDeviceGroup = APIUtil.getGroupManagementProviderService().getGroup(groupName);
            List<SenseMe> senseMes = new ArrayList<>();
            if (floorDeviceGroup != null) {
                List<Device> devices = APIUtil.getGroupManagementProviderService().getDevices(
                        floorDeviceGroup.getGroupId(),
                        0, 1000);
                for (Device device : devices) {
                    if (!device.getType().equals(SENSEME_DEVICE_TYPE)) {
                        continue;
                    }
                    if (device.getEnrolmentInfo().getStatus() != EnrolmentInfo.Status.ACTIVE &&
                            device.getEnrolmentInfo().getStatus() != EnrolmentInfo.Status.INACTIVE) {
                        continue;
                    }
                    device = APIUtil.getDeviceManagementService().getDevice(new DeviceIdentifier(
                            device.getDeviceIdentifier(), device.getType()));
                    senseMes.add(new SenseMe(device));
                }
            }
            return Response.status(Response.Status.OK).entity(senseMes).build();
        } catch (GroupManagementException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(e.getMessage()).build();
        } catch (DeviceManagementException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(e.getMessage()).build();
        }
    }

    @Override
    @Path("/notification")
    @POST
    @Produces("application/json")
    public Response sendNotifications(AlertMessage alertMessage) {
        try {
            int buildingId = Integer.parseInt(alertMessage.getBuildingId());
            int floorId = Integer.parseInt(alertMessage.getFloorId());
            String groupName = String.format(DeviceTypeConstants.FLOOR_GROUP_NAME, buildingId, floorId);
            DeviceGroup floorDeviceGroup = APIUtil.getGroupManagementProviderService().getGroup(groupName);
            if (floorDeviceGroup != null) {
                List<Device> devices = APIUtil.getGroupManagementProviderService().getDevices(
                        floorDeviceGroup.getGroupId(),
                        0, 1000);
                List<DeviceIdentifier> androidDevices = new ArrayList<>();
                for (Device device : devices) {
                    if (device.getType().equals(ANDROID_DEVICE_TYPE)) {
                        androidDevices.add(new DeviceIdentifier(device.getDeviceIdentifier(), device.getType()));
                    }
                }

                if (androidDevices.size() > 0) {
                    Notification notification = new Notification(alertMessage);
                    ProfileOperation operation = new ProfileOperation();
                    operation.setCode(NOTIFICATION);
                    operation.setType(Operation.Type.PROFILE);
                    operation.setPayLoad(notification.toJSON());
                    APIUtil.getDeviceManagementService().addOperation(ANDROID_DEVICE_TYPE, operation, androidDevices);
                }
            }
            return Response.status(Response.Status.OK).build();
        } catch (GroupManagementException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(e.getMessage()).build();
        } catch (OperationManagementException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(e.getMessage()).build();
        } catch (InvalidDeviceException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(e.getMessage()).build();
        }
    }

    @Override
    @POST
    @Path("/remove/{deviceId}")
    public Response removeBuilding(int buildingId) {
        boolean res;
        try {
            buildingDAOManager.getBuildingDAOHandler().openConnection();
            buildingDAOManager.getBuildingDAOHandler().beginTransaction();
            res = buildingDAO.removeBuilding(buildingId);
            buildingDAOManager.getBuildingDAOHandler().commitTransaction();
            return res ? Response.status(Response.Status.OK).build():Response.status(Response.Status.OK).entity
                    ("Could not delete the building.").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } finally {
            buildingDAOManager.getBuildingDAOHandler().closeConnection();
        }
    }

    @Override
    @POST
    @Path("/search/notifications")
    public Response queryNotifications(QueryObject query) {
        String querys = "";
        return null;
    }

    @Override
    @Path("/devices")
    @GET
    @Produces("application/json")
    public Response getDevicesForUser() {
        try {
            List<DeviceInfo> deviceInfos = new ArrayList<>();

            buildingDAOManager.getBuildingDAOHandler().openConnection();
            List<BuildingInfo> buildingList = this.buildingDAO.getAllBuildings();
            for (BuildingInfo buildingId : buildingList) {
                String groupName = String.format(DeviceTypeConstants.BUILDING_GROUP_NAME, buildingId.getBuildingId());
                DeviceGroup floorDeviceGroup = APIUtil.getGroupManagementProviderService().getGroup(groupName);
                if (floorDeviceGroup != null) {
                    DeviceInfo deviceInfo = new DeviceInfo("" +buildingId.getBuildingId());
                    List<Device> devices = APIUtil.getGroupManagementProviderService().getDevices(
                            floorDeviceGroup.getGroupId(),
                            0, 1000);
                    for (Device device : devices) {
                        if (!device.getType().equals(SENSEME_DEVICE_TYPE)) {
                            continue;
                        }
                        if (device.getEnrolmentInfo().getStatus() != EnrolmentInfo.Status.ACTIVE ||
                                device.getEnrolmentInfo().getStatus() != EnrolmentInfo.Status.INACTIVE) {
                            continue;
                        }
                        device = APIUtil.getDeviceManagementService().getDevice(new DeviceIdentifier(
                                device.getDeviceIdentifier(), device.getType()));
                        List<Device.Property> propertyList = device.getProperties();
                        if (device.getEnrolmentInfo().getStatus() == EnrolmentInfo.Status.ACTIVE) {
                            deviceInfo.increaseActive();
                            for (Device.Property property : propertyList) {
                                switch (property.getName()) {
                                    case "lastKnown":
                                        if (property.getValue() != null) {
                                            long timestamp = Long.parseLong(property.getValue());
                                            if ((System.currentTimeMillis() - timestamp)/1000 > 3600) {
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
            if (deviceInfos.size() > 0) {
                return Response.status(Response.Status.OK).entity(deviceInfos).build();
            } else {
                return Response.status(Response.Status.NO_CONTENT).entity(deviceInfos).build();
            }
        } catch (GroupManagementException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(e.getMessage()).build();
        } catch (DeviceManagementException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(e.getMessage()).build();
        } catch (SQLException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(e.getMessage()).build();
        } finally {
            buildingDAOManager.getBuildingDAOHandler().closeConnection();
        }
    }


    @Override
    @Path("/{buildingId}/devices")
    @GET
    @Produces("application/json")
    public Response getDevicesForFloor(@PathParam("buildingId") int buildingId) {
        try {
            List<DeviceInfo> deviceInfos = new ArrayList<>();

            buildingDAOManager.getBuildingDAOHandler().openConnection();
            List<Integer> floorNums = buildingDAO.getAvailableFloors(buildingId);
            for (int floorId : floorNums) {
                String groupName = String.format(DeviceTypeConstants.FLOOR_GROUP_NAME, buildingId, floorId);
                DeviceGroup floorDeviceGroup = APIUtil.getGroupManagementProviderService().getGroup(groupName);
                if (floorDeviceGroup != null) {
                    DeviceInfo deviceInfo = new DeviceInfo("" +floorId);
                    List<Device> devices = APIUtil.getGroupManagementProviderService().getDevices(
                            floorDeviceGroup.getGroupId(),
                            0, 1000);
                    for (Device device : devices) {
                        if (!device.getType().equals(SENSEME_DEVICE_TYPE)) {
                            continue;
                        }
                        if (device.getEnrolmentInfo().getStatus() != EnrolmentInfo.Status.ACTIVE ||
                                device.getEnrolmentInfo().getStatus() != EnrolmentInfo.Status.INACTIVE) {
                            continue;
                        }
                        device = APIUtil.getDeviceManagementService().getDevice(new DeviceIdentifier(
                                device.getDeviceIdentifier(), device.getType()));
                        List<Device.Property> propertyList = device.getProperties();
                        if (device.getEnrolmentInfo().getStatus() == EnrolmentInfo.Status.ACTIVE) {
                            deviceInfo.increaseActive();
                            for (Device.Property property : propertyList) {
                                switch (property.getName()) {
                                    case "lastKnown":
                                        if (property.getValue() != null) {
                                            long timestamp = Long.parseLong(property.getValue());
                                            if ((System.currentTimeMillis() - timestamp)/1000 > 3600) {
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
            if (deviceInfos.size() > 0) {
                return Response.status(Response.Status.OK).entity(deviceInfos).build();
            } else {
                return Response.status(Response.Status.NO_CONTENT).entity(deviceInfos).build();
            }
        } catch (GroupManagementException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(e.getMessage()).build();
        } catch (DeviceManagementException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(e.getMessage()).build();
        } catch (SQLException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(e.getMessage()).build();
        } finally {
            buildingDAOManager.getBuildingDAOHandler().closeConnection();
        }
    }

    /**
     * Create user role for building and floors.
     * @param role : Role that need to be created
     * @throws UserStoreException User Store Exception
     */
    private void addRolesForBuildingsAndFloors(String role) throws UserStoreException {
        Permission realTimeAnalytics = new Permission(DeviceTypeConstants.REALTIME_ANALYTICS_PERMISSION, CarbonConstants
                .UI_PERMISSION_ACTION);

        UserStoreManager userStoreManager = APIUtil.getUserStoreManager();
        if (userStoreManager != null) {
            if (!userStoreManager.isExistingRole(role)) {
                userStoreManager.addRole(role, null, new Permission[]{realTimeAnalytics});
            }
        } else {
            log.error("User Store Manager cannot found.");
        }
    }

    /**
     * Create device groups for building and floor and assign the given list of devices.
     *
     * @param groupName:  The name of the group
     * @param role        : The role associated with the group
     * @param description : The description for the group
     * @throws DeviceTypeException Device Type Exception
     */
    private void createAndAddGroups(String groupName, String role, String description) throws DeviceTypeException {
        try {
            DeviceGroup buildingFloorGroup;
            GroupManagementProviderService groupManagementProviderService = APIUtil.getGroupManagementProviderService();

            if (groupManagementProviderService.getGroup(groupName) != null) {
                return;
            }

            buildingFloorGroup = new DeviceGroup();
            buildingFloorGroup.setOwner(PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername());
            buildingFloorGroup.setName(groupName);
            buildingFloorGroup.setDescription(description);
            groupManagementProviderService.createGroup(buildingFloorGroup, role, new String[] { DeviceTypeConstants.REALTIME_ANALYTICS_PERMISSION });
            buildingFloorGroup = groupManagementProviderService.getGroup(groupName);
            groupManagementProviderService.manageGroupSharing(buildingFloorGroup.getGroupId(), new ArrayList<>(Arrays
                    .asList(role)));
        } catch (GroupManagementException e) {
            throw new DeviceTypeException("Error occurred while creting group with the name " + groupName, e);
        } catch (GroupAlreadyExistException e) {
            throw new DeviceTypeException("A group with the name " + groupName + " already exists.", e);
        } catch (RoleDoesNotExistException e) {
            throw new DeviceTypeException("A role with the name " + role + " does not exist", e);
        }
    }

    /**
     * Whether user is authorized to view a group.
     * @param userGroups User Groups
     * @param groupName Group Name
     * @return true if the user is authorized view the particular group unless false.
     */
    private boolean isUserAuthorizedToGroup(List<DeviceGroup> userGroups, String groupName) {
        for (DeviceGroup userGroup: userGroups) {
            if (userGroup.getName().equals(groupName)) {
                return true;
            }
        }
        return false;

    }
}