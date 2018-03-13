var devicesTemp = [];

function getDevice(dev, index, lat, long) {
    var devicesListing = $('#devices-listing');

    var lastKnownSuccess = function (data) {
        var records = JSON.parse(data);
        var record = JSON.parse(data).records[0];

        var parameterOne = null;
        var parameterTwo = null;
        var parameterThree = null;

        if (record) {
            parameterOne = record.values[typepParameter1];
            parameterTwo = record.values[typeParameter2];
            parameterThree = record.values[typeParameter3];
        }

        var myRow;
        if (parameterOne == null || parameterTwo == null || parameterThree == null) {
            myRow = "<tr onclick=\"window.location.href='details.jsp?id=" + dev.deviceIdentifier + "'\" style='cursor: pointer'><a href='#" + dev.deviceIdentifier + "'><td><div class=\"card card-stats\" style='width: 75%'> <div class=\"card-header\" data-background-color=\"purple\"> <i class=\"material-icons\">dock</i> </div> <div class=\"card-content\"> <p class=\"category\">Device</p> <h3 class=\"title\" >" + dev.name + "</h3> </div> </div>\n"
                + "</td><td>"
                + "<div class=\"card\"><div class=\"card-header card-chart\" data-background-color=\"red\" style=\"height: 90px;min-height: unset;\"><div class=\"ct-chart\" id=\"HistoricalParameterOneChart" + dev.deviceIdentifier + "\"></div></div><div class=\"card-content\"><h4 class=\"title\">N/A</h4><p class=\"category\" id=\"historicalTempAlert" + dev.deviceIdentifier + "\"></div></div>\n</td><td><div class=\"card\"><div class=\"card-header card-chart\" data-background-color=\"orange\" style=\"height: 90px;min-height: unset;\"><div class=\"ct-chart\" id=\"HistoricalparameterTwoChart" + dev.deviceIdentifier + "\"></div></div><div class=\"card-content\"><h4 class=\"title\">N/A</h4><p class=\"category\" id=\"historicalHumidAlert" + dev.deviceIdentifier + "\"></div></div>\n</td><td>"
                + "<div class=\"card\"><div class=\"card-header card-chart\" data-background-color=\"green\" style=\"height: 90px;min-height: unset;\"><div class=\"ct-chart\" id=\"HistoricalparameterThreeChart" + dev.deviceIdentifier + "\"></div></div><div class=\"card-content\"><h4 class=\"title\">N/A</h4><p class=\"category\" id=\"historicalparameterThreeAlert" + dev.deviceIdentifier + "\"></div></div>\n</td>"
                + "</a></tr>";
        }
        else {
            myRow = "<tr onclick=\"window.location.href='details.jsp?id=" + dev.deviceIdentifier + "'\" style='cursor: pointer'><a href='#" + dev.deviceIdentifier + "'><td><div class=\"card card-stats\" style='width: 75%'> <div class=\"card-header\" data-background-color=\"purple\"> <i class=\"material-icons\">dock</i> </div> <div class=\"card-content\"> <p class=\"category\">Device</p> <h3 class=\"title\" >" + dev.name + "</h3> </div> </div>\n"
                + "</td><td>"
                + "<div class=\"card\"><div class=\"card-header card-chart\" data-background-color=\"red\" style=\"height: 90px;min-height: unset;\"><div class=\"ct-chart\" id=\"HistoricalParameterOneChart" + dev.deviceIdentifier + "\"></div></div><div class=\"card-content\"><h4 class=\"title\"> " + (parameterOne)+ (units1) + "</h4><p class=\"category\" id=\"historicalTempAlert" + dev.deviceIdentifier + "\"></div></div>\n</td><td><div class=\"card\"><div class=\"card-header card-chart\" data-background-color=\"orange\" style=\"height: 90px;min-height: unset;\"><div class=\"ct-chart\" id=\"HistoricalparameterTwoChart" + dev.deviceIdentifier + "\"></div></div><div class=\"card-content\"><h4 class=\"title\"> " + (parameterTwo) +(units2)+ "</h4><p class=\"category\" id=\"historicalHumidAlert" + dev.deviceIdentifier + "\"></div></div>\n</td><td>"
                + "<div class=\"card\"><div class=\"card-header card-chart\" data-background-color=\"green\" style=\"height: 90px;min-height: unset;\"><div class=\"ct-chart\" id=\"HistoricalparameterThreeChart" + dev.deviceIdentifier + "\"></div></div><div class=\"card-content\"><h4 class=\"title\"> " + (parameterThree)+ (units3)+ "</h4><p class=\"category\" id=\"historicalparameterThreeAlert" + dev.deviceIdentifier + "\"></div></div>\n</td>"
                + "</a></tr>";
        }
        rows.push(myRow);

        devicesListing.find('tbody').append(myRow);
        initDashboardPageCharts(dev.deviceIdentifier);
        redrawGraphs(records, dev.deviceIdentifier);

        var newIndex = index + 1;
        if (devicesTemp.length > newIndex) {
            getDevice(devicesTemp[newIndex], newIndex, devicesTemp[newIndex].properties[0].value, devicesTemp[newIndex].properties[1].value);
        }

        //function to implement the regex search bar
        var $rows = $('#devices-listing tbody tr');
        $('#search').keyup(function () {
            var val = '^(?=.*\\b' + $.trim($(this).val()).split(/\s+/).join('\\b)(?=.*\\b') + ').*$',
                reg = RegExp(val, 'i'),
                text;

            $rows.show().filter(function () {
                text = $(this).text().replace(/\s+/g, ' ');
                return !reg.test(text);
            }).hide();

        });
    };

    $.ajax({
        type: "POST",
        url: "invoker/execute",
        data: {
            "uri": "/events/last-known/"+deviceType+"/" + devicesTemp[index].deviceIdentifier + "?limit=5",
            "method": "get"
        },
        success: lastKnownSuccess

    });

}

function getDevices(offset, limit) {
    var getsuccess = function (data) {
        devicesTemp = JSON.parse(data).devices;
        deviceCount = JSON.parse(data).count;//find the number of devices
        var devicesListing = $('#devices-listing');
        if (devicesTemp && devicesTemp.length > 0) {
            devicesListing.find('tbody').empty();
            getDevice(devicesTemp[0], 0, devicesTemp[0].properties[0].value, devicesTemp[0].properties[1].value);
        } else {
            var myRow = "<tr><td colspan=\"6\" style=\"padding-top: 30px;\"><strong>No Devices Found</strong></td></tr>";
            devicesListing.find('tbody').replaceWith(myRow);
        }

    };
    $.ajax({
        type: "POST",
        url: "invoker/execute",
        data: {
            "uri": "/devices/?type="+deviceType+"&requireDeviceInfo=true&offset=" + offset + "&limit=" + limit,
            "method": "get"
        },
        success: getsuccess
    });
}

function addToMap(dev, index, lat, long) {
    var KnownSuccess = function (data) {
        var records = JSON.parse(data);
        var record = JSON.parse(data).records[0];

        var parameterOne = null;
        var parameterTwo = null;
        var parameterThree = null;

        if (record) {
            parameterOne = record.values[typepParameter1];
            parameterTwo = record.values[typeParameter2];
            parameterThree = record.values[typeParameter3];
        }

        //To fix the issue of adding devices with null or undefined location values to map
        if ((lat == null || lat === "undefined" ) || (long == null || lat === "undefined")) {
            console.log('undefined lat' + lat + ' long ' + long);
        }
        else {
            addToMapPopoup(lat, long, dev.deviceIdentifier, dev.id, parameterOne, parameterTwo, parameterThree);
        }

    };

    $.ajax({
        type: "POST",
        url: "invoker/execute",
        data: {
            "uri": "/events/last-known/"+deviceType+"/" + devices[index].deviceIdentifier,
            "method": "get"
        },
        success: KnownSuccess

    });

}

function getAllDevices() {
    var success = function (data) {
        devices = JSON.parse(data).devices;
        deviceCount = JSON.parse(data).count;//find the number of devices
        //used bootpag library to implement the pagination
        $('#nav').bootpag({
            total: Math.ceil(deviceCount / 10),
            page: 1,
            maxVisible: 6,
            href: "#pro-page-{{number}}",
            leaps: false,
            next: 'next',
            prev: null
        }).on('page', function (event, num) {
            var offset = (num - 1) * 10;
            var limit = num * 10;
            getDevices(offset, limit);
        });
        var i;
        for (i = 0; i < devices.length; i++) {
            addToMap(devices[i], i, devices[i].properties[0].value, devices[i].properties[1].value);

        }
    };
    $.ajax({
        type: "POST",
        url: "invoker/execute",
        data: {"uri": "/devices/?type="+deviceType+"&requireDeviceInfo=true&offset=0&limit=100", "method": "get"},
        success: success
    });
}

function addNewDevice() {
    var deviceId = $("#deviceId").val();
    var deviceName = $("#deviceName").val();
    var deviceDesc = $("#deviceDesc").val();

    var success = function (data) {
        var config = {};
        config.deviceName = deviceName;
        config.deviceId = deviceId;

        var configSuccess = function (data) {
            var appResult = JSON.parse(data);

            config.clientId = appResult.clientId;
            config.clientSecret = appResult.clientSecret;
            config.clientSecret = appResult.clientSecret;
            config.accessToken = appResult.accessToken;
            config.refreshToken = appResult.refreshToken;
            config.scope = appResult.scope;
            //downlaod a json file
            var dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(
                JSON.stringify(config, null, 4));
            var dlAnchorElem = document.createElement('a');
            dlAnchorElem.setAttribute("href", dataStr);
            dlAnchorElem.setAttribute("download", deviceId + ".json");
            dlAnchorElem.setAttribute('visibility', 'hidden');
            dlAnchorElem.setAttribute('display', 'none');
            document.body.appendChild(dlAnchorElem);
            dlAnchorElem.click();
            $('#newDeviceModal').modal('hide');//hide popup after adding a device
            location.reload();//reload page after adding device
        };

        $.ajax({
            type: "GET",
            url: "config?deviceId=" + deviceId,
            success: configSuccess
        });
    };
    var payload = "{\n"
        + "\"name\": \"" + deviceName + "\",\n"
        + "\"deviceIdentifier\": \"" + deviceId + "\",\n"
        + "\"description\": \"" + deviceDesc + "\",\n"
        + "\"type\": \""+deviceType+"\",\n"
        + "\"enrolmentInfo\": {\"status\": \"ACTIVE\", \"ownership\": \"BYOD\"},\n"
        + "\"properties\": [{name: \"latitude\", value:\"" + lat + "\"}, {name: \"longitude\", value: \"" + lng + "\"}]\n"
        + "}";
    $.ajax({
        type: "POST",
        url: "invoker/execute",
        data: {"uri": "/device/agent/enroll", "method": "post", "payload": payload},
        success: success
    });
}

function precise_round(num, decimals) {
    var t = Math.pow(10, decimals);
    return (Math.round((num * t) + (decimals > 0 ? 1 : 0) * (Math.sign(num) * (10 / Math.pow(100, decimals)))) / t).toFixed(decimals);
}
