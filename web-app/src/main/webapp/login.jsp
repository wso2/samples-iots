<%
    //Fix for "localhost" JSESSIONID cookie not sent issue
    //https://stackoverflow.com/questions/7346919/chrome-localhost-cookie-not-being-set
    if(request.getHeader("Host").startsWith("localhost:")){
        response.sendRedirect(request.getRequestURL().toString().replace("localhost", "127.0.0.1"));
        return;
    }
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login</title>

    <link href="css/bootstrap.min.css" rel="stylesheet"/>
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons" />
    <link href="https://fonts.googleapis.com/css?family=Fjalla+One|Montserrat|Roboto" rel="stylesheet">
    <%--<link rel="stylesheet" type="text/css" href="https://fonts.googleapis.com/css?family=Roboto:300,400,500,700" />--%>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/latest/css/font-awesome.min.css" />
    <link rel="stylesheet" href="css/material-kit.css"/>
</head>
<body class="signup-page">
<nav class="navbar navbar-transparent navbar-absolute">
    <div class="container">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <a class="navbar-brand"><h4><strong>SMART</strong>LOCKER</h4></a>
        </div>
    </div>
</nav>

<div class="wrapper">
    <div class="header header-filter" style="background-image: url('images/login_bg2.jpg'); background-size: cover; background-position: top center;">
        <div class="container">
            <div class="row">
                <div class="col-md-4 col-md-offset-4 col-sm-6 col-sm-offset-3">
                    <div class="card card-signup">
                        <form class="form" method="post" action="/login">
                            <div class="header header-primary text-center">
                                <h5>Web Portal</h5>
                                <div class="social-line">
                                    <%--<a href="#pablo" class="btn btn-simple btn-just-icon">--%>
                                        <%--<i class="fa fa-globe"></i>--%>
                                    <%--</a>--%>
                                </div>
                            </div>
                            <p class="text-divider">Enter credentials to login</p>
                            <div class="content">
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
                                    <%--<input type="text" class="form-control" placeholder="Email...">--%>
                                    <div class="form-group label-floating">
                                        <label class="control-label">Username</label>
                                        <input type="text" name="inputEmail" class="form-control">
                                    </div>
                                </div>

                                <div class="input-group">
										<span class="input-group-addon">
											<i class="material-icons">lock_outline</i>
										</span>
                                    <%--<input type="password" placeholder="Password..." class="form-control" />--%>
                                    <div class="form-group label-floating">
                                        <label class="control-label">Password</label>
                                        <input type="password" name="inputPassword" class="form-control">
                                    </div>
                                </div>

                                <% if (request.getParameter("ret") != null) { %>
                                <input type="hidden" name="ret" value="<%=request.getParameter("ret")%>" />
                                <% } %>
                            </div>
                            <div class="footer text-center">
                                <input type="submit" class="btn btn-simple btn-primary btn-lg" value="Login" />
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <footer class="footer">
            <div class="container">
                <nav class="pull-left">
                    <ul>
                        <li>
                            <a href="#">
                                About Us
                            </a>
                        </li>
                        <li>
                            <a href="#">
                                Blog
                            </a>
                        </li>
                        <li>
                            <a href="#">
                                Licenses
                            </a>
                        </li>
                    </ul>
                </nav>
                <div class="copyright pull-right">
                    &copy; 2017, WSO2 IoT Team</a>
                </div>
            </div>
        </footer>

    </div>

</div>
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
<script type="text/javascript">
    $(document).ready(function () {

        // Javascript method's body can be found in assets/js/demos.js
        demo.initDashboardPageCharts();

    });
</script>
</html>
