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
<%@ page import="java.util.Date" %>
<%@ page import="org.wso2.iot.dashboard.portal.LoginController" %>
<%@include file="includes/authenticate.jsp" %>
<%
    String deviceType="weatherstation";
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

    StringEntity entity = new StringEntity("uri=/devices/"+deviceType+"/" + id + "&method=get",
            ContentType.APPLICATION_FORM_URLENCODED);
    invokerEndpoint.setEntity(entity);

    SSLContextBuilder builder = new SSLContextBuilder();
    try {
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
    } catch (Exception e) {
        e.printStackTrace();
    }
    SSLConnectionSocketFactory sslsf = null;
    try {
        sslsf = new SSLConnectionSocketFactory(builder.build());
    } catch (Exception e) {
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
    <title>Dashboard</title>
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
                <strong>Device</strong>Portal
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

            <%@ include file="pages/details-page-Segments/deviceDetailsCard.jsp" %>
            <%@ include file="pages/details-page-Segments/mapSegment.jsp" %>

            <div style=" position:absolute; bottom: 0;margin-left: 100px">
                <%@ include file="pages/details-page-Segments/footerWSO2.jsp" %>
            </div>
        </div>


    </div>
    <div id="page-content-wrapper" class="main-panel" style="padding-top: 2px;">
        <%@ include file="pages/details-page-Segments/navBar.jsp" %>

        <div class="content" style="padding-top: 2px;">
            <div id="curtain" style="text-align: center;font-size: 20px;"><STRONG>Charts Loading...</STRONG></div>
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
                        <%@ include file="pages/details-page-Segments/realTimeCardSegment.jsp" %>
                        <%@ include file="pages/details-page-Segments/realTimeChartSegment.jsp" %>
                    </div>
                    <div id="historical" class="tab-pane fade">
                        <%@ include file="pages/details-page-Segments/historicalChartsegment.jsp" %>
                    </div>


                </div>
            </div>
        </div>
        <%@ include file="pages/details-page-Segments/operationPopUpModals.jsp" %>

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
<script src="pages/details-page-scripts/functions.js"></script>
<script src="pages/details-page-scripts/cssFunctions.js"></script>
<script type="text/javascript">
    var lastKnown = {};

    var deviceType="weatherstation";

    var typeParameter1="tempf";
    var displayName1="Temperature";
    var units1="&#8451";

    var typeParameter2="humidity";
    var displayName2="Humidity";
    var units2="%";

    var typeParameter3="winddir";
    var displayName3="Wind Direction";
    var units3="&#176";

    var typeParameter4="dewptf";
    var displayName4="Dew Point";
    var units4="&#8451";

    var typeParameter5="windspeedmph";
    var displayName5="Wind Speed";
    var units5="<Strong> mph</Strong>";

    var typeParameter6="rainin";
    var displayName6="Raining";
    var units6="&#176";

    var typeParameter7="solarradiation";
    var displayName7="Solar Radiation";
    var units7="<Strong> mmpH</Strong>";

    var typeParameter8="UV";
    var displayName8="Ultra Violet";
    var units8="<Strong> milliwatts</Strong>";

    var typeParameter9="baromin";
    var displayName9="Baromin";
    var units9="<Strong> pascal</Strong>";

    document.getElementById("title1").innerHTML = displayName1+ units1;
    document.getElementById("title2").innerHTML = displayName2+ units2;
    document.getElementById("title3").innerHTML = displayName3+ units3;
    document.getElementById("title4").innerHTML = displayName4+ units4;
    document.getElementById("title5").innerHTML = displayName5+ units5;
    document.getElementById("title6").innerHTML = displayName6+ units6;
    document.getElementById("title7").innerHTML = displayName7+ units7;
    document.getElementById("title8").innerHTML = displayName8+ units8;
    document.getElementById("title9").innerHTML = displayName9+ units9;

    document.getElementById("Htitle1").innerHTML = displayName1+ units1;
    document.getElementById("Htitle2").innerHTML = displayName2+ units2;
    document.getElementById("Htitle3").innerHTML = displayName3+ units3;
    document.getElementById("Htitle4").innerHTML = displayName4+ units4;
    document.getElementById("Htitle5").innerHTML = displayName5+ units5;
    document.getElementById("Htitle6").innerHTML = displayName6+ units6;
    document.getElementById("Htitle7").innerHTML = displayName7+ units7;
    document.getElementById("Htitle8").innerHTML = displayName8+ units8;
    document.getElementById("Htitle9").innerHTML = displayName9+ units9;

    document.getElementById("cardtitle1").innerHTML = displayName1;
    document.getElementById("cardtitle2").innerHTML = displayName2;
    document.getElementById("cardtitle3").innerHTML = displayName3;
    document.getElementById("cardtitle4").innerHTML = displayName4;




    $(document).ready(function () {
        $(document).ready(function () {
            var wsStatsEndpoint = "<%=pageContext.getServletContext().getInitParameter("websocketEndpoint")%>/secured-websocket/iot.per.device.stream.carbon.super."+deviceType+"/1.0.0?"
                + "deviceId=<%=id%>&deviceType="+deviceType+"&websocketToken=<%=request.getSession(false).getAttribute(LoginController.ATTR_ACCESS_TOKEN)%>";
            realtimeGraphRefresh(wsStatsEndpoint);

            var wsAlertEndpoint = "<%=pageContext.getServletContext().getInitParameter("websocketEndpoint")%>/secured-websocket/iot.per.device.stream.carbon.super."+deviceType+".alert/1.0.0?"
                + "deviceId=<%=id%>&deviceType="+deviceType+"&websocketToken=<%=request.getSession(false).getAttribute(LoginController.ATTR_ACCESS_TOKEN)%>";
            displayAlerts(wsAlertEndpoint);
        });
    });

    //set device card details
    document.getElementById("devName").innerHTML = "<%=device.getString("name")%>";
    document.getElementById("devDetails").innerHTML = "Owned by " + "<%=enrolmentInfo.getString("owner")%>" + " and enrolled on " + "<%=new Date(enrolmentInfo.getLong("dateOfEnrolment")).toString()%>";

    //refresh graphs on click
    document.getElementById("realtimeTab").addEventListener("click", realtimeGraphRefresh());
    document.getElementById("historicalTab").addEventListener("click", historyGraphRefresh);

    //fix the issue of charts not rendering in historical tab
    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
        $(e.currentTarget.hash).find('.ct-chart').each(function (el, tab) {
            tab.__chartist__.update();
        });
    });


    var lastKnownSuccess = function (data) {
        var record = JSON.parse(data).records[0];

        if (record) {
            lastKnown = record;
            var sinceText = timeDifference(new Date(), new Date(record.timestamp), false) + " ago";
            var varOne = record.values[typeParameter1];
            var varTwo = record.values[typeParameter2];
            var varThree = record.values[typeParameter3];
            var varFour = record.values[typeParameter4];
            updateStatusCards(sinceText, varOne, varTwo, varThree, varFour);
        } else {
            //card1 status
            $("#card1").html("Unknown");
            $("#card1_alert").parent().remove();
            //card2 status
            $("#card2").html("Unknown");
            $("#card2_alert").parent().remove();
            //card3 status
            $("#card3").html("Unknown");
            $("#card3_alert").parent().remove();
            //card4 status
            $("#card4").html("Unknown");
            $("#card4_alert").parent().remove();


        }
    };
    $.ajax({
        type: "POST",
        url: "invoker/execute",
        data: {
            "uri": "/events/last-known/"+deviceType+"/<%=id%>",
            "method": "get"
        },
        success: lastKnownSuccess
    });

    $(function () {
        $('#dateRange').daterangepicker({
            timePicker: true,
            timePickerIncrement: 30,
            locale: {
                format: 'DD/MM/YYYY h:mm A'
            },
            ranges: {
                'Today': [moment().startOf('day'), moment()],
                'Yesterday': [moment().subtract(1, 'days').startOf('day'),
                    moment().subtract(1, 'days').endOf('day')],
                'Last 7 Days': [moment().subtract(6, 'days'), moment()],
                'Last 30 Days': [moment().subtract(29, 'days'), moment()],
                'This Month': [moment().startOf('month'), moment().endOf('month')],
                'Last Month': [moment().subtract(1, 'month').startOf('month'),
                    moment().subtract(1, 'month').endOf('month')]
            }
        }, datePickerCallback);

    });



    function datePickerCallback(startD, endD) {
        chartsLoading();
        var eventsSuccess = function (data) {
            var records = JSON.parse(data);
            analyticsHistory.redrawGraphs(records);
        };

        var index = 0;
        var length = 100;

        $.ajax({
            type: "POST",
            url: "invoker/execute",
            data: {
                "uri": "/events/"+deviceType+"/<%=id%>?offset=" + index + "&limit=" + length + "&from=" + new Date(
                    startD.format('YYYY-MM-DD H:mm:ss')).getTime() + "&to=" + new Date(
                    endD.format('YYYY-MM-DD H:mm:ss')).getTime(),
                "method": "get"
            },
            success: eventsSuccess
        });
    }

</script>

</html>