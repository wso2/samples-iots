<%@include file="includes/authenticate.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Analytics2</title>

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
                <li>
                    <a href="./devices.jsp">
                        <i class="material-icons">list</i>
                        <p style="font-weight: bold;">Device List</p>
                    </a>
                </li>
                <li class="active">
                    <a href="#">
                        <i class="material-icons">timeline</i>
                        <p>Analytics</p>
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
                    <div class="col-md-2">
                        <div class="card card-profile">
                            <div class="card-avatar">
                                <a href="#">
                                    <img class="img" src="images/padlock.png" />
                                </a>
                            </div>
                            <div class="content">
                                <h6 class="category text-gray">ID: LK1234</h6>
                                <h4 class="card-title">LOCKER 1</h4>
                                <p class="card-content">
                                    Owned by John. Installed on July 27th 2017. Device is updated with latest Software
                                </p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-10">
                        <div class="card card-nav-tabs">
                            <div class="card-header" data-background-color="purple">
                                <div class="nav-tabs-navigation">
                                    <div class="nav-tabs-wrapper">
                                        <span class="nav-tabs-title">Analytics: </span>
                                        <ul class="nav nav-tabs" data-tabs="tabs">
                                            <li class="active" id="realtimeTab">
                                                <a href="#realtime" data-toggle="tab">
                                                    <i class="material-icons">access_alarms</i> Realtime
                                                    <div class="ripple-container"></div>
                                                </a>
                                            </li>
                                            <li class="" id="historicalTab">
                                                <a href="#historical" data-toggle="tab">
                                                    <i class="material-icons">history</i> Historical
                                                    <div class="ripple-container"></div>
                                                </a>
                                            </li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                            <div class="card-content">
                                <div class="tab-content">
                                    <div class="tab-pane active" id="realtime">
                                        <div style="margin-right: 10%; margin-left: 10%; margin-bottom: 5%;">
                                            <h3>Quicklook Locker stats</h3>
                                            <div class="row">
                                                <div class="col-lg-4 col-md-6 col-sm-6">
                                                    <div class="card card-stats">
                                                        <div id="lock_status_color" class="card-header"
                                                             data-background-color="red">
                                                            <i class="material-icons">lock_open</i>
                                                        </div>
                                                        <div class="card-content">
                                                            <p class="category">Locker state</p>
                                                            <h3 class="title" id="lock_status">Open</h3>
                                                        </div>
                                                        <div class="card-footer">
                                                            <div class="stats" id="lock_status_alert">
                                                                <i class="material-icons text-danger">warning</i>
                                                                <a href="#">Should be closed after use</a>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="col-lg-4 col-md-6 col-sm-6">
                                                    <div class="card card-stats">
                                                        <div class="card-header" data-background-color="blue">
                                                            <i class="material-icons">person</i>
                                                        </div>
                                                        <div class="card-content">
                                                            <p class="category">Occupied</p>
                                                            <h3 class="title" id="occupied_status">No</h3>
                                                        </div>
                                                        <div class="card-footer">
                                                            <div class="stats" id="occupied_alert">
                                                                <i class="material-icons">date_range</i> Since 3 hrs
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="col-lg-4 col-md-6 col-sm-6">
                                                    <div class="card card-stats">
                                                        <div class="card-header" data-background-color="blue">
                                                            <i class="material-icons">memory</i>
                                                        </div>
                                                        <div class="card-content">
                                                            <p class="category">Metal Presence</p>
                                                            <h3 class="title" id="metal_status">Present</h3>
                                                        </div>
                                                        <div class="card-footer">
                                                            <div class="stats" id="metal_status_alert">
                                                                <i class="material-icons">warning</i>Be cautious
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <h3>Device Activity Log</h3>
                                            <table class="table" style="font-size: 15px">
                                                <thead>
                                                <tr>
                                                    <th>Time</th>
                                                    <th>Message</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <tr style="background-color: #faffd7">
                                                    <td>2017-11-01 15:00:32 IST</td>
                                                    <td>Locker is open for more than two minutes.</td>
                                                </tr>
                                                <tr>
                                                    <td>2017-11-01 14:58:32 IST</td>
                                                    <td>Unlocked the locker by user admin.</td>
                                                </tr>
                                                <tr style="background-color: #faffd7">
                                                    <td>2017-11-01 14:56:32 IST</td>
                                                    <td>Unlocking attempt denied due to incorrect code.</td>
                                                </tr>
                                                <tr style="background-color: #faffd7">
                                                    <td>2017-11-01 14:56:15 IST</td>
                                                    <td>Unlocking attempt denied due to incorrect code.</td>
                                                </tr>
                                                <tr>
                                                    <td>2017-11-01 14:58:32 IST</td>
                                                    <td>Temperature back to normal.</td>
                                                </tr>
                                                </tbody>
                                            </table>
                                        </div>
                                        <div class="card">
                                            <div class="card-header card-chart" data-background-color="red"
                                                 style="height: 30%;">
                                                <div class="ct-chart" id="realtimeState"></div>
                                            </div>
                                            <div class="card-content">
                                                <h4 class="title">OPEN/CLOSE State</h4>
                                                <p>1 for open, 0 for close</p>
                                                <%--<p class="category">--%>
                                                <%--<span class="text-success"><i class="fa fa-long-arrow-up"></i> 5% </span> increase in Temperature.</p>--%>
                                            </div>
                                            <div class="card-footer">
                                                <div class="stats">
                                                    <i class="material-icons">access_time</i> updated 4 minutes ago
                                                </div>
                                            </div>
                                        </div>
                                        <div class="card">
                                            <div class="card-header card-chart" data-background-color="blue"
                                                 style="height: 30%;">
                                                <div class="ct-chart" id="realtimeTemp"></div>
                                            </div>
                                            <div class="card-content">
                                                <h4 class="title">Temperature</h4>
                                                <%--<p class="category">--%>
                                                <%--<span class="text-success"><i class="fa fa-bolt"></i> 10hr </span> active time.</p>--%>
                                            </div>
                                            <div class="card-footer">
                                                <div class="stats">
                                                    <i class="material-icons">access_time</i> updated 4 minutes ago
                                                </div>
                                            </div>
                                        </div>
                                        <div class="card">
                                            <div class="card-header card-chart" data-background-color="blue"
                                                 style="height: 30%;">
                                                <div class="ct-chart" id="realtimeHumid"></div>
                                            </div>
                                            <div class="card-content">
                                                <h4 class="title">Humidity</h4>
                                                <%--<p class="category">--%>
                                                <%--<span class="text-success"><i class="fa fa-bolt"></i> 10hr </span> active time.</p>--%>
                                            </div>
                                            <div class="card-footer">
                                                <div class="stats">
                                                    <i class="material-icons">access_time</i> updated 7 minutes ago
                                                </div>
                                            </div>
                                        </div>
                                        <div class="card">
                                            <div class="card-header card-chart" data-background-color="blue"
                                                 style="height: 30%;">
                                                <div class="ct-chart" id="realtimeOccupancy"></div>
                                            </div>
                                            <div class="card-content">
                                                <h4 class="title">Occupancy</h4>
                                                <p>1 for occupied, 0 for free</p>
                                                <%--<p class="category">--%>
                                                <%--<span class="text-success"><i class="fa fa-long-arrow-up"></i> 5% </span> increase in Temperature.</p>--%>
                                            </div>
                                            <div class="card-footer">
                                                <div class="stats">
                                                    <i class="material-icons">access_time</i> updated 9 minutes ago
                                                </div>
                                            </div>
                                        </div>
                                        <div class="card">
                                            <div class="card-header card-chart" data-background-color="blue"
                                                 style="height: 30%;">
                                                <div class="ct-chart" id="realtimeMetal"></div>
                                            </div>
                                            <div class="card-content">
                                                <h4 class="title">Metal Presence</h4>
                                                <p>1 for present, 0 for absent</p>
                                                <%--<p class="category">--%>
                                                <%--<span class="text-success"><i class="fa fa-long-arrow-up"></i> 5% </span> increase in Temperature.</p>--%>
                                            </div>
                                            <div class="card-footer">
                                                <div class="stats">
                                                    <i class="material-icons">access_time</i> updated 5 minutes ago
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="tab-pane" id="historical">
                                        <div style="margin-right: 10%; margin-left: 10%; margin-bottom: 5%;">
                                            <%--<button class="btn btn-white" id="datepicker" onclick="datepicker()">Change date</button>--%>
                                            <input class="datepicker form-control" type="text" value="03/12/2016" />
                                            <h3>Activity Log</h3>
                                            <table class="table" style="font-size: 15px">
                                                <thead>
                                                <tr>
                                                    <th>Time</th>
                                                    <th>Message</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <tr style="background-color: #faffd7">
                                                    <td>2017-11-01 15:00:32 IST</td>
                                                    <td>Locker is open for more than two minutes.</td>
                                                </tr>
                                                <tr>
                                                    <td>2017-11-01 14:58:32 IST</td>
                                                    <td>Unlocked the locker by user admin.</td>
                                                </tr>
                                                <tr style="background-color: #faffd7">
                                                    <td>2017-11-01 14:56:32 IST</td>
                                                    <td>Unlocking attempt denied due to incorrect code.</td>
                                                </tr>
                                                <tr style="background-color: #faffd7">
                                                    <td>2017-11-01 14:56:15 IST</td>
                                                    <td>Unlocking attempt denied due to incorrect code.</td>
                                                </tr>
                                                <tr>
                                                    <td>2017-11-01 14:58:32 IST</td>
                                                    <td>Temperature back to normal.</td>
                                                </tr>
                                                </tbody>
                                            </table>
                                        </div>
                                        <div class="card">
                                            <div class="card-header card-chart" data-background-color="red"
                                                 style="height: 30%;">
                                                <div class="ct-chart" id="historicalState"></div>
                                            </div>
                                            <div class="card-content">
                                                <h4 class="title">OPEN/CLOSE State</h4>
                                                <p>1 for open, 0 for close</p>
                                                <p class="category">
                                                    <span class="text-success"><i class="fa fa-bolt"></i> 1.5hr </span>
                                                    active time.</p>
                                            </div>
                                            <div class="card-footer">
                                                <div class="stats">
                                                    <i class="material-icons">access_time</i> updated 4 minutes ago
                                                </div>
                                            </div>
                                        </div>
                                        <div class="card">
                                            <div class="card-header card-chart" data-background-color="purple"
                                                 style="height: 30%;">
                                                <div class="ct-chart" id="historicalTemp"></div>
                                            </div>
                                            <div class="card-content">
                                                <h4 class="title">Temperature</h4>
                                                <p class="category">
                                                    <span class="text-success"><i class="fa fa-long-arrow-down"></i> 10% </span>
                                                    decrease in Temperature.</p>
                                            </div>
                                            <div class="card-footer">
                                                <div class="stats">
                                                    <i class="material-icons">access_time</i> updated 4 minutes ago
                                                </div>
                                            </div>
                                        </div>
                                        <div class="card">
                                            <div class="card-header card-chart" data-background-color="purple"
                                                 style="height: 30%;">
                                                <div class="ct-chart" id="historicalHumid"></div>
                                            </div>
                                            <div class="card-content">
                                                <h4 class="title">Humidity</h4>
                                                <p class="category">
                                                    <span class="text-success"><i
                                                            class="fa fa-long-arrow-up"></i> 5% </span> increase in
                                                                                                        Temperature.</p>
                                            </div>
                                            <div class="card-footer">
                                                <div class="stats">
                                                    <i class="material-icons">access_time</i> updated 7 minutes ago
                                                </div>
                                            </div>
                                        </div>
                                        <div class="card">
                                            <div class="card-header card-chart" data-background-color="purple"
                                                 style="height: 30%;">
                                                <div class="ct-chart" id="historicalOccupancy"></div>
                                            </div>
                                            <div class="card-content">
                                                <h4 class="title">Occupancy</h4>
                                                <p>1 for occupied, 0 for free</p>
                                                <p class="category">
                                                    <span class="text-success"><i class="fa fa-group"></i> 4 </span>
                                                    people occupied locker.</p>
                                            </div>
                                            <div class="card-footer">
                                                <div class="stats">
                                                    <i class="material-icons">access_time</i> updated 9 minutes ago
                                                </div>
                                            </div>
                                        </div>
                                        <div class="card">
                                            <div class="card-header card-chart" data-background-color="purple"
                                                 style="height: 30%;">
                                                <div class="ct-chart" id="historicalMetal"></div>
                                            </div>
                                            <div class="card-content">
                                                <h4 class="title">Metal Presence</h4>
                                                <p>1 for present, 0 for absent</p>
                                                <p class="category">
                                                    <span class="text-success"><i class="fa fa-cubes"></i> 10 </span>
                                                    metal presence detected</p>
                                            </div>
                                            <div class="card-footer">
                                                <div class="stats">
                                                    <i class="material-icons">access_time</i> updated 5 minutes ago
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
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
                    <a href="http://www.wso2.com">WSO2</a> IoT Team
                </p>
            </div>
        </footer>
    </div>
</div>
</body>
<script src="js/jquery-3.2.1.min.js" type="text/javascript"></script>
<script src="js/bootstrap.min.js" type="text/javascript"></script>
<script src="js/material.min.js" type="text/javascript"></script>
<!--  Charts Plugin -->
<script src="js/chartist.min.js"></script>
<!--  Dynamic Elements plugin -->
<script src="js/arrive.min.js"></script>
<!--  PerfectScrollbar Library -->
<script src="js/perfect-scrollbar.jquery.min.js"></script>
<!--  Notifications Plugin    -->
<script src="js/bootstrap-notify.js"></script>
<!--  Google Maps Plugin    -->
<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=YOUR_KEY_HERE"></script>
<!-- Material Dashboard javascript methods -->
<script src="js/material-dashboard.js?v=1.2.0"></script>
<!-- Material Dashboard DEMO methods, don't include it in your project! -->
<script src="js/demo.js"></script>
<script type="text/javascript">
    var getUrlParameter = function getUrlParameter(sParam) {
        var sPageURL = decodeURIComponent(window.location.search.substring(1)),
            sURLVariables = sPageURL.split('&'),
            sParameterName,
            i;

        for (i = 0; i < sURLVariables.length; i++) {
            sParameterName = sURLVariables[i].split('=');

            if (sParameterName[0] === sParam) {
                return sParameterName[1] === undefined ? true : sParameterName[1];
            }
        }
    };

    function timeDifference(current, previous) {

        var msPerMinute = 60 * 1000;
        var msPerHour = msPerMinute * 60;
        var msPerDay = msPerHour * 24;
        var msPerMonth = msPerDay * 30;
        var msPerYear = msPerDay * 365;

        var elapsed = current - previous;

        if (elapsed < msPerMinute) {
            return Math.round(elapsed/1000) + ' seconds ago';
        }

        else if (elapsed < msPerHour) {
            return Math.round(elapsed/msPerMinute) + ' minutes ago';
        }

        else if (elapsed < msPerDay ) {
            return Math.round(elapsed/msPerHour ) + ' hours ago';
        }

        else if (elapsed < msPerMonth) {
            return 'approximately ' + Math.round(elapsed/msPerDay) + ' days ago';
        }

        else if (elapsed < msPerYear) {
            return 'approximately ' + Math.round(elapsed/msPerMonth) + ' months ago';
        }

        else {
            return 'approximately ' + Math.round(elapsed/msPerYear ) + ' years ago';
        }
    }

    $(document).ready(function () {
        // Javascript method's body can be found in assets/js/demos.js
        demo.initDashboardPageCharts();
    });
    document.getElementById("realtimeTab").addEventListener("click", graphRefresh);
    document.getElementById("historicalTab").addEventListener("click", graphRefresh);

    function graphRefresh() {
        demo.initDashboardPageCharts();
    }

    function getDevice() {

    }

    var lastKnownSuccess = function (data) {
        var record = JSON.parse(data).records[0];
        var isOpen = "N/A";
        var isOccupant = "N/A";
        var sinceText = "N/A";
        var isMetalPresent = "N/A";
        if (record) {
            sinceText = timeDifference(new Date(), new Date(record.timestamp));
            isOpen = record.values.open;
            isOccupant = record.values.occupancy;
            isMetalPresent = record.values.metal;
        }

        var lockStatusColor = $("#lock_status_color");
        lockStatusColor.attr("data-background-color", (isOpen) ? "red" : "green");
        lockStatusColor.find("i").html((isOpen) ? "lock_open" : "lock");
        $("#lock_status").html((isOpen) ? "Open" : "Closed");

        var lockerStatusAlert = "";
        if(isOpen && !isOccupant){
            lockerStatusAlert = "<i class=\"material-icons text-danger\">warning</i>Should be closed after use";
        } else if(isOpen && isOccupant){
            lockerStatusAlert = "<i class=\"material-icons text-danger\">warning</i>Locker is not locked";
        } else if(!isOpen && !isOccupant){
            lockerStatusAlert = "<i class=\"material-icons text-danger\">warning</i>Locker is empty";
        }
        $("#lock_status_alert").html(lockerStatusAlert);

        $("#occupied_status").html((isOccupant) ? "Yes" : "No");
        $("#occupied_alert").html("<i class=\"material-icons\">date_range</i> Since " + sinceText);

        $("#metal_status").html((isMetalPresent) ? "Present" : "No");
        $("#metal_status_alert").html((isMetalPresent) ? "<i class=\"material-icons\">warning</i>Be cautious" : "<i class=\"material-icons\">info</i>No metal found");

//            $("#lock_status_alert").
    };
    $.ajax({
               type: "POST",
               url: "/invoker/execute",
               data: {
                   "uri": "/events/last-known/locker/" + getUrlParameter('id'),
                   "method": "get"
               },
               success: lastKnownSuccess
           });
</script>
<script>
    $('.datepicker').datepicker({
                                    weekStart: 1
                                });
</script>
</html>
