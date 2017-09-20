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
	var log = new Log("device-view.js");
	var deviceType = "senseme";
	var deviceId = request.getParameter("id");
	var autoCompleteParams = [
		{"name" : "deviceId", "value" : deviceId}
	];

	if (deviceType != null && deviceType != undefined && deviceId != null && deviceId != undefined) {
		var deviceModule = require("/app/modules/business-controllers/device.js")["deviceModule"];
		var device = deviceModule.viewDevice(deviceType, deviceId);

        var serviceInvokers = require("/app/modules/oauth/token-protected-service-invokers.js")["invokers"];
        var devicemgtProps = require("/app/modules/conf-reader/main.js")["conf"];

        if (device && device.status != "error") {
			var buildingId = device.content["initialDeviceInfo"]["buildingId"];
			var floorId = device.content["initialDeviceInfo"]["floorId"];
            if (buildingId) {
                var url = devicemgtProps["httpsURL"] + "/senseme/building/" + buildingId;

                serviceInvokers.XMLHttp.get(
                    url,
                    // response callback
                    function (backendResponse) {
                        var details = JSON.parse(backendResponse["responseText"]);
                        device.content["location"] = {};
                        device.content["location"]["latitude"] = details.latitude;
                        device.content["location"]["longitude"] = details.longitude;
						device.content["buildingId"] = buildingId;
						device.content["floorId"] = floorId;
                    }
                );
            }
			return {"device": device.content, "autoCompleteParams" : autoCompleteParams, "encodedFeaturePayloads": ""};
		} else {
			response.sendError(404, "Device Id " + deviceId + " of type " + deviceType + " cannot be found!");
			exit();
		}
	}
}