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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.wso2.carbon.apimgt.annotations.api.Scope;
import org.wso2.carbon.apimgt.annotations.api.Scopes;
import org.wso2.iot.smartCity.api.dto.PlaceDevices;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * This is the API which is used to control and manage device type functionality
 */
@SwaggerDefinition(
        info = @Info(
                version = "1.0.0",
                title = "",
                extensions = {
                        @Extension(properties = {
                                @ExtensionProperty(name = "name", value = "smartCitydevice"),
                                @ExtensionProperty(name = "context", value = "/smartCity/device"),
                        })
                }
        ),
        tags = {
                @Tag(name = "smartCity, device_management", description = "")
        }
)
@Scopes(
        scopes = {
                @Scope(
                        name = "Enroll device",
                        description = "",
                        key = "perm:smartCity:enroll",
                        permissions = {"/device-mgt/devices/enroll/smartCity"}
                )
        }
)
@SuppressWarnings("NonJaxWsWebServices")
@Path("device")
public interface DeviceTypeService {
    String SCOPE = "scope";


    /**
     * To download device type agent source code as zip file
     *
     * @param senseMe for the device type instance
     * @param deviceType type of the device
     * @return Agent source code as zip file
     */
    @Path("/enroll")
    @POST
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Download agent",
            notes = "",
            response = Response.class,
            tags = "smartCity",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:smartCity:enroll")
                    })
            }
    )
    Response partialEnrollment(PlaceDevices placeDevices, @QueryParam("deviceType") String deviceType);



    /**
     * To download device type agent source code as zip file
     *
     * @param deviceId name for the device type instance
     * @return Agent source code as zip file
     */
    @Path("/enrollme")
    @POST
    @Produces("application/json")
    Response enrollDevice(@QueryParam("deviceId") String deviceId, @QueryParam("deviceType") String deviceType);


}