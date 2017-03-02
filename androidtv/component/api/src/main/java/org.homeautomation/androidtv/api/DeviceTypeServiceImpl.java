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

package org.homeautomation.androidtv.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.homeautomation.androidtv.api.constants.AndroidTVConstants;
import org.homeautomation.androidtv.api.util.APIUtil;
import org.homeautomation.androidtv.api.util.AndroidConfiguration;
import org.wso2.carbon.device.mgt.common.*;
import org.wso2.carbon.device.mgt.common.authorization.DeviceAccessAuthorizationException;
import org.wso2.carbon.device.mgt.common.configuration.mgt.ConfigurationManagementException;
import org.wso2.carbon.device.mgt.common.group.mgt.DeviceGroupConstants;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.core.operation.mgt.CommandOperation;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * This is the API which is used to control and manage device type functionality
 */
public class DeviceTypeServiceImpl implements DeviceTypeService {
    private static Log log = LogFactory.getLog(DeviceTypeService.class);

    /**
     * Play video on Android TV with given URL.
     * @param deviceId The registered device Id.
     * @return
     */
    @POST
    @Path("device/{deviceId}/video")
    public Response playVideo(@PathParam("deviceId") String deviceId, @QueryParam("url") String url) {
        try {
            if (!APIUtil.getDeviceAccessAuthorizationService().isUserAuthorized(new DeviceIdentifier(deviceId,
                    AndroidTVConstants.DEVICE_TYPE), DeviceGroupConstants.Permissions.DEFAULT_OPERATOR_PERMISSIONS)) {
                return Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).build();
            }
            String publishTopic = APIUtil.getAuthenticatedUserTenantDomain()
                    + "/" + AndroidTVConstants.DEVICE_TYPE + "/" + deviceId + "/command/video";

            Operation commandOp = new CommandOperation();
            commandOp.setCode("video");
            commandOp.setType(Operation.Type.COMMAND);
            commandOp.setEnabled(true);
            commandOp.setPayLoad(URLEncoder.encode(url, "UTF-8"));

            Properties props = new Properties();
            props.setProperty(AndroidTVConstants.MQTT_ADAPTER_TOPIC_PROPERTY_NAME, publishTopic);
            commandOp.setProperties(props);

            List<DeviceIdentifier> deviceIdentifiers = new ArrayList<>();
            deviceIdentifiers.add(new DeviceIdentifier(deviceId, AndroidTVConstants.DEVICE_TYPE));
            APIUtil.getDeviceManagementService().addOperation(AndroidTVConstants.DEVICE_TYPE, commandOp,
                    deviceIdentifiers);
            return Response.ok().build();
        } catch (InvalidDeviceException | UnsupportedEncodingException e) {
            String msg = "Invalid Device Identifiers found.";
            log.error(msg, e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (DeviceAccessAuthorizationException e) {
            log.error(e.getErrorMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } catch (OperationManagementException e) {
            log.error("Error occurred while executing command operation to remove words", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Display message in Android TV device.
     * @param deviceId : The registered device id.
     * @return
     */
    @POST
    @Path("device/{deviceId}/message")
    public Response sendMessage(@PathParam("deviceId") String deviceId, @QueryParam("message") String message) {
        try {
            if (!APIUtil.getDeviceAccessAuthorizationService().isUserAuthorized(new DeviceIdentifier(deviceId,
                    AndroidTVConstants.DEVICE_TYPE), DeviceGroupConstants.Permissions.DEFAULT_OPERATOR_PERMISSIONS)) {
                return Response.status(Response.Status.UNAUTHORIZED.getStatusCode()).build();
            }
            String publishTopic = APIUtil.getAuthenticatedUserTenantDomain()
                    + "/" + AndroidTVConstants.DEVICE_TYPE + "/" + deviceId + "/command/message";

            Operation commandOp = new CommandOperation();
            commandOp.setCode("message");
            commandOp.setType(Operation.Type.COMMAND);
            commandOp.setEnabled(true);
            commandOp.setPayLoad(message);

            Properties props = new Properties();
            props.setProperty(AndroidTVConstants.MQTT_ADAPTER_TOPIC_PROPERTY_NAME, publishTopic);
            commandOp.setProperties(props);

            List<DeviceIdentifier> deviceIdentifiers = new ArrayList<>();
            deviceIdentifiers.add(new DeviceIdentifier(deviceId, AndroidTVConstants.DEVICE_TYPE));
            APIUtil.getDeviceManagementService().addOperation(AndroidTVConstants.DEVICE_TYPE, commandOp,
                    deviceIdentifiers);
            return Response.ok().build();
        } catch (InvalidDeviceException e) {
            String msg = "Invalid Device Identifiers found.";
            log.error(msg, e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (DeviceAccessAuthorizationException e) {
            log.error(e.getErrorMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();
        } catch (OperationManagementException e) {
            log.error("Error occurred while executing command operation to remove words", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Path("device/{device_id}/register")
    @POST
    public Response register(@PathParam("device_id") String deviceId, @QueryParam("deviceName") String deviceName) {
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setId(deviceId);
        deviceIdentifier.setType(AndroidTVConstants.DEVICE_TYPE);
        try {
            if (APIUtil.getDeviceManagementService().isEnrolled(deviceIdentifier)) {
                AndroidConfiguration androidConfiguration = new AndroidConfiguration();
                androidConfiguration.setTenantDomain(APIUtil.getAuthenticatedUserTenantDomain());
                androidConfiguration.setMqttEndpoint(APIUtil.getMqttEndpoint());
                return Response.status(Response.Status.ACCEPTED.getStatusCode()).entity(androidConfiguration.toString())
                        .build();
            }
            Device device = new Device();
            device.setDeviceIdentifier(deviceId);
            EnrolmentInfo enrolmentInfo = new EnrolmentInfo();
            enrolmentInfo.setDateOfEnrolment(new Date().getTime());
            enrolmentInfo.setDateOfLastUpdate(new Date().getTime());
            enrolmentInfo.setStatus(EnrolmentInfo.Status.ACTIVE);
            device.setName(deviceName);
            device.setType(AndroidTVConstants.DEVICE_TYPE);
            enrolmentInfo.setOwner(APIUtil.getAuthenticatedUser());
            enrolmentInfo.setOwnership(EnrolmentInfo.OwnerShip.BYOD);
            device.setEnrolmentInfo(enrolmentInfo);
            boolean added = APIUtil.getDeviceManagementService().enrollDevice(device);
            if (added) {
                AndroidConfiguration androidConfiguration = new AndroidConfiguration();
                androidConfiguration.setTenantDomain(APIUtil.getAuthenticatedUserTenantDomain());
                androidConfiguration.setMqttEndpoint(APIUtil.getMqttEndpoint());
                        return Response.ok(androidConfiguration.toString()).build();
            } else {
                return Response.status(Response.Status.NOT_ACCEPTABLE.getStatusCode()).entity(false).build();
            }
        } catch (DeviceManagementException e) {
            log.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(false).build();
        } catch (ConfigurationManagementException e) {
            log.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(false).build();
        }
    }
}