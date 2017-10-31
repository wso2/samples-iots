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
var map;
function onRequest(context) {
    var constants = require("/app/modules/constants.js");
    var userModule = require("/app/modules/business-controllers/user.js")["userModule"];
    var devicemgtProps = require("/app/modules/conf-reader/main.js")["conf"];

    var user = session.get(constants["USER_SESSION_KEY"]);
    var permissions = userModule.getUIPermissions();
    var buildingId = request.getParameter("buildingId");
	var viewModel = {};
	viewModel["permissions"] = permissions;
	viewModel["buildingId"] = request.getParameter("buildingId");
	var devicemgtProps = require("/app/modules/conf-reader/main.js")["conf"];
	var serviceInvokers = require("/app/modules/oauth/token-protected-service-invokers.js")["invokers"];
	var url = devicemgtProps["httpsURL"] + "/senseme/building/" + viewModel["buildingId"];
	var floorsCount = 0;
	var authorizedFloors = [];

	serviceInvokers.XMLHttp.get(
		url, function (responsePayload) {
			if (responsePayload.status == 403) {
				viewModel.permittednone = true;
			} else if (responsePayload.status == 401) {
                viewModel.permittednone = true;
			} else {
                var building = JSON.parse(responsePayload.responseText);
                floorsCount = building.numFloors;
                viewModel["floorCount"] = floorsCount;
                viewModel["buildingName"] = building.buildingName;
                viewModel["longitude"] = building.longitude;
                viewModel["latitude"] = building.latitude;
            }

		},
		function (responsePayload) {}
	);

	if (viewModel.permittednone) {
		return viewModel;
	}
	var floors = {};
	for (var i = floorsCount; i >= 1; i--) {
		var floor = {};
		floor.active = 0;
		floor.inactive = 0;
		floor.fault = 0;
		floor.total = 0;
		floor.num = i;
		floor.suffix = ordinal_suffix_of(i);
		floor.buildingId = request.getParameter("buildingId");
		floors[i] = floor;
	}

    serviceInvokers.XMLHttp.get(devicemgtProps["httpsURL"] + "/senseme/building/authorizedFloors/" + viewModel["buildingId"] , function (responsePayload) {
            if (responsePayload.status == 200) {
            	authorizedFloors =  JSON.parse(responsePayload.responseText);
			}

        }, function (responsePayload) {

        });
	serviceInvokers.XMLHttp.get(
		url+"/floors", function (responsePayload) {
			var floorsImages = JSON.parse(responsePayload.responseText);
			viewModel["floorsWithImages"] = floorsImages;
			for (var i = 0; i < floorsImages.length; i++) {
				for (var j = 1; j <= floorsCount; j++) {
					if (floorsImages.indexOf(j) > -1) {
						floors[j].image = "exist";
						floors[j].isAuthorized = authorizedFloors[j-1];
					}
				}
			}
		},
		function (responsePayload) {
			viewModel["floorsWithImages"] = "0";
		}
	);

	serviceInvokers.XMLHttp.get(
		url+"/devices", function (responsePayload) {
			new Log().info(responsePayload);
			var devices = JSON.parse(responsePayload.responseText);
			if (devices) {

				for (var i = 0; i < devices.length; i++) {
					floors["" + devices[i].id].active = devices[i].activeDevices;
					floors["" + devices[i].id].inactive = devices[i].inactiveDevices;
					floors["" + devices[i].id].fault = devices[i].faultDevices;
					floors["" + devices[i].id].total = devices[i].totalDevices;
				}
			}
		},
		function (responsePayload) {

		}
	);
	viewModel["floors"] = floors;
	return viewModel;
}

function ordinal_suffix_of(i) {
	var j = i % 10,
		k = i % 100;
	if (j == 1 && k != 11) {
		return i + "st";
	}
	if (j == 2 && k != 12) {
		return i + "nd";
	}
	if (j == 3 && k != 13) {
		return i + "rd";
	}
	return i + "th";
}