<%--Popup modal for upgrading firmware--%>
<div class="modal fade" id="upgradeFirmware" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times
                </button>
                <h4 class="modal-title" style="color:cornflowerblue;">
                    Upgrade Firmware</h4>
            </div>
            <form id="send-operation-upgradeFirmware" method="post">
                <div class="form-group" style="padding-left: 10%; padding-right: 10%;">
                    <input type="text" name="firmwareUrl" id="firmwareUrl" value=""
                           placeholder="FirmWare URL"
                           class="form-control"/>
                </div>
            </form>

            <div class="modal-footer">
                <button type="button" class="btn btn-info btn-simple" data-dismiss="modal"
                        onclick="upgradeFirmware()">Send Operation
                </button>
            </div>
        </div>
    </div>
</div>

<%--Popup modal for upgrading the configurations--%>
<div class="modal fade" id="upgradeConfiguration" tabindex="-1" role="dialog"
     aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">&times
                </button>
                <h4 class="modal-title" style="color:cornflowerblue;">
                    Upload new configuration</h4>
            </div>

            <form id="send-operation-executionPlan" method="post">
                <div class="form-group" style="padding-left: 5%; padding-right: 5%;padding-bottom: 0;">
                        <textarea type="text" name="executionPlan" id="executionPlan" value=""
                                  placeholder="Execution Plan"
                                  class="form-control" cols="50" rows="10"
                                  style=" border: 2px solid rgba(0, 0, 0, 0.1); margin-top: 10px"></textarea>
                </div>
            </form>
            <div class="modal-footer">
                <button type="button" class="btn btn-info btn-simple" data-dismiss="modal"
                        onclick="uploadConfiguration() ">Send Operation
                </button>
            </div>
        </div>
    </div>
</div>