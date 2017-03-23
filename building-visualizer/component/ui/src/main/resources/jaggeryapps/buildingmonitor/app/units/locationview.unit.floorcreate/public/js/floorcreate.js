'use strict';

( function () {
    var fileUploadDOMElement;
    function showPreview(input) {
        $("#error-div-" + input.id).addClass("hide");
        $('#floor-plan-preview-' + input.id).addClass("hidden");
        if (validateFileUpload(input)) {
            var reader = new FileReader();
            reader.onload = function (e) {
                $('#floor-plan-preview-' + input.id).attr('src', e.target.result).removeClass("hidden");
            };
            reader.readAsDataURL(input.files[0]);
        }
    }

    /**
     * Validating the floor plan upload, before displaying the preview or sending it to back-end
     */
    function validateFileUpload(input) {
        if (fileUploadDOMElement.files && fileUploadDOMElement.files[0]) {
            var fileType = fileUploadDOMElement.files[0]["type"];
            var ValidImageTypes = ["image/gif", "image/jpeg", "image/png"];
            if ($.inArray(fileType, ValidImageTypes) >= 0) {
                return true;
            }
        }
        $("#error-div-" + input.id).removeClass("hide");
        $("#error-message-" + input.id).html("Please upload the image");
    }

    $(".files").change(function () {
        fileUploadDOMElement = this;
        showPreview(this);
    });

    $(".file-upload").on('click', function () {
		console.log(this.id);
        if (validateFileUpload(this)) {
            $('#' + this.id).submit();
        }
    });

}
());
