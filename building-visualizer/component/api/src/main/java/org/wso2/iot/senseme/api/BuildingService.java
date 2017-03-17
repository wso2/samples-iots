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
import org.wso2.iot.senseme.api.dto.BuildingInfo;
import org.wso2.iot.senseme.api.dto.FloorInfo;

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
                @Tag(name = "senseme, device_management", description = "")
        }
)
@Scopes(
        scopes = {
                @Scope(
                        name = "Enroll device",
                        description = "",
                        key = "perm:senseme:enroll",
                        permissions = {"/device-mgt/devices/enroll/senseme"}
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
                            @ExtensionProperty(name = SCOPE, value = "perm:senseme:enroll")
                    })
            }
    )
    Response addBuilding(BuildingInfo building);

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
                            @ExtensionProperty(name = SCOPE, value = "perm:senseme:enroll")
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
                            @ExtensionProperty(name = SCOPE, value = "perm:senseme:enroll")
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
                            @ExtensionProperty(name = SCOPE, value = "perm:senseme:enroll")
                    })
            }
    )

    Response getRegisteredBuildings();

    @GET
    @Produces("application/json")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "get building data",
            notes = "",
            response = Response.class,
            tags = "senseme",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:senseme:enroll")
                    })
            }
    )

    Response getBuildingData(int buildingId);

}