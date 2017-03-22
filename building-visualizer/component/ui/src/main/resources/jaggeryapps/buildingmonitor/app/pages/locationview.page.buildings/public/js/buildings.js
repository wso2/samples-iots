var webSocket;
var floorData = [];
var rangeSlider;

$(document).ready(function () {

    $(".slider-wrapper").show(1000);
    rangeSlider = $("#range-slider").bootstrapSlider();
    rangeSlider.bootstrapSlider('setAttribute', 'min', 1);
    rangeSlider.bootstrapSlider('setValue', 10);
    var url = "ws://localhost:9765/outputwebsocket/Floor-Analysis-WebSocketLocal-FloorEvent";
    createWebSocket(url);
    var numOfFloors = $("#buildingView").data("num_of_floors");
    createDataArrays(numOfFloors);

    $('#range-slider').on("slide", function () {}).on("change", function () {
        var time = rangeSlider.bootstrapSlider("getValue");
        showReacentPastData(time);
    });

    $('input[name="daterange"]').datepicker({
        orientation: "auto",
        endDate: "+0d"
    }).on("changeDate", function (e) {

    });


    $("#historic-toggle").click(function () {
        $(".date-picker").slideToggle("slow");
    });

});

window.onbeforeunload = function () {
    if (webSocket) {
        webSocket.close();
    }
};

function showReacentPastData(time){
    var numOfFloors = floorData.length;
    var tmp = 11-time;
    for (var i = 0; i < numOfFloors; i++) {
        var index = floorData[i].length-tmp;
        displyaData(i+1,floorData[i][index]);
    }

}

function createDataArrays(val){
    for (var i = 0; i < val; i++) {
        floorData[i] = [];
    }
}

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
                    reatTimeDataHandler(JSON.parse(msg.data));

                };

                webSocket.onclose = function () {
                    console.log("on close");
                };

                webSocket.error= function (err) {
                    console.log(err);
                }

            } catch (exception) {
                console.log(exception);
            }

        }

    }
}

function reatTimeDataHandler(data){
    rangeSlider.bootstrapSlider('setValue', 10);
    var buildingId = getUrlVar("buildingId");
    if(data.building === "WSO2"){
        var floorId = data.floor;
        if(data.floor==="5th floor"){
            floorId="1";
        }
        var fId = parseInt(floorId)-1;
        if (floorData[fId].length==10){
            floorData[fId].shift();
            floorData[fId].push(data);
        }else{
            floorData[fId].push(data);
        }
        displyaData(floorId,data);
    }
}

function getUrlVar(key){
    var result = new RegExp(key + "=([^&]*)", "i").exec(window.location.search);
    return result && unescape(result[1]) || "";
}

function displyaData(floorId,data){

    var canvas = document.getElementById(floorId);
    clearCanvas(canvas);
    var ctx = canvas.getContext("2d");
    if (data!=null){
        ctx.font = "14px Arial";
        ctx.fillText("Temperature: " + data.temperature,10,10);
        ctx.fillText("Air Quality: " + data.airQuality,10,30);
        ctx.fillText("Humidity: " + data.humidity,10,50);
        ctx.fillText("Light: " + data.light,10,70);
        ctx.fillText("Motion: " + data.motion,10,90);
    }else{
        ctx.font = "14px Arial";
        ctx.fillText("No value",10,10);
    }

}

function clearCanvas(cnv) {
    var ctx = cnv.getContext('2d');     // gets reference to canvas context
    ctx.beginPath();    // clear existing drawing paths
    ctx.save();         // store the current transformation matrix

    // Use the identity matrix while clearing the canvas
    ctx.setTransform(1, 0, 0, 1, 0, 0);
    ctx.clearRect(0, 0, cnv.width, cnv.height);

    ctx.restore();        // restore the transform
}