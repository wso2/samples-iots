var webSocket;
var floorData = [];
var rangeSlider;
var historicalSlider;
var timer;
var sliderPoint = 1;
var sliderPointMax = 10;
var sliderPointMin = 1;
var historicalData = [];
var isDatePick = false;

/**
 * To show received recent past data
 * @param time slider value.
 */
function showRecentPastData(time) {
    var numOfFloors = floorData.length;
    var tmp = sliderPointMax + 1 - time;
    for (var i = 0; i < numOfFloors; i++) {
        var index = floorData[i].length - tmp;
        displyaData(i + 1, floorData[i][index]);
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
                    realTimeDataHandler(JSON.parse(msg.data));
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
function realTimeDataHandler(data) {
    rangeSlider.bootstrapSlider('setValue', sliderPointMax);
    var buildingId = getUrlVar("buildingId");
    if (data.building === buildingId) {
        var floorId = data.floor;
        // if (data.floor === "5th floor") {
        //     floorId = "1";
        // }
        var fId = parseInt(floorId) - 1;
        if (floorData[fId].length == 10) {
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
    if (data != null) {
        ctx.font = "14px Arial";
        ctx.fillText("Temperature: " + data.temperature, 10, 10);
        ctx.fillText("Air Quality: " + data.airQuality, 10, 30);
        ctx.fillText("Humidity: " + data.humidity, 10, 50);
        ctx.fillText("Light: " + data.light, 10, 70);
        ctx.fillText("Motion: " + data.motion, 10, 90);
    } else {
        ctx.font = "14px Arial";
        ctx.fillText("No value", 10, 10);
    }
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
 */
var getProviderData = function (tableName, timeFrom, timeTo, start, limit, sortBy) {
    var providerData = null;
    var providerUrl = context + '/api/batch-provider?action=getLatestUpdate&tableName=' + tableName;

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
        showRecentPastData(sliderPoint);
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
        showRecentPastData(sliderPoint);
        rangeSlider.bootstrapSlider('setValue', sliderPoint);
        clearInterval(timer);
        timer = null;
        if (sliderPoint == sliderPointMax) {
            sliderPoint = sliderPointMin;
        }
    }
    else {
        timer = setInterval("slide()", 1000);
    }
}

/**
 * To get recent past data when load the page.
 * @param numOfFloors number of floors
 */
function getRecentPastdata(numOfFloors) {
    var currentTime = new Date();
    var oldTime = currentTime.getTime();
    currentTime.setMinutes(currentTime.getMinutes() - 30);

    for (var x = 0; x < numOfFloors; x++) {
        // should add floor ids as params
        floorData[x] = getProviderData("ORG_WSO2_FLOOR_PERFLOOR_SENSORSTREAM", currentTime.getTime(), oldTime, 0, 10, "DESC").reverse();
    }
    showRecentPastData(sliderPointMax);
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
function switchToLive(){
    $(".date-picker").slideToggle("slow");
    $('#historic-toggle').removeClass("history");
    $('#historic-toggle').addClass("live");
    $('#live-view').removeClass("hidden");
    $('#historical-view').addClass("hidden");
    $('#pla').removeClass("hidden");
    $('#pau').addClass("hidden");
    console.log("live view");
}

/**
 * Toggle switch to history view.
 */
function switchToHistory(){
    $(".date-picker").slideToggle("slow");
    $('#live-view').addClass("hidden");
    $('#historical-view').removeClass("hidden");
    $('#pla').addClass("hidden");
    $('#pau').removeClass("hidden");
    setHistoricalViewSlider(0, 24);
    $('#historic-toggle').removeClass("live");
    $('#historic-toggle').addClass("history");
    console.log("history view");

}

$("#pla").on("click", "i", function (event) {
    setTimer();
});

$('#range-slider').on("slide", function () {
}).on("change", function () {
    var time = rangeSlider.bootstrapSlider("getValue");
    showRecentPastData(time);
});


$('#historical-slider').on("slide", function () {
}).on("change", function () {
    if (isDatePick){
        //show historical data
    }else{
        // alert("Please pick a date");
        //display message to pick date
    }
});

$('input[name="daterange"]').datepicker({
    orientation: "auto",
    endDate: "+0d"
}).on("changeDate", function (e) {
    var selectedDate = e.date;
    var date = new Date(e.date);
    date.setHours(date.getHours()-1);
    console.log(date.getTime());

    var date = new Date(e.date);
    console.log("xxx" + date.getTime());

});

$("#historic-toggle").click(function () {
    if ($("#historic-toggle").hasClass('live')) {
        switchToHistory();

    }
    else if ($("#historic-toggle").hasClass('history')) {
        switchToLive();
    }
});

$(document).ready(function () {

    $(".slider-wrapper").show(1000);
    $('#historic-toggle').addClass("live");

    rangeSlider = $("#range-slider").bootstrapSlider();
    historicalSlider = $("#historical-slider").bootstrapSlider();

    setRealViewSlider(sliderPointMin,sliderPointMax);

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
    var url = analyticsUrl + "/outputwebsocket/Floor-Analysis-WebSocketLocal-FloorEvent";
    var numOfFloors = $("#buildingView").data("num_of_floors");
    console.log(numOfFloors);

    createWebSocket(url);
    createDataArrays(numOfFloors);
    getRecentPastdata(numOfFloors);

    $('input[name="daterange"]').datepicker({
        orientation: "auto",
        endDate: "+0d",
        autoclose: true
    }).on("changeDate", function(e) {
        var date = new Date(e.date);
        date.setHours(date.getHours()-1);
        console.log(date.getTime());

        var date = new Date(e.date);
        console.log("xxx" + date.getTime());

    });
});

window.onbeforeunload = function () {
    if (webSocket) {
        webSocket.close();
    }
};