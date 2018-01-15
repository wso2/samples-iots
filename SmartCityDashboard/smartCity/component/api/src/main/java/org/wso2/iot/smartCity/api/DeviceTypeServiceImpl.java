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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.analytics.dataservice.commons.SortByField;
import org.wso2.carbon.analytics.dataservice.commons.SortType;
import org.wso2.carbon.analytics.datasource.commons.exception.AnalyticsException;
import org.wso2.carbon.apimgt.application.extension.APIManagementProviderService;
import org.wso2.carbon.apimgt.application.extension.dto.ApiApplicationKey;
import org.wso2.carbon.apimgt.application.extension.exception.APIManagerException;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.DeviceNotFoundException;
import org.wso2.carbon.device.mgt.common.EnrolmentInfo;
import org.wso2.carbon.device.mgt.common.authorization.DeviceAccessAuthorizationException;
import org.wso2.carbon.device.mgt.common.group.mgt.DeviceGroup;
import org.wso2.carbon.device.mgt.common.group.mgt.GroupManagementException;
import org.wso2.carbon.device.mgt.core.service.GroupManagementProviderService;
import org.wso2.carbon.identity.jwt.client.extension.JWTClient;
import org.wso2.carbon.identity.jwt.client.extension.dto.AccessTokenInfo;
import org.wso2.carbon.identity.jwt.client.extension.exception.JWTClientException;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.iot.smartCity.api.constants.DeviceTypeConstants;
import org.wso2.iot.smartCity.api.dto.DeviceJSON;
import org.wso2.iot.smartCity.api.dto.PlaceDevices;
import org.wso2.iot.smartCity.api.dto.SensorRecord;
import org.wso2.iot.smartCity.api.dto.TokenInfo;
import org.wso2.iot.smartCity.api.exception.DeviceTypeException;
import org.wso2.iot.smartCity.api.util.APIUtil;


import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.core.Context;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.EnrolmentInfo;
import org.wso2.carbon.device.mgt.common.InvalidDeviceException;

import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.core.operation.mgt.CommandOperation;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.Properties;

/**
 * This is the API which is used to control and manage device type functionality
 */
@Path("device")
@SuppressWarnings("NonJaxWsWebServices")
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
        if (agentInfo.deviceId != null) {
            return Response.status(Response.Status.OK).build();
        }
        return Response.status(Response.Status.NOT_ACCEPTABLE).build();
    }


    /**
     * To download device type agent source code as zip file
     *
     * @param placeDevices name for the device type instance
     * @return the response for the enrollment
     */
    @Path("/enroll")
    @POST
    @Produces("application/json")
    public Response partialEnrollment(PlaceDevices placeDevices, @QueryParam("deviceType") String deviceType) {

        boolean status = partialRegister(placeDevices, deviceType);
        List<DeviceIdentifier> deviceIdentifierList = new ArrayList<>();
        String placeId = null;

        if (status) {

            try {
                DeviceIdentifier deviceIdentifier = new DeviceIdentifier(placeDevices.getDeviceId(), deviceType);
                deviceIdentifierList.add(deviceIdentifier);
                Device device = APIUtil.getDeviceManagementService().getDevice(deviceIdentifier);

                if (device != null) {
                    List<Device.Property> deviceProperties = device.getProperties();
                    for (Device.Property p : deviceProperties) {
                        if (p.getName().contains(DeviceTypeConstants.PLACE_ID)) {
                            placeId = p.getValue();
                        }
                    }
                } else {
                    log.error("Device for device identifier " + deviceIdentifier.getId() + " is not found.");
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
                }

                if (placeId != null) {
                    addDeviceToGroups(placeId, deviceIdentifierList);
                } else {
                    addDeviceToDefaultGroup(deviceIdentifierList);
                }
                return Response.status(Response.Status.OK).build();
            } catch (DeviceManagementException e) {
                log.error(e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
            } catch (DeviceTypeException e) {
                log.error("Error occured while adding the device " + placeDevices.getDeviceId() + " to the place", e);
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
     * @return the response to the device
     */
    @Path("/enrollme")
    @POST
    @Produces("application/text")
    public Response enrollDevice(@QueryParam("deviceId") String deviceId, @QueryParam("deviceType") String deviceType) {
        try {
            DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
            deviceIdentifier.setId(deviceId);
            deviceIdentifier.setType(deviceType);
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
                            String[] tags = {deviceType};
                            apiApplicationKey = apiManagementProviderService.generateAndRetrieveApplicationKeys(
                                    deviceType, tags, KEY_TYPE, applicationUsername, true,
                                    "3600");
                        }
                        JWTClient jwtClient = APIUtil.getJWTClientManagerService().getJWTClient();
                        String scopes = "device_type_" + deviceType + " device_" + deviceId;
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
                            String[] tags = {deviceType};
                            apiApplicationKey = apiManagementProviderService.generateAndRetrieveApplicationKeys(
                                    deviceType, tags, KEY_TYPE, applicationUsername, true,
                                    "3600");
                        }
                        JWTClient jwtClient = APIUtil.getJWTClientManagerService().getJWTClient();
                        String scopes = "device_type_" + deviceType + " device_" + deviceId;
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
     * @param placeDevices name for the device type instance
     * @return check whether device is installed into cdmf
     */
    private boolean partialRegister(PlaceDevices placeDevices, String deviceType) {
        try {
            DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
            deviceIdentifier.setId(placeDevices.getDeviceId());
            deviceIdentifier.setType(deviceType);
            if (APIUtil.getDeviceManagementService().isEnrolled(deviceIdentifier)) {
                return false;
            }
            Device device = new Device();
            device.setDeviceIdentifier(placeDevices.getDeviceId());
            EnrolmentInfo enrolmentInfo = new EnrolmentInfo();
            enrolmentInfo.setDateOfEnrolment(new Date().getTime());
            enrolmentInfo.setDateOfLastUpdate(new Date().getTime());
            enrolmentInfo.setStatus(EnrolmentInfo.Status.CREATED);
            enrolmentInfo.setOwnership(EnrolmentInfo.OwnerShip.BYOD);
            enrolmentInfo.setOwner(PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername());
            device.setEnrolmentInfo(enrolmentInfo);
            List<Device.Property> properties = new ArrayList<>();

            Device.Property placeId = new Device.Property();
            placeId.setName(DeviceTypeConstants.PLACE_ID);
            placeId.setValue(placeDevices.getPlaceId());

            Device.Property xCoordinate = new Device.Property();
            xCoordinate.setName(DeviceTypeConstants.X_COORDINATE);
            xCoordinate.setValue(placeDevices.getxCord());

            Device.Property yCoordinate = new Device.Property();
            yCoordinate.setName(DeviceTypeConstants.Y_COORDINATE);
            yCoordinate.setValue(placeDevices.getyCord());

            Device.Property deviceTypes = new Device.Property();
            deviceTypes.setName(DeviceTypeConstants.DEVICE_TYPE);
            deviceTypes.setValue(placeDevices.getDeviceType());

            properties.add(placeId);
            properties.add(xCoordinate);
            properties.add(yCoordinate);
            properties.add(deviceTypes);

            device.setProperties(properties);
            device.setName(placeDevices.getDeviceId());
            device.setType(deviceType);
            enrolmentInfo.setOwner(APIUtil.getAuthenticatedUser());
            device.setEnrolmentInfo(enrolmentInfo);
            return APIUtil.getDeviceManagementService().enrollDevice(device);
        } catch (DeviceManagementException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * Add devices to place groups which the particular device is in
     *
     * @param placeId        : The building Id which the device is enrolled.
     * @param deviceIdentifiers : List of device ids to be added to the device group.
     * @throws DeviceTypeException Device type exception
     */
    private void addDeviceToGroups(String placeId, List<DeviceIdentifier> deviceIdentifiers)
            throws DeviceTypeException {
        DeviceGroup placeDeviceGroup;
        GroupManagementProviderService groupManagementProviderService = APIUtil.getGroupManagementProviderService();

        try {
            if ((placeDeviceGroup = groupManagementProviderService
                    .getGroup(String.format(DeviceTypeConstants.PLACE_GROUP_NAME, placeId))) != null) {
                groupManagementProviderService.addDevices(placeDeviceGroup.getGroupId(), deviceIdentifiers);
            }
        } catch (GroupManagementException e) {
            throw new DeviceTypeException("Cannot add the device to the place group of " + placeId, e);
        } catch (DeviceNotFoundException e) {
            throw new DeviceTypeException("Device " + deviceIdentifiers.get(0).getId() + " cannot be found.", e);
        }

    }





    /**
     * Add devices to default group which the particular device is in
     *
     * @param deviceIdentifiers : List of device ids to be added to the device group.
     * @throws DeviceTypeException Device type exception
     */
    private void addDeviceToDefaultGroup(List<DeviceIdentifier> deviceIdentifiers)
            throws DeviceTypeException {
        GroupManagementProviderService groupManagementProviderService = APIUtil.getGroupManagementProviderService();

        try {
            String placeGroupName = String.format(DeviceTypeConstants.PLACE_GROUP_NAME, 0);
            if (groupManagementProviderService.getGroup(placeGroupName) == null) {
                String placeRole = String.format(DeviceTypeConstants.PLACE_ROLE, 0);
                APIUtil.addRolesForPlaces(placeRole);
                APIUtil.createAndAddGroups(placeGroupName, placeRole,
                                           "Group for locations");
            }
            DeviceGroup placeDeviceGroup = groupManagementProviderService.getGroup(placeGroupName);
            groupManagementProviderService.addDevices(placeDeviceGroup.getGroupId(), deviceIdentifiers);
        } catch (GroupManagementException e) {
            throw new DeviceTypeException("Cannot add the device to the default ", e);
        } catch (DeviceNotFoundException e) {
            throw new DeviceTypeException("Device " + deviceIdentifiers.get(0).getId() + " cannot be found.", e);
        } catch (UserStoreException e) {
            throw new DeviceTypeException("Cannot add user role for place.", e);
        }
    }

}
