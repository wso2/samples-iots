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


<%@page import="org.apache.http.HttpResponse" %>
<%@page import="org.apache.http.client.methods.HttpPost" %>
<%@ page import="org.apache.http.conn.ssl.SSLConnectionSocketFactory" %>
<%@ page import="org.apache.http.conn.ssl.SSLContextBuilder" %>
<%@ page import="org.apache.http.conn.ssl.TrustSelfSignedStrategy" %>
<%@ page import="org.apache.http.entity.ContentType" %>
<%@ page import="org.apache.http.entity.StringEntity" %>
<%@ page import="org.apache.http.impl.client.CloseableHttpClient" %>
<%@ page import="org.apache.http.impl.client.HttpClients" %>
<%@ page import="org.json.JSONObject" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.net.URI" %>
<%@ page import="java.net.URISyntaxException" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.security.KeyManagementException" %>
<%@ page import="java.security.KeyStoreException" %>
<%@ page import="java.security.NoSuchAlgorithmException" %>
<%@ page import="java.util.Date" %>
<%@include file="includes/authenticate.jsp" %>
<%
    String id = request.getParameter("id");
    if (id == null) {
        //  response.sendRedirect("devices.jsp");
        return;
    }

    String cookie = request.getHeader("Cookie");

    URI invokerURI = null;
    try {
        invokerURI = new URL(request.getScheme(),
                request.getServerName(),
                request.getServerPort(), request.getContextPath() + "/invoker/execute").toURI();
    } catch (URISyntaxException e) {
        e.printStackTrace();
    }
    HttpPost invokerEndpoint = new HttpPost(invokerURI);
    invokerEndpoint.setHeader("Cookie", cookie);

    StringEntity entity = new StringEntity("uri=/devices/TractorHub/" + id + "&method=get",
            ContentType.APPLICATION_FORM_URLENCODED);
    invokerEndpoint.setEntity(entity);

    SSLContextBuilder builder = new SSLContextBuilder();
    try {
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
    } catch (NoSuchAlgorithmException | KeyStoreException e) {
        e.printStackTrace();
    }
    SSLConnectionSocketFactory sslsf = null;
    try {
        sslsf = new SSLConnectionSocketFactory(builder.build());
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
        e.printStackTrace();
    }

    CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(
            sslsf).build();
    HttpResponse invokerResponse = client.execute(invokerEndpoint);

    if (invokerResponse.getStatusLine().getStatusCode() == 401) {
        return;
    }

    BufferedReader rd = new BufferedReader(new InputStreamReader(invokerResponse.getEntity().getContent()));

    StringBuilder result = new StringBuilder();
    String line;
    while ((line = rd.readLine()) != null) {
        result.append(line);
    }

    JSONObject device = new JSONObject(result.toString());
    JSONObject enrolmentInfo = device.getJSONObject("enrolmentInfo");
    JSONObject lat = (JSONObject) device.getJSONArray("properties").get(0);
    JSONObject lon = (JSONObject) device.getJSONArray("properties").get(1);

%>

<!doctype html>
<html lang="en">

<head>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <title>Tractor Hub</title>
    <meta content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0' name='viewport'/>
    <meta name="viewport" content="width=device-width"/>
    <!-- Bootstrap core CSS     -->
    <link href="css/bootstrap.min.css" rel="stylesheet"/>
    <!--  Material Dashboard CSS    -->
    <link href="css/material-dashboard.css?v=1.2.0" rel="stylesheet"/>
    <!-- For the date range picker in hisorical tab     -->
    <link href="css/daterangepicker.css" rel="stylesheet"/>
    <!--     Fonts and icons     -->
    <link href="http://maxcdn.bootstrapcdn.com/font-awesome/latest/css/font-awesome.min.css" rel="stylesheet">
    <link href='http://fonts.googleapis.com/css?family=Roboto:400,700,300|Material+Icons' rel='stylesheet'
          type='text/css'>
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.2.0/dist/leaflet.css"
          integrity="sha512-M2wvCLH6DSRazYeZRIm1JnYyh22purTM+FDB5CsyxtQJYeKq83arPe5wgbNmcFXGqiSH2XR8dT/fJISVA1r/zQ=="
          crossorigin=""/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <link href="css/updates.css" rel="stylesheet"/>

    <link href="css/simple-sidebar.css" rel="stylesheet">
</head>

<body>
<div id="wrapper" class="toggled">
    <div id="sidebar-wrapper" class="sidebar" data-color="purple" data-image="images/sidebar-1.jpg">
        <!--
    Tip 1: You can change the color of the sidebar using: data-color="purple | blue | green | orange | red"

    Tip 2: you can also add an image using data-image tag
-->
        <div class="logo list-inline">
            <a href="./devices.jsp" class="simple-text">
                <strong>Farm</strong>Portal
            </a>
        </div>
        <div class="sidebar-wrapper">
            <ul class="nav">
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


            <div id="mapid" style="width: 100%; height:50%; margin-top: 50px"></div>

            <div class="card card-stats " style="margin-bottom: 10px">
                <div class="card-content">
                    <h3 class="title" id="devName">
                    </h3>
                    <p class="category" id="devDetails"></p>
                </div>
            </div>
            <p class="copyright" style="position: absolute;bottom:0;padding-left: 100px">
                &copy;
                <script>
                    document.write(new Date().getFullYear())
                </script>
                <a href="https://wso2.com/iot">WSO2 Inc.</a>
            </p>
        </div>


    </div>
    <div id="page-content-wrapper" class="main-panel" style="padding-top: 2px;">

        <nav class="navbar navbar-transparent"
             style="padding-top: 2px;padding-bottom: 2px;text-align: center;line-height: 50px; font-size: 30px;">
            <div class="container-fluid">
                <div class="collapse navbar-collapse">
                    <ul class="nav navbar-nav navbar-left">
                        <li>
                            <a href="#menu-toggle" id="menu-toggle" style="width: 100px"> <i id="icon"
                                                                                             class="fa fa-angle-double-left"
                                                                                             style="font-size:36px"></i></a>
                        </li>
                    </ul>
                    <strong><%=device.getString("name")%>
                    </strong> TractorHub Statistics
                    <ul class="nav navbar-nav navbar-right">
                        <li>
                            <button class="btn btn-white" data-toggle="modal"
                                    data-target="#newDeviceModal"> Send Operation
                            </button>
                        </li>
                        <li class="dropdown pull-right">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                <i class="material-icons">person</i>
                                <%--<span class="notification">5</span>--%>
                                <p class="hidden-lg hidden-md">Profile</p>
                            </a>
                            <ul class="dropdown-menu">
                                <li>
                                    <a href="logout.jsp">Logout</a>
                                </li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
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
                            Select Operation</h4>
                    </div>
                    <table style="text-align: center;">
                        <tr>
                            <td>
                                <button class="btn btn-white"> Plot Path
                                </button>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <button class="btn btn-white"> Send Execution Plan
                                </button>
                            </td>
                        </tr>

                        <tr>
                            <td>
                                <button class="btn btn-white"> Control Engine
                                </button>
                            </td>
                        </tr>
                    </table>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default btn-simple"
                                data-dismiss="modal">Close
                        </button>
                    </div>
                </div>
            </div>
        </div>
        <div class="content" style="padding-top: 2px;">
            <div id="daterangebar" style="margin-left:35%;margin-top: -4px">
                <div class="menubutton">
                    <h4 style="margin-top: -4px"><strong id="dateR" style=" font-size: 20px;">Date-range</strong></h4>
                </div>
                <div class="menubutton" style="width: 440px;margin-top: -4px;">
                    <h4><input type="text" name="dateRange" id="dateRange" style="padding-left: 15px; font-size: 20px;"
                               value="01/01/2018 1:30 PM - 01/01/2018 2:00 PM"
                               class="form-control"/></h4></div>

            </div>
            <div class="container-fluid">
                <div class="tab-content">
                    <div id="realtime" class="tab-pane fade in active">
                        <div class="row" id="statusCards">
                            <div class="col-lg-3 col-md-6 col-sm-6">
                                <div class="card card-stats">
                                    <div class="card-header" data-background-color="green">
                                        <i class="material-icons">invert_colors</i>
                                    </div>
                                    <div class="card-content">
                                        <p class="category">Raining</p>
                                        <h3 class="title" id="rain_alert">Yet to be updated</h3>
                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="rain_status_alert">
                                            <i class="material-icons">update</i> Just Updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-lg-3 col-md-6 col-sm-6">
                                <div class="card card-stats">
                                    <div class="card-header" data-background-color="green">
                                        <i class="material-icons">local_gas_station</i>
                                    </div>
                                    <div class="card-content">
                                        <p class="category">Tractor Fuel Statistics</p>
                                        <h3 class="title" id="fuel_status">Yet to be updated</h3>
                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="fuel_status_alert">
                                            <i class="material-icons">update</i> Just Updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-lg-3 col-md-6 col-sm-6">
                                <div class="card card-stats">
                                    <div class="card-header" data-background-color="red">
                                        <i class="material-icons">local_shipping</i>
                                    </div>
                                    <div class="card-content">
                                        <p class="category">Tractor Engine Status</p>
                                        <h3 class="title" id="engine_status">Yet to be updated</h3>
                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="engine_status_alert">
                                            <i class="material-icons">update</i> Just Updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-lg-3 col-md-6 col-sm-6">
                                <div class="card card-stats">
                                    <div class="card-header" data-background-color="blue">
                                        <i class="material-icons">computer</i>
                                    </div>
                                    <div class="card-content">
                                        <p class="category">Tractor Load</p>
                                        <h3 class="title" id="tractorload_status">Yet to be updated</h3>
                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="tractorload_status_alert">
                                            <i class="material-icons">update</i> Just Updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-4">
                                <div class="card real" id="temp" onclick=redirect(this)>
                                    <div class="card-header card-chart" data-background-color="red">
                                        <div class="ct-chart ct-golden-section setheight" id="RealTimeTempChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Temperature&#8451</h4>
                                        <p class="category">
                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="realtimeTempLastUpdated">
                                            <i class="material-icons">access_time</i> Yet to be updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card real" id='humid' onclick=redirect(this)>
                                    <div class="card-header card-chart " data-background-color="blue">
                                        <div class="ct-chart ct-golden-section setheight"
                                             id="RealTimeHumidityChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Humidity<b>%</b></h4>
                                        <p class="category">

                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="realtimeHumidLastUpdated">
                                            <i class="material-icons">access_time</i> Yet to be updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card real" id='EngineTemp' onclick=redirect(this)>
                                    <div class="card-header card-chart" data-background-color="purple">
                                        <div class="ct-chart ct-golden-section setheight"
                                             id="RealTimeEngineTempChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Engine Temperature&#8451</h4>
                                        <p class="category">

                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="realtimeengineTempLastUpdated">
                                            <i class="material-icons">access_time</i> Yet to be updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card real" id='soilmoisture' onclick=redirect(this)>
                                    <div class="card-header card-chart" data-background-color="green">
                                        <div class="ct-chart ct-golden-section setheight"
                                             id="RealTimesoilmoistureChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Soil Moisture<b>%</b></h4>
                                        <p class="category">

                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="realtimesoilmoistureLastUpdated">
                                            <i class="material-icons">access_time</i> Yet to be updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card real" id='tractorspeed' onclick=redirect(this)>
                                    <div class="card-header card-chart" data-background-color="orange">
                                        <div class="ct-chart ct-golden-section setheight"
                                             id="RealTimeTractorSpeedChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Tractor Speed <b>mph</b></h4>
                                        <p class="category">

                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="realtimetractorspeedmphLastUpdated">
                                            <i class="material-icons">access_time</i> Yet to be updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card real" id='illumination' onclick=redirect(this)>
                                    <div class="card-header card-chart" data-background-color="green">
                                        <div class="ct-chart ct-golden-section setheight"
                                             id="RealTimeIlluminationChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Illumination <b> Candela</b></h4>
                                        <p class="category">

                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="realtimeIlluminationLastUpdated">
                                            <i class="material-icons">access_time</i> Yet to be updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card real" id='fuelusage' onclick=redirect(this)>
                                    <div class="card-header card-chart" data-background-color="red">
                                        <div class="ct-chart ct-golden-section setheight"
                                             id="RealTimeFuelUsageChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Fuel Usage <b>%</b></h4>
                                        <p class="category">

                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="realtimeFuelUsageLastUpdated">
                                            <i class="material-icons">access_time</i> Yet to be updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card real" id='tractorLoadWeight' onclick=redirect(this)>
                                    <div class="card-header card-chart" data-background-color="purple">
                                        <div class="ct-chart ct-golden-section setheight"
                                             id="RealTimetractorLoadWeightChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Tractor Load Weight<b>%</b></h4>
                                        <p class="category">

                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="realtimeTractorLoadWeightlastUpdated">
                                            <i class="material-icons">access_time</i> Yet to be updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div id="historical" class="tab-pane fade">
                        <div class="row">
                            <div class="col-md-4">
                                <div class="card his setHistorical" id='Htemp' onclick=redirect(this)>
                                    <div class="card-header card-chart" data-background-color="red">
                                        <div class="ct-chart ct-golden-section setheight"
                                             id="HistoricalTempChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Temperature&#8451</h4>
                                        <p class="category" id="historicalTempAlert">
                                    </div>

                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card his setHistorical" id='Hhumid' onclick=redirect(this)>
                                    <div class="card-header card-chart" data-background-color="blue">
                                        <div class="ct-chart ct-golden-section setheight"
                                             id="HistoricalHumidityChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Humidity<b>%</b></h4>
                                        <p class="category" id="historicalHumidAlert">
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card his setHistorical" id='Htractorspeed' onclick=redirect(this)>
                                    <div class="card-header card-chart" data-background-color="orange">
                                        <div class="ct-chart ct-golden-section setheight"
                                             id="HistoricalTractorSpeedChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Tractor Speed <b>mph</b></h4>
                                        <p class="category" id="historicaltractorSpeedLastUpdated">

                                    </div>

                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card his setHistorical" id='HEngineTemp' onclick=redirect(this)>
                                    <div class="card-header card-chart" data-background-color="purple">
                                        <div class="ct-chart ct-golden-section setheight"
                                             id="HistoricalEngineTempChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Engine Temperature&#8451</h4>
                                        <p class="category" id="historicalengineTempLastUpdated">

                                    </div>

                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card his setHistorical" id='HsoilMoisture' onclick=redirect(this)>
                                    <div class="card-header card-chart" data-background-color="green">
                                        <div class="ct-chart ct-golden-section setheight"
                                             id="HistoricalSoilMoistureChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Soil Moisture<b>%</b></h4>
                                        <p class="category" id="historicalsoilMoistureLastUpdated">

                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card his setHistorical" id='Hillumination' onclick=redirect(this)>
                                    <div class="card-header card-chart" data-background-color="purple">
                                        <div class="ct-chart ct-golden-section setheight"
                                             id="HistoricalIlluminationChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Illumination<b> Candela</b></h4>
                                        <p class="category" id="historicalilluminationLastUpdated">

                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card his setHistorical" id='HfuelUsage' onclick=redirect(this)>
                                    <div class="card-header card-chart" data-background-color="blue">
                                        <div class="ct-chart ct-golden-section setheight"
                                             id="HistoricalFuelUsageChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Fuel Usage<b>%</b></h4>
                                        <p class="category" id="historicalFuelUsageLastUpdated">

                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card his setHistorical" id='HtractorLoad' onclick=redirect(this)>
                                    <div class="card-header card-chart" data-background-color="red">
                                        <div class="ct-chart ct-golden-section setheight"
                                             id="HistoricalLoadWeightChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Tractor Load<b>%</b></h4>
                                        <p class="category" id="historicalTractorLoadLastUpdated">
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
</body>
<!--   Core JS Files   -->
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
<!-- Material Dashboard javascript methods -->
<script src="js/material-dashboard.js?v=1.2.0"></script>
<script src="js/realtime-analytics.js"></script>
<script src="js/bootstrap-datepicker.js" type="text/javascript"></script>
<script src="js/moment.min.js" type="text/javascript"></script>
<script src="js/daterangepicker.js" type="text/javascript"></script>
<script src="js/historical-analytics.js"></script>
<script src="https://unpkg.com/leaflet@1.2.0/dist/leaflet.js"
        integrity="sha512-lInM/apFSqyy1o6s89K4iQUKg6ppXEgsVxT35HbzUupEVRh2Eu9Wdl4tHj7dZO0s1uvplcYGmt3498TtHq+log=="
        crossorigin=""></script>
<script type="text/javascript">
    //function to expand charts onclick
    $("#dateR").hide();
    $('#daterangebar').hide();

    function redirect(ele) {
        var act = $(".col-md-4").hasClass("resize");
        if (!act) {
            $(".his").toggleClass("setHistorical");
        }

        var act1 = $(".card").hasClass("temp");
        if (act1) {
            $(".real").toggleClass("resize");
        }
        $('#' + ele.id).toggleClass('modal');
        $('div.card-chart').toggleClass('maxHeight');
        $('div.card').toggleClass('padzero');
        $('.ct-chart').toggleClass('fillcontent');
        $('.ct-chart').toggleClass('setheight');

        analyticsHistory.updateGraphs();

    }

    //menu toggle script
    $("#menu-toggle").click(function (e) {
        e.preventDefault();
        // $(".ct-chart").toggleClass('ct-golden-section');
        $("#wrapper").toggleClass("toggled");
        $(".real").toggleClass("resize");
        $(".real").toggleClass("temp");
        toggleDiv("statusCards");
        $('#icon').toggleClass('fa fa-angle-double-left fa fa-angle-double-right');
        setTimeout(analyticsHistory.updateGraphs, 250);

    });

    $("#historicalTab").click(function () {
        $("#dateR").show();
        $("#daterangebar").show();
    });

    $("#realtimeTab").click(function () {
        $("#dateR").hide();
        $("#daterangebar").hide();
    });


    function toggleDiv(divId) {
        var x = document.getElementById(divId);
        if (x.style.display === "none") {
            x.style.display = "block";
        } else {
            x.style.display = "none";
        }
    }


    //intialising and setting the map
    var mymap = L.map('mapid').setView([7.9, 80.56274], 7);
    L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
        attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
        maxZoom: 18,
        id: 'mapbox.streets',
        accessToken: 'pk.eyJ1IjoibGFzaGFuIiwiYSI6ImNqYmc3dGVybTFlZ3UyeXF3cG8yNGxsdzMifQ.n3QEq0-g5tVFmsQxn3JZ-A',
        maxWidth: 200,
        maxHeight: 200
    }).addTo(mymap);

    var marker = L.marker([<%=lat.getString("value")%>, <%=lon.getString("value")%>]).addTo(mymap);
    marker.bindPopup("<b><%=device.getString("name")%></b>").openPopup();


    //set device details and send device details to dashboard.jsp
    document.getElementById("devName").innerHTML = "<%=device.getString("name")%>";
    document.getElementById("devDetails").innerHTML = "Owned by " + "<%=enrolmentInfo.getString("owner")%>" + " and enrolled on " + "<%=new Date(enrolmentInfo.getLong("dateOfEnrolment")).toString()%>";

</script>
<script type="text/javascript">
    var alerts = [];
    var lastKnown = {};

    //refresh graphs on click
    document.getElementById("realtimeTab").addEventListener("click", realtimeGraphRefresh());
    document.getElementById("historicalTab").addEventListener("click", historyGraphRefresh);

    function realtimeGraphRefresh(wsEndpoint) {
        realtimeAnalytics.initDashboardPageCharts(wsEndpoint);
    }

    function historyGraphRefresh() {
        analyticsHistory.initDashboardPageCharts();
    }

    //fix the issue of charts not rendering in historical tab
    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
        $(e.currentTarget.hash).find('.ct-chart').each(function (el, tab) {
            tab.__chartist__.update();
        });
    });

    $(document).ready(function () {
        $(document).ready(function () {
            var wsStatsEndpoint = "<%=pageContext.getServletContext().getInitParameter("websocketEndpoint")%>/secured-websocket/iot.per.device.stream.carbon.super.TractorHub/1.0.0?"
                + "deviceId=<%=id%>&deviceType=TractorHub&websocketToken=<%=request.getSession(false).getAttribute(LoginController.ATTR_ACCESS_TOKEN)%>";
            realtimeGraphRefresh(wsStatsEndpoint);

            var wsAlertEndpoint = "<%=pageContext.getServletContext().getInitParameter("websocketEndpoint")%>/secured-websocket/iot.per.device.stream.carbon.super.TractorHub.alert/1.0.0?"
                + "deviceId=<%=id%>&deviceType=TractorHub&websocketToken=<%=request.getSession(false).getAttribute(LoginController.ATTR_ACCESS_TOKEN)%>";
            displayAlerts(wsAlertEndpoint);
        });
    });

    function timeDifference(current, previous, isshort) {
        var msPerMinute = 60 * 1000;
        var msPerHour = msPerMinute * 60;
        var msPerDay = msPerHour * 24;
        var msPerMonth = msPerDay * 30;
        var msPerYear = msPerDay * 365;

        var elapsed = current - previous;

        if (elapsed < msPerMinute) {
            return Math.round(elapsed / 1000) + ' seconds ago';
        } else if (elapsed < msPerHour) {
            return Math.round(elapsed / msPerMinute) + ' minutes ago';
        } else if (elapsed < msPerDay) {
            return Math.round(elapsed / msPerHour) + ' hours ago';
        } else if (elapsed < msPerMonth) {
            return Math.round(elapsed / msPerDay) + ' days ago';
        } else if (elapsed < msPerYear) {
            return Math.round(elapsed / msPerMonth) + ' months ago';
        } else {
            return Math.round(elapsed / msPerYear) + ' years ago';
        }
    }


    function displayAlerts(wsEndpoint) {
        connect(wsEndpoint);
        var ws;
        // close websocket when page is about to be unloaded
        // fixes broken pipe issue
        window.onbeforeunload = function () {
            disconnect();
        };

        //websocket connection
        function connect(target) {
            if ('WebSocket' in window) {
                ws = new WebSocket(target);
            } else if ('MozWebSocket' in window) {
                ws = new MozWebSocket(target);
            } else {
                console.log('WebSocket is not supported by this browser.');
            }
            if (ws) {
                ws.onmessage = function (event) {
                    var data = event.data;
                    var alert = JSON.parse(data).event.payloadData;
                    alerts.unshift(alert);
                    if (alerts.length > 5) {
                        alerts = alerts.slice(0, -1);
                    }
                    var realtimeAlerts = $('#realtime_alerts');
                    realtimeAlerts.find('tbody').empty();
                    for (var i = 0; i < alerts.length; i++) {
                        var row = '<tr ' + (alerts[i].level === 'Warn' ? 'style="background-color: #faffd7">' : '>') +
                            '<td>' + new Date().toLocaleString() + '</td>' +
                            '<td>' + alerts[i].message + '</td>' +
                            '</tr>';
                        realtimeAlerts.find('tbody').append(row);
                    }
                }
            }
        }

        function disconnect() {
            if (ws != null) {
                ws.close();
                ws = null;
            }
        }
    }


    function datePickerCallback(startD, endD) {
        var eventsSuccess = function (data) {
            var records = JSON.parse(data);
            analyticsHistory.redrawGraphs(records);
        };

        var index = 0;
        var length = 30;

        $.ajax({
            type: "POST",
            url: "invoker/execute",
            data: {
                "uri": "/events/TractorHub/<%=id%>?offset=" + index + "&limit=" + length + "&from=" + new Date(
                    startD.format('YYYY-MM-DD H:mm:ss')).getTime() + "&to=" + new Date(
                    endD.format('YYYY-MM-DD H:mm:ss')).getTime(),
                "method": "get"
            },
            success: eventsSuccess
        });
    }

    $(function () {
        $('#dateRange').daterangepicker({
            timePicker: true,
            timePickerIncrement: 30,
            locale: {
                format: 'MM/DD/YYYY h:mm A'
            },
            ranges: {
                'Today': [moment(), moment()],
                'Yesterday': [moment().subtract(1, 'days'),
                    moment().subtract(1, 'days')],
                'Last 7 Days': [moment().subtract(6, 'days'), moment()],
                'Last 30 Days': [moment().subtract(29, 'days'), moment()],
                'This Month': [moment().startOf('month'), moment().endOf('month')],
                'Last Month': [moment().subtract(1, 'month').startOf('month'),
                    moment().subtract(1, 'month').endOf('month')]
            }
        }, datePickerCallback);

    });

    //update the card details
    function updateStatusCards(sincetext, alert, fuelstatus, engineStatus, load) {
        //engine status
        if (alert) {
            $("#rain_alert").html("TRUE");
        }
        else {
            $("#rain_alert").html("FALSE");
        }

        //fuel status
        if (fuelstatus === 0) {
            $("#fuel_status").html("FULL");
        }
        else if (fuelstatus === 100) {
            $("#fuel_status").html("EMPTY");
        }
        else {
            $("#fuel_status").html(fuelstatus + "<b>%</b>");
        }


        //engine status
        if (!engineStatus) {
            $("#engine_status").html("ON");
        }
        else {
            $("#engine_status").html("OFF");
        }

        //load status
        if (load === 0) {
            $("#tractorload_status").html("EMPTY");
        }
        else if (load === 100) {
            $("#tractorload_status").html("FULL");
        }
        else {
            $("#tractorload_status").html(load + "<b>%</b>");
        }


    }

    var lastKnownSuccess = function (data) {
        var record = JSON.parse(data).records[0];

        if (record) {
            lastKnown = record;
            var sinceText = timeDifference(new Date(), new Date(record.timestamp), false) + " ago";
            var alert = record.values.raining;
            var fuelstatus = record.values.fuelUsage;
            var engineStatus = record.values.engineidle;
            var load = record.values.loadWeight;
            updateStatusCards(sinceText, alert, fuelstatus, engineStatus, load);
        } else {
            //alert status
            $("#rain_alert").html("Unknown");
            $("#rain_status_alert").parent().remove();

            //temperature status
            $("#fuel_status").html("Unknown");
            $("#fuel_status_alert").parent().remove();

            //wind direction status
            $("#engine_status").html("Unknown");
            $("#engine_status_alert").parent().remove();

            //wind speeed status
            $("#tractorload_status").html("Unknown");
            $("#tractorload_status_alert").parent().remove();
        }
    };
    $.ajax({
        type: "POST",
        url: "invoker/execute",
        data: {
            "uri": "/events/last-known/TractorHub/<%=id%>",
            "method": "get"
        },
        success: lastKnownSuccess
    });


</script>

</html>