/*
* Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.homeautomation.garbagebin.api;

import io.swagger.annotations.*;
import org.wso2.carbon.apimgt.annotations.api.Scope;
import org.wso2.carbon.apimgt.annotations.api.Scopes;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This is the controller API which is used to control agent side functionality
 */

@SwaggerDefinition(
        info = @Info(
                version = "1.0.0",
                title = "",
                extensions = {
                        @Extension(properties = {
                                @ExtensionProperty(name = "name", value = "garbagebin"),
                                @ExtensionProperty(name = "context", value = "/garbagebin"),
                        })
                }
        ),
        tags = {
                @Tag(name = "garbagebin,device_management", description = "")
        }
)
@Scopes(
        scopes = {
                @Scope(
                        name = "Enroll device",
                        description = "",
                        key = "perm:garbagebin:enroll",
                        permissions = {"/device-mgt/devices/enroll/garbagebin"}
                )
        }
)
public interface DeviceTypeService {

    @Path("device/{deviceId}/change-levels")
    @POST
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Change Levels",
            notes = "",
            response = Response.class,
            tags = "garbagebin",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = "scope", value = "perm:garbagebin:enroll")
                    })
            }
    )
    Response updateConfigs(@PathParam("deviceId") String deviceId,
                           @QueryParam("max_height") int maxLevel,
                           @QueryParam("sensor_height") int sensorHeight,
                           @Context HttpServletResponse response);

    /**
     * Retrieve Sensor data for the given time period
     *
     * @param deviceId   unique identifier for given device type instance
     * @param sensorName name of the sensor
     * @param from       starting time
     * @param to         ending time
     * @return response with List<SensorRecord> object which includes sensor data which is requested
     */
    @Path("device/stats/{deviceId}/sensors/{sensorName}")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Retreive Sensor data for the device type",
            notes = "",
            response = Response.class,
            tags = "raspberrypi",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = "scope", value = "perm:garbagebin:enroll")
                    })
            }
    )
    Response getSensorStats(@PathParam("deviceId") String deviceId, @PathParam("sensorName") String sensorName,
                            @QueryParam("from") long from, @QueryParam("to") long to);

    @Path("/device/{device_id}")
    @DELETE
    Response removeDevice(@PathParam("device_id") String deviceId);

    @Path("/device/{device_id}")
    @PUT
    Response updateDevice(@PathParam("device_id") String deviceId, @QueryParam("name") String name);

    @Path("/device/{device_id}")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response getDevice(@PathParam("device_id") String deviceId);

    @Path("/devices")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Response getAllDevices();


    /**
     * download the agent.
     */
    @Path("device/download")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Download the agent.",
            notes = "",
            response = Response.class,
            tags = "garbagebin",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = "scope", value = "perm:garbagebin:enroll")
                    })
            }
    )
    Response downloadSketch(@QueryParam("deviceName") String deviceName, @QueryParam("sketch_type") String sketchType);
}
