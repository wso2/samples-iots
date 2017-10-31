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

var tempGraph=[];
var motionGraph=[];
var humidityGraph=[];
var lightGraph=[];

var tempChartData = [];
var motionChartData = [];
var humidityChartData = [];
var lightChartData = [];


var palette = new Rickshaw.Color.Palette({scheme: "classic9"});

/**
 * To show received data
 * @param sliderVal value of slider.
 * @param buildingData data of building
 */
function handleData(sliderVal, buildingData) {
    var index = sliderVal-1;
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
        light = "OFF";
    }else if (data.light>500){
        light="ON";
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
 * Initialize floor graph instances
 * @param numOfFloors number of floors.
 */
function createGraphs(numOfFloors){

    for (var i = 0; i < numOfFloors; i++) {
        tempChartData[i] = [];
        motionChartData[i] = [];
        humidityChartData[i] = [];
        lightChartData[i] = [];

        tempGraph[i] = {graph:{}};
        motionGraph[i] ={graph:{}};
        humidityGraph[i] ={graph:{}};
        lightGraph[i] ={graph:{}};
    }

}

/**
 * Initialize the web-sockets to get the real-time data.
 */
function createWebSocket(host) {

    if (!("WebSocket" in window)) {
        console.log("browser doesn't support");
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
        if(isLive){
            rangeSlider.bootstrapSlider('setValue', sliderPointMax);
            displyaData(floorId, data);
        }
        updateGraphs(floorId, data);
    }
}

function updateGraphs(floorId, data) {
    var fId = parseInt(floorId) - 1;
    console.log("-------------------------------")
    console.log(data);

    tempChartData[fId].push({
        x: parseInt(data.time)/1000,
        y: parseFloat(data.temperature)
    });

    tempChartData[fId].shift();
    tempGraph[fId].graph.update();

    motionChartData[fId].push({
        x: parseInt(data.time)/1000,
        y: parseFloat(data.motion)
    });
    motionChartData[fId].shift();
    motionGraph[fId].graph.update();

    humidityChartData[fId].push({
        x: parseInt(data.time)/1000,
        y: parseFloat(data.humidity)
    });
    humidityChartData[fId].shift();
    humidityGraph[fId].graph.update();

    lightChartData[fId].push({
        x: parseInt(data.time)/1000,
        y: parseFloat(data.light)
    });
    lightChartData[fId].shift();
    lightGraph[fId].graph.update();

}

/* passing the data to draw graphs according to floor number */

function processCharts(numOfFloors){
    for (var k = 0; k < numOfFloors; k++) {
        processChartContext(k);
    }
}

/* passing the data to draw graphs  */

function processChartContext(fId){
    var floorId = fId + 1;
    var tempChartName=["Temperature" + floorId];
    processMultiChart(("div-chart-temp-"+floorId),("chart_temp_"+floorId),tempChartData[fId],tempChartName,tempGraph[fId],("y_axis_temp_"+floorId),("legend_temp_"+floorId));
    var motionChartName=["Motion" + floorId];
    processMultiChart(( "div-chart-motion-"+floorId),("chart_motion_"+floorId),motionChartData[fId],motionChartName,motionGraph[fId],("y_axis_motion_"+floorId),("legend_motion_"+floorId));
    var humidityChartName=["Humidity" + floorId];
    processMultiChart(("div-chart-humidity-"+floorId),("chart_humidity_"+floorId),humidityChartData[fId],humidityChartName,humidityGraph[fId],("y_axis_humidity_"+floorId),("legend_humidity_"+floorId));
    var lightChartName=["Light level" + floorId];
    processMultiChart(("div-chart-light-"+floorId),("chart_light_"+floorId),lightChartData[fId],lightChartName,lightGraph[fId],("y_axis_light_"+floorId),("legend_light_"+floorId));

}
/*
    Creating a graph */

function processMultiChart(outerDiv,chartDiv,chartData,name,graph,yAxis,legend) {

    var tNow = new Date().getTime() / 1000;

        for (var i = 0; i < 30; i++) {
            chartData.push({
                x: tNow - (30 - i) * 15,
                y: parseFloat(0)
            });
        }


    series=[];
        obj = {
            'color':palette.color(),
            'data':chartData,
            'name': name
        }
        series.push(obj);


    graph.graph = new Rickshaw.Graph({
        element: document.getElementById(chartDiv),
        width: $("#"+outerDiv).width() - 50,
        height: 300,
        stack: false,
        padding: {top: 0.2, left: 0.0, right: 0.0, bottom: 0.2},
        renderer: "area",
        interpolation: "linear",
        padding: {top: 0.2, left: 0.0, right: 0.0, bottom: 0.2},
        xScale: d3.time.scale(),
        series: series
    });

    graph.graph.render();

    var xAxis = new Rickshaw.Graph.Axis.Time({
        graph: graph.graph
    });

    xAxis.render();

    new Rickshaw.Graph.Axis.Y({
        graph: graph.graph,
        orientation: 'left',
        height: 300,
        tickFormat: Rickshaw.Fixtures.Number.formatKMBT,
        element: document.getElementById(yAxis)
    });

    new Rickshaw.Graph.HoverDetail({
        graph: graph.graph,
        formatter: function (series, x, y) {
            var date = '<span class="date">' + moment(x * 1000).format('Do MMM YYYY h:mm:ss a') + '</span>';
            var swatch = '<span class="detail_swatch" style="background-color: ' + series.color + '"></span>';
            return swatch + series.name + ": " + parseInt(y) + '<br>' + date;
        }
    });

    var legendObj = new Rickshaw.Graph.Legend( {
        graph: graph.graph,
        element: document.getElementById(legend)
    } );

    var highlighter = new Rickshaw.Graph.Behavior.Series.Highlight({
        graph: graph.graph,
        legend: legendObj
    });
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
 * To play slider.
 */
function slide() {
    if (isLive) {
        if (sliderPoint < sliderPointMax) {
            rangeSlider.bootstrapSlider('setValue', sliderPoint);
            handleData(sliderPoint, floorData);
            sliderPoint++;
        } else {
            sliderPoint = sliderPointMax;
            setTimer(rangeSlider,sliderPointMax,floorData);
        }
    }else{
        if (sliderPoint < historySliderPointMax) {
            historicalSlider.bootstrapSlider('setValue', sliderPoint);
            handleData(sliderPoint, historicalData);
            sliderPoint++;
        } else {
            sliderPoint = historySliderPointMax;
            setTimer(historicalSlider,historySliderPointMax,historicalData);
        }
    }

}

function handleTimer(slider,point,maxPoint,data){
    // stop
    $("#pla i").removeClass("fw-right");
    $("#pla i").addClass("fw-circle");
    handleData(point, data);
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
 * To set timer.
 */
function setTimer(slider,maxPoint,data){
    if (timer) {
        // stop
        clearInterval(timer);
        timer = null;
        handleTimer(slider,sliderPoint,maxPoint,data);
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
    handleData(sliderPointMax, floorData);
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

    var currentDate = new Date();
    var difInHr = (currentDate - date)/3600000;

    if(difInHr < 6){
        historicalData = getProviderData("ORG_WSO2_FLOOR_SUMMARIZED6HR_PERFLOOR_SENSORSTREAM", date.getTime(), end.getTime(), 0, 25,
                "DESC", buildingId, numOfFloors, "getBuildingData");
        return historicalData;
    }else if (difInHr < 24){
        historicalData = getProviderData("ORG_WSO2_FLOOR_SUMMARIZED_PERFLOOR_SENSORSTREAM", date.getTime(), end.getTime(), 0, 25,
            "DESC", buildingId, numOfFloors, "getBuildingData");
        return historicalData;
    }else if (difInHr < 168){
        historicalData = getProviderData("ORG_WSO2_FLOOR_SUMMARIZED1HR_PERFLOOR_SENSORSTREAM", date.getTime(), end.getTime(), 0, 25,
            "DESC", buildingId, numOfFloors, "getBuildingData");
        return historicalData;
    }else{
        historicalData = getProviderData("ORG_WSO2_FLOOR_SUMMARIZED3HR_PERFLOOR_SENSORSTREAM", date.getTime(), end.getTime(), 0, 25,
             "DESC", buildingId, numOfFloors, "getBuildingData");
        return historicalData;
    }

}

/**
 * To configure slider to display data.
 * @param slider slider type
 * @param sliderPointMin minimum value of slider
 * @param sliderPointMax maximum value of slider
 */
function setSlider(slider,sliderPointMin, sliderPointMax) {
    slider.bootstrapSlider('refresh');
    slider.bootstrapSlider('setAttribute', 'min', sliderPointMin);
    slider.bootstrapSlider('setAttribute', 'max', sliderPointMax);
    slider.bootstrapSlider('setValue', sliderPointMax);
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
    handleData(sliderPointMax, floorData);
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
    handleData(historySliderPointMax, historicalData);
}

$("#pla").on("click", "i", function (event) {
    if(isLive){
        setTimer(rangeSlider,sliderPointMax,floorData);
    }else if(!isLive){
        setTimer(historicalSlider,historySliderPointMax,historicalData);
    }
});


$('#range-slider').on("slide", function () {
}).on("change", function () {
    var time = rangeSlider.bootstrapSlider("getValue");
    sliderPoint = time;
    handleData(time, floorData);
});


$('#historical-slider').on("slide", function () {
}).on("change", function () {
    var time = historicalSlider.bootstrapSlider("getValue");
    sliderPoint = time;
    handleData(time, historicalData);
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
    numOfFloors = $("#group-2").data("num_of_floors");

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
            console.log(err);
        }
    });
    var url = analyticsUrl + "/outputwebsocket/Floor-Analysis-WebSocketLocal-FloorEvent";

    createWebSocket(url);
    createDataArrays(numOfFloors);
    createGraphs(numOfFloors);
    processCharts(numOfFloors);
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
$(document).on( "click", ".view-analytics", function(e) {
    e.stopPropagation();

});


