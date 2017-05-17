var wsConnection;

$(window).load(function () {
    var successCallBack = function (response) {
        var devices = JSON.parse(response);

        var deviceData = document.getElementById('edge-device-data');
        deviceData.innerHTML = '';

        $.each(devices, function (index, data) {

            var row = '<tr>' +
                '<td>'+ data.edgeDeviceName +'</td>' +
                '<td>' + data.edgeDeviceSerial + '</td>' +
                '<td class="text-center">' +
                '<form class="form-inline" role="form">' +
                '<div class="form-group">' +
                '<input type="text" class="form-control" id="deviceCommand" placeholder="Your command">' +
                '</div>' +
                '<button class="btn btn-primary btn-small">Execute</button>' +
                '</form>' +
                '</td>' +
                '<td>' +
                '<p class="device-message">Tempreature : ' +
                '<span class="device-message-body"><strong>98F</strong></span>' +
                '</p>' +
                '</td>' +
                '<td class="text-center">' +
                '<button class="btn btn-primary btn-remove-device" type="button">' +
                '<i class="fw fw-delete fw-helper fw-helper-circle-outline add-margin-right-1x"></i> Remove Device' +
                '</button>' +
                '</td>' +
                '</tr>';

            deviceData.innerHTML += row
        });


        var websocketurlStream = $("#edge-device-details").attr("data-websocketurlStream");
        connect(wsConnection, websocketurlStream);
    };

    var errorCallBack = function (response) {
        console.log(response);
    };

    var deviceid = document.getElementById('edge-device-details').getAttribute('data-deviceid');
    var uri = "/androidtv/device/" + deviceid + "/xbee-all";
    contentType = "application/json";
    invokerUtil.get(uri, successCallBack, errorCallBack, contentType);
});
$(window).load(function () {
    var successCallBack = function (response) {
        var devices = JSON.parse(response);

        var deviceData = document.getElementById('edge-device-data');
        deviceData.innerHTML = '';

        $.each(devices, function (index, data) {

            var row = '<tr>' +
                '<td>'+ data.edgeDeviceName +'</td>' +
                '<td>' + data.edgeDeviceSerial + '</td>' +
                '<td class="text-center">' +
                '<form class="form-inline" role="form">' +
                '<div class="form-group">' +
                '<input type="text" class="form-control" id="deviceCommand" placeholder="Your command">' +
                '</div>' +
                '<button class="btn btn-primary btn-small">Execute</button>' +
                '</form>' +
                '</td>' +
                '<td>' +
                '<p class="device-message">Tempreature : ' +
                '<span class="device-message-body"><strong>98F</strong></span>' +
                '</p>' +
                '</td>' +
                '<td class="text-center">' +
                '<button class="btn btn-primary btn-remove-device" type="button">' +
                '<i class="fw fw-delete fw-helper fw-helper-circle-outline add-margin-right-1x"></i> Remove Device' +
                '</button>' +
                '</td>' +
                '</tr>';

            deviceData.innerHTML += row
        });
    };

    var errorCallBack = function (response) {
        console.log(response);
    };

    var deviceid = document.getElementById('edge-device-details').getAttribute('data-deviceid');
    var uri = "/androidtv/device/" + deviceid + "/xbee-all";
    contentType = "application/json";
    invokerUtil.get(uri, successCallBack, errorCallBack, contentType);
});

$(window).unload(function () {
    disconnect(wsConnection);
});

//websocket connection
function connect(wsConnection, target) {
    if ('WebSocket' in window) {
        wsConnection = new WebSocket(target);
    } else if ('MozWebSocket' in window) {
        wsConnection = new MozWebSocket(target);
    } else {
        console.log('WebSocket is not supported by this browser.');
    }
    if (wsConnection) {
        wsConnection.onmessage = function (event) {
            var dataPoint = JSON.parse(event.data);

        };
    }
}

function disconnect(wsConnection) {
    if (wsConnection != null) {
        wsConnection.close();
        wsConnection = null;
    }
}
