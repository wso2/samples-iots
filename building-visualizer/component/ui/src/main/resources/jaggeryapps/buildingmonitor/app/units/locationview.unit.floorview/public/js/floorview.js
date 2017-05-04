var modalPopup = ".modal";
var modalPopupContainer = modalPopup + " .modal-content";
var modalPopupContent = modalPopup + " .modal-content";
var isAddDeviceMode =false;
var sensorConfigs  = null;
var heatMapInstances = {};
var currentHeatmapInstance = {};
var sensorValues = {};

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
    var floorId;
    var buildingId;
    var rangeSlider;
    var historicalSlider;
    var isSliderChanged = false;
    var currentSliderValue = 0;
    var currentSelection = $("form input:radio").val();
    var isHistoricalView = false;
    var timeouts = [];
    var heatMapConfig = {
        container: document.getElementById('image'),
        radius: 100,
        maxOpacity: .5,
        minOpacity: 0,
        blur: .75,
        onExtremaChange: function onExtremaChange(data) {
            updateLegend(data);
        }
    };
    var historicalData = [];
    var recentPastData = [];
    var selectedDate;
    var lastFetchedTime;
    var gradientCfg = {};
    var legendCanvas = document.createElement('canvas');
    var legendCtx = legendCanvas.getContext('2d');
    legendCanvas.width = 100;
    legendCanvas.height = 10;

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

    $("#add-device").on('click', function () {
        if (isAddDeviceMode) {
            isAddDeviceMode = false;
            $("#device").text("Add a device");
            $('#image').css('cursor', 'default');
        } else {
            isAddDeviceMode = true;
            getDeviceTypes();
            $('#image').css('cursor', 'crosshair');
            $("#device").text("Cancel");
        }
    });

    /**
     * Creates the heatMap.
     */
    var createHeatMap = function () {
        if ($.isEmptyObject(heatMapInstances) && sensorConfigs != null) {

            for (var key in sensorConfigs) {
                if (sensorConfigs.hasOwnProperty(key)) {
                    var instance;
                    instance = h337.create(heatMapConfig);
                    instance.setDataMin(sensorConfigs[key].min);
                    instance.setDataMax(sensorConfigs[key].max);
                    heatMapInstances[key]=instance;
                }
            }
        }
        if ($.isEmptyObject(currentHeatmapInstance) && sensorConfigs != null) {
            var config = {
                container: document.getElementById('heat-map-hidden'),
                radius: 100,
                maxOpacity: .5,
                minOpacity: 0,
                blur: .75,
                onExtremaChange: function onExtremaChange(data) {
                    updateLegend(data);
                }
            };

            for (var k in sensorConfigs) {
                if (sensorConfigs.hasOwnProperty(k)) {
                    var currentInstance;
                    currentInstance = window.h337.create(config);
                    currentInstance.setDataMin(sensorConfigs[k].min);
                    currentInstance.setDataMax(sensorConfigs[k].max);
                    currentHeatmapInstance[k]=currentInstance;
                }
            }
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

        for (var key in sensorConfigs) {
            if (sensorConfigs.hasOwnProperty(key)) {
                if (dataValues[key]) {
                    var dataPoint = {
                        x: dataValues.location.coordinates[0],
                        y: dataValues.location.coordinates[1],
                        value : dataValues[key] * Math.pow(10, sensorConfigs[key].decimal)
                    };
                    var currentData = currentHeatmapInstance[key].getData();
                    currentData.data = processHeatMapDataPoints(currentData.data, dataPoint);
                    currentData.data.push(dataPoint);
                    currentHeatmapInstance[key].setData(currentData);

                }
            }

            if (sensorValues[key].length==16){
                sensorValues[key].shift();
            }
            sensorValues[key].push(currentHeatmapInstance[key].getData());
        }

        //problem in light data point

        if (!isHistoricalView) {
            if (!isSliderChanged || currentSliderValue == rangeSlider.bootstrapSlider('getAttribute', 'max')) {
                heatMapInstances[currentSelection].setData({data: []});
                heatMapInstances[currentSelection].setData(currentHeatmapInstance[currentSelection].getData());
            }
        }
    };

    /**
     * To initialize the web-sockets to get the real-time data.
     */
    var intializeWebsockets = function () {
        var analyticsUrl = "wss://localhost:9445";
        $.ajax({
            url:context + '/api/analytics/',
            method: "GET",
            contentType: "application/json",
            async: false,
            success: function (data) {
                analyticsUrl = data;
            },
            error : function (err) {
            }
        });

        var webSocketURL = analyticsUrl + '/outputwebsocket/Floor-Analysis-WebSocketLocal-DeviceFloorEvent';

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

        webSocketURL = analyticsUrl + '/outputwebsocket/Floor-Analysis-WebSocketLocal-AlertEvent';
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
        console.log("current");
        console.log(currentSelection);
        getSensorConfiguration();
        intializeWebsockets();
        createHeatMap();
        floorId = $("#image").attr("floorId");
        buildingId = $("#image").attr("buildingId");

        rangeSlider = $("#range-slider").bootstrapSlider({
            ticks: [-30, -25, -20, -15,-10, -5, 0],
            ticks_labels: ['-3h', '-2.5h', '-2h', '-1.5h', '-1h', '0.5h', 'current'],
            step : 5,
            formatter: function(value) {
                var full = value / 10;
                return full + "h";
            }
        });
	    rangeSlider.bootstrapSlider('refresh');
        rangeSlider.bootstrapSlider('setAttribute', 'min', -30);
        rangeSlider.bootstrapSlider('setAttribute', 'max', 0);
        rangeSlider.bootstrapSlider('setValue', 0); 

        historicalSlider = $("#historical-slider").bootstrapSlider({
            ticks: [0, 4, 8, 12, 16, 20, 24],
            ticks_labels: ['0h', '4h', '8h', '12h', '16h', '20h', '24h'],
            formatter: function(value) {
                return value + "h";
            }
        });
	    historicalSlider.bootstrapSlider('refresh');
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
            $("#historical-view").removeClass("hidden");
            selectedDate = e.date;
            var date = new Date(e.date);
            date.setHours(date.getHours()-1);
            historicalData = getHistoricalData("getHistoricalData","ORG_WSO2_FLOOR_SUMMARIZED_DEVICE_FLOOR_SENSORSTREAM", date.getTime());
            updateHistoricData(historicalData[currentSliderValue]);
        });

        var date = new Date();
        date.setMinutes(date.getMinutes() - 210);
        recentPastData = getHistoricalData("getRecentPastData","ORG_WSO2_FLOOR_SUMMARIZED_DEVICE_FLOOR_SENSORSTREAM", date.getTime());
        lastFetchedTime = new Date().getTime();
        //$('#image canvas').addClass('hidden');
        loadNotifications();

        $("#svg rect").click(function (e) {
            e.preventDefault();
            var id = $(this).attr("id");
            var deviceId = id.substring(7);
            window.open(context + "/senseme?id="+deviceId,"_self");

        });

    });

    var isDataExist = function(dataArray, dataPoint) {
        for (var data in dataArray) {
            if (dataArray[data].x == dataPoint.x && dataArray[data].y == dataPoint.y) {
                return true;
            }
        }
    };

    var loadNotifications = function() {
        var messageSideBar = ".sidebar-messages";
        if ($("#right-sidebar").attr("is-authorized") == "true") {
            var notifications = $("#notifications");
            var currentUser = notifications.data("currentUser");

            var serviceURL = backendEndBasePath + "/notifications?status=NEW";

            $.template("notification-listing", notifications.attr("src"), function (template) {
                var currentDate = new Date();
                currentDate.setHours(currentDate.getHours() - 24);
                var endDate = new Date();
                var data = getProviderData("ORG_WSO2_FLOOR_ALERTNOTIFICATIONS", currentDate.getTime(), endDate.getTime(), 0, 20, "DESC");

                if (data) {
                    var viewModel = {};
                    var notifications = [];

                    for (var index in data) {
                        var notification = {};
                        notification.heading = data[index].type + " Alert";
                        notification.description = data[index].type + " value is " + data[index].value.toFixed(2) + ". " + data[index].information;
                        notification.timeStamp =  new Date(data[index].timeStamp)
                        notifications.push(notification);
                    }

                    viewModel.notifications = notifications;
                    $(messageSideBar).html(template(viewModel));
                } else {
                    $(messageSideBar).html("<h4 class ='message-danger text-center'>No new notifications found</h4>");
                }
            });
        } else {
            $(messageSideBar).html("<h4 class ='message-danger text-center'>You are not authorized to view notifications</h4>");
        }
    };

    $("#notification-bubble-wrapper").click(function() {
        loadNotifications();

    });

    /**
     * To update the heat map
     * @param historicalData HistoricalData, derived
     */
    var updateHistoricData = function(historicalData) {
        var max = 0;
        heatMapInstances[currentSelection].setData({data: []});
        for (var data in historicalData) {
            var dataPoint = {
                x: historicalData[data].xCoordinate,
                y: historicalData[data].yCoordinate,
                value: historicalData[data][currentSelection] * Math.pow(10, sensorConfigs[currentSelection].decimal)
            };

            if (!isDataExist(heatMapInstances[currentSelection].getData().data, dataPoint)) {
                if (dataPoint.value > max) {
                    max = dataPoint.value;
                }
                heatMapInstances[currentSelection].addData(dataPoint);
            }
        }
        heatMapInstances[currentSelection].setDataMax(sensorConfigs[currentSelection].max);
        heatMapInstances[currentSelection].setDataMin(sensorConfigs[currentSelection].min);
    };

    /**
     * To update the heat map on changing the slider.
     */
    var updateHeatMapOnSlideChange = function () {
        var minuteToMilliseconds = 1800000;
        isSliderChanged = true;
        heatMapInstances[currentSelection].setData({data:[]});

        if (!isHistoricalView) {
            var currentTime = new Date().getTime();

            if (currentTime - lastFetchedTime > minuteToMilliseconds) {  var date = new Date();
                date.setMinutes(date.getMinutes() - 210);
                recentPastData = getHistoricalData("getRecentPastData","ORG_WSO2_FLOOR_SUMMARIZED_DEVICE_FLOOR_SENSORSTREAM", date.getTime());
                lastFetchedTime = new Date().getTime();
            }
            var max = rangeSlider.bootstrapSlider("getAttribute", 'max');
            var min = rangeSlider.bootstrapSlider("getAttribute", 'min');
            currentSliderValue = rangeSlider.bootstrapSlider("getValue");
            if (currentSliderValue == 0) {
                heatMapInstances[currentSelection].setData(currentHeatmapInstance[currentSelection].getData());
            } else {
                var effective_value = (currentSliderValue * -1)/5;
                updateHistoricData(recentPastData[6-effective_value]);
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

            var increment = 5;
            if (isHistoricalView) {
                increment = 1;
            }
            for (var i = 0, len = max; currentSliderValue <= len; currentSliderValue += increment, i++) {
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
                }, i * 500, currentSliderValue));
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
        heatMapInstances[currentSelection].setData({data:[]});

        currentSelection = $(this).val();

        if (!isHistoricalView) {
            if (!isSliderChanged || currentSliderValue == rangeSlider.bootstrapSlider("getAttribute", "max")) {
                heatMapInstances[currentSelection].setData(currentHeatmapInstance[currentSelection].getData());
            } else {
                updateHeatMapOnSlideChange();
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
            heatMapInstances[currentSelection].setData(currentHeatmapInstance[currentSelection].getData());
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

    //$('#image').bind('click', function (ev) {
    //
    //});

    var updateHeatMap = function () {
        heatMapInstances[currentSelection].setData(currentHeatmapInstance[currentSelection].getData());
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
        var providerUrl = context + '/api/batch-provider?action=getData&tableName=' + tableName + "&buildingId=" + buildingId + "&floorId=" + floorId;

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
     *
     */
    var getHistoricalData = function (action,tableName, timeFrom) {
        var providerData = null;
        var providerUrl = context + '/api/batch-provider?action=' + action + '&tableName=' + tableName +
            '&buildingId=' +  buildingId + "&floorId=" + floorId;

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


    function updateLegend(data) {
        // the onExtremaChange callback gives us min, max, and the gradientConfig
        // so we can update the legend
        $('min').innerHTML = data.min;
        $('max').innerHTML = data.max;
        // regenerate gradient image
        if (data.gradient != gradientCfg) {
            gradientCfg = data.gradient;
            var gradient = legendCtx.createLinearGradient(0, 0, 100, 1);
            for (var key in gradientCfg) {
                gradient.addColorStop(key, gradientCfg[key]);
            }
            legendCtx.fillStyle = gradient;
            legendCtx.fillRect(0, 0, 100, 10);
            $('gradient').src = legendCanvas.toDataURL();
        }
    }

    /**
     * Load devices;
     */
    loadDevices();
}(window, document, context));

/**
 * Add a new device;
 */
function addDevice () {
    var deviceIdValue = document.getElementsByName('deviceId')[0].value;;
    var xCordValue = document.getElementsByName('xCord')[0].value;;
    var yCordValue = document.getElementsByName('yCord')[0].value;;
    var floorIdValue = $("#image").attr("floorId");
    var buildingIdValue = $("#image").attr("buildingId");
    var deviceType = $("#deviceType").val();
    var deviceData = {deviceId:deviceIdValue, buildingId:buildingIdValue, floorNumber:floorIdValue, xCord:xCordValue, yCord:yCordValue };
    var addDeviceApi = "/senseme/device/enroll?deviceType=" + deviceType;

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


/**
 * Place the device into the image
 * @param deviceId
 * @param x
 * @param y
 * @param status
 */
function placeDevice(deviceId, x, y, status) {
    var rect = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
    rect.setAttributeNS(null,"id", "myrect-" + deviceId);
    rect.setAttributeNS(null,"fill", "#6C7A89");
    if (status) {
        if (status == "ACTIVE") {
            rect.setAttributeNS(null,"fill", "#2574A9");
        } else if (status == "FAULT") {
            rect.setAttributeNS(null,"fill", "#E74C3C");
        }
    }
    rect.setAttributeNS(null,"stroke", "black");
    rect.setAttributeNS(null,"stroke-width", "1");
    rect.setAttributeNS(null,"x", x-15);
    rect.setAttributeNS(null,"y", y-15);
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
            console.log(devices);
            for(var i = 0; i < devices.length; i++) {
                var device = devices[i];
                placeDevice(device.deviceId, device.xCord, device.yCord, device.status);
            }
        }
    }, function (jqXHR) {
    }, "application/json");
}

/**
 * to get existing device types
 */
function getDeviceTypes() {
    
}

// $("#device").click(getDeviceTypes);

/**
 * to get sensor configuration details
 */
function getSensorConfiguration() {
    var providerUrl = context + '/api/sensors/all';
    $.ajax({
        url: providerUrl,
        method: "GET",
        contentType: "application/json",
        async: false,
        success: function (data) {
            sensorConfigs = data;
            console.log("success");
            console.log(data);
            for (var key in sensorConfigs) {
                if (sensorConfigs.hasOwnProperty(key)) {
                    sensorValues[key]={};
                }
            }
            console.log(data);

        },
        error: function (err) {
            console.log("error");
            console.log(err);
        }
    });
}
