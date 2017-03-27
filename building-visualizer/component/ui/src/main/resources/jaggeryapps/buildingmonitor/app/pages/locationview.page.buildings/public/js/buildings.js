var webSocket;
var floorData = [];
var rangeSlider;
var historicalSlider;
var timer;
var sliderPoint = 1;
var sliderPointMax = 10;
var historySliderPointMax = 24;
var sliderPointMin = 1;
var historicalData = [];
var isDatePick = false;
var buildingId;
var numOfFloors;
var isLive = true;
var isPause = false;

/**
 * To show received data
 * @param sliderVal value of slider.
 * @param sliderMax maximum value of slider
 * @param buildingData data of building
 */
function handleData(sliderVal, sliderMax, buildingData) {
    var numOfFloors = buildingData.length;
    var index = sliderMax - sliderVal;
    for (var i = 0; i < numOfFloors; i++) {
        if (buildingData[i].length == 0) {
            continue;
        } else {
            if (buildingData[i][index] != null) {
                displyaData(i + 1, buildingData[i][index]);
            } else {
                displyaError(i + 1);
            }
        }
    }
}

/**
 * To display received data.
 * @param floorId floor number
 * @param data relative data for floor number
 */
function displyaData(floorId,data) {
    var light;
    var motion;
    if(data.light<500){
        light = "ON";
    }else if (data.light>500){
        light="OFF";
    }

    if (data.motion>0.5){
        motion = "TRUE";
    }else{
        motion = "FALSE";
    }
    $( "#"+floorId ).find( "#temperature").text(parseInt(data.temperature)+"°C");
    $( "#"+floorId ).find( "#humidity").text(parseInt(data.humidity).toFixed(2));
    $( "#"+floorId ).find( "#light").text(light);
    $( "#"+floorId ).find( "#motion").text(motion);
    $( "#"+floorId ).find( "#airquality").text(parseInt(data.airQuality).toFixed(2));
}

/**
 * To display error message.
 * @param floorId floor number
 */
function displyaError(floorId) {
    $( "#"+floorId ).find( "#temperature").text("-");
    $( "#"+floorId ).find( "#humidity").text("-");
    $( "#"+floorId ).find( "#light").text("-");
    $( "#"+floorId ).find( "#motion").text("-");
    $( "#"+floorId ).find( "#airquality").text("-");
}

// /**
//  * To display received data.
//  * @param floorId floor number
//  * @param data relative data for floor number
//  */
// function displyaData(floorId, data) {
//     var canvas = document.getElementById(floorId);
//     clearCanvas(canvas);
//     var ctx = canvas.getContext("2d");
//     ctx.font = "14px Arial";
//     ctx.fillText("Temperature: " + data.temperature, 10, 10);
//     ctx.fillText("Air Quality: " + data.airQuality, 10, 30);
//     ctx.fillText("Humidity: " + data.humidity, 10, 50);
//     ctx.fillText("Light: " + data.light, 10, 70);
//     ctx.fillText("Motion: " + data.motion, 10, 90);
//
// }
//
// /**
//  * To display error message.
//  * @param floorId floor number
//  */
// function displyaError(floorId) {
//     var canvas = document.getElementById(floorId);
//     clearCanvas(canvas);
//     if (canvas != null) {
//         var ctx = canvas.getContext("2d");
//         ctx.font = "14px Arial";
//         ctx.fillText("No data", 10, 10);
//     }
// }

/**
 * Initialize floor data arrays
 * @param val number of floors.
 */
function createDataArrays(val) {
    for (var i = 0; i < val; i++) {
        floorData[i] = [];
        historicalData[i] = [];
    }
}

/**
 * Initialize the web-sockets to get the real-time data.
 */
function createWebSocket(host) {

    if (!("WebSocket" in window)) {
        console.log("browser doens't support");
        //add meaningful message
    } else {
        //The user has WebSockets
        console.log("browser support");

        connect();

        function connect() {
            try {
                webSocket = new WebSocket(host);

                webSocket.onopen = function () {
                    console.log("on open");
                };
                webSocket.onmessage = function (msg) {
                    console.log("on message");
                    handleRealTimeData(JSON.parse(msg.data));
                };
                webSocket.onclose = function () {
                    console.log("on close");
                };
                webSocket.error = function (err) {
                    console.log(err);
                }

            } catch (exception) {
                console.log(exception);
            }
        }
    }
}

/**
 * To manage receiving real time data .
 * @param data received data
 */
function handleRealTimeData(data) {
    rangeSlider.bootstrapSlider('setValue', sliderPointMax);
    if (data.building === buildingId) {
        var floorId = data.floor;
        var fId = parseInt(floorId) - 1;
        if (floorData[fId].length == sliderPointMax) {
            floorData[fId].shift();
            floorData[fId].push(data);
        } else {
            floorData[fId].push(data);
        }
        displyaData(floorId, data);
    }
}

/**
 * To get query param .
 * @param key key value
 */
function getUrlVar(key) {
    var result = new RegExp(key + "=([^&]*)", "i").exec(window.location.search);
    return result && unescape(result[1]) || "";
}

// /**
//  * To clear data on canvas.
//  * @param cnv canvas
//  */
// function clearCanvas(cnv) {
//     var ctx = cnv.getContext('2d');     // gets reference to canvas context
//     ctx.beginPath();    // clear existing drawing paths
//     ctx.save();         // store the current transformation matrix
//
//     // Use the identity matrix while clearing the canvas
//     ctx.setTransform(1, 0, 0, 1, 0, 0);
//     ctx.clearRect(0, 0, cnv.width, cnv.height);
//
//     ctx.restore();        // restore the transform
// }

/**
 * Calling batch provider api.
 * @param tableName table name
 * @param timeFrom starting time
 * @param timeTo end time
 * @param start starting point
 * @param limit limit
 * @param sortBy sorting method
 * @param buildingId id of the building
 * @param floorCount number of floors
 * @param action action to perform
 */
var getProviderData = function (tableName, timeFrom, timeTo, start, limit, sortBy, buildingId, floorCount, action) {
    var providerData = null;

    var providerUrl = context + '/api/batch-provider?action=' + action + '&tableName=' + tableName;

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
        providerUrl += "&sortBy=" + sortBy
    }
    if (buildingId) {
        providerUrl += "&buildingId=" + buildingId
    }
    if (floorCount) {
        providerUrl += "&floorCount=" + floorCount
    }
    $.ajax({
        url: providerUrl,
        method: "GET",
        contentType: "application/json",
        async: false,
        success: function (data) {
            providerData = data;
        },
        error: function (err) {
            console.log(err);
        }
    });
    return providerData;
};


/**
 * To play slider. (live view)
 */
function slide() {
    if (isLive) {
        if (sliderPoint <= sliderPointMax) {
            rangeSlider.bootstrapSlider('setValue', sliderPoint);
            handleData(sliderPoint, sliderPointMax, floorData);
            sliderPoint++;
        } else {
            sliderPoint = sliderPointMax;
            setTimer();
        }
    }else{
        if (sliderPoint <= historySliderPointMax) {
            historicalSlider.bootstrapSlider('setValue', sliderPoint);
            handleData(sliderPoint, historySliderPointMax, historicalData);
            sliderPoint++;
        } else {
            sliderPoint = historySliderPointMax;
            setTimer();
        }
    }

}


function handleTimer(slider,point,maxPoint,data){
    // stop
    $("#pla i").removeClass("fw-right");
    $("#pla i").addClass("fw-circle");
    handleData(point, maxPoint, data);
    slider.bootstrapSlider('setValue', point);
    if (point == maxPoint) {
        sliderPoint = sliderPointMin;
        $("#pla i").removeClass("fw-circle");
        $("#pla i").addClass("fw-right");
    }else{
        isPause=true;
    }

}
/**
 * To set timer. (live view)
 */
function setTimer() {
    if (isLive) {
        if (timer) {
            // stop
            clearInterval(timer);
            timer = null;
            handleTimer(rangeSlider,sliderPoint,sliderPointMax,floorData);
        }
        else {
            if(isPause){
                $("#pla i").removeClass("fw-circle");
                $("#pla i").addClass("fw-right");
                isPause=false;
            }
            timer = setInterval(function () {
                slide();
            }, 2000);
        }

    } else {
        if (timer) {
            // stop
            clearInterval(timer);
            timer = null;
            handleTimer(historicalSlider,sliderPoint,historySliderPointMax,historicalData);

        }
        else {
            if(isPause){
                $("#pla i").removeClass("fw-circle");
                $("#pla i").addClass("fw-right");
                isPause=false;
            }
            timer = setInterval(function () {
                slide();
            }, 1000);
        }
    }

}

/**
 * To get recent past data when load the page.
 * @param numOfFloors number of floors
 */
function getRecentPastdata(numOfFloors) {
    var currentTime = new Date();
    var oldTime = currentTime.getTime();
    currentTime.setHours(currentTime.getHours() - 3);
    floorData = getProviderData("ORG_WSO2_FLOOR_PERFLOOR_SENSORSTREAM", currentTime.getTime(), oldTime, 0, 10, "DESC",
        buildingId, numOfFloors, "getBuildingData");
    handleData(sliderPointMax, sliderPointMax, floorData);
}

/**
 * To get recent past data when load the page.
 * @param numOfFloors number of floors
 * @param date selected date
 */
function getHistoricaldata(numOfFloors, date) {

    var end = new Date(date.getFullYear()
        , date.getMonth()
        , date.getDate()
        , 23, 59, 59);

    historicalData = getProviderData("ORG_WSO2_FLOOR_SUMMARIZED_PERFLOOR_SENSORSTREAM", date.getTime(), end.getTime(), 0, 25,
        "DESC", buildingId, numOfFloors, "getBuildingData");
    return historicalData;

}

/**
 * To configure slider to display data.
 * @param slider slider type
 * @param sliderPointMin minimum value of slider
 * @param sliderPointMax maximum value of slider
 */
function setSlider(slider,sliderPointMin, sliderPointMax) {
    slider.bootstrapSlider('setAttribute', 'min', sliderPointMin);
    slider.bootstrapSlider('setAttribute', 'max', sliderPointMax);
    slider.bootstrapSlider('setValue', sliderPointMax);
    slider.bootstrapSlider('refresh');

}

/**
 * Toggle switch to live view.
 */
function switchToLive() {

    if(isPause){
        $("#pla i").removeClass("fw-circle");
        $("#pla i").addClass("fw-right");
        isPause=false;
        sliderPoint=sliderPointMin;
    }

    isLive = true;
    $(".date-picker").slideToggle("slow");
    $('input[name="daterange"]').val('');

    if (isDatePick) {
        isDatePick = false;
        $('#historical-view').addClass("hidden");
    }

    $('#historic-toggle').removeClass("history");
    $('#historic-toggle').addClass("live");
    $('#live-view').removeClass("hidden");

    setSlider(rangeSlider, sliderPointMin, sliderPointMax);
    handleData(sliderPointMax, sliderPointMax, floorData);
}

/**
 * Toggle switch to history view.
 */
function switchToHistory() {

    if(isPause){
        $("#pla i").removeClass("fw-circle");
        $("#pla i").addClass("fw-right");
        isPause=false;
        sliderPoint=sliderPointMin;
    }

    isLive = false;
    $(".date-picker").slideToggle("slow");
    $('#live-view').addClass("hidden");
    $('#pla').addClass("hidden");
    $('#historic-toggle').removeClass("live");
    $('#historic-toggle').addClass("history");
}

/**
 * To show historical view slider.
 */
function displayHistorySlider() {
    $('#historical-view').removeClass("hidden");
    $('#pla').removeClass("hidden");
    setSlider(historicalSlider, sliderPointMin, historySliderPointMax);
    handleData(historySliderPointMax, historySliderPointMax, historicalData);
}

$("#pla").on("click", "i", function (event) {
    setTimer();
});


$('#range-slider').on("slide", function () {
}).on("change", function () {
    var time = rangeSlider.bootstrapSlider("getValue");
    sliderPoint = time;
    handleData(time, sliderPointMax, floorData);
});


$('#historical-slider').on("slide", function () {
}).on("change", function () {
    var time = historicalSlider.bootstrapSlider("getValue");
    sliderPoint = time;
    handleData(time, historySliderPointMax, historicalData);
});

$("#historic-toggle").click(function () {
    if ($("#historic-toggle").hasClass('live')) {
        switchToHistory();

    }
    else if ($("#historic-toggle").hasClass('history')) {
        switchToLive();
    }
});

var updateAlertCount = function () {
    var providerData = null;
    var providerUrl = context + '/api/batch-provider?action=getCount&tableName=ORG_WSO2_FLOOR_ALERTNOTIFICATIONS&buildingId=' + getUrlVar("buildingId") + "&floorCount=" + floorData.length;

    $.ajax({
        url: providerUrl,
        method: "GET",
        contentType: "application/json",
        async: false,
        success: function (data) {
            providerData = data;

            for (var i = 0; i < floorData.length; i++) {
                if (providerData[i] > 0) {
                    $("#alerts_" + (i + 1)).html(providerData[i]);
                    $("#div_" + (i + 1)).removeClass("message-success").addClass("message-danger");
                }
            }
        },
        error: function (err) {
            console.log(err);
        }
    });
};

$(document).ready(function () {

    $(".slider-wrapper").show(1000);
    $('#historic-toggle').addClass("live");
    buildingId = getUrlVar("buildingId");
    numOfFloors = $("#buildingView").data("num_of_floors");

    rangeSlider = $("#range-slider").bootstrapSlider(
        {
            ticks: [1,2,4,6,8,10],
            ticks_labels: ['-9m','-8m','-6m','-4m', '-2m',  'latest']

        }
    );
    historicalSlider = $("#historical-slider").bootstrapSlider(
        {
            ticks: [1, 4, 8, 12, 16, 20, 24],
            ticks_labels: ['0h','4h', '8h', '12h', '16h', '20h', '24h']
        }
    );

    setSlider(rangeSlider, sliderPointMin, sliderPointMax);

    var analyticsUrl = "wss://localhost:9445";
    $.ajax({
        url: context + '/api/analytics/',
        method: "GET",
        contentType: "application/json",
        async: false,
        success: function (data) {
            analyticsUrl = data;
        },
        error: function (err) {
        }
    });
    var url = analyticsUrl + "/outputwebsocket/Floor-Analysis-WebSocketLocal-FloorEvent";

    createWebSocket(url);
    createDataArrays(numOfFloors);
    getRecentPastdata(numOfFloors);
    updateAlertCount();

    $('input[name="daterange"]').datepicker({
        orientation: "auto",
        endDate: "+0d",
        autoclose: true
    }).on("changeDate", function (e) {
        isDatePick = true;
        var date = new Date(e.date);
        historicalData = getHistoricaldata(numOfFloors, date);
        displayHistorySlider();
    });
	loadLeafletMap();
});

window.onbeforeunload = function () {
    if (webSocket) {
        webSocket.close();
    }
};

function loadLeafletMap() {
	var buildingLocationId = "#building-location",
		location_lat = $(buildingLocationId).data("lat"),
		location_long = $(buildingLocationId).data("long"),
		container = "building-location",
		zoomLevel = 13,
		tileSet = "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
		attribution = "&copy; <a href='https://openstreetmap.org/copyright'>OpenStreetMap</a> contributors";

	if (location_long && location_lat) {
		map = L.map(container).setView([location_lat, location_long], zoomLevel);
		L.tileLayer(tileSet, {attribution: attribution}).addTo(map);

		var m = L.marker([location_lat, location_long], {"opacity": 0.70}).addTo(map).bindPopup("Your Building is here");
		m.on('mouseover', function (e) {
			this.openPopup();
		});
		m.on('mouseout', function (e) {
			this.closePopup();
		});
		$("#map-error").hide();
		$("#device-location").show();
		setTimeout(function(){ map.invalidateSize()}, 400);
	} else {
		$("#device-location").hide();
		$("#map-error").show();
	}
}
