<%--Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.--%>

<%--WSO2 Inc. licenses this file to you under the Apache License,--%>
<%--Version 2.0 (the "License"); you may not use this file except--%>
<%--in compliance with the License.--%>
<%--You may obtain a copy of the License at--%>

<%--http://www.apache.org/licenses/LICENSE-2.0--%>

<%--Unless required by applicable law or agreed to in writing,--%>
<%--software distributed under the License is distributed on an--%>
<%--"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY--%>
<%--KIND, either express or implied. See the License for the--%>
<%--specific language governing permissions and limitations--%>
<%--under the License.--%>


<%@include file="includes/authenticate.jsp" %>
<html>
<head>
    <title>Device List</title>
    <link href="css/bootstrap.min.css" rel="stylesheet"/>
    <link href="css/material-icons.css" rel="stylesheet"/>
    <link href="css/material-dashboard.css" rel="stylesheet"/>
    <link href="css/font-awesome.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.2.0/dist/leaflet.css"
          integrity="sha512-M2wvCLH6DSRazYeZRIm1JnYyh22purTM+FDB5CsyxtQJYeKq83arPe5wgbNmcFXGqiSH2XR8dT/fJISVA1r/zQ=="
          crossorigin=""/>
    <link href="css/updates.css" rel="stylesheet"/>

</head>
<body>
<div class="wrapper">
    <div class="sidebar" data-color="blue" data-image="images/sidebar-1.jpg">
        <div class="logo">
            <a href="./devices.jsp" class="simple-text">
                <strong>Device</strong>Portal
            </a>
        </div>
        <div class="sidebar-wrapper">
            <%@ include file="pages/device-page-segments/serachBar.jsp" %>

            <p class="copyright" style="position: absolute;bottom:0;padding-left: 100px">
                &copy;
                <script>
                    document.write(new Date().getFullYear())
                </script>
                <a href="https://wso2.com/iot">WSO2 Inc.</a>
            </p>
        </div>
    </div>
    <div class="main-panel">

        <div class="content" style="margin-top:5px ; padding: 0 0">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-md-12">
                        <div class="card card-plain">
                            <div class="card-header" data-background-color="blue">
                                <%@ include file="pages/device-page-segments/navBar.jsp" %>
                                <table style="width:100%">
                                    <tr>
                                        <th>
                                            <h4 class="title" style="font-size: 30px; padding-left: 10px;">Devices
                                                enrolled</h4>
                                        </th>
                                        <th style="text-align: center">
                                            <button class="btn btn-white" data-toggle="modal"
                                                    data-target="#newDeviceModal">Add
                                                Device
                                            </button>
                                        </th>
                                    </tr>
                                </table>
                                <%@ include file="pages/device-page-segments/addDevicePopUpModal.jsp" %>
                            </div>
                            <div class="tab-content">
                                <div id="tableview" class="tab-pane fade in active" style>
                                    <%@ include file="pages/device-page-segments/tableTab.jsp" %>
                                </div>
                                <div id="mapView" class="tab-pane fade  ">
                                    <%@ include file="pages/device-page-segments/mapTab.jsp" %>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>

            </div>
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
<script src="js/chartist.min.js"></script>
<script type="text/javascript" src="js/libs/jquery.bootpag.js"></script>
<script src="js/moment.min.js" type="text/javascript"></script>
<script src="js/daterangepicker.js" type="text/javascript"></script>
<script src="https://unpkg.com/leaflet@1.2.0/dist/leaflet.js"
        integrity="sha512-lInM/apFSqyy1o6s89K4iQUKg6ppXEgsVxT35HbzUupEVRh2Eu9Wdl4tHj7dZO0s1uvplcYGmt3498TtHq+log=="
        crossorigin=""></script>
<script type="text/javascript">

    var deviceType="weatherstation";

    var typepParameter1="tempf";
    var displayName1="Temperature";
    var units1="&#8451";

    var typeParameter2="humidity";
    var displayName2="Humidity";
    var units2="%";

    var typeParameter3="winddir";
    var displayName3="Wind Direction";
    var units3="&#176";


    document.getElementById("prameter1").innerHTML = displayName1;
    document.getElementById("prameter2").innerHTML = displayName2;
    document.getElementById("prameter3").innerHTML = displayName3;

    //fixed the issue with map not rendering in tabbed view and pop up model
    $("a[href='#mapView']").on('shown.bs.tab', function (e) {
        mymap.invalidateSize();
        //hide the search bar on map view
        $('#hide').hide();
    });

    $('#newDeviceModal').on('show.bs.modal', function () {
        setTimeout(function () {
            map.invalidateSize();
        }, 200);
    });
    $("a[href='#tableview']").on('shown.bs.tab', function (e) {
        //show the search bar on table view
        $('#hide').show();
    });


    $(document).ready(function () {
        getDevices(0, 10);//load first page
        getAllDevices();//add all devices to map
    });


</script>
<script src="pages/device-page-scripts/mapViewJs.js" type="text/javascript"></script>
<script src="pages/device-page-scripts/tableCharts.js" type="text/javascript"></script>
<script src="pages/device-page-scripts/modalMap.js" type="text/javascript"></script>
<script src="pages/device-page-scripts/functions.js" type="text/javascript"></script>
</html>
