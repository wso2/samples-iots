var modalPopup = ".modal";
var modalPopupContainer = modalPopup + " .modal-content";
var modalPopupContent = modalPopup + " .modal-content";
var isAddDeviceMode =false;

/*
 * set popup maximum height function.
 */
function setPopupMaxHeight() {
	var maxHeight = "max-height";
	var marginTop = "margin-top";
	var body = "body";
	$(modalPopupContent).css(maxHeight, ($(body).height() - ($(body).height() / 100 * 30)));
	$(modalPopupContainer).css(marginTop, (-($(modalPopupContainer).height() / 2)));
}

/*
 * show popup function.
 */
function showPopup() {
	$(modalPopup).modal('show');
}

/*
 * hide popup function.
 */
function hidePopup() {
	$(modalPopupContent).html("");
	$(modalPopupContent).removeClass("operation-data");
	$(modalPopup).modal('hide');
	$('body').removeClass('modal-open').css('padding-right','0px');
	$('.modal-backdrop').remove();
}

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
    var selectedDate;

    $("#show-analytics").on('click', function () {
        if ($("#show-analytics").hasClass("show-analytics")) {
            $("#radio-selections").removeClass("hidden");
            $('#image canvas').removeClass('hidden');
            $("#show-analytics").addClass("hide-analytics").removeClass("show-analytics");
            $("#analytics").html("Hide Analytics");
            $('.slider-wrapper').show(1000);
            $(".slider-wrapper").click();
            //rangeSlider.bootstrapSlider('setValue', -1, true, true);
        } else {
            $("#radio-selections").addClass("hidden");
            $('#image canvas').addClass('hidden');
            $("#show-analytics").removeClass("hide-analytics").addClass("show-analytics");
            $("#analytics").html("Show Analytics");
            $('.slider-wrapper').hide(1000);
        }
    });

	$("#add-device").on('click', function () {
		if (isAddDeviceMode) {
			isAddDeviceMode = false;
			$("#device").text("Add a device");
			$('#image').css('cursor', 'default');
		} else {
			isAddDeviceMode = true;
			$('#image').css('cursor', 'url("https://s3-us-west-2.amazonaws.com/s.cdpn.io/9632/happy.png"),auto');
			$("#device").text("Cancel");
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

        if (!isHistoricalView) {
            if (!isSliderChanged || currentSliderValue == 0) {
                switch (currentSelection) {
                    case "Temperature" :
                        temperatureMapInstance.addData(temperatureDataPoint);
                        temperatureMapInstance.repaint();
                        break;
                    case "Motion" :
                        motionMapInstance.addData(motionDataPoint);
                        motionMapInstance.repaint();
                        break;
                    case "Humidity" :
                        humidityMapInstance.addData(humidityDataPoint);
                        humidityMapInstance.repaint();
                        break;
                    case "Light" :
                        lightMapInstance.addData(lightDataPoint);
                        lightMapInstance.repaint();
                        break;
                }
            }
        }

        if (temperatureMapData.length == 10) {
            temperatureMapData.shift();
        }
        if (motionMapData.length == 10) {
            motionMapData.shift();
        }
        if (humidityMapData.length == 10) {
            humidityMapData.shift();
        }
        if (lightMapData.length == 10) {
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
        buildingId = "WSO2";
        floorId = "5th floor";

        rangeSlider = $("#range-slider").bootstrapSlider({
            ticks: [-3, -2, -1, 0],
            ticks_labels: ['-3h', '-2h', '-1h', 'current'],
            formatter: function(value) {
                return value + "h";
            }
        });
        rangeSlider.bootstrapSlider('setAttribute', 'min', -3);
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

        $('#range-slider').on("slide", function () {
            updateHeatMapOnSlideChange();

        }).on("change", function () {
            updateHeatMapOnSlideChange();
        });

        $('#historical-slider').on("slide", function () {
            updateHeatMapOnSlideChange();

        }).on("change", function () {
            updateHeatMapOnSlideChange();
        });

        $('input[name="daterange"]').datepicker({
            orientation: "auto",
            endDate: "+0d",
            autoclose: true
        }).on("changeDate", function(e) {
            // var endDate = new Date(e.date);
            // endDate.setHours(endDate.getHours() + 24);
            selectedDate = e.date;
            var endDate = new Date(e.date);
            endDate.setHours(endDate.getHours() + 24);

            var fromDate = new Date(e.date);
            fromDate.setHours(fromDate.getHours() + 23);
            var historicalData = getProviderData("ORG_WSO2_FLOOR_SUMMARIZED_DEVICE_FLOOR_SENSORSTREAM", fromDate.getTime(), endDate.getTime());
            updateHistoricData(historicalData);
        });

        $('#image canvas').addClass('hidden');
    });

    /**
     * To update the heat map
     * @param data
     */
    var updateHistoricData = function(historicalData) {

        switch (currentSelection) {
            case "Temperature" :
                temperatureMapInstance.setData({data: []});
                for (var data in historicalData) {
                    var dataPoint = {
                        x: historicalData[data].xCoordinate,
                        y: historicalData[data].yCoordinate,
                        value: historicalData[data].temperature
                    };
                    temperatureMapInstance.addData(dataPoint);
                }
                temperatureMapInstance.repaint();
                break;

            case "Motion" :
                motionMapInstance.setData({data: []});
                for (var data in historicalData) {
                    var dataPoint = {
                        x: historicalData[data].xCoordinate,
                        y: historicalData[data].yCoordinate,
                        value: historicalData[data].motion
                    };
                    motionMapInstance.addData(dataPoint);
                }
                motionMapInstance.repaint();
                break;
            case "Humidity" :
                humidityMapInstance.setData({data: []});
                for (var data in historicalData) {
                    var dataPoint = {
                        x: historicalData[data].xCoordinate,
                        y: historicalData[data].yCoordinate,
                        value: historicalData[data].humidity
                    };
                    humidityMapInstance.addData(dataPoint);
                }
                humidityMapInstance.repaint();
                break;
            case "Light" :
                lightMapInstance.setData({data: []});
                for (var data in historicalData) {
                    var dataPoint = {
                        x: historicalData[data].xCoordinate,
                        y: historicalData[data].yCoordinate,
                        value: historicalData[data].light
                    };
                    lightMapInstance.addData(dataPoint);
                }
                lightMapInstance.repaint();
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
                var endDate = new Date();
                endDate.setHours(endDate.getHours() + currentSliderValue);
                var fromDate = new Date();
                fromDate.setHours(fromDate.getHours() + currentSliderValue -1);
                var recentPastData = getProviderData("ORG_WSO2_FLOOR_DEVICE_SENSORSTREAM", fromDate.getTime(), endDate.getTime());
                var length = recentPastData.length;
                switch (currentSelection) {
                    case "Temperature" :
                        for (var i = 0; i < length; i++) {
                            var dataPoint = {
                                x: recentPastData[i].xCoordinate,
                                y: recentPastData[i].yCoordinate,
                                value: recentPastData[i].temperature
                            };
                            temperatureMapInstance.addData(dataPoint);
                        }
                        temperatureMapInstance.repaint();
                        break;
                    case "Motion" :
                        for (var i = 0; i < length; i++) {
                            var dataPoint = {
                                x: recentPastData[i].xCoordinate,
                                y: recentPastData[i].yCoordinate,
                                value: recentPastData[i].motion
                            };
                            motionMapInstance.addData(dataPoint);
                        }
                        motionMapInstance.repaint();
                        break;
                    case "Humidity" :
                        for (var i = 0; i < length; i++) {
                            var dataPoint = {
                                x: recentPastData[i].xCoordinate,
                                y: recentPastData[i].yCoordinate,
                                value: recentPastData[i].humidity
                            };
                            humidityMapInstance.addData(dataPoint);
                        }
                        humidityMapInstance.repaint();
                        break;
                    case "Light" :
                        for (var i = 0; i < length; i++) {
                            var dataPoint = {
                                x: recentPastData[i].xCoordinate,
                                y: recentPastData[i].yCoordinate,
                                value: recentPastData[i].light
                            };
                            lightMapInstance.addData(dataPoint);
                        }
                        lightMapInstance.repaint();
                        break;
                }
            }
        } else {
            var max =  historicalSlider.bootstrapSlider("getAttribute", 'max');
            var min = historicalSlider.bootstrapSlider("getAttribute", 'min');
            currentSliderValue = historicalSlider.bootstrapSlider("getValue");
            var endDate = new Date(selectedDate);
            endDate.setHours(endDate.getHours() + currentSliderValue);
            var fromDate = new Date(selectedDate);
            fromDate.setHours(fromDate.getHours() + currentSliderValue -1);
            var historicalData = getProviderData("ORG_WSO2_FLOOR_SUMMARIZED_DEVICE_FLOOR_SENSORSTREAM", fromDate.getTime(), endDate.getTime());
            var length = historicalData.length;
            switch (currentSelection) {
                case "Temperature" :
                    temperatureMapInstance.setData({data: []});
                    for (var i = 0; i < length; i++) {
                        var dataPoint = {
                            x: historicalData[i].xCoordinate,
                            y: historicalData[i].yCoordinate,
                            value: historicalData[i].temperature
                        };
                        temperatureMapInstance.addData(dataPoint);
                    }
                    temperatureMapInstance.repaint();
                    break;
                case "Motion" :
                    motionMapInstance.setData({data: []});
                    for (var i = 0; i < length; i++) {
                        var dataPoint = {
                            x: historicalData[i].xCoordinate,
                            y: historicalData[i].yCoordinate,
                            value: historicalData[i].motion
                        };
                        motionMapInstance.addData(dataPoint);
                    }
                    motionMapInstance.repaint();
                    break;
                case "Humidity" :
                    humidityMapInstance.setData({data: []});
                    for (var i = 0; i < length; i++) {
                        var dataPoint = {
                            x: historicalData[i].xCoordinate,
                            y: historicalData[i].yCoordinate,
                            value: historicalData[i].humidity
                        };
                        humidityMapInstance.addData(dataPoint);
                    }
                    humidityMapInstance.repaint();
                    break;
                case "Light" :
                    lightMapInstance.setData({data: []});
                    for (var i = 0; i < length; i++) {
                        var dataPoint = {
                            x: historicalData[i].xCoordinate,
                            y: historicalData[i].yCoordinate,
                            value: historicalData[i].light
                        };
                        lightMapInstance.addData(dataPoint);
                    }
                    lightMapInstance.repaint();
                    break;
            }
            // if (historicalData) {
            //     data = {data: []};
            //     heatmapInstance.setData(data);
            //     heatmapInstance = h337.create(heatMapConfig);
            //     var length = historicalData.length * ((currentSliderValue-min) / (max-min));
            //     for (var i = 0; i < length; i++) {
            //         var dataPoint = {
            //             x: historicalData[i].xCoordinate,
            //             y: historicalData[i].yCoordinate,
            //             value: historicalData[i].temperature
            //         };
            //         heatmapInstance.addData(dataPoint);
            //     }
            // } else {
            //
            // }
        }
    };


    /**
     * To make animation out of the heat-map for the real-time data.
     */
    $( "#play" ).click(function(e) {
        e.preventDefault();
        if (!isHistoricalView) {
            if ($("#play").hasClass('play')) {
                $(this).addClass('pause').removeClass('play');
                $("#pau").removeClass("hidden");
                $("#pla").addClass("hidden");
                var currentSliderValue = rangeSlider.bootstrapSlider("getValue");

                if (currentSliderValue == 0) {
                    rangeSlider.bootstrapSlider("setValue", -3);
                    currentSliderValue = -3;
                    updateHeatMapOnSlideChange();
                }

                for (var i = 0, len = rangeSlider.bootstrapSlider("getAttribute", "max"); currentSliderValue <= len; currentSliderValue++, i++) {
                    timeouts.push(setTimeout(function (y) {
                        rangeSlider.bootstrapSlider("setValue", y);
                        updateHeatMapOnSlideChange();

                        if (y == 0) {
                            $("#pla").removeClass("hidden");
                            $("#pau").addClass("hidden");
                            $("#play").addClass('play').removeClass('pause');
                        }
                    }, i * 500 * 2, currentSliderValue));
                }
            } else {
                $("#pla").removeClass("hidden");
                $("#pau").addClass("hidden");
                if (timeouts) {
                    for (var i = 0; i < timeouts.length; i++) {
                        clearTimeout(timeouts[i]);
                    }
                }
                $(this).addClass('play').removeClass('pause');
            }
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
            var endDate = new Date(selectedDate);
            endDate.setHours(endDate.getHours() + 24);

            var fromDate = new Date(selectedDate);
            fromDate.setHours(fromDate.getHours() + 23);
            var historicalData = getProviderData("ORG_WSO2_FLOOR_SUMMARIZED_DEVICE_FLOOR_SENSORSTREAM", fromDate.getTime(), endDate.getTime());
            updateHistoricData(historicalData);
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
        } else {
            $("#live-view").removeClass("hidden");
            $("#historical-view").addClass("hidden");
            updateHeatMap();
        }
    });

	//$('#image').bind('click', function (ev) {
	//
	//});

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

	loadDevices();
}(window, document, context));

function addDevice () {
	var deviceIdValue = document.getElementsByName('deviceId')[0].value;;
	var xCordValue = document.getElementsByName('xCord')[0].value;;
	var yCordValue = document.getElementsByName('yCord')[0].value;;
	var floorIdValue = $("#image").attr("floorId");
	var buildingIdValue = $("#image").attr("buildingId");
	var deviceData = {deviceId:deviceIdValue, buildingId:buildingIdValue, floorNumber:floorIdValue, xCord:xCordValue, yCord:yCordValue };
	var addDeviceApi = "/senseme/device/enroll";
	invokerUtil.post(addDeviceApi, deviceData, function(data, textStatus, jqXHR){
		if (jqXHR.status == 200) {
			placeDevice(deviceId, xCordValue, yCordValue)
		}
	}, function(jqXHR){
		if (jqXHR.status == 400) {
			console.log("error")
		} else {
			var response = JSON.parse(jqXHR.responseText).message;

		}
	},"application/json","application/json");
	hidePopup();
}


function placeDevice(deviceId, x, y, status) {

	var rect = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
	rect.setAttributeNS(null,"id", "myrect-" + deviceId);
	rect.setAttributeNS(null,"fill", "grey");
	if (status) {
		if (status == "ACTIVE") {
			rect.setAttributeNS(null,"fill", "blue");
		} else if (status == "FAULT") {
			rect.setAttributeNS(null,"fill", "red");
		}
	}
	rect.setAttributeNS(null,"stroke", "black");
	rect.setAttributeNS(null,"stroke-width", "5");
	rect.setAttributeNS(null,"x", x);
	rect.setAttributeNS(null,"y", y);
	rect.setAttributeNS(null,"width", "30");
	rect.setAttributeNS(null,"height", "30");
	var svg = document.getElementById("svg");
	svg.appendChild(rect);
}

function loadDevices() {
	var floorIdValue = $("#image").attr("floorId");
	var buildingIdValue = $("#image").attr("buildingId");
	var deviceApi = "/senseme/building/" + buildingIdValue + "/" + floorIdValue + "/devices";
	invokerUtil.get(deviceApi, function (data, textStatus, jqXHR) {
		if (jqXHR.status == 200) {
			var devices = JSON.parse(data);
			for(var i = 0; i < devices.length; i++) {
				var device = devices[i];
				console.log(device);
				placeDevice(device.deviceId, device.xCord, device.yCord, device.status);
			}
		}
	}, function (jqXHR) {
	}, "application/json");
}

