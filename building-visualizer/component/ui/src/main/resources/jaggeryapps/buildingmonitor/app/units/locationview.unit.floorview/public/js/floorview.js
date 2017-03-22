(function (window, document, context) {
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
    var historicalSlider;
    var isSliderChanged = false;
    var currentSliderValue = 0;
    var currentSelection = "Temperature";
    var isHistoricalView = false;
    var temperatureMapData = [];
    var motionMapData = [];
    var lightMapData = [];
    var humidityMapData = [];
    var timeouts = [];
    var heatMapConfig = {
        container: document.getElementById('image'),
        radius: 200,
        maxOpacity: .5,
        minOpacity: 0,
        blur: .75
    };
    var historicalData = [];
    var recentPastData = [];
    var selectedDate;


    $("#show-analytics").on('click', function () {
        if ($("#show-analytics").hasClass("show-analytics")) {
            $("#radio-selections").removeClass("hidden");
            $('#image canvas').removeClass('hidden');
            $("#show-analytics").addClass("hide-analytics").removeClass("show-analytics");
            $("#analytics").html("Hide Analytics");
            $('.slider-wrapper').show();
            $(".slider-wrapper").click();
        } else {
            $("#radio-selections").addClass("hidden");
            $('#image canvas').addClass('hidden');
            $("#show-analytics").removeClass("hide-analytics").addClass("show-analytics");
            $("#analytics").html("Show Analytics");
            $('.slider-wrapper').hide();
        }
    });

    /**
     * Creates the heatMap.
     */
    var createHeatMap = function () {
        if (!temperatureMapInstance) {
            temperatureMapInstance = h337.create(heatMapConfig);
            motionMapInstance = h337.create(heatMapConfig);
            lightMapInstance = h337.create(heatMapConfig);
            humidityMapInstance = h337.create(heatMapConfig);
            temperatureMapInstance.setDataMin(1500);
            temperatureMapInstance.setDataMax(2800);
            motionMapInstance.setDataMin(0);
            motionMapInstance.setDataMax(10000);
            lightMapInstance.setDataMin(0);
            lightMapInstance.setDataMax(10000);
            humidityMapInstance.setDataMin(0);
            humidityMapInstance.setDataMax(10000);
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
            currentTemperatureMap.setDataMin(1500);
            currentTemperatureMap.setDataMax(2800);
            currentHumidityMap.setDataMin(0);
            currentHumidityMap.setDataMax(10000);
            currentLightMap.setDataMin(0);
            currentLightMap.setDataMax(10000);
            currentMotionMap.setDataMin(0);
            currentMotionMap.setDataMax(10000);
        }
    };

    var processHeatMapDataPoints = function(heatMapDataPoints, currentPoint) {
        for (var i = heatMapDataPoints.length - 1; i >= 0; i--) {
            if (heatMapDataPoints[i].x == currentPoint.x && heatMapDataPoints[i].y == currentPoint.y) {
                heatMapDataPoints.splice(i, 1);
                break;
            }
        }
        return heatMapDataPoints;
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
            value: dataValues.temperature * 100
        };
        var currentTemperatureData = currentTemperatureMap.getData();
        currentTemperatureData.data = processHeatMapDataPoints(currentTemperatureData.data, temperatureDataPoint);
        currentTemperatureData.data.push(temperatureDataPoint);
        currentTemperatureMap.setData(currentTemperatureData);

        var humidityDataPoint = {
            x: dataValues.location.coordinates[0],
            y: dataValues.location.coordinates[1],
            value: dataValues.humidity * 10000
        };

        var currentHumidityData = currentHumidityMap.getData();
        currentHumidityData.data = processHeatMapDataPoints(currentHumidityData.data, humidityDataPoint);
        currentHumidityData.data.push(humidityDataPoint);
        currentHumidityData.data.push(humidityDataPoint);
        currentHumidityMap.setData(currentHumidityData);

        var lightDataPoint = {
            x: dataValues.location.coordinates[0],
            y: dataValues.location.coordinates[1],
            value: dataValues.light * 10000
        };
        var currentLightData = currentLightMap.getData();
        currentLightData.data = processHeatMapDataPoints(currentLightData.data, lightDataPoint);
        currentLightData.data.push(lightDataPoint);
        currentLightMap.setData(currentLightData);

        var motionDataPoint = {
            x: dataValues.location.coordinates[0],
            y: dataValues.location.coordinates[1],
            value: dataValues.motion * 10000
        };
        var currentMotionData = currentMotionMap.getData();
        currentMotionData.data = processHeatMapDataPoints(currentMotionData.data, motionDataPoint);
        currentMotionData.data.push(motionDataPoint);
        currentMotionMap.setData(currentMotionData);

        if (!isHistoricalView) {
            if (!isSliderChanged || currentSliderValue == rangeSlider.bootstrapSlider('getAttribute', 'max')) {
                switch (currentSelection) {
                    case "Temperature" :
                        temperatureMapInstance.setData({data: []});
                        temperatureMapInstance.setData(currentTemperatureMap.getData());
                        break;
                    case "Motion" :
                        motionMapInstance.setData({data: []});
                        motionMapInstance.setData(currentMotionMap.getData());
                        break;
                    case "Humidity" :
                        humidityMapInstance.setData({data: []});
                        humidityMapInstance.setData(currentHumidityMap.getData());
                        break;
                    case "Light" :
                        lightMapInstance.setData({data: []});
                        lightMapInstance.setData(currentLightMap.getData());
                        break;
                }
            }
        }

        if (temperatureMapData.length == 16) {
            temperatureMapData.shift();
        }
        if (motionMapData.length == 16) {
            motionMapData.shift();
        }
        if (humidityMapData.length == 16) {
            humidityMapData.shift();
        }
        if (lightMapData.length == 16) {
            lightMapData.shift();
        }
        temperatureMapData.push(currentTemperatureMap.getData());
        motionMapData.push(currentMotionMap.getData());
        humidityMapData.push(currentHumidityMap.getData());
        lightMapData.push(currentLightMap.getData());
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

        rangeSlider = $("#range-slider").bootstrapSlider({
            ticks: [-15, -10, -5, 0],
            ticks_labels: ['-15s', '-10s', '-5s', 'current'],
            formatter: function(value) {
                return value + "s";
            }
        });
        rangeSlider.bootstrapSlider('setAttribute', 'min', -15);
        rangeSlider.bootstrapSlider('setAttribute', 'max', 0);
        rangeSlider.bootstrapSlider('setValue', 0);

        historicalSlider = $("#historical-slider").bootstrapSlider({
            ticks: [0, 4, 8, 12, 16, 20, 24],
            ticks_labels: ['0h', '4h', '8h', '12h', '16h', '20h', '24h'],
            formatter: function(value) {
                return value + "h";
            }
        });
        historicalSlider.bootstrapSlider('setAttribute', 'min', 0);
        historicalSlider.bootstrapSlider('setAttribute', 'max', 24);
        historicalSlider.bootstrapSlider('setValue', 0);

        $('#range-slider').on("change", function () {
            updateHeatMapOnSlideChange();
        });

        $('#historical-slider').on("change", function () {
            updateHeatMapOnSlideChange();
        });

        $('input[name="daterange"]').datepicker({
            orientation: "auto",
            endDate: "+0d",
            autoclose: true
        }).on("changeDate", function(e) {
            selectedDate = e.date;
            var date = new Date(e.date);
            date.setHours(date.getHours()-1);
            console.log(date.getTime());

            var date = new Date(e.date);
            console.log("xxx" + date.getTime());

            historicalData = getHistoricalData("ORG_WSO2_FLOOR_SUMMARIZED_DEVICE_FLOOR_SENSORSTREAM", date.getTime());
            updateHistoricData(historicalData[currentSliderValue]);
        });

        $('#image canvas').addClass('hidden');
    });

    var isDataExist = function(dataArray, dataPoint) {
        for (var data in dataArray) {
            if (dataArray[data].x == dataPoint.x && dataArray[data].y == dataPoint.y) {
                return true;
            }
        }
    };

    /**
     * To update the heat map
     * @param historicalData HistoricalData, derived
     */
    var updateHistoricData = function(historicalData) {
        var max = 0;
        switch (currentSelection) {
            case "Temperature" :
                temperatureMapInstance.setData({data: []});

                for (var data in historicalData) {
                    var dataPoint = {
                        x: historicalData[data].xCoordinate,
                        y: historicalData[data].yCoordinate,
                        value: historicalData[data].temperature * 100
                    };

                    if (!isDataExist(temperatureMapInstance.getData().data, dataPoint)) {
                        if (dataPoint.value > max) {
                            max = dataPoint.value;
                        }
                        temperatureMapInstance.addData(dataPoint);
                    }
                }
                temperatureMapInstance.setDataMax(max);
                break;
            case "Motion" :
                motionMapInstance.setData({data: []});
                for (var data in historicalData) {
                    var dataPoint = {
                        x: historicalData[data].xCoordinate,
                        y: historicalData[data].yCoordinate,
                        value: historicalData[data].motion * 10000
                    };
                    if (!isDataExist(motionMapInstance.getData().data, dataPoint)) {
                        if (dataPoint.value > max) {
                            max = dataPoint.value;
                        }
                        motionMapInstance.addData(dataPoint);
                    }
                }
                motionMapInstance.setDataMax(max);
                break;
            case "Humidity" :
                humidityMapInstance.setData({data: []});
                for (var data in historicalData) {
                    var dataPoint = {
                        x: historicalData[data].xCoordinate,
                        y: historicalData[data].yCoordinate,
                        value: historicalData[data].humidity * 10000
                    };
                    if (!isDataExist(humidityMapInstance.getData().data, dataPoint)) {
                        if (dataPoint.value > max) {
                            max = dataPoint.value;
                        }
                        humidityMapInstance.addData(dataPoint);
                    }
                }
                humidityMapInstance.setDataMax(max);
                break;
            case "Light" :
                lightMapInstance.setData({data: []});
                for (var data in historicalData) {
                    var dataPoint = {
                        x: historicalData[data].xCoordinate,
                        y: historicalData[data].yCoordinate,
                        value: historicalData[data].light * 10000
                    };
                    if (!isDataExist(lightMapInstance.getData().data, dataPoint)) {
                        if (dataPoint.value > max) {
                            max = dataPoint.value;
                        }
                        lightMapInstance.addData(dataPoint)
                    }
                }
                lightMapInstance.setDataMax(max);
                break;
        }

    };

    /**
     * To update the heat map on changing the slider.
     */
    var updateHeatMapOnSlideChange = function () {
        isSliderChanged = true;
        switch (currentSelection) {
            case "Temperature" :  temperatureMapInstance.setData({data:[]}); break;
            case "Motion" : motionMapInstance.setData({data:[]});break;
            case "Humidity" : humidityMapInstance.setData({data:[]});break;
            case "Light" : lightMapInstance.setData({data:[]}); break;
        }

        if (!isHistoricalView) {
            var max = rangeSlider.bootstrapSlider("getAttribute", 'max');
            var min = rangeSlider.bootstrapSlider("getAttribute", 'min');
            currentSliderValue = rangeSlider.bootstrapSlider("getValue");
            if (currentSliderValue == 0) {
                switch (currentSelection) {
                    case "Temperature" :
                        temperatureMapInstance.setData(currentTemperatureMap.getData());
                        break;
                    case "Motion" :
                        motionMapInstance.setData(currentMotionMap.getData());
                        break;
                    case "Humidity" :
                        humidityMapInstance.setData(currentHumidityMap.getData());
                        break;
                    case "Light" :
                        lightMapInstance.setData(currentLightMap.getData());
                        break;
                }
            } else {
                switch (currentSelection) {
                    case "Temperature" :
                        temperatureMapInstance.setData(temperatureMapData[min * -1 + currentSliderValue]);
                        break;
                    case "Motion" :
                        motionMapInstance.setData(motionMapData[min * -1 + currentSliderValue]);
                        break;
                    case "Humidity" :
                        humidityMapInstance.setData(humidityMapData[min * -1 + currentSliderValue]);
                        break;
                    case "Light" :
                        lightMapInstance.setData(lightMapData[min * -1 +  currentSliderValue]);
                        break;
                }
            }
        } else {
            currentSliderValue = historicalSlider.bootstrapSlider("getValue");
            updateHistoricData(historicalData[currentSliderValue]);
        }
    };


    /**
     * To make animation out of the heat-map for the real-time data.
     */
    $( "#play" ).click(function(e) {
        e.preventDefault();
        var currentSliderValue;
        var max;
        var min;
        if (!isHistoricalView) {
            currentSliderValue = rangeSlider.bootstrapSlider("getValue");
            max = rangeSlider.bootstrapSlider("getAttribute", 'max');
            min = rangeSlider.bootstrapSlider("getAttribute", 'min');
        } else {
            currentSliderValue = historicalSlider.bootstrapSlider("getValue");
            max = historicalSlider.bootstrapSlider("getAttribute", 'max');
            min = historicalSlider.bootstrapSlider("getAttribute", 'min');
        }
        if ($("#play").hasClass('play')) {
            $(this).addClass('pause').removeClass('play');
            $("#pau").removeClass("hidden");
            $("#pla").addClass("hidden");

            if (currentSliderValue == max) {
                rangeSlider.bootstrapSlider("setValue", min);
                currentSliderValue = min;
                updateHeatMapOnSlideChange();
            }

            for (var i = 0, len = max; currentSliderValue <= len; currentSliderValue++, i++) {
                timeouts.push(setTimeout(function (y) {
                    if (!isHistoricalView) {
                        rangeSlider.bootstrapSlider("setValue", y);
                    } else {
                        historicalSlider.bootstrapSlider("setValue", y);
                    }
                    updateHeatMapOnSlideChange();
                    if (y == 0) {
                        $("#pla").removeClass("hidden");
                        $("#pau").addClass("hidden");
                        $("#play").addClass('play').removeClass('pause');
                    }
                }, i * 2000, currentSliderValue));
            }
        } else {
            $("#pla").removeClass("hidden");
            $("#pau").addClass("hidden");
            if (timeouts) {
                for (var i = 0; i < timeouts.length; i++) {
                    clearTimeout(timeouts[i]);
                }
            }
            timeouts = [];
            $(this).addClass('play').removeClass('pause');
        }


    });

    $("form input:radio").change(function () {
        switch (currentSelection) {
            case "Temperature" :  temperatureMapInstance.setData({data:[]}); break;
            case "Motion" : motionMapInstance.setData({data:[]});break;
            case "Humidity" : humidityMapInstance.setData({data:[]});break;
            case "Light" : lightMapInstance.setData({data:[]}); break;
        }

        currentSelection = $(this).val();

        if (!isHistoricalView) {
            if (!isSliderChanged || currentSliderValue == rangeSlider.bootstrapSlider("getAttribute", "max")) {
                switch (currentSelection) {
                    case "Temperature" :
                        temperatureMapInstance.setData(currentTemperatureMap.getData());
                        break;
                    case "Motion" :
                        motionMapInstance.setData(currentMotionMap.getData());
                        break;
                    case "Humidity" :
                        humidityMapInstance.setData(currentHumidityMap.getData());
                        break;
                    case "Light" :
                        lightMapInstance.setData(currentLightMap.getData());
                        break;
                }
            } else {
                var min  = rangeSlider.bootstrapSlider("getAttribute", "min");
                switch (currentSelection) {
                    case "Temperature" :
                        temperatureMapInstance.setData(temperatureMapData[min * -1 + currentSliderValue]);
                        break;
                    case "Motion" :
                        motionMapInstance.setData(motionMapData[min * -1 + currentSliderValue]);
                        break;
                    case "Humidity" :
                        humidityMapInstance.setData(humidityMapData[min * -1 + currentSliderValue]);
                        break;
                    case "Light" :
                        lightMapInstance.setData(lightMapData[min * -1 +  currentSliderValue]);
                        break;
                }
            }
        } else {
            updateHeatMapOnSlideChange();
        }

    });

    $("#historic-toggle").click(function(){
        isHistoricalView = !isHistoricalView;
        $('.date-picker').slideToggle("slow");

        if (isHistoricalView) {
            $("#live-view").addClass("hidden");
            $("#historical-view").removeClass("hidden");
            switch (currentSelection) {
                case "Temperature" :  temperatureMapInstance.setData({data:[]}); break;
                case "Motion" : motionMapInstance.setData({data:[]});break;
                case "Humidity" : humidityMapInstance.setData({data:[]});break;
                case "Light" : lightMapInstance.setData({data:[]}); break;
            }
            historicalSlider.bootstrapSlider("setValue", historicalSlider.bootstrapSlider("getAttribute", "max"));
            currentSliderValue = historicalSlider.bootstrapSlider("getValue");
        } else {
            $("#live-view").removeClass("hidden");
            $("#historical-view").addClass("hidden");
            rangeSlider.bootstrapSlider("setValue", rangeSlider.bootstrapSlider("getAttribute", "max"));
            currentSliderValue = rangeSlider.bootstrapSlider("getValue");
            updateHeatMap();
        }
    });

    var updateHeatMap = function () {
        switch (currentSelection) {
            case "Temperature" :
                temperatureMapInstance.setData({data: []});
                temperatureMapInstance.setData(currentTemperatureMap.getData());
                break;
            case "Motion" :
                motionMapInstance.setData({data: []});
                motionMapInstance.setData(currentMotionMap.getData());
                break;
            case "Humidity" :
                humidityMapInstance.setData({data: []});
                humidityMapInstance.setData(currentHumidityMap.getData());
                break;
            case "Light" :
                lightMapInstance.setData({data: []});
                lightMapInstance.setData(currentLightMap.getData());
                break;
        }

    };

    /**
     * To get the historical data for a certain period of time.
     * @param tableName Name of the table to fetch the data from
     * @param timeFrom Start time
     * @param timeTo End time
     *
     */
    var getProviderData = function (tableName, timeFrom, timeTo, start, limit, sortBy) {
        var providerData = null;
        var providerUrl = context + '/api/batch-provider?action=getData&tableName=' + tableName;

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
                notifyUser(err, "danger", DANGER_TIMEOUT, "top-center");
            }
        });
        return providerData;
    };

    /**
     * To get the historical data for a certain period of time.
     * @param tableName Name of the table to fetch the data from
     * @param timeFrom Start time
     * @param timeTo End time
     *
     */
    var getHistoricalData = function (tableName, timeFrom) {
        var providerData = null;
        var providerUrl = context + '/api/batch-provider?action=getHistoricalData&tableName=' + tableName;

        if (timeFrom) {
            providerUrl += '&timeFrom=' + timeFrom;
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
                notifyUser(err, "danger", DANGER_TIMEOUT, "top-center");
            }
        });
        return providerData;
    };
}(window, document, context));