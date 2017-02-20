function onSenseMeUpdate() {
    alert("got it");
}

$(document).ready(function(){
    $(".checkboxSenseme").on("click", function(){
        //inplemnet
    });

    var devicesAPI = "/api/device-mgt/v1.0/devices?type=senseme";
    $("#device-listing").html('<span id="device-listing-status-msg"></span>');
    var successCallback = function (data) {
        if (!data) {
            $('#device-listing-status-msg').text('No Device are available to be displayed.');
            return;
        }
        data = JSON.parse(data);
        if (data.devices.length == 0) {
            $('#device-listing-status-msg').text('No Device are available to be displayed.');
            return;
        }
        if (data.devices.length > 0) {
        // <label class="wr-input-control dropdown">
        //              <span class="helper" title="App Type">App Type<span
        // class="wr-help-tip glyphicon glyphicon-question-sign"></span></span>
        //       <select class="form-control col-sm-8 operationDataKeys appTypesInput" id="type"
        //     data-key="type">
        //              <option>Public</option>
        //              <option>Enterprise</option>
        //              </select>
        //              </label>
            var label = document.createElement('label');
            label.classList.add("wr-input-control");
            label.classList.add("col-sm-12");
            label.classList.add("dropdown");

            var select = document.createElement('select');
            select.classList.add("form-control");
            select.setAttribute("id", "type");
            select.setAttribute("onchange", "onSenseMeUpdate()");

            for (i = 0; i < data.devices.length; i++) {
                var device = data.devices[i];
                var option = document.createElement('option');
                txt = document.createTextNode(device.name+' - Floor1');
                option.innerText = txt.textContent;
                select.appendChild(option);
                // device.type = tempDevice.type;
                // device.name = tempDevice.name;
                // device.deviceIdentifier = tempDevice.deviceIdentifier;
                // var properties = {};
                // var enrolmentInfo = {};
                // properties.VENDOR = tempDevice.deviceInfo.vendor;
                // properties.DEVICE_MODEL = tempDevice.deviceInfo.deviceModel;
                // device.enrolmentInfo = tempDevice.enrolmentInfo;
                // device.properties = properties;
                // devices.push(device);
            }
            label.appendChild(select);
            $('#device-listing').append(label);
        } else {
            $('#device-listing-status-msg').text('No Device are available to be displayed.');
        }
    };

    invokerUtil.get(devicesAPI,
                     successCallback,
                     function (message) {
                         $('#device-listing-status-msg').text('Server is unable to perform the search please enroll at least one device or check the search query');
                     }
    );
});