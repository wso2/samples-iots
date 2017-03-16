/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


var custom = custom || {};

(function() {
 //   google.load("visualization", "1");
    var data = undefined;
    var timeline = undefined;
    var webSockets = [];

    // Set callback to run when API is loaded
//    google.setOnLoadCallback(drawVisualization);

    function drawVisualization() {
        // Create and populate a data table.
        data = new google.visualization.DataTable();
        data.addColumn('datetime', 'start');
        data.addColumn('datetime', 'end');
        data.addColumn('string', 'content');

        // specify options
        var options = {
            "width":  "100%",
            "style": "box",
            "showNavigation":true
        };
        // Instantiate our timeline object.
        timeline = new links.Timeline(document.getElementById('timeline'), options);

        // Draw our timeline with the created data and options
        timeline.draw(data);
        timeline.setAutoScale(true);


        // set a custom range from -2 minute to +3 minutes current time
        var start = new Date((new Date()).getTime() - 2 * 60 * 1000);
        var end   = new Date((new Date()).getTime() );
        timeline.setVisibleChartRange(start, end);

    }

    /**
     * To get the historical data for a certain period of time.
     * @param tableName Name of the table to fetch the data from
     * @param timeFrom Start time
     * @param timeTo End time
     *
     */
    var getProviderData = function (tableName, timeFrom, timeTo, start, limit, sortBy) {
        var providerData = null;
        var providerUrl = '/buildingmonitor/apis/batch-provider.jag?action=getData&tableName=' + tableName;

        if (timeFrom && timeTo) {
            providerUrl += '&timeFrom=' + timeFrom + '&timeTo=' + timeTo;
        }
        if (start) {
            providerUrl += "&start=" + start;
        }
        if (limit) {
            providerUrl += "&limit=" + limit;
        }
        if (sortBy) {
            providerUrl += "&sortBy="  +sortBy
        }
        $.ajax({
            url:providerUrl,
            method: "GET",
            contentType: "application/json",
            async: false,
            success: function (data) {
                providerData = data;
            },
            error : function (err) {
                notifyUser(err, "danger", constants.DANGER_TIMEOUT, "top-center");
            }
        });
        return providerData;
    };

    /**
     * To initialize the web-sockets to get the real-time data.
     */
    var intializeWebsockets = function () {
        var webSocketURL = 'ws://localhost:9765/outputwebsocket/Floor-Analysis-WebSocketLocal-DeviceTemperatureEvent';
        var ws = new WebSocket(webSocketURL);
        ws.onopen = function () {
            notifyUser("You are now connected to Sensor stream!", "success", constants.SUCCESS_TIMEOUT, "top-center");
        };
        ws.onmessage = function (evt) {
            heatMapManagement.functions.handleRealTimeData(JSON.parse(evt.data));
        };
        ws.onclose = function () {
            notifyUser("Sense stream connection lost with the server", "danger", constants.DANGER_TIMEOUT, "top-center");
        };
        ws.onerror = function (err) {
            notifyUser(err, "danger", constants.DANGER_TIMEOUT, "top-center");
        };
        webSockets.push(ws);

        webSocketURL = 'ws://localhost:9765/outputwebsocket/Floor-Analysis-WebSocketLocal-AlertEvent';
        var wsAlert = new WebSocket(webSocketURL);
        wsAlert.onopen = function () {
            notifyUser("You are now connected to Alert stream!", "success", constants.SUCCESS_TIMEOUT, "top-center");
        };
        wsAlert.onmessage = function (evt) {
            var alertData = JSON.parse(evt.data);
            notifyUser("Alert from " + alertData.buildingId + " building, " + alertData.floorId +
                " floor. " + alertData.type + " value is " + alertData.value.toFixed(2) + ". " + alertData.information,
                "warning", constants.WARNING_TIMEOUT, "bottom-left");
        };
        wsAlert.onclose = function () {
            notifyUser("Alert stream connection lost with the server", "danger", constants.DANGER_TIMEOUT, "top-center");
        };
        wsAlert.onerror = function (err) {
            notifyUser(err, "danger", constants.DANGER_TIMEOUT, "top-center");
        };
        webSockets.push(wsAlert);
    };

    /**
     * To close the web sockets.
     */
    var closeWebSockets = function () {
        for (var index = 0; index < webSockets.length; index++) {
            webSockets[index].close();
        }
    };

    /**
     * To notify the user.
     * @param message Message that need to be passed in the notification.
     * @param status Status level of the message
     * @param timeout Time-out to close this particular alert
     * @param pos Position to display the alery
     */
    function notifyUser(message, status, timeout, pos) {
        $.UIkit.notify({
            message: message,
            status: status,
            timeout: timeout,
            pos: pos
        });
    }

    function handlePlay() {

    }

    custom.functions = {
        getProviderData : getProviderData,
        initializeWebSockets : intializeWebsockets,
        closeWebSockets : closeWebSockets
    }

})();
