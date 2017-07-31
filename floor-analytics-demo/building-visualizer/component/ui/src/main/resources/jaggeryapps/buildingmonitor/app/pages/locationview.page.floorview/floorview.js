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

function onRequest() {
    var constants = require("/app/modules/constants.js");
    var userModule = require("/app/modules/business-controllers/user.js")["userModule"];
    var devicemgtProps = require("/app/modules/conf-reader/main.js")["conf"];

    var user = session.get(constants["USER_SESSION_KEY"]);
    var permissions = userModule.getUIPermissions();

    var buildingId = request.getParameter("buildingId");
    var floorId = request.getParameter("floorId");

    var serviceInvokers = require("/app/modules/oauth/token-protected-service-invokers.js")["invokers"];
    var hearders = [{"name": constants["ACCEPT_IDENTIFIER"], "value": "image/*"}];
    var url = devicemgtProps["httpsURL"] + "/senseme/building/" + buildingId +"/" + floorId;

    var viewModel = {};
    viewModel["permissions"] = permissions;
    viewModel["buildingId"] = buildingId;
    viewModel["floorId"] = floorId;

    var isExistingCheckUrl = devicemgtProps["httpsURL"] + "/senseme/building/isExistingBuilding/" + buildingId;
    serviceInvokers.XMLHttp.get(url, function (responsePayload) {
        if (responsePayload.status == 404) {
            viewModel.buildingNotFound = true;
        }
    }, function (responsePayload) {
    }, null);

    if (viewModel.buildingNotFound) {
        return viewModel;
    }
    serviceInvokers.HttpClient.get(url , function (responsePayload, responseHeaders, status) {
        if (status == 200) {
            var streamObject = new Stream(responsePayload);
            var IOUtils = Packages.org.apache.commons.io.IOUtils;
            var Base64 = Packages.org.apache.commons.codec.binary.Base64;
            viewModel["imageObj"] = Base64.encodeBase64String(IOUtils.toByteArray(streamObject.getStream()));
        }
    }, function (responsePayload) {
        if (responsePayload.getStatusCode() == 401) {
            viewModel.permittednone = true;
        } else if (responsePayload.getStatusCode() == 403) {
            viewModel.permittednone = true;
        }
    }, hearders);
    return viewModel;
}