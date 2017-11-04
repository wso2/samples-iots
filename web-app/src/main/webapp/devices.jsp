<%@include file="includes/authenticate.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Locker List</title>

    <link href="css/bootstrap.min.css" rel="stylesheet" />
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons" />
    <link href="css/material-dashboard.css" rel="stylesheet" />
    <link href="http://maxcdn.bootstrapcdn.com/font-awesome/latest/css/font-awesome.min.css" rel="stylesheet">
    <link href='http://fonts.googleapis.com/css?family=Roboto:400,700,300|Material+Icons' rel='stylesheet'
          type='text/css'>
</head>
<body>
<div class="wrapper">
    <div class="sidebar" data-color="purple" data-image="images/login_bg2.jpg">
        <!--
    Tip 1: You can change the color of the sidebar using: data-color="purple | blue | green | orange | red"

    Tip 2: you can also add an image using data-image tag
-->
        <div class="logo">
            <a href="#" class="simple-text">
                <strong>Smart</strong>Locker
            </a>
        </div>
        <div class="sidebar-wrapper">
            <ul class="nav">
                <li class="active">
                    <a href="devices.jsp">
                        <i class="material-icons">list</i>
                        <p style="font-weight: bold;">Device List</p>
                    </a>
                </li>
            </ul>
        </div>
    </div>
    <div class="main-panel">
        <%@include file="includes/nav-menu.jsp" %>
        <div class="content">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-md-12">
                        <div class="card card-plain">
                            <div class="card-header" data-background-color="purple">
                                <h4 class="title">Lockers enrolled</h4>
                                <p class="category">Below are the list of lockers enrolled with the server</p>
                                <button class="btn btn-white" data-toggle="modal" data-target="#newDeviceModal">Add
                                                                                                                Locker
                                </button>
                                <%--Popup modal for adding new device--%>
                                <div class="modal fade" id="newDeviceModal" tabindex="-1" role="dialog"
                                     aria-labelledby="myModalLabel" aria-hidden="true">
                                    <div class="modal-dialog">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <button type="button" class="close" data-dismiss="modal"
                                                        aria-hidden="true">&times;
                                                </button>
                                                <h4 class="modal-title" id="myModalLabel" style="color:purple;">Enter
                                                                                                                Locker
                                                                                                                Details</h4>
                                            </div>
                                            <%--<div class="modal-body" style="color:black;">--%>
                                            <%--Far far away, behind the word mountains, far from the countries Vokalia and Consonantia, there live the blind texts. Separated they live in Bookmarksgrove right at the coast of the Semantics, a large language ocean. A small river named Duden flows by their place and supplies it with the necessary regelialia. It is a paradisematic country, in which roasted parts of sentences fly into your mouth. Even the all-powerful Pointing has no control about the blind texts it is an almost unorthographic life One day however a small line of blind text by the name of Lorem Ipsum decided to leave for the far World of Grammar.--%>
                                            <%--</div>--%>
                                            <form id="new-device-form" method="post">
                                                <div class="form-group" style="padding-left: 10%; padding-right: 10%;">
                                                    <input type="text" name="deviceId" id="deviceId" value=""
                                                           placeholder="Device ID"
                                                           class="form-control" />
                                                </div>
                                                <div class="form-group" name="deviceName" id="deviceName"
                                                     style="padding-left: 10%; padding-right: 10%;">
                                                    <input type="text" value="" placeholder="Device Name"
                                                           class="form-control" />
                                                </div>
                                                <div class="form-group" name="deviceDesc" id="deviceDesc"
                                                     style="padding-left: 10%; padding-right: 10%;">
                                                    <input type="text" value="" placeholder="Device description"
                                                           class="form-control" />
                                                </div>
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
                                <div class="modal fade" id="lockCodeModal" tabindex="-1" role="dialog"
                                     aria-labelledby="lockCodeModalLabel" aria-hidden="true">
                                    <div class="modal-dialog">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <button type="button" class="close" data-dismiss="modal"
                                                        aria-hidden="true">&times;
                                                </button>
                                                <h4 class="modal-title" id="lockCodeModalLabel" style="color:purple;">
                                                    Generated LockCode
                                                    Details</h4>
                                            </div>
                                            <form id="new-lockcode-form">
                                                <div class="col-md-4 col-md-offset-4">
                                                    <input type="text" id="lock-code"
                                                           style="font-size: 2em;letter-spacing: 30px" value="0"
                                                           class="form-control">
                                                </div>
                                            </form>
                                            <div class="modal-footer">
                                                <button type="button" class="btn btn-default btn-simple"
                                                        data-dismiss="modal">Close
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <%--Popup modal for editing share settings--%>
                                <div class="modal fade" id="shareSettingsModal" tabindex="-1" role="dialog"
                                     aria-labelledby="myModalLabel" aria-hidden="true">
                                    <div class="modal-dialog">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <button type="button" class="close" data-dismiss="modal"
                                                        aria-hidden="true">&times;
                                                </button>
                                                <h4 class="modal-title" id="myModalLabel2" style="color:purple;">Edit
                                                                                                                 Share
                                                                                                                 Settings</h4>
                                            </div>
                                            <%--<div class="modal-body" style="color:black;">--%>
                                            <%--Far far away, behind the word mountains, far from the countries Vokalia and Consonantia, there live the blind texts. Separated they live in Bookmarksgrove right at the coast of the Semantics, a large language ocean. A small river named Duden flows by their place and supplies it with the necessary regelialia. It is a paradisematic country, in which roasted parts of sentences fly into your mouth. Even the all-powerful Pointing has no control about the blind texts it is an almost unorthographic life One day however a small line of blind text by the name of Lorem Ipsum decided to leave for the far World of Grammar.--%>
                                            <%--</div>--%>
                                            <form>
                                                <div class="form-group" style="padding-left: 10%; padding-right: 10%;">
                                                    <input type="text" value="" placeholder="User ID"
                                                           class="form-control" />
                                                </div>
                                                <%--<div class="form-group" style="padding-left: 10%; padding-right: 10%;">--%>
                                                <%--<input type="text" value="" placeholder="Device Name" class="form-control" />--%>
                                                <%--</div>--%>
                                                <%--<div class="form-group" style="padding-left: 10%; padding-right: 10%;">--%>
                                                <%--<input type="text" value="" placeholder="Device description" class="form-control" />--%>
                                                <%--</div>--%>
                                            </form>
                                            <div style="margin-right: 10%; margin-left: 10%">
                                                <table class="table" style="font-size: 15px">
                                                    <thead>
                                                    <tr>
                                                        <th class="text-center">#</th>
                                                        <th>Name</th>
                                                        <th class="text-left">Actions</th>
                                                    </tr>
                                                    </thead>
                                                    <tbody>
                                                    <tr>
                                                        <td class="text-center">1</td>
                                                        <td>Andrew Mike</td>
                                                        <td class="td-actions text-right">
                                                            <button type="button" rel="tooltip" title="View Profile"
                                                                    class="btn btn-info btn-simple btn-xs">
                                                                <i class="fa fa-user"></i>
                                                            </button>
                                                            <button type="button" rel="tooltip" title="Remove"
                                                                    class="btn btn-danger btn-simple btn-xs">
                                                                <i class="fa fa-times"></i>
                                                            </button>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="text-center">2</td>
                                                        <td>John Doe</td>
                                                        <td class="td-actions text-right">
                                                            <button type="button" rel="tooltip" title="View Profile"
                                                                    class="btn btn-info btn-simple btn-xs">
                                                                <i class="fa fa-user"></i>
                                                            </button>
                                                            <button type="button" rel="tooltip" title="Remove"
                                                                    class="btn btn-danger btn-simple btn-xs">
                                                                <i class="fa fa-times"></i>
                                                            </button>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="text-center">3</td>
                                                        <td>Alex Mike</td>
                                                        <td class="td-actions text-right">
                                                            <button type="button" rel="tooltip" title="View Profile"
                                                                    class="btn btn-info btn-simple btn-xs">
                                                                <i class="fa fa-user"></i>
                                                            </button>
                                                            <button type="button" rel="tooltip" title="Remove"
                                                                    class="btn btn-danger btn-simple btn-xs">
                                                                <i class="fa fa-times"></i>
                                                            </button>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="text-center">3</td>
                                                        <td>Alex Mike</td>
                                                        <td class="td-actions text-right">
                                                            <button type="button" rel="tooltip" title="View Profile"
                                                                    class="btn btn-info btn-simple btn-xs">
                                                                <i class="fa fa-user"></i>
                                                            </button>
                                                            <button type="button" rel="tooltip" title="Remove"
                                                                    class="btn btn-danger btn-simple btn-xs">
                                                                <i class="fa fa-times"></i>
                                                            </button>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="text-center">3</td>
                                                        <td>Alex Mike</td>
                                                        <td class="td-actions text-right">
                                                            <button type="button" rel="tooltip" title="View Profile"
                                                                    class="btn btn-info btn-simple btn-xs">
                                                                <i class="fa fa-user"></i>
                                                            </button>
                                                            <button type="button" rel="tooltip" title="Remove"
                                                                    class="btn btn-danger btn-simple btn-xs">
                                                                <i class="fa fa-times"></i>
                                                            </button>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="text-center">3</td>
                                                        <td>Alex Mike</td>
                                                        <td class="td-actions text-right">
                                                            <button type="button" rel="tooltip" title="View Profile"
                                                                    class="btn btn-info btn-simple btn-xs">
                                                                <i class="fa fa-user"></i>
                                                            </button>
                                                            <button type="button" rel="tooltip" title="Remove"
                                                                    class="btn btn-danger btn-simple btn-xs">
                                                                <i class="fa fa-times"></i>
                                                            </button>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="text-center">3</td>
                                                        <td>Alex Mike</td>
                                                        <td class="td-actions text-right">
                                                            <button type="button" rel="tooltip" title="View Profile"
                                                                    class="btn btn-info btn-simple btn-xs">
                                                                <i class="fa fa-user"></i>
                                                            </button>
                                                            <button type="button" rel="tooltip" title="Remove"
                                                                    class="btn btn-danger btn-simple btn-xs">
                                                                <i class="fa fa-times"></i>
                                                            </button>
                                                        </td>
                                                    </tr>
                                                    </tbody>
                                                </table>
                                            </div>
                                            <div class="modal-footer">
                                                <button type="button" class="btn btn-default btn-simple"
                                                        data-dismiss="modal">Close
                                                </button>
                                                <button type="button" class="btn btn-info btn-simple">Save</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="card-content table-responsive">
                                <table class="table table-hover" id="devices-listing">
                                    <thead>
                                    <th>Device Name</th>
                                    <th>Status</th>
                                    <th>Occupancy</th>
                                    <th>Owner</th>
                                    <th></th>
                                    <th></th>
                                    <th></th>
                                    </thead>
                                    <tbody>
                                    <tr>
                                        <td>Locker 1</td>
                                        <td>OPEN</td>
                                        <td>AVAILABLE</td>
                                        <td>
                                            <button class="btn btn-primary btn-fab btn-fab-mini btn-round">
                                                <i class="material-icons">refresh</i>
                                            </button>
                                        </td>
                                        <td>
                                            <button class="btn btn-info">Analytics</button>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>Locker 2</td>
                                        <td>CLOSED</td>
                                        <td>OCCUPIED</td>
                                        <td>
                                            <button class="btn btn-primary btn-fab btn-fab-mini btn-round">
                                                <i class="material-icons">refresh</i>
                                            </button>
                                        </td>
                                        <td>
                                            <button class="btn btn-info">Analytics</button>
                                        </td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <footer class="footer">
            <div class="container-fluid">
                <nav class="pull-left">
                    <ul>
                        <li>
                            <a href="#">
                                About
                            </a>
                        </li>
                        <li>
                            <a href="#">
                                Blog
                            </a>
                        </li>
                    </ul>
                </nav>
                <p class="copyright pull-right">
                    &copy;
                    <script>
                        document.write(new Date().getFullYear())
                    </script>
                    <a href="https://wso2.com/iot">WSO2 IoT Team</a>
                </p>
            </div>
        </footer>
    </div>
</div>
</div>
</body>
<script src="js/jquery.min.js" type="text/javascript"></script>
<script src="js/bootstrap.min.js" type="text/javascript"></script>
<script src="js/material.min.js" type="text/javascript"></script>
<script src="js/nouislider.min.js" type="text/javascript"></script>
<script src="js/bootstrap-datepicker.js" type="text/javascript"></script>
<script src="js/material-kit.js" type="text/javascript"></script>
<script src="js/bootstrap-notify.js" type="text/javascript"></script>
<script src="js/material-dashboard.js" type="text/javascript"></script>
<script type="text/javascript">
    $(document).ready(function () {
        // Javascript method's body can be found in assets/js/demos.js
        //demo.initDashboardPageCharts();
        getAllDevices();
    });

    function generateKey(deviceId) {
        var lockCode = ("" + Math.random()).substring(2, 6);
        var success = function (data) {
            $('#lock-code').val(lockCode);
            $('#lockCodeModal').modal('show');
        };
        var currentUser = "<%=session.getAttribute(LoginController.ATTR_USER_NAME)%>";
        var payload = "{'deviceIdentifiers':[" + deviceId
                      + "],'operation':{'code':'lock_code','type':'PROFILE','status':'PENDING','control':'REPEAT','payLoad':'"
                      + lockCode + "," + currentUser + "','enabled':true}}";
        $.ajax({
                   type: "POST",
                   url: "/invoker/execute",
                   data: {"uri": "/devices/locker/operations", "method": "post", "payload": payload},
                   success: success
               });
    }

    function getAllDevices() {
        var success = function (data) {
            var devices = JSON.parse(data).devices;
            var devicesListing = $('#devices-listing');
            if (devices) {
                devicesListing.find('tbody').empty();
                for (var i = 0; i < devices.length; i++) {
                    var lastKnownEP = {"uri": "/events/last-known/locker/" + devices[i].deviceIdentifier, "method": "get"};
                    var lastKnownSuccess = function (data) {
                        var record = JSON.parse(data).records[0];
                        if(!record){return;}
                        var time = new Date(record.timestamp);
                        var device;
                        for (var j = 0; j < devices.length; j++) {
                            if (record.values.meta_deviceId === devices[j].deviceIdentifier) {
                                device = devices[j];
                                break;
                            }
                        }
                        var isOpen = record.values.open;
                        var isOccupant = record.values.occupancy;
                        var myRow = "<tr><a href='#" + device.deviceIdentifier + "'><td>" + device.name + "</td><td>"
                                    + (isOpen ? "OPEN" : "CLOSED") + "</td><td>" + (isOccupant ? "OCCUPIED" :
                                                                                    "AVAILABLE") + "</td><td>"
                                    + device.enrolmentInfo.owner + "</td>" +
                                    "<td><button class=\"btn btn-primary btn-fab btn-fab-mini btn-round\" onclick='getAllDevices()'>"
                                    + "<i class=\"material-icons\">refresh</i>"
                                    + "</button>"
                                    + "<button class=\"btn btn-primary btn-fab btn-fab-mini btn-round\" onclick='generateKey(\""
                                    + device.deviceIdentifier + "\")'>"
                                    + "<i class=\"material-icons\">vpn_key</i>"
                                    + "</button>"
                                    + "<button class=\"btn btn-primary btn-fab btn-fab-mini btn-round\" onclick=\"window.location.href='/analytics.jsp'\">"
                                    + "<i class=\"material-icons\">insert_chart</i>"
                                    + "</button>"
                                    + "<button class=\"btn btn-primary btn-fab btn-fab-mini btn-round\" onclick=\"window.location.href='/realtime_analytics.jsp'\">"
                                    + "<i class=\"material-icons\">remove_red_eye</i>"
                                    + "</button></td>"
                                    + "</a></tr>";
                        devicesListing.find('tbody').append(myRow);
                    };
                    $.ajax({
                               type: "POST",
                               url: "/invoker/execute",
                               data: {"uri": "/events/last-known/locker/" + devices[i].deviceIdentifier, "method": "get"},
                               success: lastKnownSuccess
                           });
                }
            }
        };
        $.ajax({
                   type: "POST",
                   url: "/invoker/execute",
                   data: {"uri": "/devices/?type=locker&requireDeviceInfo=true", "method": "get"},
                   success: success
               });
    }

    function addNewDevice() {
        var deviceId = $("#deviceId").val();
        var deviceName = $("#deviceName").val();
        var deviceDesc = $("#deviceDesc").val();

        var success = function (data) {
            var config = {};
            config.type = type;
            config.deviceId = deviceId;
            $.ajax({
                       type: "GET",
                       url: "/devices/agent/locker/" + deviceId + "/config",
                       success: function (data, status, xhr) {
                           var dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(
                               JSON.stringify(data, null, 4));
                           var dlAnchorElem = document.getElementById('downloadAnchorElem');
                           dlAnchorElem.setAttribute("href", dataStr);
                           dlAnchorElem.setAttribute("download", deviceId + ".json");
                           dlAnchorElem.click();
//                           $("#modalDevice").modal('show');
                       },
                       error: function (xhr, status, error) {
//                           $(errorMsg).text("Device Created, But failed to download the agent configuration.");
//                           $(errorMsgWrapper).removeClass("hidden");
                       }
                   });
        };

        var payload = "{\n"
                      + "\"name\": deviceName,\n"
                      + "\"deviceIdentifier\": deviceId,\n"
                      + "\"description\": deviceDesc,\n"
                      + "\"type\": \"locker\",\n"
                      + "\"enrolmentInfo\": {\"status\": \"ACTIVE\", \"ownership\": \"BYOD\"},\n"
                      + "\"properties\": []\n"
                      + "}";
        $.ajax({
                   type: "POST",
                   url: "/invoker/execute",
                   data: {"uri": "/device/agent/enroll", "method": "post", "payload": payload},
                   success: success
               });
    }
</script>

</html>
