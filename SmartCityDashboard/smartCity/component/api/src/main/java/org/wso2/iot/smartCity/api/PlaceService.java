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

import io.swagger.annotations.*;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.wso2.carbon.apimgt.annotations.api.Scope;
import org.wso2.carbon.apimgt.annotations.api.Scopes;
import org.wso2.iot.smartCity.api.dto.AlertMessage;
import org.wso2.iot.smartCity.api.dto.PlaceInfo;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;


/**
 * This is the API which is used to control and manage place data
 */
@SwaggerDefinition(
        info = @Info(
                version = "1.0.0",
                title = "",
                extensions = {
                        @Extension(properties = {
                                @ExtensionProperty(name = "name", value = "smartCity"),
                                @ExtensionProperty(name = "context", value = "/smartCity/place"),
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
                        description = "Enrolling a device",
                        key = "perm:smartCity:enroll",
                        permissions = {"/device-mgt/devices/enroll"}
                ),
                @Scope(
                        name = "Add place",
                        description = "Adding a place to map",
                        key = "perm:place:add",
                        permissions = {"/smartCity/place/add"}
                ),
                @Scope(
                        name = "View place",
                        description = "To view a place",
                        key = "perm:place:view",
                        permissions = {"/smartCity/place/view"}
                ),
                @Scope(
                        name = "Delete place",
                        description = "Deleting a place",
                        key = "perm:place:remove",
                        permissions = {"/smartCity/place/remove"}
                )
        }
)
@Path("/place")
@SuppressWarnings("NonJaxWsWebServices")
public interface PlaceService {
    String SCOPE = "scope";


    /**
     * To insert place data into db
     *
     * @param place for the place
     * @return response
     */
    @POST
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Add place",
            notes = "",
            response = Response.class,
            tags = "smartCity",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:place:add")
                    })
            }
    )
    Response addPlace(PlaceInfo place);

    @GET
    @Path("/{placeId}")
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Retrieve place ",
            notes = "",
            response = Response.class,
            tags = "smartCity",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:place:view")
                    })
            }
    )
    Response getRegisteredPlace(@PathParam("placeId") int placeId);

    /**
     * To update place image into db
     *
     * @param placeId for the place identifier
     * @param fileInputStream for File stream
     * @param fileDetail for Attachment details
     * @return response
     */

    @Path("/{placeId}/addPlan")
    @POST
    @Consumes("multipart/form-data")
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "upload place plan",
            notes = "",
            response = Response.class,
            tags = "smartCity",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:place:add")
                    })
            }
    )
    Response addImage(int placeId, InputStream fileInputStream, Attachment fileDetail);

    @Path("/test")
    @GET
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Test API",
            notes = "",
            response = Response.class,
            tags = "smartCity",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:smartCity:enroll")
                    })
            }
    )
    Response test();


    @Path("/{placeId}/getPlan")
    @GET
    @Produces("image/*")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Download Image",
            notes = "",
            response = Response.class,
            tags = "smartCity",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:place:view")
                    })
            }
    )

    Response getPlacePlan(int placeId);


    @GET
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "get place details",
            notes = "",
            response = Response.class,
            tags = "smartCity",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:place:view")
                    })
            }
    )

    Response getRegisteredPlaces();


    @POST
    @Path("/update")
    @Produces("application/json")
    @Consumes("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Update existing place",
            notes = "",
            response = Response.class,
            tags = "smartCity",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:place:add")
                    })
            }
    )


    Response updatePlace(PlaceInfo placeInfo);

    @Path("/{placeId}/devices")
    @GET
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Get devices for the place",
            notes = "",
            response = Response.class,
            tags = "smartCity",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:place:view")
                    })
            }
    )
    Response getDevicesForPlace(@PathParam("placeId") int placeId);

    @Path("/{placeId}/devicesDetails")
    @GET
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Get devices details for the place",
            notes = "",
            response = Response.class,
            tags = "smartCity",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:place:view")
                    })
            }
    )
    Response getDevicesForPlaceDetails(@PathParam("placeId") int placeId);


    @Path("/devices")
    @GET
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Get all devices for user",
            notes = "",
            response = Response.class,
            tags = "smartCity",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:place:view")
                    })
            }
    )
    Response getDevicesForUser();

    @Path("/remove/{placeId}")
    @POST
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Remove registered place.",
            notes = "",
            response = Response.class,
            tags = "smartCity",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:place:remove")
                    })
            }
    )
    Response removePlace(int placeId);


    @Path("/isExistingPlace/{placeId}")
    @GET
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "To check whether a place exist with the given id",
            notes = "",
            response = Response.class,
            tags = "smartCity",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:place:add")
                    })
            }
    )
    Response isExistingPlace(@PathParam("placeId") int placeId);
}