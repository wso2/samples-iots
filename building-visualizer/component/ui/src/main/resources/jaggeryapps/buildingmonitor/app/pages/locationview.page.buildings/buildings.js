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

function onRequest(context) {
    var constants = require("/app/modules/constants.js");
    var userModule = require("/app/modules/business-controllers/user.js")["userModule"];
    var devicemgtProps = require("/app/modules/conf-reader/main.js")["conf"];

    var user = session.get(constants["USER_SESSION_KEY"]);
    var permissions = userModule.getUIPermissions();

    if (!permissions.VIEW_DASHBOARD) {
        response.sendRedirect(devicemgtProps["appContext"] + "devices");
        return;
    }

	context.handlebars.registerHelper('times', function(n, block) {
		var accum = '';
		for(var i = n; i >= 1; --i)
			accum += block.fn(i);
		return accum;
	});
	var viewModel = {};
	viewModel["buildingId"] = request.getParameter("buildingId");;
	var devicemgtProps = require("/app/modules/conf-reader/main.js")["conf"];
	var serviceInvokers = require("/app/modules/oauth/token-protected-service-invokers.js")["invokers"];
	var url = devicemgtProps["httpsURL"] + "/senseme/building/" + viewModel["buildingId"];
	serviceInvokers.XMLHttp.get(
		url, function (responsePayload) {
			var building = JSON.parse(responsePayload.responseText);
			viewModel["floorCount"] = building.numFloors;

		},
		function (responsePayload) {
			viewModel["floorCount"] = "0";
		}
	);

	serviceInvokers.XMLHttp.get(
		url+"/floors", function (responsePayload) {
			viewModel["floorsWithImages"] = JSON.parse(responsePayload.responseText);
			console.log(responsePayload.responseText);
		},
		function (responsePayload) {
			viewModel["floorsWithImages"] = "0";
		}
	);
	new Log().error(viewModel);
	return viewModel;
}