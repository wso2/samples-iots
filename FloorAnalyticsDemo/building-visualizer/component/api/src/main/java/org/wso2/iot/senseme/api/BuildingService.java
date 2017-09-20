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

import io.swagger.annotations.*;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.wso2.carbon.apimgt.annotations.api.Scope;
import org.wso2.carbon.apimgt.annotations.api.Scopes;
import org.wso2.iot.senseme.api.dto.AlertMessage;
import org.wso2.iot.senseme.api.dto.BuildingInfo;
import org.wso2.iot.senseme.api.dto.FloorInfo;
import org.wso2.iot.senseme.api.dto.QueryObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;


/**
 * This is the API which is used to control and manage building data
 */
@SwaggerDefinition(
        info = @Info(
                version = "1.0.0",
                title = "",
                extensions = {
                        @Extension(properties = {
                                @ExtensionProperty(name = "name", value = "senseme"),
                                @ExtensionProperty(name = "context", value = "/senseme/building"),
                        })
                }
        ),
        tags = {
                @Tag(name = "device_management", description = "")
        }
)
@Scopes(
        scopes = {
                @Scope(
                        name = "Enroll device",
                        description = "Enrolling a senseme device",
                        key = "perm:senseme:enroll",
                        permissions = {"/device-mgt/devices/enroll/senseme"}
                ),
                @Scope(
                        name = "Add building",
                        description = "Adding a building to map",
                        key = "perm:building:add",
                        permissions = {"/buildingmonitor/building/add"}
                ),
                @Scope(
                        name = "Add floor",
                        description = "Adding a floor to building",
                        key = "perm:floor:add",
                        permissions = {"/buildingmonitor/building/floor/add"}
                ),
                @Scope(
                        name = "View building",
                        description = "To view a building",
                        key = "perm:building:view",
                        permissions = {"/buildingmonitor/building/view"}
                ),
                @Scope(
                        name = "View floor",
                        description = "To view a floor",
                        key = "perm:floor:view",
                        permissions = {"/buildingmonitor/building/floor/view"}
                ),
                @Scope(
                        name = "Delete building",
                        description = "Deleting a building",
                        key = "perm:building:remove",
                        permissions = {"/buildingmonitor/building/remove"}
                )
        }
)
@Path("/building")
@SuppressWarnings("NonJaxWsWebServices")
public interface BuildingService {
    String SCOPE = "scope";


    /**
     * To insert building data into db
     *
     * @param building for the building
     * @return response
     */
    @POST
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Download agent",
            notes = "",
            response = Response.class,
            tags = "senseme",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:building:add")
                    })
            }
    )
    Response addBuilding(BuildingInfo building);

    @GET
    @Path("/{buildingId}")
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Retrieve building ",
            notes = "",
            response = Response.class,
            tags = "senseme",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:building:view")
                    })
            }
    )
    Response getRegisteredBuilding(@PathParam("buildingId") int buildingId);

    /**
     * To update building image into db
     *
     * @param buildingId for the building identifier
     * @param floorId for the floor identifier
     * @param fileInputStream for File stream
     * @param fileDetail for Attachment details
     * @return response
     */

    @Path("/{buildingId}/{floorId}")
    @POST
    @Consumes("multipart/form-data")
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "upload floor plan",
            notes = "",
            response = Response.class,
            tags = "senseme",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:floor:add")
                    })
            }
    )
    Response addFloor(int buildingId, int floorId, InputStream fileInputStream, Attachment fileDetail);

    @Path("/test")
    @GET
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Test API",
            notes = "",
            response = Response.class,
            tags = "senseme",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:senseme:enroll")
                    })
            }
    )
    Response test();


    @Path("/{buildingId}/{floorId}")
    @GET
    @Produces("image/*")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Download Image",
            notes = "",
            response = Response.class,
            tags = "senseme",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:floor:view")
                    })
            }
    )

    Response getFloorPlan(int buildingId, int floorId);


    @GET
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "get building details",
            notes = "",
            response = Response.class,
            tags = "senseme",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:building:view")
                    })
            }
    )

    Response getRegisteredBuildings();


    @POST
    @Path("/update")
    @Produces("application/json")
    @Consumes("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Update existing building",
            notes = "",
            response = Response.class,
            tags = "senseme",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:building:add")
                    })
            }
    )


    Response updateBuilding(BuildingInfo buildingInfo);

    @GET
    @Path("/{buildingId}")
    @Produces("application/json")
    @Consumes("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Get existing floor Ids which have an image uploaded.",
            notes = "",
            response = Response.class,
            tags = "senseme",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:building:view")
                    })
            }
    )


    Response getAvailableFloors(@PathParam("buildingId") int buildingId);

    @Path("/{buildingId}/{floorId}/devices")
    @GET
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Get devices for the floor",
            notes = "",
            response = Response.class,
            tags = "senseme",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:building:view")
                    })
            }
    )
    Response getDevices(@PathParam("buildingId") int buildingId, @PathParam("floorId") int floorId);

    @Path("/authorizedFloors/{buildingId}")
    @GET
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Get authorized floors of a building",
            notes = "",
            response = Response.class,
            tags = "senseme",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:floor:view")
                    })
            }
    )
    Response getAuthorizedFloors(@PathParam("buildingId") int buildingId);

    @Path("/{buildingId}/devices")
    @GET
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Get devices for the building",
            notes = "",
            response = Response.class,
            tags = "senseme",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:building:view")
                    })
            }
    )
    Response getDevicesForFloor(@PathParam("buildingId") int buildingId);

    @Path("/devices")
    @GET
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Get devices for the user",
            notes = "",
            response = Response.class,
            tags = "senseme",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:building:view")
                    })
            }
    )
    Response getDevicesForUser();

    @Path("/notification")
    @POST
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "POST notifications to android devices in the group",
            notes = "",
            response = Response.class,
            tags = "senseme",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:senseme:enroll")
                    })
            }
    )
    Response sendNotifications(AlertMessage alertMessage);

    @Path("/remove/{buildingId}")
    @POST
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Remove registered building.",
            notes = "",
            response = Response.class,
            tags = "senseme",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:building:remove")
                    })
            }
    )
    Response removeBuilding(int buildingId);


    @Path("/search/notifications")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Query and get the notifications.",
            notes = "",
            response = Response.class,
            tags = "senseme",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:senseme:enroll")
                    })
            }
    )
    Response queryNotifications(QueryObject queryObject);

    @Path("/isExistingBuilding/{buildingId}")
    @GET
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "To check whether a building exist with the given id",
            notes = "",
            response = Response.class,
            tags = "senseme",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:floor:add")
                    })
            }
    )
    Response isExistingBuilding(@PathParam("buildingId") int buildingId);
}