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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import io.swagger.annotations.Info;
import io.swagger.annotations.ResponseHeader;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.homeautomation.androidtv.api.constants.AndroidTVConstants;
import org.wso2.carbon.apimgt.annotations.api.Scope;
import org.wso2.carbon.apimgt.annotations.api.Scopes;

import javax.ws.rs.*;
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
                                @ExtensionProperty(name = "name", value = "androidtv"),
                                @ExtensionProperty(name = "context", value = "/androidtv"),
                        })
                }
        ),
        tags = {
                @Tag(name = "androidtv,device_management", description = "")
        }
)
@Scopes(
        scopes = {
                @Scope(
                        name = "Enroll device",
                        description = "",
                        key = "perm:androidtv:enroll",
                        permissions = {"/device-mgt/devices/enroll/androidtv"}
                )
        }
)
@SuppressWarnings("NonJaxWsWebServices")
public interface DeviceTypeService {
    /**
     * End point to send video to the device
     *
     * @param deviceId The registered device Id.
     */
    @POST
    @Path("device/{deviceId}/video")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "End point to send video to the android TV",
            notes = "",
            tags = "androidtv,device_management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidTVConstants.SCOPE, value = "perm:androidtv:enroll")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK.",
                    response = Response.class,
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Type",
                                    description = "The content type of the body"),
                            @ResponseHeader(
                                    name = "Last-Modified",
                                    description = "Date and time the resource was last modified.\n" +
                                            "Used by caches, or in conditional requests."),
                    }),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. \n Invalid Device Identifiers found.",
                    response = Response.class),
            @ApiResponse(
                    code = 401,
                    message = "Unauthorized. \n Unauthorized request."),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n Error occurred while executing command operation to"
                            + " send keywords",
                    response = Response.class)
    })
    Response playVideo(
            @ApiParam(
                    name = "deviceId",
                    value = "The registered device Id.",
                    required = true)
            @PathParam("deviceId") String deviceId,
            @ApiParam(
                    name = "url",
                    value = "Video url to be sent",
                    required = true)
            @QueryParam("url") String url);

    /**
     * End point to send message to Android TV device.
     */
    @POST
    @Path("device/{deviceId}/message")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Send message to Android tv",
            notes = "",
            response = Response.class,
            tags = "androidtv,device_management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidTVConstants.SCOPE, value = "perm:androidtv:enroll")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK.",
                    response = Response.class,
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Type",
                                    description = "The content type of the body"),
                            @ResponseHeader(
                                    name = "Last-Modified",
                                    description = "Date and time the resource was last modified.\n" +
                                            "Used by caches, or in conditional requests."),
                    }),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. \n Invalid Device Identifiers found.",
                    response = Response.class),
            @ApiResponse(
                    code = 401,
                    message = "Unauthorized. \n Unauthorized request."),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n Error occurred while executing command operation to"
                            + " send threashold",
                    response = Response.class)
    })
    Response sendMessage(
            @ApiParam(
                    name = "deviceId",
                    value = "The registered device Id.",
                    required = true)
            @PathParam("deviceId") String deviceId,
            @ApiParam(
                    name = "message",
                    value = "The message to be displayed.",
                    required = true)
            @QueryParam("message") String message);


    /**
     * End point to send a siddhi query to Android TV device.
     */
    @POST
    @Path("device/{deviceId}/edgeQuery")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Send siddhi query to Android tv",
            notes = "",
            response = Response.class,
            tags = "androidtv,device_management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidTVConstants.SCOPE, value = "perm:androidtv:enroll")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK.",
                    response = Response.class,
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Type",
                                    description = "The content type of the body"),
                            @ResponseHeader(
                                    name = "Last-Modified",
                                    description = "Date and time the resource was last modified.\n" +
                                            "Used by caches, or in conditional requests."),
                    }),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. \n Invalid Device Identifiers found.",
                    response = Response.class),
            @ApiResponse(
                    code = 401,
                    message = "Unauthorized. \n Unauthorized request."),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n Error occurred while executing command operation to"
                            + " send threashold",
                    response = Response.class)
    })
    Response sendEdgeQuery(
            @ApiParam(
                    name = "deviceId",
                    value = "The registered device Id.",
                    required = true)
            @PathParam("deviceId") String deviceId,
            @ApiParam(
                    name = "edgeQuery",
                    value = "The query to be send.",
                    required = true)
            @FormParam("edgeQuery") String edgeQuery);

    /**
     * End point to configure XBee gateway of Android TV device.
     */
    @POST
    @Path("device/{deviceId}/xbee-config")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Configure XBee gateway of Android TV device",
            notes = "",
            response = Response.class,
            tags = "androidtv,device_management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidTVConstants.SCOPE, value = "perm:androidtv:enroll")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK.",
                    response = Response.class,
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Type",
                                    description = "The content type of the body"),
                            @ResponseHeader(
                                    name = "Last-Modified",
                                    description = "Date and time the resource was last modified.\n" +
                                                  "Used by caches, or in conditional requests."),
                    }),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. \n Invalid Device Identifiers found.",
                    response = Response.class),
            @ApiResponse(
                    code = 401,
                    message = "Unauthorized. \n Unauthorized request."),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n Error occurred while executing command operation to"
                              + " send threshold",
                    response = Response.class)
    })
    Response configureXBeeDevice(
            @ApiParam(
                    name = "deviceId",
                    value = "The registered device Id.",
                    required = true)
            @PathParam("deviceId") String deviceId,
            @ApiParam(
                    name = "config-url",
                    value = "XBee configuration url for gateway device.",
                    required = true)
            @QueryParam("config-url") String url);


    /**
     * End point to Add XBee edge device to Android TV gateway.
     */
    @POST
    @Path("device/{deviceId}/xbee")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Add XBee edge device to Android TV gateway",
            notes = "",
            response = Response.class,
            tags = "androidtv,device_management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidTVConstants.SCOPE, value = "perm:androidtv:enroll")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK.",
                    response = Response.class,
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Type",
                                    description = "The content type of the body"),
                            @ResponseHeader(
                                    name = "Last-Modified",
                                    description = "Date and time the resource was last modified.\n" +
                                                  "Used by caches, or in conditional requests."),
                    }),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. \n Invalid Device Identifiers found.",
                    response = Response.class),
            @ApiResponse(
                    code = 401,
                    message = "Unauthorized. \n Unauthorized request."),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n Error occurred while executing command operation to"
                              + " send threshold",
                    response = Response.class)
    })
    Response addEdgeDevice(
            @ApiParam(
                    name = "deviceId",
                    value = "The registered device Id.",
                    required = true)
            @PathParam("deviceId") String deviceId,
            @ApiParam(
                    name = "serial",
                    value = "Serial number of the Xbee edge module.",
                    required = true)
            @QueryParam("serial") String serial,
            @ApiParam(
                    name = "name",
                    value = "Name of the Xbee edge module.",
                    required = true)
            @QueryParam("name") String name);

    /**
     * End point to Send command to XBee edge device connected with Android TV gateway.
     */
    @POST
    @Path("device/{deviceId}/xbee-command")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Send command to XBee edge device connected with Android TV gateway",
            notes = "",
            response = Response.class,
            tags = "androidtv,device_management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidTVConstants.SCOPE, value = "perm:androidtv:enroll")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK.",
                    response = Response.class,
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Type",
                                    description = "The content type of the body"),
                            @ResponseHeader(
                                    name = "Last-Modified",
                                    description = "Date and time the resource was last modified.\n" +
                                                  "Used by caches, or in conditional requests."),
                    }),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. \n Invalid Device Identifiers found.",
                    response = Response.class),
            @ApiResponse(
                    code = 401,
                    message = "Unauthorized. \n Unauthorized request."),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n Error occurred while executing command operation to"
                              + " send threshold",
                    response = Response.class)
    })
    Response sendCommandToEdgeDevice(
            @ApiParam(
                    name = "deviceId",
                    value = "The registered device Id.",
                    required = true)
            @PathParam("deviceId") String deviceId,
            @ApiParam(
                    name = "serial",
                    value = "Serial number of the Xbee edge module.",
                    required = true)
            @QueryParam("serial") String serial,
            @ApiParam(
                    name = "command",
                    value = "Command to be send",
                    required = true)
            @QueryParam("command") String command);

    /**
     * End point to remove XBee edge device to Android TV gateway.
     */
    @DELETE
    @Path("device/{deviceId}/xbee")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "DELETE",
            value = "Remove XBee edge device from Android TV gateway",
            notes = "",
            response = Response.class,
            tags = "androidtv,device_management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidTVConstants.SCOPE, value = "perm:androidtv:enroll")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK.",
                    response = Response.class,
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Type",
                                    description = "The content type of the body"),
                            @ResponseHeader(
                                    name = "Last-Modified",
                                    description = "Date and time the resource was last modified.\n" +
                                                  "Used by caches, or in conditional requests."),
                    }),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. \n Invalid Device Identifiers found.",
                    response = Response.class),
            @ApiResponse(
                    code = 401,
                    message = "Unauthorized. \n Unauthorized request."),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n Error occurred while executing command operation to"
                              + " send threshold",
                    response = Response.class)
    })
    Response removeEdgeDevice(
            @ApiParam(
                    name = "deviceId",
                    value = "The registered device Id.",
                    required = true)
            @PathParam("deviceId") String deviceId,
            @ApiParam(
                    name = "serial",
                    value = "Serial number of the Xbee edge module.",
                    required = true)
            @QueryParam("serial") String serial);

    /**
     * End point to get XBee edge devices attached to Android TV gateway.
     */
    @GET
    @Path("device/{deviceId}/xbee-all")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Get all XBee edge device attached to Android TV gateway",
            notes = "",
            response = Response.class,
            tags = "androidtv,device_management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidTVConstants.SCOPE, value = "perm:androidtv:enroll")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "OK.",
                    response = Response.class,
                    responseHeaders = {
                            @ResponseHeader(
                                    name = "Content-Type",
                                    description = "The content type of the body"),
                            @ResponseHeader(
                                    name = "Last-Modified",
                                    description = "Date and time the resource was last modified.\n" +
                                                  "Used by caches, or in conditional requests."),
                    }),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. \n Invalid Device Identifiers found.",
                    response = Response.class),
            @ApiResponse(
                    code = 401,
                    message = "Unauthorized. \n Unauthorized request."),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n Error occurred while executing command operation to"
                              + " send threshold",
                    response = Response.class)
    })
    Response getEdgeDevices(
            @ApiParam(
                    name = "deviceId",
                    value = "The registered device Id.",
                    required = true)
            @PathParam("deviceId") String deviceId);

    /**
     * Enroll devices.
     */
    @POST
    @Path("device/{device_id}/register")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Enroll device",
            notes = "",
            response = Response.class,
            tags = "androidtv,device_management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidTVConstants.SCOPE, value = "perm:androidtv:enroll")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 202,
                    message = "Accepted.",
                    response = Response.class),
            @ApiResponse(
                    code = 406,
                    message = "Not Acceptable"),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n Error on retrieving stats",
                    response = Response.class)
    })
    Response register(
            @ApiParam(
                    name = "deviceId",
                    value = "Device identifier id of the device to be added",
                    required = true)
            @PathParam("device_id") String deviceId,
            @ApiParam(
                    name = "deviceName",
                    value = "Device name of the device to be added",
                    required = true)
            @QueryParam("deviceName") String deviceName);

    /**
     * Retrieve Sensor data for the device type
     */
    @Path("device/stats/{deviceId}")
    @GET
    @ApiOperation(

            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Retrieve Sensor data for the device type",
            notes = "",
            response = Response.class,
            tags = "androidtv,device_management",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = AndroidTVConstants.SCOPE, value = "perm:androidtv:enroll")
                    })
            }
    )
    @Consumes("application/json")
    @Produces("application/json")
    Response getAndroidTVStats(@PathParam("deviceId") String deviceId, @QueryParam("from") long from,
                                      @QueryParam("to") long to,@QueryParam("sensorType") String sensorType);


}