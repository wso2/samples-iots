(function (window) {
    var webSockets = [];
    var DANGER_TIMEOUT = 20000;
    var WARNING_TIMEOUT = 15000;
    var SUCCESS_TIMEOUT = 10000;
    var ws;
    var wsAlert;
    var temperatureMapInstance;
    var currentTemperatureMap;
    var heatMapConfig = {
        container: document.getElementById('image'),
        radius: 200,
        maxOpacity: .5,
        minOpacity: 0,
        blur: .75
    };


    $("#show-analytics").on('click', function () {
        if ($("#show-analytics").hasClass("show-analytics")) {
            intializeWebsockets();
            createHeatMap();
            $('#image canvas').removeClass('hidden');
            $("#show-analytics").addClass("hide-analytics").removeClass("show-analytics");
            $("#show-analytics").html("Hide Analytics");
        } else {
            $('#image canvas').addClass('hidden');
            $("#show-analytics").removeClass("hide-analytics").addClass("show-analytics");
            $("#show-analytics").html("Show Analytics");
        }
    });

    /**
     * Creates the heatMap.
     */
    var createHeatMap = function () {
        if (!temperatureMapInstance) {
            temperatureMapInstance = h337.create(heatMapConfig);
        }
        if (!currentTemperatureMap) {
            var config = {
                container: document.getElementById('heat-map-hidden'),
                radius: 200,
                maxOpacity: .5,
                minOpacity: 0,
                blur: .75
            };
            currentTemperatureMap = window.h337.create(config);
        }
    };


    /**
     * To handle the real-time data.
     * @param dataValues
     */
    var handleRealTimeData = function(dataValues) {
        if (temperatureMapInstance != null && dataValues.temperature > 0) {
            var dataPoint = {
                x: dataValues.location.coordinates[0],
                y: dataValues.location.coordinates[1],
                value: dataValues.temperature
            };
            currentTemperatureMap.addData(dataPoint);

           /* if (!isSliderChanged) {
                heatmapInstance.addData(dataPoint);
            } else if (!isHistoricalView && currentSliderValue == 10) {*/
            temperatureMapInstance.addData(dataPoint);
          /*  }
            if (heatMapData.length == 10) {
                heatMapData.shift();
            }
            heatMapData.push(currentHeatMap.getData());*/
        }
    };

    /**
     * To initialize the web-sockets to get the real-time data.
     */
    var intializeWebsockets = function () {
        var webSocketURL = 'ws://localhost:9765/outputwebsocket/Floor-Analysis-WebSocketLocal-DeviceTemperatureEvent';

        if (!ws) {
            ws = new WebSocket(webSocketURL);
            ws.onopen = function () {
                notifyUser("You are now connected to Sensor stream!", "success", SUCCESS_TIMEOUT, "top-center");
            };
            ws.onmessage = function (evt) {
                handleRealTimeData(JSON.parse(evt.data));
                //  heatMapManagement.functions.handleRealTimeData(JSON.parse(evt.data));
            };
            ws.onclose = function () {
                notifyUser("Sense stream connection lost with the server", "danger", DANGER_TIMEOUT, "top-center");
            };
            ws.onerror = function (err) {
                notifyUser(err, "danger", DANGER_TIMEOUT, "top-center");
            };
            webSockets.push(ws);
        }

        if (!wsAlert) {
            webSocketURL = 'ws://localhost:9765/outputwebsocket/Floor-Analysis-WebSocketLocal-AlertEvent';
            wsAlert = new WebSocket(webSocketURL);
            wsAlert.onopen = function () {
                notifyUser("You are now connected to Alert stream!", "success", SUCCESS_TIMEOUT, "top-center");
            };
            wsAlert.onmessage = function (evt) {
                var alertData = JSON.parse(evt.data);
                notifyUser("Alert from " + alertData.buildingId + " building, " + alertData.floorId +
                    " floor. " + alertData.type + " value is " + alertData.value.toFixed(2) + ". " + alertData.information,
                    "warning", WARNING_TIMEOUT, "bottom-left");
            };
            wsAlert.onclose = function () {
                notifyUser("Alert stream connection lost with the server", "danger", DANGER_TIMEOUT, "top-center");
            };
            wsAlert.onerror = function (err) {
                notifyUser(err, "danger", DANGER_TIMEOUT, "top-center");
            };
            webSockets.push(wsAlert);
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

    /**
     * Need to close the web sockets before refreshing the page.
     */
    window.onbeforeunload = function () {
        if (ws) {
            ws.close();
        }
        if (wsAlert) {
            wsAlert.close();
        }
    };
}(window));