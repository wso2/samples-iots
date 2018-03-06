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


<%
    //Fix for "localhost" JSESSIONID cookie not sent issue
    //https://stackoverflow.com/questions/7346919/chrome-localhost-cookie-not-being-set
    if (request.getHeader("Host").startsWith("localhost:")) {
        response.sendRedirect(request.getRequestURL().toString().replace("localhost", "127.0.0.1"));
        return;
    }
%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Login</title>
    <link href="css/bootstrap.min.css" rel="stylesheet"/>
    <link href="css/material-icons.css" rel="stylesheet"/>
    <link href="css/font-awesome.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/material-kit.css"/>
</head>
<body class="signup-page" background="images/windows_8_simple_background-wallpaper-2560x1600.jpg">
<nav class="navbar navbar-transparent navbar-absolute">
    <div class="container">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header" style=" margin-left: 30%;">
            <a class="navbar-brand"><h4 style="font-size: 3em"><strong>TRACTOR</strong>HUB</h4></a>
        </div>
    </div>
</nav>

<div class="wrapper">
    <div class="header header-filter">
        <div class="container">
            <div class="row" style="margin-top: 100px">
                <div class="col-sm-4 col-md-offset-4 col-sm-6 col-sm-offset-3">
                    <div class="card card-signup">
                        <form class="form" method="post" action="login">
                            <div class="header header-primary text-center">
                                <h5>Web Portal</h5>
                            </div>
                            <p class="text-divider">Enter credentials to login</p>
                            <div class="content">
                                <!--Generate an error if username or password is incorrect -->
                                <% if ("fail".equals(request.getParameter("status"))) { %>
                                <div class="alert alert-dismissible alert-danger">
                                    <button type="button" class="close" data-dismiss="alert">&times;</button>
                                    <h4>Login Failed!</h4>
                                    <p>Please verify username and password again.</p>
                                </div>
                                <% } %>
                                <div class="input-group">
										<span class="input-group-addon">
											<i class="material-icons">email</i>
										</span>
                                    <div class="form-group label-floating">
                                        <label class="control-label">Username</label>
                                        <input type="text" name="inputEmail" class="form-control">
                                    </div>
                                </div>

                                <div class="input-group">
										<span class="input-group-addon">
											<i class="material-icons">lock_outline</i>
										</span>
                                    <div class="form-group label-floating">
                                        <label class="control-label">Password</label>
                                        <input type="password" name="inputPassword" class="form-control">
                                    </div>
                                </div>
                                <!--To access pages devices.jsp and details.jsp directly after login in once -->
                                <% if (request.getParameter("ret") != null) { %>
                                <input type="hidden" name="ret" value="<%=request.getParameter("ret")%>"/>
                                <% } %>
                                <% if (request.getParameter("q") != null) { %>
                                <input type="hidden" name="q" value="<%=request.getParameter("q")%>"/>
                                <% } %>
                            </div>
                            <div class="footer text-center">
                                <input type="submit" class="btn btn-simple btn-primary btn-lg" value="Login"/>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

        </div>


    </div>

</div>
<p class="copyright" style="position: absolute;bottom:20;padding-left: 100px;color: white">
    &copy;
    <script>
        document.write(new Date().getFullYear())
    </script>
    <a href="https://wso2.com/iot">WSO2 Inc.</a>
</p>
</body>
<script src="js/jquery.min.js" type="text/javascript"></script>
<script src="js/bootstrap.min.js" type="text/javascript"></script>
<script src="js/material.min.js" type="text/javascript"></script>
<script src="js/nouislider.min.js" type="text/javascript"></script>
<script src="js/bootstrap-datepicker.js" type="text/javascript"></script>
<script src="js/material-kit.js" type="text/javascript"></script>
<script src="js/jquery.min.js" type="text/javascript"></script>
<script src="js/bootstrap.min.js" type="text/javascript"></script>
<script src="js/material.min.js" type="text/javascript"></script>
<script src="js/nouislider.min.js" type="text/javascript"></script>
<script src="js/bootstrap-datepicker.js" type="text/javascript"></script>
<script src="js/material-kit.js" type="text/javascript"></script>
<script src="js/bootstrap-notify.js" type="text/javascript"></script>
<script src="js/material-dashboard.js" type="text/javascript"></script>
</html>
