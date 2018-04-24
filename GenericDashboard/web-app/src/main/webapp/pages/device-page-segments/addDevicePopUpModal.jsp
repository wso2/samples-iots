<%--Popup modal for adding new device--%>
<div class="modal fade" id="newDeviceModal" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times
                </button>
                <h4 class="modal-title" id="myModalLabel" style="color:cornflowerblue;">
                    Enter
                    Weather station
                    Details</h4>
            </div>
            <form id="new-device-form" method="post">
                <div class="form-group" style="padding-left: 10%; padding-right: 10%;">
                    <input type="text" name="deviceId" id="deviceId" value=""
                           placeholder="Device ID"
                           class="form-control"/>
                </div>
                <div class="form-group" style="padding-left: 10%; padding-right: 10%;">
                    <input type="text" value="" placeholder="Device Name"
                           name="deviceName" id="deviceName"
                           class="form-control"/>
                </div>
                <div class="form-group" style="padding-left: 10%; padding-right: 10%;">
                    <input type="text" value="" placeholder="Device description"
                           name="deviceDesc" id="deviceDesc"
                           class="form-control"/>
                </div>
                <div id="inputMapId"></div>
            </form>
            <div class="modal-footer">
                <button type="button" class="btn btn-default btn-simple"
                        data-dismiss="modal">Close
                </button>
                <button type="button" class="btn btn-info btn-simple"
                        onclick="addNewDevice()">Add
                </button>
            </div>
        </div>
    </div>
</div>