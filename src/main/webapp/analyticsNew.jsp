<%@include file="includes/authenticate.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Analytics2</title>

    <link href="css/bootstrap.min.css" rel="stylesheet"/>
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons" />
    <link href="css/material-dashboard.css" rel="stylesheet" />
    <link href="http://maxcdn.bootstrapcdn.com/font-awesome/latest/css/font-awesome.min.css" rel="stylesheet">
    <link href='http://fonts.googleapis.com/css?family=Roboto:400,700,300|Material+Icons' rel='stylesheet' type='text/css'>
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
        <%@include file="includes/nav-menu.jsp"%>
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
                                            <span class="nav-tabs-title">Analytics</span>
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
                                            <div class="card">
                                                <div class="card-header card-chart" data-background-color="green" style="height: 30%;">
                                                    <div class="ct-chart" id="dailySalesChart"></div>
                                                </div>
                                                <div class="card-content">
                                                    <h4 class="title">ON/OFF State</h4>
                                                    <p class="category">
                                                        <span class="text-success"><i class="fa fa-bolt"></i> 10hr </span> active time.</p>
                                                </div>
                                                <div class="card-footer">
                                                    <div class="stats">
                                                        <i class="material-icons">access_time</i> updated 4 minutes ago
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="card">
                                                <div class="card-header card-chart" data-background-color="blue" style="height: 30%;">
                                                    <div class="ct-chart" id="emailsSubscriptionChart"></div>
                                                </div>
                                                <div class="card-content">
                                                    <h4 class="title">Temperature</h4>
                                                    <p class="category">
                                                        <span class="text-success"><i class="fa fa-long-arrow-up"></i> 5% </span> increase in Temperature.</p>
                                                </div>
                                                <div class="card-footer">
                                                    <div class="stats">
                                                        <i class="material-icons">access_time</i> updated 4 minutes ago
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="tab-pane" id="historical">
                                            <div class="card">
                                                <div class="card-header card-chart" data-background-color="red" style="height: 30%;">
                                                    <div class="ct-chart" id="completedTasksChart"></div>
                                                </div>
                                                <div class="card-content">
                                                    <h4 class="title">Humidity</h4>
                                                    <p class="category">
                                                        <span class="text-success"><i class="fa fa-long-arrow-down"></i> 20% </span> decrease in Humidity.</p>
                                                </div>
                                                <div class="card-footer">
                                                    <div class="stats">
                                                        <i class="material-icons">access_time</i> updated 10 minutes ago
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
                                Home
                            </a>
                        </li>
                        <li>
                            <a href="#">
                                Company
                            </a>
                        </li>
                        <li>
                            <a href="#">
                                Portfolio
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
                    <a href="http://www.creative-tim.com">Creative Tim</a>, made with love for a better web
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
        $(document).ready(function() {
            // Javascript method's body can be found in assets/js/demos.js
            demo.initDashboardPageCharts();
        });
        document.getElementById("realtimeTab").addEventListener("click", graphRefresh);
        document.getElementById("historicalTab").addEventListener("click", graphRefresh);
        function graphRefresh() {
            demo.initDashboardPageCharts();
        }
    </script>
</html>
