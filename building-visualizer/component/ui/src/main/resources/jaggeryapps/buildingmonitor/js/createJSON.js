/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//JSON obj array
var markerInfo = {
    // metaData: []
};

//console.log(baseMap);

$('form').validate({
    // Specify the validation rules
    rules: {
        locationName: {
            required: true
            , minlength: 3
        }
        , address: {
            required: true
        }
        , floors: {
            required: true,
            min: 1
        }
    },
    messages: {
        locationName: {
            required: "Please enter the location name"
            , minlength: "Location name should be more than 3 characters"
        }
        , address: {
            required: "Please enter your address"
        }
        , floors: {
            required: "Please provide floor number",
            min: "Please enter a value greater than 0"
        }
    }
});

$('#home').on('click', '[data-toggle=update-data]', function (e) {
    var KEY_NAME = "yesin";
    var objectJSON;
    var metaData;

    if ($(e.target).closest('form').valid()) {
        var markerDetails = {};
        var coordinates = {};
        var element = $(e.target).closest('.item');

        //passing the lat and lng values to coordinates obj
        coordinates["lat"] = element.data('lat');
        coordinates["lng"] = element.data('lng');

        // //get the values of the form elements
        // var id = element.prop('id');

        //get the values of the form elements
        var markerId = element.prop('id');

        var name = element.find('[name=locationName]').val();
        var address = element.find('[name=address]').val();
        var floors = element.find('[name=floors]').val();

        // updates panel heading title on save
        $('#heading' + markerId).find('.panel-title').text(name);

        //check whether a JSON is stored inside local storage
        if (window.localStorage.getItem(KEY_NAME) === null) {
            objectJSON = {
                metaData: []
            };

            var floorplan = [];
            var floorObjs = {};

            for (var i = 1; i <= floors; i++) {
                floorObjs["floor" + i] = "";
            }

            // floorObjs["floor1"] = "";
            floorplan.push(floorObjs);

            //prepare the properties of the object
            markerDetails["id"] = markerId;
            markerDetails["address"] = address;
            markerDetails["buildingPlan"] = "";
            markerDetails["coordinates"] = coordinates;
            markerDetails["floorplan"] = floorplan;
            markerDetails["floors"] = floors;
            markerDetails["locationName"] = name;

            bindPopUpInfo(name, floors, address);

            objectJSON.metaData.push(markerDetails);

        } else if (window.localStorage.getItem(KEY_NAME) !== null) {
            objectJSON = JSON.parse(window.localStorage.getItem(KEY_NAME));
            createMarkerObj(markerId, name, address, floors);
        }

        //Add new marker details to existing JSON
        function createMarkerObj(markerId, name, address, floors) {
            var existing = false;
            var noReplicate = false;

            $.each(objectJSON.metaData, function (i, val) {
                console.log("Adding to existing JSON");
                if (val.id == markerId) {
                    existing = true;
                    //update properties of saved object
                    val.locationName = name;
                    val.address = address;
                    val.floors = floors;

                    bindPopUpInfo(name, floors, address);
                }

                if (existing == true) {
                    noReplicate = true
                }

            });

            if (noReplicate == false) {
                console.log("No similar object found. Creating New");
                var floorplan = [];
                var floorObjs = {};

                for (var i = 1; i <= floors; i++) {
                    floorObjs["floor" + i] = "";
                }

                // floorObjs["floor1"] = "";
                floorplan.push(floorObjs);

                //prepare the properties of the object
                markerDetails["id"] = markerId;
                markerDetails["address"] = address;
                markerDetails["buildingPlan"] = "";
                markerDetails["coordinates"] = coordinates;
                markerDetails["floorplan"] = floorplan;
                markerDetails["floors"] = floors;
                markerDetails["locationName"] = name;

                bindPopUpInfo(name, floors, address);

                objectJSON.metaData.push(markerDetails);
            }
        }


        //append form details to marker popup
        function bindPopUpInfo(name, floors, address) {
            var popupContent = '<h4>' + name + '</h4>' +
                '<ul>' +
                '<li>No of Floors: ' + floors + ' </li>' +
                '<li> Address: ' + address + '</li>' +
                '</ul>';

            var popup = L.popup({
                autoPan: true,
                keepInView: true,
                className: markerId
            })
                .setContent(popupContent);


            var markerObj = markers[markerId];
            markerObj.unbindPopup();
            markerObj.bindPopup(popup);

            console.log(popup.className);


            console.log(objectJSON);

            var KEY_NAME = 'yesin';
            var result = JSON.stringify(objectJSON);
            window.localStorage.setItem(KEY_NAME, result);

            alert('Saved');


        }

    );