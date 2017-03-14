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
    var heatmapInstance;
    var currentHeatMap;
    var heatMapData = [];
    var rangeSlider;
    var isSliderChanged = false;
    var currentSliderValue = 60;
    var historicalData;
    var isHistoricalView = false;

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
     * To get the historical data for a certain period of time
     * @param timeFrom Start time
     * @param timeTo End time
     *
     */
    var getProviderData = function (timeFrom, timeTo) {
        $.ajax({
            url: '/buildingmonitor/apis/batch-provider.jag?action=getData&timeFrom=' + timeFrom + '&timeTo=' + timeTo,
            method: "GET",
            contentType: "application/json",
            async: false,
            success: function (data) {
                heatMapManagement.functions.updateHistoricalData(data);
            }
        });

    };

    /**
     * To initialize the web-sockets to get the real-time data.
     */
    var intializeWebsockets = function () {
        var webSocketURL = 'ws://localhost:9765/outputwebsocket/Floor-Analysis-WebSocketLocal-DeviceTemperatureEvent';
        var ws = new WebSocket(webSocketURL);
        ws.onopen = function () {
            console.log("opened");
        };
        ws.onmessage = function (evt) {
            heatMapManagement.functions.handleRealTimeData(JSON.parse(evt.data));
        };
        ws.onclose = function () {
            console.log("closed!");
        };
        ws.onerror = function (err) {
            console.log("Error: " + err);
        };
        webSockets.push(ws);

        webSocketURL = 'ws://localhost:9765/outputwebsocket/Floor-Analysis-WebSocketLocal-AlertTemperatureEvent';
        var wsAlert = new WebSocket(webSocketURL);
        wsAlert.onopen = function () {
            console.log("opened");
        };
        wsAlert.onmessage = function (evt) {
            console.log(evt.data);
        };
        wsAlert.onclose = function () {
            console.log("closed!");
        };
        wsAlert.onerror = function (err) {
            console.log("Error: " + err);
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

    custom.functions = {
        getProviderData : getProviderData,
        initializeWebSockets : intializeWebsockets,
        closeWebSockets : closeWebSockets
    }

})();


