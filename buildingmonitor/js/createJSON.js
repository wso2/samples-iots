/**
 * Created by yesin on 1/20/17.
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
            min:"Please enter a value greater than 0"
        }
    }
});

$('#home').on('click', '[data-toggle=update-data]', function(e){
    var KEY_NAME = "yesin";
    var objectJSON;
    var metaData;

    if($(e.target).closest('form').valid()) {

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
        $('#heading'+ markerId).find('.panel-title').text(name);

        //check whether a JSON is stored inside local storage
        if(window.localStorage.getItem(KEY_NAME) === null){
            objectJSON = {
                metaData : []
            };

            var floorplan = [];
            var floorObjs = {};

            for(var i = 1; i <= floors; i++){
                floorObjs["floor"+ i] = "";
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

        }else if(window.localStorage.getItem(KEY_NAME) !== null){
            objectJSON = JSON.parse(window.localStorage.getItem(KEY_NAME));
            createMarkerObj(markerId, name, address, floors);
        }

        //Add new marker details to existing JSON
        function createMarkerObj(markerId, name, address, floors){
            var existing  = false;
            var noReplicate = false;

            $.each(objectJSON.metaData, function(i, val){
                console.log("Adding to existing JSON");
                if(val.id == markerId){
                    existing = true;
                    //update properties of saved object
                    val.locationName = name;
                    val.address = address;
                    val.floors = floors;

                    bindPopUpInfo(name, floors, address);
                }

                if(existing == true){
                    noReplicate = true
                }

            });

            if(noReplicate == false){
                console.log("No similar object found. Creating New");
                var floorplan = [];
                var floorObjs = {};

                for(var i = 1; i <= floors; i++){
                    floorObjs["floor"+ i] = "";
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


        //obj array to be inside markerInfo obj array

        //After you've done fiddling with the object you can pass the object into the JSON array
        // markerInfo.metaData.push(markerDetails);

        //object is created to be pushed to result JSON
        // console.log(markerDetails);


        //get the JSON obj array
        //     $.get('result.json', function (objectJSON) {

        //append form details to marker popup
        function bindPopUpInfo(name, floors, address){
            var popupContent = '<h4>'+ name +'</h4>' +
                '<ul>' +
                '<li>No of Floors: '+ floors+' </li>' +
                '<li> Address: '+ address+'</li>' +
                '</ul>';

            var popup = L.popup({
                autoPan: true,
                keepInView: true,
                className: markerId})
                .setContent(popupContent);


            var markerObj =  markers[markerId];
            markerObj.unbindPopup();
            markerObj.bindPopup(popup);

            console.log(popup.className);

            // $(this).attr("id", markerId);
        }


                // markers[markerId]._popup._content = popupContent;
                // console.log( markers[markerId]._popup._content);


                //collapse panel
                // $('#'+$('.item').attr('id')+' .panel-default > .panel-heading').attr("aria-expanded", "false");
                // $('#'+$(this).attr('id')+' .panel-default > .panel-heading').attr("expanded", "false");
                // $('#'+$('.item').attr('id')+' .panel-default > .panel-heading').accordion({active: false}).click();

                console.log(objectJSON);

                var KEY_NAME = 'yesin';
                var result = JSON.stringify(objectJSON);
                window.localStorage.setItem(KEY_NAME, result);

                alert('Saved');

            // });

        // markers[markerId]._popup._content

        // //update popup
        // $.get('result.json', function (data) {
        //     $.each(data.metaData, function(i, n){
        //         if(n.id = markerId){
        //
        //             // baseMap.removeLayer(markers[$(this).attr('id')]);
        //             //
        //             // var idNo = $(this).attr('id');
        //             // // Remove the link
        //             // $(this).parent('div').remove();
        //             //
        //             // $('#home').find('#' + idNo).remove();
        //             // console.log(markers);
        //             //     markers.splice(idNo,1);
        //             //
        //
        //
        //             // addingMarker(null, n[i]);
        //         }
        //     });
        // });
    }

});