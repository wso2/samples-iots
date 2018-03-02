<%--Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.--%>

<%--WSO2 Inc. licenses this file to you under the Apache License,--%>
<%--Version 2.0 (the "License"); you may not use this file except--%>
<%--in compliance with the License.--%>
<%--You may obtain a copy of the License at--%>

<%--http://www.apache.org/licenses/LICENSE-2.0--%>

<%--Unless required by applicable law or agreed to in writing,--%>
<%--software distributed under the License is distributed on an--%>
<%--"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY--%>
<%--KIND, either express or implied. See the License for the--%>
<%--specific language governing permissions and limitations--%>
<%--under the License.--%>


<%@include file="includes/authenticate.jsp" %>
<html>
<head>
    <title>Farm Manager</title>
    <link href="css/bootstrap.min.css" rel="stylesheet"/>
    <link href="css/material-icons.css" rel="stylesheet"/>
    <link href="css/material-dashboard.css" rel="stylesheet"/>
    <link href="css/font-awesome.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.2.0/dist/leaflet.css"
          integrity="sha512-M2wvCLH6DSRazYeZRIm1JnYyh22purTM+FDB5CsyxtQJYeKq83arPe5wgbNmcFXGqiSH2XR8dT/fJISVA1r/zQ=="
          crossorigin=""/>
    <link href="css/updates.css" rel="stylesheet"/>

</head>
<body>
<div class="wrapper">
    <div class="sidebar" data-color="blue" data-image="images/sidebar-1.jpg">
        <div class="logo">
            <a href="./devices.jsp" class="simple-text">
                <strong>Farm</strong>Portal
            </a>
        </div>
        <div class="sidebar-wrapper">
            <div class="form-group label-floating is-empty" id="hide"
                 style="margin-top: 30px;margin-right: 10px;margin-left: 10px ">
                <label class="control-label">Search Device</label>
                <input type="search" id="search" class="form-control">
                <span class="material-input"></span></div>
            <p class="copyright" style="position: absolute;bottom:0;padding-left: 100px">
                &copy;
                <script>
                    document.write(new Date().getFullYear())
                </script>
                <a href="https://wso2.com/iot">WSO2 Inc.</a>
            </p>
        </div>
    </div>
    <div class="main-panel">

        <div class="content" style="margin-top:5px ; padding: 0 0">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-md-12">
                        <div class="card card-plain">
                            <div class="card-header" data-background-color="blue">
                                <div class="nav-tabs-wrapper">
                                    <ul class="nav nav-tabs" data-tabs="tabs">
                                        <li class="active" id="tableViewTab">
                                            <a href="#tableview" data-toggle="tab">
                                                <i class="material-icons">access_alarms</i> Table view
                                                <div class="ripple-container"></div>
                                            </a>
                                        </li>
                                        <li class="" id="mapViewTab">
                                            <a href="#mapView" data-toggle="tab">
                                                <i class="material-icons">map</i> Map view
                                                <div class="ripple-container"></div>
                                            </a>
                                        </li>
                                        <li class="dropdown pull-right">
                                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                                <i class="material-icons">person</i>
                                                <%--<span class="notification">5</span>--%>
                                                <p class="hidden-lg hidden-md">Profile</p>
                                            </a>
                                            <ul class="dropdown-menu">
                                                <li>
                                                    <a href="logout.jsp">Logout</a>
                                                </li>
                                            </ul>
                                        </li>
                                    </ul>
                                </div>

                                <table style="width:100%">
                                    <tr>
                                        <th>
                                            <h4 class="title" style="font-size: 30px; padding-left: 10px;">Farms
                                                enrolled</h4>
                                        </th>
                                        <th style="text-align: center">
                                            <button class="btn btn-white" data-toggle="modal"
                                                    data-target="#newDeviceModal">Add
                                                Farm
                                            </button>
                                        </th>
                                    </tr>
                                </table>

                                <%--Popup modal for adding new device--%>
                                <div class="modal fade" id="newDeviceModal" tabindex="-1" role="dialog"
                                     aria-labelledby="myModalLabel" aria-hidden="true">
                                    <div class="modal-dialog">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <button type="button" class="close" data-dismiss="modal"
                                                        aria-hidden="true">&times
                                                </button>
                                                <h4 class="modal-title" id="myModalLabel" style="color:cornflowerblue;">
                                                    Enter
                                                    Farm details
                                                    Details</h4>
                                            </div>
                                            <form id="new-device-form" method="post">
                                                <div class="form-group" style="padding-left: 10%; padding-right: 10%;">
                                                    <input type="text" name="deviceId" id="deviceId" value=""
                                                           placeholder="Device ID"
                                                           class="form-control"/>
                                                </div>
                                                <div class="form-group" style="padding-left: 10%; padding-right: 10%;">
                                                    <input type="text" value="" placeholder="Device Name"
                                                           name="deviceName" id="deviceName"
                                                           class="form-control"/>
                                                </div>
                                                <div class="form-group" style="padding-left: 10%; padding-right: 10%;">
                                                    <input type="text" value="" placeholder="Device description"
                                                           name="deviceDesc" id="deviceDesc"
                                                           class="form-control"/>
                                                </div>
                                                <div id="inputMapId"></div>
                                            </form>
                                            <div class="modal-footer">
                                                <button type="button" class="btn btn-default btn-simple"
                                                        data-dismiss="modal">Close
                                                </button>
                                                <button type="button" class="btn btn-info btn-simple"
                                                        onclick="addNewDevice()">Add
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="tab-content">
                                <div id="tableview" class="tab-pane fade in active" style>
                                    <div class="card-content table-responsive">
                                        <table class="table table-hover" id="devices-listing" style="height: 100%">
                                            <thead>
                                            <th>Device Name</th>
                                            <th>Temperature</th>
                                            <th>Humidity</th>
                                            <th>Soil Moisture</th>
                                            <th></th>
                                            <th></th>
                                            <th></th>
                                            </thead>
                                            <tbody>
                                            <tr>
                                                <td colspan="6">Loading...</td>
                                            </tr>
                                            </tbody>
                                        </table>
                                        <div id="nav"></div>
                                    </div>
                                </div>
                                <div id="mapView" class="tab-pane fade  ">
                                    <div id="mapid" style="width: 100%; height:80%;"></div>
                                </div>

                            </div>

                        </div>

                    </div>
                </div>

            </div>
        </div>
    </div>
</div>

</body>
<script src="js/jquery.min.js" type="text/javascript"></script>
<script src="js/bootstrap.min.js" type="text/javascript"></script>
<script src="js/material.min.js" type="text/javascript"></script>
<script src="js/nouislider.min.js" type="text/javascript"></script>
<script src="js/bootstrap-datepicker.js" type="text/javascript"></script>
<script src="js/material-kit.js" type="text/javascript"></script>
<script src="js/bootstrap-notify.js" type="text/javascript"></script>
<script src="js/material-dashboard.js" type="text/javascript"></script>
<script src="js/chartist.min.js"></script>

<script type="text/javascript" src="js/libs/jquery.bootpag.js"></script>

<script src="js/moment.min.js" type="text/javascript"></script>
<script src="js/daterangepicker.js" type="text/javascript"></script>
<script src="https://unpkg.com/leaflet@1.2.0/dist/leaflet.js"
        integrity="sha512-lInM/apFSqyy1o6s89K4iQUKg6ppXEgsVxT35HbzUupEVRh2Eu9Wdl4tHj7dZO0s1uvplcYGmt3498TtHq+log=="
        crossorigin=""></script>
<script type="text/javascript">

    var devices = [];
    var devicesTemp = [];
    var rows = [];
    var deviceCount;
    var temp = [];
    var humid = [];
    var soilMoisture = [];
    historicalTempLabel = ['0s']
    historicalTempSeries = [0]
    historicalHumidLabel = ['0s']
    historicalHumidSeries = [0]
    historicalsoilMoistureLabel = ['0s']
    historicalsoilMoistureSeries = [0]

    //initialising the map view tab
    var mymap = L.map('mapid').setView([7.9, 80.56274], 8);
    L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
        attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
        maxZoom: 18,
        id: 'mapbox.streets',
        accessToken: 'pk.eyJ1IjoibGFzaGFuIiwiYSI6ImNqYmc3dGVybTFlZ3UyeXF3cG8yNGxsdzMifQ.n3QEq0-g5tVFmsQxn3JZ-A',
        closePopupOnClick: false,
    }).addTo(mymap);

    //adding the legend
    var legend = L.control({position: 'topright'});
    legend.onAdd = function (map) {
        var div = L.DomUtil.create('div', 'info legend');
        div.innerHTML += '<table><tr><td><i class=\"tiny material-icons\" >wb_sunny</i></td><td>Temperature</td></tr><tr><td><i class=\"tiny material-icons\">opacity</i></td><td> Humidity </td></tr><tr><td><i class=\"tiny material-icons\" >gradient</i></td><td>Soil Moisture</td></tr></table>';
        return div;
    };
    legend.addTo(mymap);


    //add devices to map as popups
    function addToMapPopoup(lat, long, devName, devId, temp, humidity, soilMoisture) {
        var popupLocation = new L.LatLng(lat, long);
        if (temp == null) {
            temp = 0;
        }
        if (humidity == null) {
            humidity = 0;
        }
        if (soilMoisture == null) {
            soilMoisture = 0;
        }
        var popupContent = "<div onclick=\"window.location.href='details.jsp?id=" + devName + "'\"><b id='TractorHub" + devId + "' >" + devName + "</b><br><table><tr><td><i class=\"tiny material-icons\" >wb_sunny</i></td><td>" + precise_round(temp, 3) + "&#8451</td><td><i class=\"tiny material-icons\">opacity</i></td><td>" + humidity + "%</td><td><i class=\"tiny material-icons\" >gradient</i></td><td>" + soilMoisture + "<strong>%</strong></td></table></div>";
        popup = new L.Popup({maxWidth: "auto", autoPan: false, closeButton: false, closeOnClick: false});
        popup.setLatLng(popupLocation);
        popup.setContent(popupContent);
        mymap.addLayer(popup);
    }

    //initialising the input map
    var map = L.map('inputMapId').setView([7.65655, 80.77148], 7);

    L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);

    var popup = L.popup();
    var lat;
    var lng;

    //generates popup and assigns latitude and longitude values to variables
    function onMapClick(e) {
        popup
            .setLatLng(e.latlng)
            .setContent("Location with coordinates " + e.latlng.toString() + " is selected")
            .openOn(map);
        lat = e.latlng.lat;
        lng = e.latlng.lng;

        latValue(lat);
        lngValue(lng);

    }

    // returns value of latitude
    function latValue(lat) {
        return lat;
    }

    // returns value of longitude
    function lngValue(lng) {
        return lng;
    }

    map.on('click', onMapClick);

    //fixed the issue with map not rendering in tabbed view and pop up model
    $("a[href='#mapView']").on('shown.bs.tab', function (e) {
        mymap.invalidateSize();
        //hide the search bar on map view
        $('#hide').hide();
    });

    $('#newDeviceModal').on('show.bs.modal', function () {
        setTimeout(function () {
            map.invalidateSize();
        }, 200);
    });
    $("a[href='#tableview']").on('shown.bs.tab', function (e) {
        //show the search bar on table view
        $('#hide').show();
    });

</script>
<script type="text/javascript">

    $(document).ready(function () {
        getDevices(0, 10);//load first page
        getAllDevices();//add all devices to map
    });

    function getDevice(dev, index, lat, long) {
        var devicesListing = $('#devices-listing');

        var lastKnownSuccess = function (data) {
            var records = JSON.parse(data);
            var record = JSON.parse(data).records[0];

            var temperature = null;
            var humidity = null;
            var soilMoisture = null;

            if (record) {
                temperature = record.values.temperature;
                humidity = record.values.humidity;
                soilMoisture = record.values.soilMoisture;
            }

            var myRow;
            if (temperature == null || humidity == null || soilMoisture == null) {
                myRow = "<tr onclick=\"window.location.href='details.jsp?id=" + dev.deviceIdentifier + "'\" style='cursor: pointer'><a href='#" + dev.deviceIdentifier + "'><td><div class=\"card card-stats\" style='width: 75%'> <div class=\"card-header\" data-background-color=\"purple\"> <i class=\"material-icons\">nature</i> </div> <div class=\"card-content\"> <p class=\"category\">Farm</p> <h3 class=\"title\" >" + dev.name + "</h3> </div> </div>\n"
                    + "</td><td>"
                    + "<div class=\"card\"><div class=\"card-header card-chart\" data-background-color=\"red\" style=\"height: 90px;min-height: unset;\"><div class=\"ct-chart\" id=\"HistoricalTempChart" + dev.deviceIdentifier + "\"></div></div><div class=\"card-content\"><h4 class=\"title\">N/A</h4><p class=\"category\" id=\"historicalTempAlert" + dev.deviceIdentifier + "\"></div></div>\n</td><td><div class=\"card\"><div class=\"card-header card-chart\" data-background-color=\"orange\" style=\"height: 90px;min-height: unset;\"><div class=\"ct-chart\" id=\"HistoricalHumidityChart" + dev.deviceIdentifier + "\"></div></div><div class=\"card-content\"><h4 class=\"title\">N/A</h4><p class=\"category\" id=\"historicalHumidAlert" + dev.deviceIdentifier + "\"></div></div>\n</td><td>"
                    + "<div class=\"card\"><div class=\"card-header card-chart\" data-background-color=\"green\" style=\"height: 90px;min-height: unset;\"><div class=\"ct-chart\" id=\"HistoricalsoilMoistureChart" + dev.deviceIdentifier + "\"></div></div><div class=\"card-content\"><h4 class=\"title\">N/A</h4><p class=\"category\" id=\"historicalsoilMoistureAlert" + dev.deviceIdentifier + "\"></div></div>\n</td>"
                    + "</a></tr>";
            }
            else {
                myRow = "<tr onclick=\"window.location.href='details.jsp?id=" + dev.deviceIdentifier + "'\" style='cursor: pointer'><a href='#" + dev.deviceIdentifier + "'><td><div class=\"card card-stats\" style='width: 75%'> <div class=\"card-header\" data-background-color=\"purple\"> <i class=\"material-icons\">nature</i> </div> <div class=\"card-content\"> <p class=\"category\">Farm</p> <h3 class=\"title\" >" + dev.name + "</h3> </div> </div>\n"
                    + "</td><td>"
                    + "<div class=\"card\"><div class=\"card-header card-chart\" data-background-color=\"red\" style=\"height: 90px;min-height: unset;\"><div class=\"ct-chart\" id=\"HistoricalTempChart" + dev.deviceIdentifier + "\"></div></div><div class=\"card-content\"><h4 class=\"title\"> " + ( precise_round(temperature, 3)) + "&#8451</h4><p class=\"category\" id=\"historicalTempAlert" + dev.deviceIdentifier + "\"></div></div>\n</td><td><div class=\"card\"><div class=\"card-header card-chart\" data-background-color=\"orange\" style=\"height: 90px;min-height: unset;\"><div class=\"ct-chart\" id=\"HistoricalHumidityChart" + dev.deviceIdentifier + "\"></div></div><div class=\"card-content\"><h4 class=\"title\"> " + (humidity) + "%</h4><p class=\"category\" id=\"historicalHumidAlert" + dev.deviceIdentifier + "\"></div></div>\n</td><td>"
                    + "<div class=\"card\"><div class=\"card-header card-chart\" data-background-color=\"green\" style=\"height: 90px;min-height: unset;\"><div class=\"ct-chart\" id=\"HistoricalsoilMoistureChart" + dev.deviceIdentifier + "\"></div></div><div class=\"card-content\"><h4 class=\"title\"> " + (soilMoisture) + "<strong>%</strong></h4><p class=\"category\" id=\"historicalsoilMoistureAlert" + dev.deviceIdentifier + "\"></div></div>\n</td>"
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
                    console.log(text);
                }).hide();

            });
        };

        $.ajax({
            type: "POST",
            url: "invoker/execute",
            data: {
                "uri": "/events/last-known/TractorHub/" + devicesTemp[index].deviceIdentifier + "?limit=5",
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
                "uri": "/devices/?type=TractorHub&requireDeviceInfo=true&offset=" + offset + "&limit=" + limit,
                "method": "get"
            },
            success: getsuccess
        });
    }

    function addToMap(dev, index, lat, long) {
        var KnownSuccess = function (data) {
            var records = JSON.parse(data);
            var record = JSON.parse(data).records[0];

            var temperature = null;
            var humidity = null;
            var soilMoisture = null;

            if (record) {
                temperature = record.values.temperature;
                humidity = record.values.humidity;
                soilMoisture = record.values.soilMoisture;
            }

            //To fix the issue of adding devices with null or undefined location values to map
            if ((lat == null || lat === "undefined" ) || (long == null || lat === "undefined")) {
                console.log('undefined lat' + lat + ' long ' + long);
            }
            else {
                addToMapPopoup(lat, long, dev.deviceIdentifier, dev.id, temperature, humidity, soilMoisture);
            }

        };

        $.ajax({
            type: "POST",
            url: "invoker/execute",
            data: {
                "uri": "/events/last-known/TractorHub/" + devices[index].deviceIdentifier,
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
            data: {"uri": "/devices/?type=TractorHub&requireDeviceInfo=true&offset=0&limit=100", "method": "get"},
            success: success
        });
    }


    function addNewDevice() {
        var deviceId = $("#deviceId").val();
        var deviceName = $("#deviceName").val();
        var deviceDesc = $("#deviceDesc").val();

        var success = function (data) {
            var config = {};
            config.deviceType = "TractorHub";
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
            + "\"type\": \"TractorHub\",\n"
            + "\"enrolmentInfo\": {\"status\": \"ACTIVE\", \"ownership\": \"BYOD\"},\n"
            + "\"properties\": [{name: \"FarmLatitude\", value:\"" + lat + "\"}, {name: \"FarmLongitude\", value: \"" + lng + "\"}]\n"
            + "}";
        $.ajax({
            type: "POST",
            url: "invoker/execute",
            data: {"uri": "/device/agent/enroll", "method": "post", "payload": payload},
            success: success
        });
    }
</script>
<script type="text/javascript">
    //function for the charts
    function initDashboardPageCharts(deviceId) {
        temp[deviceId] = {};
        humid[deviceId] = {};
        soilMoisture[deviceId] = {};

        //use this to get different variables for different devices
        this["historicalTempLabel" + deviceId] = ['0s']
        this["historicalTempSeries" + deviceId] = [0]
        this["historicalHumidLabel" + deviceId] = ['0s']
        this["historicalHumidSeries" + deviceId] = [0]
        this["historicalsoilMoistureLabel" + deviceId] = ['0s']
        this["historicalsoilMoistureSeries" + deviceId] = [0]

        /* ----------==========      Temperature Chart initialization    ==========---------- */
        dataHistoricalTempChart = {
            labels: this["historicalTempLabel" + deviceId],
            series: [
                this["historicalTempSeries" + deviceId]
            ]
        };

        optionsHistoricalTempChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                tension: 0
            }),
            showArea: true,
            low: -50,
            high: 50, // creative tim: we recommend you to set the high sa the biggest value + something for a better
            // look
            chartPadding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        temp[deviceId] =
            new Chartist.Line('#HistoricalTempChart' + deviceId, dataHistoricalTempChart, optionsHistoricalTempChart);
        md.startAnimationForLineChart(temp[deviceId]);

        /* ----------==========      Humidity Chart initialization    ==========---------- */
        dataHistoricalHumidChart = {
            labels: this["historicalHumidLabel" + deviceId],
            series: [
                this["historicalHumidSeries" + deviceId]
            ]
        };

        optionsHistoricalHumidChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                tension: 0
            }),
            showArea: true,
            low: 0,
            high: 100, // creative tim: we recommend you to set the high sa the biggest value + something for a better
            // look
            chartPadding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        humid[deviceId] =
            new Chartist.Line('#HistoricalHumidityChart' + deviceId, dataHistoricalHumidChart, optionsHistoricalHumidChart);
        md.startAnimationForLineChart(humid[deviceId]);

        /* ----------==========      Wind direction Chart initialization    ==========---------- */
        dataHistoricalsoilMoistureChart = {
            labels: this["historicalsoilMoistureLabel" + deviceId],
            series: [
                this["historicalsoilMoistureSeries" + deviceId]
            ]
        };

        optionsHistoricalsoilMoistureChart = {
            lineSmooth: Chartist.Interpolation.cardinal({
                tension: 0
            }),
            showArea: true,
            low: 0,
            high: 100, // creative tim: we recommend you to set the high sa the biggest value + something for a better
            // look
            chartPadding: {
                top: 0,
                right: 0,
                bottom: 0,
                left: 0
            }
        };

        soilMoisture[deviceId] =
            new Chartist.Line('#HistoricalsoilMoistureChart' + deviceId, dataHistoricalsoilMoistureChart, optionsHistoricalsoilMoistureChart);
        md.startAnimationForLineChart(soilMoisture[deviceId]);


    }

    function timeDifference(current, previous) {
        var msPerMinute = 60 * 1000;
        var msPerHour = msPerMinute * 60;
        var msPerDay = msPerHour * 24;
        var msPerMonth = msPerDay * 30;
        var msPerYear = msPerDay * 365;

        var elapsed = current - previous;

        if (elapsed < msPerMinute) {
            return Math.round(elapsed / 1000) + ' seconds ago';
        } else if (elapsed < msPerHour) {
            return Math.round(elapsed / msPerMinute) + ' minutes ago';
        } else if (elapsed < msPerDay) {
            return Math.round(elapsed / msPerHour) + ' hours ago';
        } else if (elapsed < msPerMonth) {
            return Math.round(elapsed / msPerDay) + ' days ago';
        } else if (elapsed < msPerYear) {
            return Math.round(elapsed / msPerMonth) + ' months ago';
        } else {
            return Math.round(elapsed / msPerYear) + ' years ago';
        }
    }

    function redrawGraphs(events, deviceId) {

        var sumTemp = 0;
        var sumHumid = 0;
        var sumsoilMoisture = 0;


        if (events.count > 0) {

            var currentTime = new Date();
            this["historicalTempLabel" + deviceId].length = 0;
            this["historicalTempSeries" + deviceId].length = 0;
            this["historicalHumidLabel" + deviceId].length = 0;
            this["historicalHumidSeries" + deviceId].length = 0;
            this["historicalsoilMoistureLabel" + deviceId].length = 0;
            this["historicalsoilMoistureSeries" + deviceId].length = 0;

            for (var i = events.records.length - 1; i >= 0; i--) { //invert the results so final record comes last

                var record = events.records[i];

                var sinceText = timeDifference(currentTime, new Date(record.timestamp));
                var dataPoint = record.values;
                var temperature = dataPoint.temperature;
                var humidity = dataPoint.humidity;
                var rain = dataPoint.soilMoisture;

                if (temperature)
                    sumTemp += temperature;

                if (humidity)
                    sumHumid += humidity;

                if (soilMoisture)
                    sumsoilMoisture += soilMoisture;

                this["historicalTempLabel" + deviceId].push(sinceText);
                this["historicalTempSeries" + deviceId].push(temperature);

                this["historicalHumidLabel" + deviceId].push(sinceText);
                this["historicalHumidSeries" + deviceId].push(humidity);

                this["historicalsoilMoistureLabel" + deviceId].push(sinceText);
                this["historicalsoilMoistureSeries" + deviceId].push(rain);

                temp[deviceId].update();
                humid[deviceId].update();
                soilMoisture[deviceId].update();


            }
        } else {
            //if there is no records in this period display no records

            temp[deviceId].update();
            humid[deviceId].update();
            soilMoisture[deviceId].update();


        }
    }

    function precise_round(num, decimals) {
        var t = Math.pow(10, decimals);
        return (Math.round((num * t) + (decimals > 0 ? 1 : 0) * (Math.sign(num) * (10 / Math.pow(100, decimals)))) / t).toFixed(decimals);
    }


</script>
</html>
