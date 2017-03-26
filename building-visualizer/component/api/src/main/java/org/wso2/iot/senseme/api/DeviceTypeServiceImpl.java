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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.analytics.dataservice.commons.SortByField;
import org.wso2.carbon.analytics.dataservice.commons.SortType;
import org.wso2.carbon.analytics.datasource.commons.exception.AnalyticsException;
import org.wso2.carbon.apimgt.application.extension.APIManagementProviderService;
import org.wso2.carbon.apimgt.application.extension.dto.ApiApplicationKey;
import org.wso2.carbon.apimgt.application.extension.exception.APIManagerException;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.*;
import org.wso2.carbon.device.mgt.common.authorization.DeviceAccessAuthorizationException;
import org.wso2.carbon.device.mgt.common.group.mgt.DeviceGroup;
import org.wso2.carbon.device.mgt.common.group.mgt.GroupAlreadyExistException;
import org.wso2.carbon.device.mgt.common.group.mgt.GroupManagementException;
import org.wso2.carbon.device.mgt.core.service.GroupManagementProviderService;
import org.wso2.carbon.identity.jwt.client.extension.JWTClient;
import org.wso2.carbon.identity.jwt.client.extension.dto.AccessTokenInfo;
import org.wso2.carbon.identity.jwt.client.extension.exception.JWTClientException;
import org.wso2.carbon.user.api.Permission;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.iot.senseme.api.constants.DeviceTypeConstants;
import org.wso2.iot.senseme.api.dto.DeviceJSON;
import org.wso2.iot.senseme.api.dto.SenseMe;
import org.wso2.iot.senseme.api.dto.SensorRecord;
import org.wso2.iot.senseme.api.dto.TokenInfo;
import org.wso2.iot.senseme.api.util.APIUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * This is the API which is used to control and manage device type functionality
 */
@Path("device")
public class DeviceTypeServiceImpl implements DeviceTypeService {

    private static final String KEY_TYPE = "PRODUCTION";
    private static Log log = LogFactory.getLog(DeviceTypeService.class);
    private static ApiApplicationKey apiApplicationKey;

    /**
     * @param agentInfo device owner,id
     * @return true if device instance is added to map
     */
    @Path("/register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerDevice(final DeviceJSON agentInfo) {
        if ((agentInfo.deviceId != null) && (agentInfo.owner != null)) {
            return Response.status(Response.Status.OK).build();
        }
        return Response.status(Response.Status.NOT_ACCEPTABLE).build();
    }

    /**
     * Retrieve Sensor data for the given time period
     *
     * @param deviceId unique identifier for given device type instance
     * @param from     starting time
     * @param to       ending time
     * @return response with List<SensorRecord> object which includes sensor data which is requested
     */
    @Path("/stats/{deviceId}")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    public Response getSensorStats(@PathParam("deviceId") String deviceId, @QueryParam("from") long from,
                                   @QueryParam("to") long to, @QueryParam("sensorType") String sensorType) {
        String fromDate = String.valueOf(from * 1000);
        String toDate = String.valueOf(to * 1000);
        String query = "deviceId:" + deviceId + " AND deviceType:" +
                       DeviceTypeConstants.DEVICE_TYPE + " AND time : [" + fromDate + " TO" +
                " " + toDate + "]";
        String sensorTableName = null;
        if (sensorType.equals(DeviceTypeConstants.SENSOR_TYPE_MOTION)) {
            sensorTableName = DeviceTypeConstants.MOTION_EVENT_TABLE;
        } else if (sensorType.equals(DeviceTypeConstants.SENSOR_TYPE_LIGHT)) {
            sensorTableName = DeviceTypeConstants.LIGHT_EVENT_TABLE;
        } else if (sensorType.equals(DeviceTypeConstants.SENSOR_TYPE_TEMPERATURE)) {
            sensorTableName = DeviceTypeConstants.TEMPERATURE_EVENT_TABLE;
        } else if (sensorType.equals(DeviceTypeConstants.SENSOR_TYPE_HUMIDITY)) {
            sensorTableName = DeviceTypeConstants.HUMIDITY_EVENT_TABLE;
        }
        try {
            if (!APIUtil.getDeviceAccessAuthorizationService().isUserAuthorized(new DeviceIdentifier(deviceId,
                                                                                                     DeviceTypeConstants.DEVICE_TYPE))) {
                return Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).build();
            }
            if (sensorTableName != null) {
                List<SortByField> sortByFields = new ArrayList<>();
                SortByField sortByField = new SortByField("time", SortType.ASC);
                sortByFields.add(sortByField);
                List<SensorRecord> sensorRecords = APIUtil.getAllEventsForDevice(sensorTableName, query, sortByFields);
                return Response.status(Response.Status.OK.getStatusCode()).entity(sensorRecords).build();
            }
        } catch (AnalyticsException e) {
            String errorMsg = "Error on retrieving stats on table " + sensorTableName + " with query " + query;
            log.error(errorMsg);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(errorMsg).build();
        } catch (DeviceAccessAuthorizationException e) {
            log.error(e.getErrorMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    /**
     * To download device type agent source code as zip file
     *
     * @param senseMe name for the device type instance
     * @return
     */
    @Path("/enroll")
    @POST
    @Produces("application/json")
    public Response partialEnrollment(SenseMe senseMe) {
        boolean status = partialRegister(senseMe);
        List<DeviceIdentifier> deviceIdentifierList = new ArrayList<>();

        String buildingId = null, floorId = null, floorRole, buildingRole;

        if (status) {

            try {
                DeviceIdentifier deviceIdentifier = new DeviceIdentifier(senseMe.getDeviceId(), DeviceTypeConstants
                        .DEVICE_TYPE);
                deviceIdentifierList.add(deviceIdentifier);
                Device device = APIUtil.getDeviceManagementService().getDevice(deviceIdentifier);

                if (device != null) {
                    List<Device.Property> deviceProperties = device.getProperties();
                    for (Device.Property p : deviceProperties) {
                        if (p.getName().contains(DeviceTypeConstants.BUILDING_ID)) {
                            buildingId = p.getValue();
                        } else if (p.getName().contains(DeviceTypeConstants.FLOOR_ID)) {
                            floorId = p.getValue();
                        }
                    }
                } else {
                    log.error("Device for device identifier " + deviceIdentifier.getId() + " is not found.");
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
                }

                if (floorId == null || buildingId == null) {
                    log.error("Building ID and Floor ID not found.");
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
                }

                buildingRole = String.format(DeviceTypeConstants.BUILDING_ROLE, buildingId);
                floorRole = String.format(DeviceTypeConstants.FLOOR_ROLE, buildingId, floorId);

                createAndAddRoles(buildingRole, floorRole);
                createAndAddGroups(device, buildingId, floorId, buildingRole, floorRole, deviceIdentifierList);

                return Response.status(Response.Status.OK).build();
            } catch (DeviceManagementException e) {
                log.error(e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
            } catch (GroupManagementException e) {
                log.error(e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
            } catch (GroupAlreadyExistException e) {
                log.error(e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
            } catch (DeviceNotFoundException e) {
                log.error(e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
            } catch (UserStoreException e) {
                log.error(e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }
    }

    /**
     * To download device type agent source code as zip file
     *
     * @param deviceId name for the device type instance
     * @return
     */
    @Path("/enrollme")
    @POST
    @Produces("application/text")
    public Response enrollDevice(@QueryParam("deviceId") String deviceId) {
        try {
            DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
            deviceIdentifier.setId(deviceId);
            deviceIdentifier.setType(DeviceTypeConstants.DEVICE_TYPE);
            if (APIUtil.getDeviceManagementService().isEnrolled(deviceIdentifier)) {
                Device device = APIUtil.getDeviceManagementService().getDevice(deviceIdentifier);
                if (device.getEnrolmentInfo().getStatus() != EnrolmentInfo.Status.ACTIVE) {
                    try {
                        PrivilegedCarbonContext.startTenantFlow();
                        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(MultitenantConstants
                                                                                                      .SUPER_TENANT_DOMAIN_NAME, true);
                        PrivilegedCarbonContext.getThreadLocalCarbonContext().setUsername(device.getEnrolmentInfo()
                                                                                                  .getOwner());
                        if (apiApplicationKey == null) {
                            String applicationUsername =
                                    PrivilegedCarbonContext.getThreadLocalCarbonContext().getUserRealm()
                                            .getRealmConfiguration().getAdminUserName();
                            applicationUsername =
                                    applicationUsername + "@" + APIUtil.getAuthenticatedUserTenantDomain();
                            APIManagementProviderService apiManagementProviderService =
                                    APIUtil.getAPIManagementProviderService();
                            String[] tags = {DeviceTypeConstants.DEVICE_TYPE};
                            apiApplicationKey = apiManagementProviderService.generateAndRetrieveApplicationKeys(
                                    DeviceTypeConstants.DEVICE_TYPE, tags, KEY_TYPE, applicationUsername, true,
                                    "3600");
                        }
                        JWTClient jwtClient = APIUtil.getJWTClientManagerService().getJWTClient();
                        String scopes = "device_type_" + DeviceTypeConstants.DEVICE_TYPE + " device_" + deviceId;
                        AccessTokenInfo accessTokenInfo = jwtClient.getAccessToken(apiApplicationKey.getConsumerKey()
                                , apiApplicationKey.getConsumerSecret(), device.getEnrolmentInfo().getOwner()
                                                                                           + "@" +
                                                                                           APIUtil.getAuthenticatedUserTenantDomain(),

                                                                                   scopes);

                        //create token
                        TokenInfo tokenInfo = new TokenInfo();
                        tokenInfo.setAccessToken(accessTokenInfo.getAccessToken());
                        tokenInfo.setRefreshToken(accessTokenInfo.getRefreshToken());
                        String appKey =
                                apiApplicationKey.getConsumerKey() + ":" + apiApplicationKey.getConsumerSecret();
                        tokenInfo.setAppKey(Base64.encodeBase64String(appKey.getBytes()));
                        //TODO smooth enrollment flow
                        EnrolmentInfo enrolmentInfo = device.getEnrolmentInfo();
                        enrolmentInfo.setStatus(EnrolmentInfo.Status.ACTIVE);
                        device.setEnrolmentInfo(enrolmentInfo);
                        APIUtil.getDeviceManagementService().modifyEnrollment(device);

                        return Response.status(Response.Status.OK.getStatusCode()).entity(tokenInfo.toString()).build();
                    } finally {
                        PrivilegedCarbonContext.endTenantFlow();
                    }
                } else {
                    //return Response.status(Response.Status.CONFLICT.getStatusCode()).build();

                    //handling conflict scenario
                    try {
                        PrivilegedCarbonContext.startTenantFlow();
                        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(MultitenantConstants
                                                                                                      .SUPER_TENANT_DOMAIN_NAME, true);
                        PrivilegedCarbonContext.getThreadLocalCarbonContext().setUsername(device.getEnrolmentInfo()
                                                                                                  .getOwner());
                        if (apiApplicationKey == null) {
                            String applicationUsername =
                                    PrivilegedCarbonContext.getThreadLocalCarbonContext().getUserRealm()
                                            .getRealmConfiguration().getAdminUserName();
                            applicationUsername =
                                    applicationUsername + "@" + APIUtil.getAuthenticatedUserTenantDomain();
                            APIManagementProviderService apiManagementProviderService =
                                    APIUtil.getAPIManagementProviderService();
                            String[] tags = {DeviceTypeConstants.DEVICE_TYPE};
                            apiApplicationKey = apiManagementProviderService.generateAndRetrieveApplicationKeys(
                                    DeviceTypeConstants.DEVICE_TYPE, tags, KEY_TYPE, applicationUsername, true,
                                    "3600");
                        }
                        JWTClient jwtClient = APIUtil.getJWTClientManagerService().getJWTClient();
                        String scopes = "device_type_" + DeviceTypeConstants.DEVICE_TYPE + " device_" + deviceId;
                        AccessTokenInfo accessTokenInfo = jwtClient.getAccessToken(apiApplicationKey.getConsumerKey()
                                , apiApplicationKey.getConsumerSecret(), device.getEnrolmentInfo().getOwner()
                                                                                           + "@" +
                                                                                           APIUtil.getAuthenticatedUserTenantDomain(),

                                                                                   scopes);

                        //create token
                        TokenInfo tokenInfo = new TokenInfo();
                        tokenInfo.setAccessToken(accessTokenInfo.getAccessToken());
                        tokenInfo.setRefreshToken(accessTokenInfo.getRefreshToken());
                        String appKey =
                                apiApplicationKey.getConsumerKey() + ":" + apiApplicationKey.getConsumerSecret();
                        tokenInfo.setAppKey(Base64.encodeBase64String(appKey.getBytes()));
                        return Response.status(Response.Status.OK.getStatusCode()).entity(tokenInfo.toString()).build();
                    } finally {
                        PrivilegedCarbonContext.endTenantFlow();
                    }
                }
            } else {
                return Response.status(Response.Status.NO_CONTENT.getStatusCode()).build();
            }

        } catch (DeviceManagementException e) {
            log.error("Failed to access device mgt service.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (JWTClientException e) {
            log.error("Failed to generate token", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (UserStoreException e) {
            log.error("Failed to access user stoer", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (APIManagerException e) {
            log.error("Failed to access apimgt service.", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Register device into device management service
     *
     * @param senseMe     name for the device type instance
     * @return check whether device is installed into cdmf
     */
    private boolean partialRegister(SenseMe senseMe) {
        try {
            DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
            deviceIdentifier.setId(senseMe.getDeviceId());
            deviceIdentifier.setType(DeviceTypeConstants.DEVICE_TYPE);
            if (APIUtil.getDeviceManagementService().isEnrolled(deviceIdentifier)) {
                return false;
            }
            Device device = new Device();
            device.setDeviceIdentifier(senseMe.getDeviceId());
            EnrolmentInfo enrolmentInfo = new EnrolmentInfo();
            enrolmentInfo.setDateOfEnrolment(new Date().getTime());
            enrolmentInfo.setDateOfLastUpdate(new Date().getTime());
            enrolmentInfo.setStatus(EnrolmentInfo.Status.CREATED);
            enrolmentInfo.setOwnership(EnrolmentInfo.OwnerShip.BYOD);
            enrolmentInfo.setOwner(PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername());
            device.setEnrolmentInfo(enrolmentInfo);
            List<Device.Property> properties = new ArrayList<>();

            Device.Property buildingId = new Device.Property();
            buildingId.setName(DeviceTypeConstants.BUILDING_ID);
            buildingId.setValue(senseMe.getBuildingId());

            Device.Property floorId = new Device.Property();
            floorId.setName(DeviceTypeConstants.FLOOR_ID);
            floorId.setValue(senseMe.getFloorNumber());

            Device.Property xCoordinate = new Device.Property();
            xCoordinate.setName(DeviceTypeConstants.X_COORDINATE);
            xCoordinate.setValue(senseMe.getxCord());

            Device.Property yCoordinate = new Device.Property();
            yCoordinate.setName(DeviceTypeConstants.Y_COORDINATE);
            yCoordinate.setValue(senseMe.getyCord());

            properties.add(buildingId);
            properties.add(floorId);
            properties.add(xCoordinate);
            properties.add(yCoordinate);

            device.setProperties(properties);
            device.setName(senseMe.getDeviceId());
            device.setType(DeviceTypeConstants.DEVICE_TYPE);
            enrolmentInfo.setOwner(APIUtil.getAuthenticatedUser());
            device.setEnrolmentInfo(enrolmentInfo);
            return APIUtil.getDeviceManagementService().enrollDevice(device);
        } catch (DeviceManagementException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    private Permission[] getPermissions() {
        Permission realTimeAnalytics = new Permission(DeviceTypeConstants.REALTIME_ANALYTICS_PERMISSION, CarbonConstants
                .UI_PERMISSION_ACTION);
        return new Permission[]{realTimeAnalytics};

    }

    /**
     * Create device groups for building and floor and assign the given list of devices.
     * @param device : Device object that is being enrolled.
     * @param buildingId : The building Id which the device is enrolled.
     * @param floorId : The floorId where the device is enrolled.
     * @param buildingRole : The role name of the building.
     * @param floorRole : The role name of the floor.
     * @param deviceIdentifiers : List of device ids to be added to the device group.
     * @throws GroupManagementException
     * @throws DeviceNotFoundException
     * @throws GroupAlreadyExistException
     */
    private void createAndAddGroups(Device device, String buildingId, String floorId, String buildingRole, String
            floorRole, List<DeviceIdentifier> deviceIdentifiers)
            throws GroupManagementException, DeviceNotFoundException, GroupAlreadyExistException {
        DeviceGroup buildingDeviceGroup, floorDeviceGroup;
        GroupManagementProviderService groupManagementProviderService = APIUtil
                .getGroupManagementProviderService();

        if ((buildingDeviceGroup = groupManagementProviderService.getGroup(String.format(DeviceTypeConstants
                .BUILDING_GROUP_NAME, buildingId))) != null) {
            groupManagementProviderService.addDevices(buildingDeviceGroup.getGroupId(), deviceIdentifiers);

        } else {
            buildingDeviceGroup = new DeviceGroup();
            buildingDeviceGroup.setName(String.format(DeviceTypeConstants.BUILDING_GROUP_NAME, buildingId));
            buildingDeviceGroup.setOwner(device.getEnrolmentInfo().getOwner());
            buildingDeviceGroup.setDescription("Device group for building id " + buildingId);
            groupManagementProviderService.createGroup(buildingDeviceGroup, buildingRole, new
                    String[]{DeviceTypeConstants.REALTIME_ANALYTICS_PERMISSION});
            buildingDeviceGroup = groupManagementProviderService.getGroup(String.format(DeviceTypeConstants
                    .BUILDING_GROUP_NAME, buildingId));
            groupManagementProviderService.addDevices(buildingDeviceGroup.getGroupId(), deviceIdentifiers);
        }

        if ((floorDeviceGroup = groupManagementProviderService.getGroup(String.format(DeviceTypeConstants
                .FLOOR_GROUP_NAME, buildingId, floorId))) != null) {
            groupManagementProviderService.addDevices(floorDeviceGroup.getGroupId(), deviceIdentifiers);

        } else {
            floorDeviceGroup = new DeviceGroup();
            floorDeviceGroup.setName(String.format(DeviceTypeConstants.FLOOR_GROUP_NAME, buildingId, floorId));
            floorDeviceGroup.setOwner(device.getEnrolmentInfo().getOwner());
            floorDeviceGroup.setDescription("Device group for floor " + floorId + "of building " + buildingId);
            groupManagementProviderService.createGroup(floorDeviceGroup, floorRole, new
                    String[]{DeviceTypeConstants.REALTIME_ANALYTICS_PERMISSION });
            floorDeviceGroup = groupManagementProviderService.getGroup(String.format(DeviceTypeConstants
                    .FLOOR_GROUP_NAME, buildingId, floorId));
            groupManagementProviderService.addDevices(floorDeviceGroup.getGroupId(), deviceIdentifiers);
        }
    }

    /**
     * Create user roles for building and floors.
     * @param buildingRole : Building role name which is to be created.
     * @param floorRole : Floor role name which is to be created.
     * @throws UserStoreException
     */
    private void createAndAddRoles(String buildingRole, String floorRole) throws
            UserStoreException {

        UserStoreManager userStoreManager = APIUtil.getUserStoreManager();
        if (userStoreManager != null) {
            if (!userStoreManager.isExistingRole(buildingRole)) {
                userStoreManager.addRole(buildingRole, null, getPermissions());
            }
            if (!userStoreManager.isExistingRole(floorRole)){
                userStoreManager.addRole(floorRole, null, getPermissions());
            }

        } else {
            log.error("User Store Manager cannot found.");
        }
    }
}
