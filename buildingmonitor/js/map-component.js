//image bounds
var baseMap;

//Array to store marker latlng and ID
var markers = [];

// quick and dirty: create a big icon
L.Icon.Big = L.Icon.Default.extend({
    options: {
        iconSize: new L.Point(30, 49),
    }
});

//THE ICONS
var bigIcon = new L.Icon.Big();
var smallIcon = new L.Icon.Default();

//on load
initialiseMap();

//add base map
function initialiseMap() {

    //Renders map to user's current location
    baseMap = L.map('openStreetMapId').locate({setView: true, maxZoom: 17, animate: true, duration: 3});

    var sidebar = L.control.sidebar('sidebar', {position: 'right'}).addTo(baseMap);

    //Rendering resources
    L.tileLayer("http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
        maxZoom: 19,
        attribution: 'Map data © <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, CC-BY-SA.'
    }).addTo(baseMap);

    //show current location
    baseMap.on('locationfound', showMe);
    function showMe(e) {
        popup.setLatLng(e.latlng).setContent("Hi! You're Here")
            .openOn(baseMap)
            .keepInView;
    }

    //On location error
    function onLocationError(e) {
        alert(e.message);
    }

    baseMap.on('locationerror', onLocationError);

    var popup = L.popup();

}

// HTML5 local storage
var KEY_NAME = "yesin";
var objectJSON;


//    reading data from JSON
$(document).ready(function () {
    // $.get('result.json', function (objectJSON) {
    if(window.localStorage.getItem(KEY_NAME) === null){

    } else{

        objectJSON = JSON.parse(window.localStorage.getItem(KEY_NAME));
        console.log(objectJSON);
        $.each(objectJSON.metaData, function(i, n){
            addingMarker(null, n);
        });
        // localStorage.clear();
    }

    // });
});

function openWin(id) {
    window.open("floor.jag?id=" + id);
}

// Script for adding marker
function addingMarker(e, d) {

    var cord,
        locationName,
        address,
        floorCount,
        markerId,
        popup;

    if(e !== null){
        $('body.fixed ').removeClass('marker-cursor');
        $('#openStreetMapId').removeClass('marker-cursor');
        cord = e.latlng;
        locationName = "";
        address = "";
        floorCount = "";

        popup = L.popup({
            autoPan: true,
            keepInView: true})
            .setContent('<p>Hello there!<br />Please enter the marker details! :)</p>');
    }
    else {
        cord = L.latLng(d.coordinates.lat, d.coordinates.lng);
        locationName = d.locationName;
        address = d.address;
        floorCount = d.floors;
        markerId = d.id;
//            markers._leaflet_id = markerId;

        //popup content
        var popupContent = '<h4>'+ locationName +'</h4>' +
            '<ul>' +
            '<li>No of Floors: '+ floorCount+' </li>' +
            '<li> Address: '+ address+'</li>' +
            '</ul>';

        popup = L.popup({
            autoPan: true,
            keepInView: true})
            .setContent(popupContent);
    }

    //variable for marker
    var marker;

    //markers info JSON array
    var markerInfo = {
        info: []
    };

    marker = new L.marker(cord, {
        title: "Resource Location",
        alt: "Resource Location",
        riseOnHover: true,
        draggable: true
    }).bindPopup(popup);

    marker._leaflet_id = markerId;


    marker.on('click', onMarkerClick);
    marker.on("popupopen", onPopupOpen);
    marker.addTo(baseMap);


    if(e !== null){
        markerId = marker._leaflet_id;
        console.log(marker._leaflet_id);
    }

    markers[markerId] = marker;
    console.log(markerId);
    console.log(markers[markerId]);

    //append the information to the sidebar
    $('#home').append(
        <!-- collapse -->
        '<div class="item panel-group add-margin-bottom-2x" role="tablist" aria-multiselectable="false" id="' + markerId + '" data-lat="' + marker._latlng.lat + '" data-lng="' + marker._latlng.lng + '" >' +
        //'<div class="" role="tablist" aria-multiselectable="false">'+
        '<div class="panel panel-default">'+
        '<div class="panel-heading" role="tab" id="heading' + markerId + '" data-toggle="collapse" href="#accordion' + markerId + '" aria-controls="accordion' + markerId + '">'+
        '<h4 class="panel-title">New Building</h4>'+
        '<div class="btn-group">' +
        '<button type="button" class="btn btn-primary add-margin-right-2x" data-toggle="edit-floors">' +
        '<span class="icon fw-stack add-margin-right-1x">'+
        '<i class="fw fw-edit  fw-helper fw-helper-circle-outline"></i>' +
        '</span>Edit Floors' +
        '</button>'+
        '<button type="button" class="btn btn-secondary remove add-margin-right-2x" data-toggle="" id="' + markerId + '">' +
        '<span class="icon fw-stack add-margin-right-1x">'+
        '<i class="fw fw-delete  fw-helper fw-helper-circle-outline"></i>' +
        '</span>' +
        'Delete Building</button>' +
        '<div class="clearfix"></div>'+
        '</div>' +
//                            '<a href="#" class="remove" id="' + markerId + '">' +
//                                '<span class="icon fw-stack">' +
//                                '<i class="fw fw-delete fw-stack-1x"></i>' +
//                                '</span> Remove' +
//                            '</a>' +
//                            '<button type="button" class="btn btn-secondary" data-toggle="edit-floors">Edit Floors</button>' +

        '</div>'+
        '<div id="accordion' + markerId + '" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading' + markerId + '">'+
        '<div class="panel-body">'+
        '<form action="" method="post" id="register-form-'+ markerId +'" novalidate="novalidate">' +
        '<div class="form-group">' +
        '<label for="locationName">Location Name *</label>' +
        '<input type="text" class="form-control" name="locationName" placeholder="e.g.: Home" value="' + locationName + '">' +
        '</div>' +
        '<div class="form-group">' +
        '<label for="address">Address *</label>' +
        '<input type="text" class="form-control" name="address" placeholder="e.g.: 13/13, Mockingbird Ave, New York" value="'+address+'">' +
        '</div>' +
        '<div class="form-group">' +
        '<label for="floors">No. of Floors *</label>' +
        '<input type="number" class="form-control required" name="floors" placeholder="e.g.: Number of floors" value="'+floorCount+'">' +
        '</div>' +
        '<div class="form-group">' +
        '<button type="button" class="btn btn-primary" data-toggle="update-data">Save</button>' +
//                                '<button type="button" class="btn btn-secondary" data-toggle="edit-floors">Edit Floors</button>' +
        '</div>' +
        '</form>' +
        '</div>'+
        '</div>'+
        '</div>'+
//       '</div>'+
        '</div>');
    <!-- /collapse -->

    //Adding panel heading on load only
    if(e == null){
        $('#heading'+markerId).find('.panel-title').text(locationName);
    }


    // Remove Marker
    $('.remove').on("click", function () {
        // Remove the marker
        baseMap.removeLayer(markers[$(this).attr('id')]);

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

//open marker popup
function markerFunction(id) {
//        console.log(markers[id]);
    markers[id].openPopup();

}

$('#home').on('click', '[data-toggle=edit-floors]', function(e){
    if($(e.target).closest('.panel-heading').next('.panel-collapse').find('form').valid()) {
        var markerID = $(e.target).closest('.item').prop('id');
        openWin(markerID);
    }
});

$('.leaflet-popup').on('click', '[data-toggle=edit-floors]', function(e){
    console.log(e.target);
    if($(e.target).closest('.panel-heading').next('.panel-collapse').find('form').valid()) {
        var markerID = $(e.target).closest('.item').prop('id');
        openWin(markerID);
    }
});


// Function to handle delete as well as other events on marker popup open
function onPopupOpen() {
    var tempMarker = this;
}

//Set
function onMarkerClick(e) {
    $('div').removeClass('active-marker');
    $('div #' + e.target._leaflet_id).addClass('active-marker');
    for (var mark in markers) {
        markers[mark].setIcon(smallIcon);
    }
    var offset = baseMap.panTo(markers[mark].getLatLng());
    baseMap.panBy(offset);
}

function onAddMarker() {

    baseMap.once('click', addingMarker);
//        if(click !== null){
    $('body.fixed ').addClass('marker-cursor');
    $('#openStreetMapId').addClass('marker-cursor');
//        }
}