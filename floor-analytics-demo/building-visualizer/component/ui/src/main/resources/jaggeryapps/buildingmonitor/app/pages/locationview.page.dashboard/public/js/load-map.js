/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

var map;
var locateMe; //locate me position
//Array to store marker latlng and ID
var markers = [];
var buildingsMap = [];

var modalPopup = ".modal";
var modalPopupContainer = modalPopup + " .modal-content";
var modalPopupContent = modalPopup + " .modal-content";

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
    $('body').removeClass('modal-open').css('padding-right', '0px');
    $('.modal-backdrop').remove();
}

function locateMe (e) {
    locateMe.start()
}

function zoomIn (e) {
    map.zoomIn();
}

function zoomOut (e) {
    map.zoomOut();
}

function loadLeafletMap() {
    var deviceLocationID = "#device-location"
    container = "device-location",
        zoomLevel = 13,
        tileSet = "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
        attribution = "&copy; <a href='https://openstreetmap.org/copyright'>OpenStreetMap</a> contributors";
    var menuItems = [{
        text: 'Add Building',
        callback: addBuilding
    }, {
        text: 'Locate me',
        callback: locateMe
    }, '-', {
        text: 'Zoom in',
        callback: zoomIn
    }, {
        text: 'Zoom out',
        callback: zoomOut
    }];

    if (!$("#device-location").attr("add-building")) {
        menuItems.shift();
    }
    map = L.map(container, {
        contextmenu: true,
        contextmenuWidth: 140,
        contextmenuItems: menuItems
    }).locate({setView: true, maxZoom: 17, animate: true, duration: 3});
    L.tileLayer(tileSet, {attribution: attribution}).addTo(map);

    //locate me button
    locateMe = L.control.locate({
        strings: {
            title: "Locate me"
        }
    }).addTo(map);

    setTimeout(function () {
        map.invalidateSize();
    }, 400);

    preLoadDevices();
    preLoadBuildings();
}

function preLoadDevices() {
    var getDevicesApi = "/senseme/building/0/0/devices";
    invokerUtil.get(getDevicesApi, function (data, textStatus, jqXHR) {
        if (jqXHR.status == 200) {
            var devices = JSON.parse(data);
            for(var j = 0; j < devices.length; j++) {
                var deviceObj = devices[j];
                var cord = {"lat": deviceObj.xCord, "lng": deviceObj.yCord};
                var location = {
                    deviceId: deviceObj.deviceId,
                    longitude: deviceObj.xCord,
                    latitude: deviceObj.yCord
                };
                addingMarkerLocation(cord, location);
            }
        }
    }, function (jqXHR) {
        console.log(jqXHR.responseText);
    }, "application/json");
}

function preLoadBuildings() {
    var getBuildingDevicesApi = "/senseme/building/devices";
    var devices = {};
    invokerUtil.get(getBuildingDevicesApi, function (data, textStatus, jqXHR) {
        var status = jqXHR.status;
        console.log("status "  +status);
        if (status >= 200 && status < 300) {
            if (status == 200) {
                devices = JSON.parse(data);
            }
            var getBuildingApi = "/senseme/building";
            invokerUtil.get(getBuildingApi, function (data, textStatus, jqXHR) {
                console.log("here ///");
                if (jqXHR.status == 200) {
                    //[{"buildingId":1,"buildingName":"ayyoobs1","owner":"admin","longitude":"79.97607422294095","latitude":"6.995539474716988","numFloors":4}
                    var buildings = JSON.parse(data);
                    var buildingIds;
                    if (buildings.length > 0) {
                        for (var i = 0; i < buildings.length; i++) {
                            if (i == 0) {
                                buildingIds = buildings[i].buildingId;
                            } else {
                                buildingIds = buildingIds + "," + buildings[i].buildingId;
                            }
                        }
                        var providerUrl = context + '/api/batch-provider?action=getMapAlertCount&tableName=ORG_WSO2_FLOOR_ALERTNOTIFICATIONS&buildingSet=' + buildingIds;

                        $.ajax({
                            url: providerUrl,
                            method: "GET",
                            contentType: "application/json",
                            contentType: "application/json",
                            contentType: "application/json",
                            contentType: "application/json",
                            async: false,
                            success: function (buildingAlertCount) {
                                for (var i = 0; i < buildings.length; i++) {
                                    var obj = buildings[i];
                                    var cord = {"lat" : obj.latitude, "lng" : obj.longitude};
                                    var buildingdevice = {"active" : 0,
                                        "inactive" : 0,
                                        "fault":0, "total":0, "alerts" : 0};

                                    for(var j = 0; j < devices.length; j++) {
                                        if (devices[j].id == obj.buildingId) {
                                            var deviceobj = devices[j];
                                            //[{"id":"9","activeDevices":0,"faultDevices":0,"inactiveDevices":4,"totalDevices":4}]
                                            var alert = buildingAlertCount[obj.buildingId];
                                            if (!alert && alert== undefined) {
                                                alert = 0;
                                            }
                                            buildingdevice = {"active" : deviceobj.activeDevices,
                                                "inactive" : deviceobj.inactiveDevices,
                                                "fault":deviceobj.faultDevices, "total":deviceobj.totalDevices, "alerts" : alert};
                                            break;
                                        }

                                    }
                                    addingMarker(cord, obj.buildingName, obj.buildingId, buildings[i], buildingdevice);
                                    //printBuildingData(obj);
                                }

                            },
                            error: function (err) {
                                console.log(err);
                            }
                        });
                    }
                }
            }, function (jqXHR) {
                console.log(jqXHR.responseText);

            }, "application/json");
        }

    }, function (jqXHR) {
        console.log(jqXHR.responseText);
    }, "application/json");
}


function printBuildingData(buildingInfo) {
    var buildingContent = $("#cont")
    var data = "<div><p>Building Name: " + buildingInfo.buildingName +
        "Owner:" + buildingInfo.owner + " </p></div>";
    buildingContent.find("#buildingContent").append(data);
}

$(document).ready(function () {
    loadLeafletMap();
});

function onAddMarker() {
    map.once('click', addBuilding);
//        if(click !== null){
    $('body.fixed ').addClass('marker-cursor');
    $('#device-location').addClass('marker-cursor');
//        }
}

function onAddMarkerLocation() {
    map.once('click', addDevice);
    $('body.fixed ').addClass('marker-cursor-location');
    $('#device-location').addClass('marker-cursor-location');
}

function onMarkerClick(e) {
    $('div').removeClass('active-marker');
    $('div #' + e.target._leaflet_id).addClass('active-marker');
    for (var mark in markers) {
        markers[mark].setIcon(smallIcon);
    }
    var offset = map.panTo(markers[mark].getLatLng());
    map.panBy(offset);
}

var tmpEventStore;

function saveBuilding() {
    var buildingName = document.getElementsByName('locationName')[0].value;
    var noOffloors = document.getElementsByName('floors')[0].value;
    var lat = document.getElementsByName('lat')[0].value;
    var long = document.getElementsByName('long')[0].value;
    var addBuildingApi = "/senseme/building";
    var buildingdata = {buildingName: buildingName, longitude: lat, latitude: long, numFloors: noOffloors};

    invokerUtil.post(addBuildingApi, buildingdata, function (data, textStatus, jqXHR) {
        if (jqXHR.status == 200 && data) {
            var cord = {"lat": lat, "lng": long};
            var building = {
                buildingName: buildingName,
                longitude: lat,
                latitude: long,
                numFloors: noOffloors,
                buildingId: data
            };
            addingMarker(cord, buildingName, data, building);
        }
    }, function (jqXHR) {
        if (jqXHR.status == 400) {
            console.log("error")
        } else {
            var response = JSON.parse(jqXHR.responseText).message;

        }
    }, "application/json", "application/json");

    hidePopup();
}

function saveLocation() {
    var deviceIdValue = document.getElementsByName('deviceId')[0].value;
    var lat = document.getElementsByName('lat')[0].value;
    var long = document.getElementsByName('long')[0].value;
    var deviceData = {deviceId:deviceIdValue, xCord:lat, yCord:long };
    var addDeviceApi = "/senseme/device/enroll?deviceType=senseme";

    invokerUtil.post(addDeviceApi, deviceData, function (data, textStatus, jqXHR) {
        if (jqXHR.status == 200) {
            var cord = {"lat": lat, "lng": long};
            var location = {
                deviceId: deviceIdValue,
                longitude: lat,
                latitude: long
            };
            addingMarkerLocation(cord, location);
        }
    }, function (jqXHR) {
        if (jqXHR.status == 400) {
            console.log("error");
        } else {
            var response = JSON.parse(jqXHR.responseText).message;

        }
    }, "application/json", "application/json");

    hidePopup();
}

function addBuilding(e) {
    //save building here.
    tmpEventStore = e;
    var cord = e.latlng;
    var content = $("#building-response-template");
    content.find("#lat").attr('value', cord.lat);
    content.find("#long").attr('value', cord.lng);
    $(modalPopupContent).html(content.html());
    showPopup();
}

function addDevice(e) {
    //save building here.
    tmpEventStore = e;
    var cord = e.latlng;
    var content = $("#device-response-template");
    content.find("#lat").attr('value', cord.lat);
    content.find("#long").attr('value', cord.lng);
    $(modalPopupContent).html(content.html());
    showPopup();
}

function onMarkerDragged(event) {
    var marker = event.target;
    var latitude = event.target._latlng.lat;
    var longitude = event.target._latlng.lng;

    var tmp_building = buildingsMap[marker._leaflet_id];
    tmp_building.longitude = latitude;
    tmp_building.latitude = longitude;

    var updateBuildingApi = "/senseme/building/update";

    //TODO : Update the building data base.
    invokerUtil.post(updateBuildingApi, tmp_building, function (data, textStatus, jqXHR) {
        if (jqXHR.status == 200 && data) {
            console.log(jqXHR);
        }
    }, function (jqXHR) {
        if (jqXHR.status == 400) {
            console.log("error")
        } else {
            var response = JSON.parse(jqXHR.responseText).message;

        }
    }, "application/json", "application/json");

    //apiInvokerUTIL.post
    //marker.setLatLng(event.latlng, {id:marker.title, draggable:'true'}).bindPopup(popup).update();

}

function addingMarker(cord, locationName, buildingId, building, buildingdevice) {
    var markerId,
        popup;

    $('body.fixed ').removeClass('marker-cursor');
    $('#device-location').removeClass('marker-cursor');

    var content = $("#device-popup-template").clone();
    var sidebar = $("#sidebar-messages");
    content.attr("id","device-building-" + buildingId);

    console.log(buildingdevice);
    if (buildingdevice && buildingdevice != undefined) {
        content.find("#building-active").text(buildingdevice.active);
        content.find("#building-inactive").text(buildingdevice.inactive);
        content.find("#building-fault").text(buildingdevice.fault);
        content.find("#building-alerts").text(buildingdevice.alerts);
    } else {
        content.find("#building-active").text("0");
        content.find("#building-inactive").text("0");
        content.find("#building-fault").text("0");
        content.find("#building-alerts").text("0");
    }
    content.find("#building-content").text(locationName);
    content.find("#building-content-div").attr("data-buildingid", buildingId);
    content.find("#building-content-div").attr("data-markerid", markerId);
    content.find("#building-content-div").attr("id","building-content-" + buildingId);
    content.find("#building-location").attr("href","/buildingmonitor/buildings?buildingId=" + buildingId);

    popup = L.popup({
        autoPan: true,
        keepInView: true
    })
        .setContent(content.html());

    //variable for marker
    var marker;

    //markers info JSON array
    var markerInfo = {
        info: []
    };

    marker = new L.marker(cord, {
        title: locationName,
        alt: locationName,
        riseOnHover: true,
        draggable: true
    }).bindPopup(popup);

    marker._leaflet_id = markerId;

    marker.on('click', onMarkerClick);

    marker.on('dragend', onMarkerDragged);

    //marker.on("popupopen", onPopupOpen);
    marker.addTo(map);
    markerId = marker._leaflet_id;

    if (building != null) {
        buildingsMap[markerId] = building;
    }

    markers[markerId] = marker;
    addBuildingMenu(buildingId,locationName, markerId, buildingdevice);

    // Remove Marker
    $('.remove').on("click", function () {
        // Remove the marker
        map.removeLayer(markers[$(this).attr('id')]);

        var idNo = $(this).attr('id');
        // Remove the link
        $(this).parent('div').remove();

        $('#home').find('#' + idNo).remove();

        if(window.localStorage.getItem(KEY_NAME) !== null){
            $.each(objectJSON.metaData, function(i, values){
                if(values.id == idNo){
                    objectJSON.metaData.splice(i, 1);
                    var result = JSON.stringify(objectJSON);
                    window.localStorage.setItem(KEY_NAME, result);
                }
            });
        }
    });

    /*
     Pan to Marker on item panel hover
     */
    $('.item').on("mouseover", function (e) {
        $('div').removeClass('active-marker');
        $('#' + $(this).attr('id') + ' .panel-default > .panel-heading').addClass('active-marker');
        for (var mark in markers) {
            markers[mark].setIcon(smallIcon);
        }
        markerFunction($(this).attr('id'));
        markers[$(this).attr('id')].setIcon(bigIcon);
        var mid = $(this).attr('id');
        var LatLng = markers[mid].getLatLng();
        var offset = baseMap.panTo(LatLng);
        baseMap.panBy(offset);
    });
}

function addingMarkerLocation(cord, location) {
    var markerId, popup;

    $('body.fixed ').removeClass('marker-cursor-location');
    $('#device-location').removeClass('marker-cursor-location');

    console.log(location);

    var content = $("#device-location-popup-template").clone();
    content.attr("id","device-" + location.deviceId);

    content.find("#location-content").text(location.deviceId);
    content.find("#location-view").attr("href","/buildingmonitor/senseme?id=" + location.deviceId);

    var deviceIcon = L.icon({
                                iconUrl: '/buildingmonitor/public/cdmf.unit.lib.leaflet/js/images/device.png',
                                shadowUrl: '/buildingmonitor/public/cdmf.unit.lib.leaflet/js/images/marker-shadow.png',
                                iconSize: [40, 40], // size of the icon
                                shadowSize: [40, 40], // size of the shadow
                                iconAnchor: [20, 40], // point of the icon which will correspond to marker's location
                                shadowAnchor: [20, 40],  // the same for the shadow
                                popupAnchor: [-3, -40] // point from which the popup should open relative to the iconAnchor
                            });

    popup = L.popup({
                        autoPan: true,
                        keepInView: true
                    }).setContent(content.html());

    //variable for marker
    var marker = new L.marker(cord, {
        icon: deviceIcon,
        title: location.deviceId,
        alt: location.deviceId,
        riseOnHover: true,
        draggable: true
    }).bindPopup(popup);

    marker._leaflet_id = markerId;

    marker.on('click', onMarkerClick);

    marker.addTo(map);
    markerId = marker._leaflet_id;
    markers[markerId] = marker;
}

function addBuildingMenu(buildingId, buildingName, markerId, buildingdevice) {
    var content = $("#device-building-template").clone();
    var sidebar = $("#sidebar-messages");
    content.attr("id","device-building-" + buildingId);
    if (buildingdevice && buildingdevice != undefined) {
        content.find("#building-active").text(buildingdevice.active);
        content.find("#building-inactive").text(buildingdevice.inactive);
        content.find("#building-fault").text(buildingdevice.fault);
        content.find("#building-alerts").text(buildingdevice.alerts);
    } else {
        content.find("#building-active").text("0");
        content.find("#building-inactive").text("0");
        content.find("#building-fault").text("0");
        content.find("#building-alerts").text("0");
    }
    content.find("#building-content").text(buildingName);
    content.find("#building-content-div").attr("data-buildingid", buildingId);
    content.find("#building-content-div").attr("data-markerid", markerId);
    content.find("#building-content-div").attr("id","building-content-" + buildingId);
    sidebar.append(content);
}

function focusBuilding(id) {
    var buildingId = $("#" + id).attr('data-buildingid');
    var markerId = $("#" + id).attr('data-markerid');
    var mark = markers[markerId].openPopup();
    var offset = map.panTo(mark.getLatLng());
    map.panBy(offset);
}