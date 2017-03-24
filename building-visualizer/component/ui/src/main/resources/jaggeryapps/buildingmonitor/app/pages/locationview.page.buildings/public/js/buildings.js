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
            }
        }
    }
}

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

/**
 * To display received data.
 * @param floorId floor number
 * @param data relative data for floor number
 */
function displyaData(floorId, data) {

    var canvas = document.getElementById(floorId);
    clearCanvas(canvas);
    var ctx = canvas.getContext("2d");
    ctx.font = "14px Arial";
    ctx.fillText("Temperature: " + data.temperature, 10, 10);
    ctx.fillText("Air Quality: " + data.airQuality, 10, 30);
    ctx.fillText("Humidity: " + data.humidity, 10, 50);
    ctx.fillText("Light: " + data.light, 10, 70);
    ctx.fillText("Motion: " + data.motion, 10, 90);

}

/**
 * To clear data on canvas.
 * @param cnv canvas
 */
function clearCanvas(cnv) {
    var ctx = cnv.getContext('2d');     // gets reference to canvas context
    ctx.beginPath();    // clear existing drawing paths
    ctx.save();         // store the current transformation matrix

    // Use the identity matrix while clearing the canvas
    ctx.setTransform(1, 0, 0, 1, 0, 0);
    ctx.clearRect(0, 0, cnv.width, cnv.height);

    ctx.restore();        // restore the transform
}

/**
 * Calling batch provider api.
 * @param tableName table name
 * @param timeFrom starting time
 * @param timeTo end time
 * @param start starting point
 * @param limit limit
 * @param sortBy sorting method
 * @param buildingId id of the building
 * @param floorCount number of vloors
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
 * To play slider.
 */
function slide() {
    if (sliderPoint <= sliderPointMax) {
        rangeSlider.bootstrapSlider('setValue', sliderPoint);
        handleData(sliderPoint, sliderPointMax, floorData);
        sliderPoint++;
    } else {
        sliderPoint = sliderPointMax;
        setTimer();
    }
}

/**
 * To set timer.
 */
function setTimer() {
    if (timer) {
        // stop
        handleData(sliderPoint, sliderPointMax, floorData);
        rangeSlider.bootstrapSlider('setValue', sliderPoint);
        clearInterval(timer);
        timer = null;
        if (sliderPoint == sliderPointMax) {
            sliderPoint = sliderPointMin;
        }
    }
    else {
        timer = setInterval(function () {
            slide();
        }, 1000);
    }
}

/**
 * To play slider.
 */
function slidePau() {
    if (sliderPoint <= historySliderPointMax) {
        historicalSlider.bootstrapSlider('setValue', sliderPoint);
        handleData(sliderPoint, historySliderPointMax, historicalData);
        sliderPoint++;
    } else {
        sliderPoint = historySliderPointMax;
        setTimerPau();
    }
}

/**
 * To set timer.
 */
function setTimerPau() {
    if (timer) {
        // stop
        handleData(sliderPoint, historySliderPointMax, historicalData);
        historicalSlider.bootstrapSlider('setValue', sliderPoint);
        clearInterval(timer);
        timer = null;
        if (sliderPoint == historySliderPointMax) {
            sliderPoint = sliderPointMin;
        }
    }
    else {
        timer = setInterval(function () {
            slidePau();
        }, 1000);
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
 * To configure slider to display real time data.
 * @param sliderPointMin minimum value of slider
 * @param sliderPointMax maximum value of slider
 */
function setRealViewSlider(sliderPointMin, sliderPointMax) {
    rangeSlider.bootstrapSlider('setAttribute', 'min', sliderPointMin);
    historicalSlider.bootstrapSlider('setAttribute', 'max', sliderPointMax);
    rangeSlider.bootstrapSlider('setValue', sliderPointMax);
}

/**
 * To configure slider to display historical data.
 * @param sliderPointMin minimum value of slider
 * @param sliderPointMax maximum value of slider
 */
function setHistoricalViewSlider(sliderPointMin, sliderPointMax) {
    historicalSlider.bootstrapSlider('setAttribute', 'min', sliderPointMin);
    historicalSlider.bootstrapSlider('setAttribute', 'max', sliderPointMax);
    historicalSlider.bootstrapSlider('setValue', sliderPointMax);
}

/**
 * Toggle switch to live view.
 */
function switchToLive() {
    isDatePick = false;
    $(".date-picker").slideToggle("slow");
    $('#historic-toggle').removeClass("history");
    $('#historic-toggle').addClass("live");
    $('#live-view').removeClass("hidden");
    $('#historical-view').addClass("hidden");
    $('#pla').removeClass("hidden");
    $('#pau').addClass("hidden");
}

/**
 * Toggle switch to history view.
 */
function switchToHistory() {
    $(".date-picker").slideToggle("slow");
    $('#live-view').addClass("hidden");
    $('#pla').addClass("hidden");
    if (isDatePick) {
        displayHistorySlider()
    }

}

function displayHistorySlider() {
    $('#historical-view').removeClass("hidden");
    $('#pau').removeClass("hidden");
    setHistoricalViewSlider(0, 24);
    $('#historic-toggle').removeClass("live");
    $('#historic-toggle').addClass("history");
}

$("#pla").on("click", "i", function (event) {
    setTimer();
});

$("#pau").on("click", "i", function (event) {
    setTimerPau();

});

$('#range-slider').on("slide", function () {
}).on("change", function () {
    var time = rangeSlider.bootstrapSlider("getValue");
    handleData(time, sliderPointMax, floorData);
});


$('#historical-slider').on("slide", function () {
}).on("change", function () {
    var time = historicalSlider.bootstrapSlider("getValue");
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
                    $("#alerts_" + (i + 1)).html("You have " + providerData[i] + " new alerts in this floor.");
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

    rangeSlider = $("#range-slider").bootstrapSlider();
    historicalSlider = $("#historical-slider").bootstrapSlider();

    setRealViewSlider(sliderPointMin, sliderPointMax);

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
        displayHistorySlider();
        var date = new Date(e.date);
        historicalData = getHistoricaldata(numOfFloors, date);
    });
});

window.onbeforeunload = function () {
    if (webSocket) {
        webSocket.close();
    }
};