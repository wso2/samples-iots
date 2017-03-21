(function (window, document) {
    var webSockets = [];
    var DANGER_TIMEOUT = 20000;
    var WARNING_TIMEOUT = 15000;
    var SUCCESS_TIMEOUT = 10000;
    var ws;
    var wsAlert;
    var temperatureMapInstance;
    var motionMapInstance;
    var lightMapInstance;
    var humidityMapInstance;
    var currentTemperatureMap;
    var currentMotionMap;
    var currentLightMap;
    var currentHumidityMap;
    var floorId;
    var buildingId;
    var rangeSlider;
    var isSliderChanged = false;
    var currentSelection = "Temperature";
    var heatMapConfig = {
        container: document.getElementById('image'),
        radius: 200,
        maxOpacity: .5,
        minOpacity: 0,
        blur: .75
    };


    $("#show-analytics").on('click', function () {
        if ($("#show-analytics").hasClass("show-analytics")) {
            $("#radio-selections").removeClass("hidden");
            $('#image canvas').removeClass('hidden');
            $("#show-analytics").addClass("hide-analytics").removeClass("show-analytics");
            $("#analytics").html("Hide Analytics");
            $('.slider-wrapper').show(1000);
        } else {
            $("#radio-selections").addClass("hidden");
            $('#image canvas').addClass('hidden');
            $("#show-analytics").removeClass("hide-analytics").addClass("show-analytics");
            $("#analytics").html("Show Analytics");
            $('.slider-wrapper').hide(1000);
        }
    });

    /**
     * Creates the heatMap.
     */
    var createHeatMap = function () {
        if (!temperatureMapInstance) {
            // heatMapConfig.gradient = {
            //     '0': 'rgb(255, 255, 255)',
            //     '1': 'rgb(0, 0, 0)'
            // };
            temperatureMapInstance = h337.create(heatMapConfig);
            // heatMapConfig.gradient = {
            //     '0': 'rgb(255, 255, 255)',
            //     '1': 'rgb(255, 0, 0)'
            // };
            motionMapInstance = h337.create(heatMapConfig);
            // heatMapConfig.gradient = {
            //     '0': 'rgb(255, 255, 255)',
            //     '1': 'rgb(0, 255, 0)'
            // };
            lightMapInstance = h337.create(heatMapConfig);
            // heatMapConfig.gradient = {
            //     '0': 'rgb(255, 255, 255)',
            //     '1': 'rgb(0, 0, 255)'
            // };
            humidityMapInstance = h337.create(heatMapConfig);
            motionMapInstance.setDataMin(0);
            motionMapInstance.setDataMax(1);
            lightMapInstance.setDataMin(0);
            lightMapInstance.setDataMax(1);
            humidityMapInstance.setDataMin(0);
            humidityMapInstance.setDataMax(1);
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
            currentHumidityMap = window.h337.create(config);
            currentLightMap = window.h337.create(config);
            currentMotionMap = window.h337.create(config);
        }
    };

    /**
     * To handle the real-time data.
     * @param dataValues
     */
    var handleRealTimeData = function(dataValues) {
        if (dataValues.location.building != buildingId || dataValues.location.floor != floorId) {
            return;
        }

        var temperatureDataPoint = {
            x: dataValues.location.coordinates[0],
            y: dataValues.location.coordinates[1],
            value: dataValues.temperature
        };
        currentTemperatureMap.addData(temperatureDataPoint);
        /* if (!isSliderChanged) {
         heatmapInstance.addData(dataPoint);
         } else if (!isHistoricalView && currentSliderValue == 10) {*/

        /*  }
         if (heatMapData.length == 10) {
         heatMapData.shift();
         }
         heatMapData.push(currentHeatMap.getData());*/

        var humidityDataPoint = {
            x: dataValues.location.coordinates[0],
            y: dataValues.location.coordinates[1],
            value: dataValues.humidity
        };
        currentHumidityMap.addData(humidityDataPoint);

        var lightDataPoint = {
            x: dataValues.location.coordinates[0],
            y: dataValues.location.coordinates[1],
            value: dataValues.light
        };
        currentLightMap.addData(lightDataPoint);

        var motionDataPoint = {
            x: dataValues.location.coordinates[0],
            y: dataValues.location.coordinates[1],
            value: dataValues.motion
        };
        currentMotionMap.addData(motionDataPoint);

        switch (currentSelection) {
            case "Temperature" : temperatureMapInstance.addData(temperatureDataPoint); break;
            case "Motion" : motionMapInstance.addData(motionDataPoint); break;
            case "Humidity" : humidityMapInstance.addData(humidityDataPoint); break;
            case "Light" : lightMapInstance.addData(lightDataPoint); break;
        }
    };

    /**
     * To initialize the web-sockets to get the real-time data.
     */
    var intializeWebsockets = function () {
        var webSocketURL = 'ws://localhost:9765/outputwebsocket/Floor-Analysis-WebSocketLocal-DeviceFloorEvent';

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

        webSocketURL = 'ws://localhost:9765/outputwebsocket/Floor-Analysis-WebSocketLocal-AlertEvent';
        wsAlert = new WebSocket(webSocketURL);
        wsAlert.onopen = function () {
            notifyUser("You are now connected to Alert stream!", "success", SUCCESS_TIMEOUT, "top-center");
        };
        wsAlert.onmessage = function (evt) {
            var alertData = JSON.parse(evt.data);
            if (alertData.buildingId == buildingId && alertData.floorId == floorId) {
                notifyUser("Alert from " + alertData.buildingId + " building, " + alertData.floorId +
                    " floor. " + alertData.type + " value is " + alertData.value.toFixed(2) + ". " + alertData.information,
                    "warning", WARNING_TIMEOUT, "bottom-left");
            }
        };
        wsAlert.onclose = function () {
            notifyUser("Alert stream connection lost with the server", "danger", DANGER_TIMEOUT, "top-center");
        };
        wsAlert.onerror = function (err) {
            notifyUser(err, "danger", DANGER_TIMEOUT, "top-center");
        };
        webSockets.push(wsAlert);
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

    $(document).ready(function(){
        intializeWebsockets();
        createHeatMap();
        floorId = $("#image").attr("floorId");
        buildingId = $("#image").attr("buildingId");
        console.log(floorId);
        console.log(buildingId);
        buildingId = "WSO2";
        floorId = "5th floor";

        rangeSlider = $("#range-slider").bootstrapSlider();
        rangeSlider.bootstrapSlider('setValue', 10);

        $('#range-slider').on("slide", function () {
            updateHeatMapOnSlideChange();

        }).on("change", function () {
            updateHeatMapOnSlideChange();
        });

        $('input[name="daterange"]').datepicker({
            orientation: "auto",
            endDate: "+0d"
        }).on("changeDate", function(e) {

        });
    });

    /**
     * To update the heat map on changing the slider.
     */
    var updateHeatMapOnSlideChange = function () {
        isSliderChanged = true;
        var max = rangeSlider.bootstrapSlider("getAttribute", 'max');
        var min = rangeSlider.bootstrapSlider("getAttribute", 'min');
        currentSliderValue = rangeSlider.bootstrapSlider("getValue");

        var data = {data: []};
        heatmapInstance.setData(data);
        heatmapInstance = h337.create(heatMapConfig);

        if (!isHistoricalView) {
            if (currentSliderValue == 10) {
                heatmapInstance.setData(data);
                heatmapInstance = h337.create(heatMapConfig);
                heatmapInstance.setData(currentHeatMap.getData());
            } else {
                heatmapInstance.setData(heatMapData[currentSliderValue - 1]);
            }
        } else {
            if (historicalData) {
                data = {data: []};
                heatmapInstance.setData(data);
                heatmapInstance = h337.create(heatMapConfig);
                var length = historicalData.length * ((currentSliderValue-min) / (max-min));
                for (var i = 0; i < length; i++) {
                    var dataPoint = {
                        x: historicalData[i].xCoordinate,
                        y: historicalData[i].yCoordinate,
                        value: historicalData[i].temperature
                    };
                    heatmapInstance.addData(dataPoint);
                }
            } else {

            }
        }
    };

    $("form input:radio").change(function () {
        switch (currentSelection) {
            case "Temperature" :  temperatureMapInstance.setData({data:[]}); break;
            case "Motion" : motionMapInstance.setData({data:[]});break;
            case "Humidity" : humidityMapInstance.setData({data:[]});break;
            case "Light" : lightMapInstance.setData({data:[]}); break;
        }
        currentSelection = $(this).val();
        switch (currentSelection) {
            case "Temperature" :  temperatureMapInstance.setData(currentTemperatureMap.getData()); break;
            case "Motion" : motionMapInstance.setData(currentMotionMap.getData());break;
            case "Humidity" : humidityMapInstance.setData(currentHumidityMap.getData());break;
            case "Light" : lightMapInstance.setData(currentLightMap.getData()); break;
        }

    });

}(window, document));