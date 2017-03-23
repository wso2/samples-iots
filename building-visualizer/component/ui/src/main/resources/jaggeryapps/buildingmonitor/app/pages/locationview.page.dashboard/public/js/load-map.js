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

function loadLeafletMap() {
    var deviceLocationID = "#device-location"
    container = "device-location",
        zoomLevel = 13,
        tileSet = "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
        attribution = "&copy; <a href='https://openstreetmap.org/copyright'>OpenStreetMap</a> contributors";
    map = L.map(container).locate({setView: true, maxZoom: 17, animate: true, duration: 3});
    L.tileLayer(tileSet, {attribution: attribution}).addTo(map);
    setTimeout(function () {
        map.invalidateSize()
    }, 400);

    //TODo : When loading the map, call to backend and get all the buildings available and show them on the map.
    preLoadBuildings();
}

function preLoadBuildings() {
    var getBuildingApi = "/senseme/building";
    invokerUtil.get(getBuildingApi, function (data, textStatus, jqXHR) {
        if (jqXHR.status == 200) {
            var buildingIds;
            //[{"buildingId":1,"buildingName":"ayyoobs1","owner":"admin","longitude":"79.97607422294095","latitude":"6.995539474716988","numFloors":4}
            var buildings = JSON.parse(data);
            for (var i = 0; i < buildings.length; i++) {
                var obj = buildings[i];
                console.log(obj);
                var cord = {"lat": obj.latitude, "lng": obj.longitude};
                console.log(cord);
                addingMarker(cord, obj.buildingName, obj.buildingId, buildings[i]);
                //printBuildingData(obj);
                if (i == 0) {
                    buildingIds =  obj.buildingId;
                } else {
                    buildingIds = buildingIds + "," + obj.buildingId;
                }

            }
            if (buildings.length > 0) {
                loadNotifications(buildings, buildingIds);
            }
        }
    }, function (jqXHR) {
    }, "application/json");

}

function loadNotifications(buildingData, buildingIds) {
    var messageSideBar = ".sidebar-messages";
    if ($("#right-sidebar").attr("is-authorized") == "true") {
        var notifications = $("#notifications");
        var currentUser = notifications.data("currentUser");

        $.template("notification-listing", notifications.attr("src"), function (template) {
            var currentDate = new Date();
            currentDate.setHours(currentDate.getHours() - 24);
            var endDate = new Date();

            var providerData = null;
            console.log(buildingIds);
            var providerUrl = context + '/api/batch-provider?action=getMapAlertCount&tableName=ORG_WSO2_FLOOR_ALERTNOTIFICATIONS&buildingSet=' + buildingIds;

            $.ajax({
                url: providerUrl,
                method: "GET",
                contentType: "application/json",
                async: false,
                success: function (data) {
                    var viewModel = {};
                    var notifications = [];

                    for (var index in data) {
                        var notification = {};
                        notification.buildingId = buildingData[index].buildingId;
                        notification.buildingName = buildingData[index].buildingName;
                        notification.alertCount = data[index]
                        notifications.push(notification);
                    }

                    viewModel.notifications = notifications;
                    $(messageSideBar).html(template(viewModel));
                },
                error: function (err) {
                    console.log(err);
                }
            });
        });
    } else {
        $(messageSideBar).html("<h4 class ='message-danger text-center'>You are not authorized to view notifications</h4>");
    }

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
    var cords = tmpEventStore.latlng;
    var addBuildingApi = "/senseme/building";
    var buildingdata = {buildingName: buildingName, longitude: cords.lat, latitude: cords.lng, numFloors: noOffloors};

    console.log(buildingdata);
    invokerUtil.post(addBuildingApi, buildingdata, function (data, textStatus, jqXHR) {
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

    hidePopup();
    location.reload();
}

function addBuilding(e) {
    //save building here.
    tmpEventStore = e;
    var cord = e.latlng;
    var content = $("#building-response-template");

    $(modalPopupContent).html(content.html());
    showPopup();
    setTimeout(function () {
        hidePopup();
        // location.reload(true);

    }, 20000);
    addingMarker(cord, null, null);
}

function onMarkerDragged(event) {
    var marker = event.target;
    console.log("Marker ");
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

// Script for adding marker
function addingMarker(cord, locationName, buildingId, building) {

    var markerId,
        popup;
    $('body.fixed ').removeClass('marker-cursor');
    $('#device-location').removeClass('marker-cursor');

    popup = L.popup({
        autoPan: true,
        keepInView: true
    })
        .setContent('<p>Hello there!<br /><a href="/buildingmonitor/buildings?buildingId=' + buildingId + '" class="btn btn-primary">' +
            "Get into " + locationName + '</a></p>');

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
    console.log(marker._leaflet_id);

    if (building != null) {
        buildingsMap[markerId] = building;
    }

    markers[markerId] = marker;
    console.log(markerId);
    console.log(markers[markerId]);

    //Adding panel heading on load only
    //if(e == null){
    //	$('#heading'+markerId).find('.panel-title').text(locationName);
    //}


    // Remove Marker
    $('.remove').on("click", function () {
        // Remove the marker
        map.removeLayer(markers[$(this).attr('id')]);

        var idNo = $(this).attr('id');
        // Remove the link
        $(this).parent('div').remove();

        $('#home').find('#' + idNo).remove();
        console.log(typeof objectJSON.metaData);

        if (window.localStorage.getItem(KEY_NAME) !== null) {
            $.each(objectJSON.metaData, function (i, values) {
                // console.log(values[i]);
                if (values.id == idNo) {
                    objectJSON.metaData.splice(i, 1);
                    console.log("accessed");
                    console.log(objectJSON);
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
