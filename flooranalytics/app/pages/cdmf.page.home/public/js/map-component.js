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



//add base map
function initialiseMap() {

    //Renders map to user's current location
    baseMap = L.map('build-location').locate({setView: true, maxZoom: 17, animate: true, duration: 3});


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




//    reading data from JSON
$(document).ready(function () {
    //on load
    initialiseMap();
    // $.get('result.json', function (objectJSON) {

    // });
});

