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

package org.wso2.iot.alertme.api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.wso2.carbon.apimgt.annotations.api.Scope;
import org.wso2.carbon.apimgt.annotations.api.Scopes;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
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
                                @ExtensionProperty(name = "name", value = "alertme"),
                                @ExtensionProperty(name = "context", value = "/alertme"),
                        })
                }
        ),
        tags = {
                @Tag(name = "alertme", description = "")
        }
)
@Scopes(
        scopes = {
                @Scope(
                        name = "Enroll device",
                        description = "",
                        key = "perm:alertme:enroll",
                        permissions = {"/device-mgt/devices/enroll/alertme"}
                )
        }
)
@SuppressWarnings("NonJaxWsWebServices")
public interface DeviceTypeService {
    String SCOPE = "scope";

    /**
     * @param deviceId  unique identifier for given device type instance
     * @param state     change status of sensor: on/off
     */
    @Path("device/{deviceId}/change-status")
    @POST
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Switch Status",
            notes = "",
            response = Response.class,
            tags = "alertme",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:alertme:enroll")
                    })
            }
    )
    Response changeStatus(@PathParam("deviceId") String deviceId,
                          @QueryParam("state") String state,
                          @Context HttpServletResponse response);


    /**
     * @param deviceId  unique identifier for given device type instance
     * @param alertfrom device id to get alerts from
     */
    @Path("device/{deviceId}/getalerts")
    @POST
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "getalertsfrom",
            notes = "",
            response = Response.class,
            tags = "alertme",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:alertme:enroll")
                    })
            }
    )
    Response getAlerts(@PathParam("deviceId") String deviceId, @QueryParam("alertfrom") String alertfrom,@Context HttpServletResponse response);



    /**
     * @param deviceId  unique identifier for given device type instance
     * @param range     range for the sensor
     */
    @Path("device/{deviceId}/setrange")
    @POST
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Switch Status",
            notes = "",
            response = Response.class,
            tags = "alertme",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:alertme:enroll")
                    })
            }
    )
    Response setRange(@PathParam("deviceId") String deviceId,
                          @QueryParam("range") String range,
                          @Context HttpServletResponse response);




    /**
     * @param deviceId  unique identifier for given device type instance
     * @param alerttype     type of the alert: motor/bulb
     * @param duration     alert duration : 10 Seconds
     */
    @Path("device/{deviceId}/alert")
    @POST
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Alert alertme",
            notes = "",
            response = Response.class,
            tags = "alertme",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:alertme:enroll")
                    })
            }
    )
    Response alert(@PathParam("deviceId") String deviceId,
                          @QueryParam("alerttype") String alerttype,
                          @QueryParam("duration") String duration,
                          @Context HttpServletResponse response);





    /**
     * Retrieve Sensor data for the given time period
     * @param deviceId unique identifier for given device type instance
     * @param from  starting time
     * @param to    ending time
     * @return  response with List<SensorRecord> object which includes sensor data which is requested
     */
    @Path("device/stats/{deviceId}")
    @GET
    @Consumes("application/json")
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Sensor Stats",
            notes = "",
            response = Response.class,
            tags = "alertme",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:alertme:enroll")
                    })
            }
    )
    Response getSensorStats(@PathParam("deviceId") String deviceId, @QueryParam("from") long from,
                            @QueryParam("to") long to, @QueryParam("sensorType") String sensorType);



    /**
     * Retrieve Sensor data for the given time period
     * @param deviceId unique identifier for given device type instance
     * @return  response with is device worn or not
     */
    @Path("device/isworn/{deviceId}")
    @GET
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Is Worn",
            notes = "",
            response = Response.class,
            tags = "alertme",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:alertme:enroll")
                    })
            }
    )
    Response isworn(@PathParam("deviceId") String deviceId);



    /**
     * To download device type agent source code as zip file
     * @param deviceName   name for the device type instance
     * @param sketchType   folder name where device type agent was installed into server
     * @return  Agent source code as zip file
     */
    @Path("/device/download")
    @GET
    @Produces("application/zip")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Download agent",
            notes = "",
            response = Response.class,
            tags = "alertme",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:alertme:enroll")
                    })
            }
    )
    Response downloadSketch(@QueryParam("deviceName") String deviceName, @QueryParam("sketchType") String sketchType);
}