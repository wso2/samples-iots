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
<%@ page import="java.net.URL" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.net.URISyntaxException" %>
<%@ page import="java.security.NoSuchAlgorithmException" %>
<%@ page import="java.security.KeyManagementException" %>
<%@ page import="org.json.JSONException" %>
<%@ page import="java.security.KeyStoreException" %>
<%@ page import="org.wso2.iot.weatherstation.portal.LoginController" %>
<%@include file="includes/authenticate.jsp" %>
<%
    String id = request.getParameter("id");
    if (id == null) {
        response.sendRedirect("devices.jsp");
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

    StringEntity entity = new StringEntity("uri=/devices/weatherstation/" + id + "&method=get",
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
    try {

    } catch (JSONException e) {
%>
Error occurred while fetching device info.
<%
    }
%>

<!doctype html>
<html lang="en">

<head>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <title>Weather Station details</title>
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
</head>

<body>
<div class="wrapper">
    <div class="sidebar" data-color="purple" data-image="images/sidebar-1.jpg">
        <!--
    Tip 1: You can change the color of the sidebar using: data-color="purple | blue | green | orange | red"

    Tip 2: you can also add an image using data-image tag
-->
        <div class="logo">
            <a class="simple-text" id="devName">
                Weather Station
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
                <li >
                    <a href="Dashboard.jsp">
                        <i class="material-icons">dashboard</i>
                        <p>Dashboard</p>
                    </a>
                </li>
            </ul>
            <footer>
            <div class="card card-stats " style="margin-bottom: 10px">
                <div class="card-content">
                    <h3 class="title" id="devNam">
                    </h3>
                    <p class="category" id="devDetails"></p>
                </div>
            </div>
            </footer>


        </div>

    </div>
    <div class="main-panel">
        <nav class="navbar navbar-transparent navbar-absolute">
            <div class="container-fluid">
                <div class="collapse navbar-collapse">
                    <ul class="nav navbar-nav navbar-right">
                        <li >
                            <a href="./devices.jsp">
                                <p ><i class="material-icons">list</i>Device List</p>
                            </a>
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
        <div class="content">
            <div class="container-fluid">
                <div class="tab-content">
                    <div id="realtime" class="tab-pane fade in active">
                        <div class="row">
                            <div class="col-lg-3 col-md-6 col-sm-6">
                                <div class="card card-stats">
                                    <div class="card-header" data-background-color="orange">
                                        <i class="material-icons">brightness_low</i>
                                    </div>
                                    <div class="card-content">
                                        <p class="category">Temperature</p>
                                        <h3 class="title" id="temperature">Yet to be updated
                                        </h3>
                                    </div>
                                    <div class="card-footer">
                                        <div class="stats"  id="temperature_status_alert">
                                            <i class="material-icons">update</i> Just Updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-lg-3 col-md-6 col-sm-6">
                                <div class="card card-stats">
                                    <div class="card-header" data-background-color="green">
                                        <i class="material-icons">invert_colors</i>
                                    </div>
                                    <div class="card-content">
                                        <p class="category">Humidity</p>
                                        <h3 class="title" id="humidity">Yet to be updated</h3>
                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="humidity_status_alert">
                                            <i class="material-icons">update</i> Just Updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-lg-3 col-md-6 col-sm-6">
                                <div class="card card-stats">
                                    <div class="card-header" data-background-color="red">
                                        <i class="material-icons">zoom_out_map</i>
                                    </div>
                                    <div class="card-content">
                                        <p class="category">Wind Direction</p>
                                        <h3 class="title" id="wind_status">Yet to be updated</h3>
                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="wind_status_alert">
                                            <i class="material-icons">update</i> Just Updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-lg-3 col-md-6 col-sm-6">
                                <div class="card card-stats">
                                    <div class="card-header" data-background-color="blue">
                                        <i class="material-icons">forward</i>
                                    </div>
                                    <div class="card-content">
                                        <p class="category">Wind Speed</p>
                                        <h3 class="title" id="windspeed_status">Yet to be updated</h3>
                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="windspeed_status_alert">
                                            <i class="material-icons">update</i> Just Updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-4">
                                <div class="card" id="temp" >
                                    <div class="card-header card-chart" data-background-color="red">
                                        <div class="ct-chart" id="RealTimeTempChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Temperature</h4>
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
                                <div class="card" id='humid' >
                                    <div class="card-header card-chart" data-background-color="blue">
                                        <div class="ct-chart" id="RealTimeHumidityChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Humidity</h4>
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
                                <div class="card" id='winddir' >
                                    <div class="card-header card-chart" data-background-color="green">
                                        <div class="ct-chart" id="RealTimeWindDirChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Wind Direction</h4>
                                        <p class="category">

                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="realtimeWindDirLastUpdated">
                                            <i class="material-icons">access_time</i> Yet to be updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='windspeed' >
                                    <div class="card-header card-chart" data-background-color="purple">
                                        <div class="ct-chart" id="RealTimeWindSpeedChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Wind Speed</h4>
                                        <p class="category">

                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="realtimewindspeedmphLastUpdated">
                                            <i class="material-icons">access_time</i> Yet to be updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='indoorTemp' >
                                    <div class="card-header card-chart" data-background-color="orange">
                                        <div class="ct-chart" id="RealTimeIndoorTempChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Indoor Temperature</h4>
                                        <p class="category">

                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="realtimeindoortempLastUpdated">
                                            <i class="material-icons">access_time</i> Yet to be updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='indoorHumid' >
                                    <div class="card-header card-chart" data-background-color="yellow">
                                        <div class="ct-chart" id="RealTimeIndoorHumidityChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Indoor Humidity</h4>
                                        <p class="category">

                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="realtimeindoorhumidLastUpdated">
                                            <i class="material-icons">access_time</i> Yet to be updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-4">
                                <div class="card" id='windgust' >
                                    <div class="card-header card-chart" data-background-color="orange">
                                        <div class="ct-chart" id="RealTimeWindGustChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Wind Gust</h4>
                                        <p class="category">

                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="realtimewindgustLastUpdated">
                                            <i class="material-icons">access_time</i> Yet to be updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='dewpoint' >
                                    <div class="card-header card-chart" data-background-color="purple">
                                        <div class="ct-chart" id="RealTimeDewPointChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Dew Point</h4>
                                        <p class="category">

                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="realtimedewptfLastUpdated">
                                            <i class="material-icons">access_time</i> Yet to be updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='windchill' >
                                    <div class="card-header card-chart" data-background-color="red">
                                        <div class="ct-chart" id="RealTimeWindChillChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Wind Chill</h4>
                                        <p class="category">

                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="realtimewindchillfLastUpdated">
                                            <i class="material-icons">access_time</i> Yet to be updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='raining' >
                                    <div class="card-header card-chart" data-background-color="yellow">
                                        <div class="ct-chart" id="RealTimeRainingChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Raining</h4>
                                        <p class="category">

                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="realtimerainingLastUpdated">
                                            <i class="material-icons">access_time</i> Yet to be updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='baromin' >
                                    <div class="card-header card-chart" data-background-color="green">
                                        <div class="ct-chart" id="RealTimeBarominChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Baromin</h4>
                                        <p class="category">

                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="realtimebarominLastUpdated">
                                            <i class="material-icons">access_time</i> Yet to be updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='lowbatt' >
                                    <div class="card-header card-chart" data-background-color="blue">
                                        <div class="ct-chart" id="RealTimeLowbatChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Lowbatt</h4>
                                        <p class="category">

                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="realtimelowbattLastUpdated">
                                            <i class="material-icons">access_time</i> Yet to be updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-4">
                                <div class="card" id='dailyraining' >
                                    <div class="card-header card-chart" data-background-color="green">
                                        <div class="ct-chart" id="RealTimeDailyRainingChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Daily Raining</h4>
                                        <p class="category">

                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="realtimedailyrainingLastUpdated">
                                            <i class="material-icons">access_time</i> Yet to be updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='weeklyraining' >
                                    <div class="card-header card-chart" data-background-color="yellow">
                                        <div class="ct-chart" id="RealTimeWeeklyRainingChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Weekly raining</h4>
                                        <p class="category">

                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="realtimeweeklyrainingLastUpdated">
                                            <i class="material-icons">access_time</i> Yet to be updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='monthlyraining' >
                                    <div class="card-header card-chart" data-background-color="orange">
                                        <div class="ct-chart" id="RealTimeMonthlyRainingChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Monthly Raining</h4>
                                        <p class="category">

                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="realtimemonthlyrainingLastUpdated">
                                            <i class="material-icons">access_time</i> Yet to be updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='yearlyraining' >
                                    <div class="card-header card-chart" data-background-color="purple">
                                        <div class="ct-chart" id="RealTimeYearlyRainingChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Yearly Raining</h4>
                                        <p class="category">

                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="realtimeyearlyrainingLastUpdated">
                                            <i class="material-icons">access_time</i> Yet to be updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='solarradiation' >
                                    <div class="card-header card-chart" data-background-color="red">
                                        <div class="ct-chart" id="RealTimeSolarRadiationChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Solar Radiation</h4>
                                        <p class="category">

                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="realtimesolarradiationLastUpdated">
                                            <i class="material-icons">access_time</i> Yet to be updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='uv' >
                                    <div class="card-header card-chart" data-background-color="blue">
                                        <div class="ct-chart" id="RealTimeUltraVioletChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Ultra Violet</h4>
                                        <p class="category">

                                    </div>
                                    <div class="card-footer">
                                        <div class="stats" id="realtimeuvLastUpdated">
                                            <i class="material-icons">access_time</i> Yet to be updated
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>
                    <div id="historical" class="tab-pane fade">
                        <div style="margin-right:70%;margin-left:2%  ">
                            <h4><strong>Select Date-range</strong> <input type="text" name="dateRange" id="dateRange"
                                                         value="01/01/2017 1:30 PM - 01/01/2017 2:00 PM"
                                                         class="form-control"/></h4>

                        </div>
                        <div class="row">
                            <div class="col-md-4">
                                <div class="card" id='Htemp' >
                                    <div class="card-header card-chart" data-background-color="red">
                                        <div class="ct-chart" id="HistoricalTempChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Temperature</h4>
                                        <p class="category" id="historicalTempAlert">
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='Hhumid' >
                                    <div class="card-header card-chart" data-background-color="blue">
                                        <div class="ct-chart" id="HistoricalHumidityChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Humidity</h4>
                                        <p class="category" id="historicalHumidAlert">
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='Hwinddir' >
                                    <div class="card-header card-chart" data-background-color="green">
                                        <div class="ct-chart" id="HistoricalWindDirChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Wind Direction</h4>
                                        <p class="category" id="historicalWindDirAlert">

                                    </div>

                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='Hwindspeed' >
                                    <div class="card-header card-chart" data-background-color="purple">
                                        <div class="ct-chart" id="HistoricalWindSpeedChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Wind Speed</h4>
                                        <p class="category" id="historicalwindspeedLastUpdated">

                                    </div>

                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='Hitemp' >
                                    <div class="card-header card-chart" data-background-color="orange">
                                        <div class="ct-chart" id="HistoricalIndoorTempChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Indoor Temperature</h4>
                                        <p class="category" id="historicalindoortempfLastUpdated">

                                    </div>

                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='Hihumid' >
                                    <div class="card-header card-chart" data-background-color="yellow">
                                        <div class="ct-chart" id="HistoricalIndoorHumidityChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Indoor Humidity</h4>
                                        <p class="category" id="historicalindoorhumidityLastUpdated">

                                    </div>

                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-4">
                                <div class="card" id='Hwindgust' >
                                    <div class="card-header card-chart" data-background-color="orange">
                                        <div class="ct-chart" id="HistoricalWindGustChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Wind Gust</h4>
                                        <p class="category" id="historicalwindgustLastUpdated">

                                    </div>

                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='Hdewpoint' >
                                    <div class="card-header card-chart" data-background-color="purple">
                                        <div class="ct-chart" id="HistoricalDewPointChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Dew Point</h4>
                                        <p class="category" id="historicaldewptfLastUpdated">

                                    </div>

                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='Hwindchill' >
                                    <div class="card-header card-chart" data-background-color="red">
                                        <div class="ct-chart" id="HistoricalWindChillChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Wind Chill</h4>
                                        <p class="category" id="historicalwindchillfLastUpdated">

                                    </div>

                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='Hraining' >
                                    <div class="card-header card-chart" data-background-color="yellow">
                                        <div class="ct-chart" id="HistoricalRainingChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Raining</h4>
                                        <p class="category" id="historicalrainingLastUpdated">

                                    </div>

                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='Hbaromin' >
                                    <div class="card-header card-chart" data-background-color="green">
                                        <div class="ct-chart" id="HistoricalBarominChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Baromin</h4>
                                        <p class="category" id="historicalbarominLastUpdated">

                                    </div>

                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='Hlowbatt' >
                                    <div class="card-header card-chart" data-background-color="blue">
                                        <div class="ct-chart" id="HistoricalLowbatChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Lowbatt</h4>
                                        <p class="category" id="historicallowbattLastUpdated">

                                    </div>

                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-4">
                                <div class="card" id='Hdailyraining' >
                                    <div class="card-header card-chart" data-background-color="green">
                                        <div class="ct-chart" id="HistoricalDailyRainingChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Daily Raining</h4>
                                        <p class="category" id="historicaldailyrainingLastUpdated">

                                    </div>

                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='Hweeklyraining' >
                                    <div class="card-header card-chart" data-background-color="yellow">
                                        <div class="ct-chart" id="HistoricalWeeklyRainingChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Weekly raining</h4>
                                        <p class="category" id="historicalweeklyrainingLastUpdated">

                                    </div>

                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='Hmonthly raining' >
                                    <div class="card-header card-chart" data-background-color="orange">
                                        <div class="ct-chart" id="HistoricalMonthlyRainingChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Monthly Raining</h4>
                                        <p class="category" id="historicalmonthlyrainingLastUpdated">

                                    </div>

                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='Hyearlyraining' >
                                    <div class="card-header card-chart" data-background-color="purple">
                                        <div class="ct-chart" id="HistoricalYearlyRainingChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Yearly Raining</h4>
                                        <p class="category" id="historicalyearlyrainingLastUpdated">

                                    </div>

                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='Hsolarradiation' >
                                    <div class="card-header card-chart" data-background-color="red">
                                        <div class="ct-chart" id="HistoricalSolarRadiationChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Solar Radiation</h4>
                                        <p class="category" id="historicalsolarradiationLastUpdated">

                                    </div>

                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="card" id='Huv' >
                                    <div class="card-header card-chart" data-background-color="blue">
                                        <div class="ct-chart" id="HistoricalUltraVioletChart"></div>
                                    </div>
                                    <div class="card-content">
                                        <h4 class="title">Ultra Violet</h4>
                                        <p class="category" id="historicaluvLastUpdated">

                                    </div>

                                </div>
                            </div>
                        </div>
                    </div>


                </div>
            </div>
        </div>
        <footer class="footer">
            <p class="copyright pull-right">
                &copy;
                <script>
                    document.write(new Date().getFullYear())
                </script>
                <a href="https://wso2.com/iot">WSO2 Inc.</a>
            </p>
        </footer>
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
<script src="js/historical-analytics.js"></script>
<script src="js/realtime-analytics.js"></script>
<script src="js/bootstrap-datepicker.js" type="text/javascript"></script>
<script src="js/moment.min.js" type="text/javascript"></script>
<script src="js/daterangepicker.js" type="text/javascript"></script>
<script type="text/javascript">

    //set device details and send device details to dashboard.jsp
    localStorage.setItem("deviceId","<%=id%>");
    localStorage.setItem("deviceName","<%=device.getString("name")%>");
    localStorage.setItem("owner","<%=enrolmentInfo.getString("owner")%>");
    localStorage.setItem("date","<%=new Date(enrolmentInfo.getLong("dateOfEnrolment")).toString()%>");
    document.getElementById("devName").innerHTML="<%=device.getString("name")%>";
    document.getElementById("devDetails").innerHTML="Owned by "+"<%=enrolmentInfo.getString("owner")%>"+" and enrolled on "+"<%=new Date(enrolmentInfo.getLong("dateOfEnrolment")).toString()%>";

    function datePickerCallback(startD, endD) {
        var eventsSuccess = function (data) {
            var records = JSON.parse(data);
            console.log(records);
            analyticsHistory.redrawGraphs(records);

        };

        var index = 0;
        var length = 100;

        $.ajax({
            type: "POST",
            url: "invoker/execute",
            data: {
                "uri": "/events/weatherstation/<%=id%>?offset=" + index + "&limit=" + length + "&from=" + new Date(
                    startD.format('YYYY-MM-DD H:mm:ss')).getTime() + "&to=" + new Date(
                    endD.format('YYYY-MM-DD H:mm:ss')).getTime(),
                "method": "get"
            },
            success: eventsSuccess
        });
    };

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

        $(window).scroll(function () {
            if ($('#dateRange').length) {
                $('#dateRange').daterangepicker("close");
            }
        })
    });
</script>
<script type="text/javascript">
    var alerts = [];

    function timeDifference(current, previous, isShort) {
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
            return 'approximately ' + Math.round(elapsed / msPerDay) + ' days ago';
        } else if (elapsed < msPerYear) {
            return 'approximately ' + Math.round(elapsed / msPerMonth) + ' months ago';
        } else {
            return 'approximately ' + Math.round(elapsed / msPerYear) + ' years ago';
        }
    }

    $(document).ready(function () {
        $(document).ready(function () {
            var wsStatsEndpoint = "<%=pageContext.getServletContext().getInitParameter("websocketEndpoint")%>/secured-websocket/iot.per.device.stream.carbon.super.weatherstation/1.0.0?"
                + "deviceId=<%=id%>&deviceType=weatherstation&websocketToken=<%=request.getSession(false).getAttribute(LoginController.ATTR_ACCESS_TOKEN)%>";
            realtimeGraphRefresh(wsStatsEndpoint);

            var wsAlertEndpoint = "<%=pageContext.getServletContext().getInitParameter("websocketEndpoint")%>/secured-websocket/iot.per.device.stream.carbon.super.weatherstation.alert/1.0.0?"
                + "deviceId=<%=id%>&deviceType=weatherstation&websocketToken=<%=request.getSession(false).getAttribute(LoginController.ATTR_ACCESS_TOKEN)%>";
            displayAlerts(wsAlertEndpoint);
        });
    });

    //refresh graphs on click
    document.getElementById("realtimeTab").addEventListener("click", realtimeGraphRefresh());
    document.getElementById("historicalTab").addEventListener("click", setTimeout(historyGraphRefresh,1000));

    function realtimeGraphRefresh(wsEndpoint) {
        realtimeAnalytics.initDashboardPageCharts(wsEndpoint);
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
                    console.log(data);
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

    function historyGraphRefresh() {
        analyticsHistory.initDashboardPageCharts();
        var start = moment().subtract(1, 'days');
        var end = moment();
        datePickerCallback(start, end);
    }

    //update the card details
    function updateStatusCards(sinceText, temperature, humidity, windDir,windSpeed) {

        //temperature status
        $("#temperature").html(temperature);

        //humidity status
        $("#humidity").html(humidity);

        //wind status
        $("#wind_status").html(windDir);

        //wind speed
        console.log(windSpeed);
        $("#windspeed_status").html(windSpeed);

    }

    var lastKnown = {};
    var lastKnownSuccess = function (data) {
        var record = JSON.parse(data).records[0];

        if (record) {
            lastKnown = record;
            var sinceText = timeDifference(new Date(), new Date(record.timestamp), false) + " ago";
            var temperature = record.values.tempf;
            var humidity = record.values.humidity;
            var windDir = record.values.winddir;
            var windSpeed=record.values.windspeedmph;
            updateStatusCards(sinceText, temperature, humidity, windDir,windSpeed);
        } else {
            //temperature status
            $("#temperature").html("Unknown");
            $("#temperature_status_alert").parent().remove();

            //humidity status
            $("#humidity").html("Unknown");
            $("#humidity_status_alert").parent().remove();

            //wind direction status
            $("#wind_status").html("Unknown");
            $("#wind_status_alert").parent().remove();

            //wind speeed status
            $("#windspeed_status").html("Unknown");
            $("#windspeed_status_alert").parent().remove();
        }
    };
    $.ajax({
        type: "POST",
        url: "invoker/execute",
        data: {
            "uri": "/events/last-known/weatherstation/<%=id%>",
            "method": "get"
        },
        success: lastKnownSuccess
    });
</script>

</html>