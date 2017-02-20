function onSenseMeUpdate() {
    var selectedSenseMeId = $("#senseme-listener").find(":selected").val();
    var selectedSenseMeName = $("#senseme-listener").find(":selected").text();
    var alertMeId = $(".device-id[data-deviceid]").data("deviceid");
    if (selectedSenseMeId == "null") {
        return;
    }
    var devicesAPI = "/alertme/device/{deviceId}/getalerts?senseMeId=" + selectedSenseMeId;
    invokerUtil.post(devicesAPI.replace("{deviceId}", alertMeId), {},
                     function (data) {
                         showSuccessAlert(
                             "Your AlertMe device is now getting alerts from SenseMe device: '" + selectedSenseMeName
                             + "'.");
                     },
                     function (message) {
                         showErrorAlert(
                             "Server is unable to perform the search please enroll at least one device or check the search query.");
                     }
    );
}

$(document).ready(function () {
    var devicesAPI = "/api/device-mgt/v1.0/devices?type=senseme";

    var successCallback = function (data) {
        $("#device-listing").html();
        if (!data) {
            showErrorAlert("No Device are available to be displayed.");
            return;
        }
        data = JSON.parse(data);
        if (data.devices.length == 0) {
            showErrorAlert("No Device are available to be displayed.");
            return;
        }
        if (data.devices.length > 0) {
            var label = document.createElement('label');
            label.classList.add("wr-input-control");
            label.classList.add("col-sm-12");
            label.classList.add("dropdown");

            var select = document.createElement('select');
            select.classList.add("form-control");
            select.setAttribute("id", "senseme-listener");
            select.setAttribute("onchange", "onSenseMeUpdate()");

            var option = document.createElement('option');
            var txt = document.createTextNode('--Change--');
            option.innerText = txt.textContent;
            option.setAttribute("value", "null");
            select.appendChild(option);

            for (i = 0; i < data.devices.length; i++) {
                var device = data.devices[i];
                var option = document.createElement('option');
                txt = document.createTextNode(device.name + ' - Floor1');
                option.innerText = txt.textContent;
                option.setAttribute("value", device.deviceIdentifier);
                select.appendChild(option);
                // properties.VENDOR = tempDevice.deviceInfo.vendor;
                // device.enrolmentInfo = tempDevice.enrolmentInfo;
            }

            label.appendChild(select);
            $('#device-listing').append(label);
        } else {
            showErrorAlert("No Device are available to be displayed.");
        }
    };

    invokerUtil.get(devicesAPI,
                    successCallback,
                    function (message) {
                        showErrorAlert(
                            "Server is unable to perform the search please enroll at least one device or check the search query.");
                    }
    );
});

function showErrorAlert(message) {
    var errorMsgWrapper = "#device-listing-status-msg";
    var errorMsg = "#device-listing-status-msg span";
    $(errorMsg).text(message);
    $(errorMsgWrapper).removeClass("hidden");
    $(errorMsgWrapper).removeClass("alert-danger");
    $(errorMsgWrapper).removeClass("alert-success");
    $(errorMsgWrapper).addClass("alert-danger");
}

function showSuccessAlert(message) {
    var errorMsgWrapper = "#device-listing-status-msg";
    var errorMsg = "#device-listing-status-msg span";
    $(errorMsgWrapper).removeClass("alert-danger");
    $(errorMsgWrapper).removeClass("alert-success");
    $(errorMsgWrapper).addClass("alert-success");
    $(errorMsg).text(message);
    $(errorMsgWrapper).removeClass("hidden");
}