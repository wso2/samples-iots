var wsConnection;

$(document).on('submit', 'form', function (e) {
    e.preventDefault();
});

$(window).load(function () {
    var deviceId = document.getElementById('edge-device-details').getAttribute('data-deviceid');
    var successCallBack = function (response) {
        var devices = JSON.parse(response);

        var deviceData = document.getElementById('edge-device-data');
        deviceData.innerHTML = '';

        $.each(devices, function (index, data) {
            var row = '<tr>' +
                '<td>'+ data.edgeDeviceName +'</td>' +
                '<td>' + data.edgeDeviceSerial + '</td>' +
                '<td class="text-center">' +
                '<form class="form-inline" action="/androidtv/device/{deviceId}/xbee-command" method="POST" data-payload="" id="form-xbee-' + data.edgeDeviceSerial + '-command">' +
                      '<input type="hidden" id="deviceId" placeholder="deviceId" class="form-control" data-param-type="path" value="' + deviceId + '">' +
                      '<input type="hidden" id="serial" placeholder="serial" class="form-control" data-param-type="query" value="' + data.edgeDeviceSerial + '">' +
                      '<div class="form-group">' +
                      '<input type="text" id="command" placeholder="Command" class="form-control" data-param-type="query" value="">' +
                      '</div>' +
                      '<button class="btn btn-primary btn-small" id="btnSend" type="button" onclick="submitForm(\'form-xbee-' + data.edgeDeviceSerial + '-command\')" class="btn btn-default">Send Command</button>' +
                '</form>' +
                '</td>' +
                '<td>' +
                '<p class="device-message" id="xbee-' + data.edgeDeviceSerial + '-message"></p>' +
                '</td>' +
                '<td class="text-center">' +
                '<button class="btn btn-primary btn-remove-device" type="button" onclick="removeEdgeDevice(\'' + deviceId + '\',\''+ data.edgeDeviceSerial +'\')">' +
                '<i class="fw fw-delete fw-helper fw-helper-circle-outline add-margin-right-1x"></i> Remove Device' +
                '</button>' +
                '</td>' +
                '</tr>';
            deviceData.innerHTML += row;
        });

        var websocketurlStream = $("#edge-device-details").attr("data-websocketurlStream");
        connect(wsConnection, websocketurlStream);
    };

    var errorCallBack = function (response) {
        console.log(response);
    };

    var uri = "/androidtv/device/" + deviceId + "/xbee-all";
    invokerUtil.get(uri, successCallBack, errorCallBack, "application/json");
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
            var serial = dataPoint[5];
            var msg = dataPoint[6];
            $('#xbee-' + serial + '-message').html(msg);
        };
    }
}

function disconnect(wsConnection) {
    if (wsConnection != null) {
        wsConnection.close();
        wsConnection = null;
    }
}

function removeEdgeDevice(deviceId, serial) {
    var url = "/androidtv/device/" + deviceId + "/xbee?serial=" + serial;
    invokerUtil.delete(url, function () {
        location.reload();
    }, function (e) {
        console.log(e);
    })
}
