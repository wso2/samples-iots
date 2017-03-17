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
	$('body').removeClass('modal-open').css('padding-right','0px');
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
	setTimeout(function(){ map.invalidateSize()}, 400);

	//TODo : When loading the map, call to backend and get all the buildings available and show them on the map.
	var getAllBuildingsAPI = "";
    invokerUtil.get();
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

function addBuilding (e) {

	tmpEventStore = e;
	var content = $("#building-response-template");
	var buttonme = content.find("#buttonme");
	if (buttonme) {
		buttonme.remove();
	}

	var button = '<button id="buttonme" type="button" class="btn btn-primary" data-toggle="update-data" onclick="saveBuilding()">Save</button>';
	content.find("#building-button").append(button);
	$(modalPopupContent).html(content.html());


	showPopup();
	setTimeout(function () {
		hidePopup();
		// location.reload(true);

	}, 20000);
}

function saveBuilding () {
	//save building here.
	var e = document.getElementsByName('locationName')[0];
	var noOffloors = document.getElementsByName('floors')[0].value;
	var buildingName = e.value;
	var cords = tmpEventStore.latlng;

	console.log(buildingName);
	console.log(noOffloors);
	console.log(cords);

	addingMarker(tmpEventStore, buildingName);

	var addBuildingApi = "/senseme/building";
	var buildingdata = {buildingName:buildingName, longitude:cords.lat, latitude:cords.lng, noFloors:noOffloors};

	console.log(buildingdata);

	var buildingId;

	invokerUtil.post(addBuildingApi, buildingdata, function(data, textStatus, jqXHR){
		if (jqXHR.status == 200 && data) {
			console.log(jqXHR);
		}
	}, function(jqXHR){
		if (jqXHR.status == 400) {
			console.log("error")
		} else {
			var response = JSON.parse(jqXHR.responseText).message;

		}
	},"application/json","application/json");

	hidePopup();
	console.log("Building added");
	//ToDo : Send the building info to backend and get the building Id.
	location.href = "/buildingmonitor/buildings?buildingId=1";
}

// Script for adding marker
function addingMarker(e, locationName) {

	var cord,
		markerId,
		popup;

	$('body.fixed ').removeClass('marker-cursor');
	$('#device-location').removeClass('marker-cursor');
	cord = e.latlng;


	popup = L.popup({
		autoPan: true,
		keepInView: true})
		.setContent('<p>Hello there!<br /><a href="/buildingmonitor/buildings?buildingId=1" class="btn btn-primary">' +
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
	//marker.on("popupopen", onPopupOpen);
	marker.addTo(map).openPopup();
	markerId = marker._leaflet_id;
	console.log(marker._leaflet_id);

	markers[markerId] = marker;
	console.log(markerId);
	console.log(markers[markerId]);

	//Adding panel heading on load only
	if(e == null){
		$('#heading'+markerId).find('.panel-title').text(locationName);
	}


	// Remove Marker
	$('.remove').on("click", function () {
		// Remove the marker
		map.removeLayer(markers[$(this).attr('id')]);

		var idNo = $(this).attr('id');
		// Remove the link
		$(this).parent('div').remove();

		$('#home').find('#' + idNo).remove();
		console.log(typeof objectJSON.metaData);

		if(window.localStorage.getItem(KEY_NAME) !== null){
			$.each(objectJSON.metaData, function(i, values){
				// console.log(values[i]);
				if(values.id == idNo){
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
		$('#'+$(this).attr('id')+' .panel-default > .panel-heading').addClass('active-marker');
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
