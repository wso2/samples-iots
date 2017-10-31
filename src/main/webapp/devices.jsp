<%--
  Created by IntelliJ IDEA.
  User: nuwan
  Date: 10/31/17
  Time: 10:41 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>

    <link href="css/bootstrap.min.css" rel="stylesheet"/>
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons" />
    <link href="css/material-dashboard.css" rel="stylesheet" />
    <link href="http://maxcdn.bootstrapcdn.com/font-awesome/latest/css/font-awesome.min.css" rel="stylesheet">
    <link href='http://fonts.googleapis.com/css?family=Roboto:400,700,300|Material+Icons' rel='stylesheet' type='text/css'>
</head>
<body>
<div class="wrapper">
    <div class="sidebar" data-color="purple" data-image="images/login_bg.jpeg">
        <!--
    Tip 1: You can change the color of the sidebar using: data-color="purple | blue | green | orange | red"

    Tip 2: you can also add an image using data-image tag
-->
        <div class="logo">
            <a href="http://www.creative-tim.com" class="simple-text">
                SmartLocker
            </a>
        </div>
        <div class="sidebar-wrapper">
            <ul class="nav">
                <%--<li>--%>
                    <%--<a href="dashboard.html">--%>
                        <%--<i class="material-icons">dashboard</i>--%>
                        <%--<p>Dashboard</p>--%>
                    <%--</a>--%>
                <%--</li>--%>
                <li class="active">
                    <a href="./table.html">
                        <i class="material-icons">content_paste</i>
                        <p>Locker List</p>
                    </a>
                </li>
                <li>
                    <a href="./user.html">
                        <i class="material-icons">person</i>
                        <p>User Profile</p>
                    </a>
                </li>
                <%--<li>--%>
                    <%--<a href="./typography.html">--%>
                        <%--<i class="material-icons">library_books</i>--%>
                        <%--<p>Typography</p>--%>
                    <%--</a>--%>
                <%--</li>--%>
                <%--<li>--%>
                    <%--<a href="./icons.html">--%>
                        <%--<i class="material-icons">bubble_chart</i>--%>
                        <%--<p>Icons</p>--%>
                    <%--</a>--%>
                <%--</li>--%>
                <%--<li>--%>
                    <%--<a href="./maps.html">--%>
                        <%--<i class="material-icons">location_on</i>--%>
                        <%--<p>Maps</p>--%>
                    <%--</a>--%>
                <%--</li>--%>
                <%--<li>--%>
                    <%--<a href="./notifications.html">--%>
                        <%--<i class="material-icons text-gray">notifications</i>--%>
                        <%--<p>Notifications</p>--%>
                    <%--</a>--%>
                <%--</li>--%>
                <%--<li class="active-pro">--%>
                    <%--<a href="upgrade.html">--%>
                        <%--<i class="material-icons">unarchive</i>--%>
                        <%--<p>Upgrade to PRO</p>--%>
                    <%--</a>--%>
                <%--</li>--%>
            </ul>
        </div>
    </div>
    <div class="main-panel">
        <nav class="navbar navbar-transparent navbar-absolute">
            <div class="container-fluid">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle" data-toggle="collapse">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="#"> Table List </a>
                </div>
                <div class="collapse navbar-collapse">
                    <ul class="nav navbar-nav navbar-right">
                        <li>
                            <a href="#pablo" class="dropdown-toggle" data-toggle="dropdown">
                                <i class="material-icons">dashboard</i>
                                <p class="hidden-lg hidden-md">Dashboard</p>
                            </a>
                        </li>
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                <i class="material-icons">notifications</i>
                                <span class="notification">5</span>
                                <p class="hidden-lg hidden-md">Notifications</p>
                            </a>
                            <ul class="dropdown-menu">
                                <li>
                                    <a href="#">Mike John responded to your email</a>
                                </li>
                                <li>
                                    <a href="#">You have 5 new tasks</a>
                                </li>
                                <li>
                                    <a href="#">You're now friend with Andrew</a>
                                </li>
                                <li>
                                    <a href="#">Another Notification</a>
                                </li>
                                <li>
                                    <a href="#">Another One</a>
                                </li>
                            </ul>
                        </li>
                        <li>
                            <a href="#pablo" class="dropdown-toggle" data-toggle="dropdown">
                                <i class="material-icons">person</i>
                                <p class="hidden-lg hidden-md">Profile</p>
                            </a>
                        </li>
                    </ul>
                    <form class="navbar-form navbar-right" role="search">
                        <div class="form-group  is-empty">
                            <input type="text" class="form-control" placeholder="Search">
                            <span class="material-input"></span>
                        </div>
                        <button type="submit" class="btn btn-white btn-round btn-just-icon">
                            <i class="material-icons">search</i>
                            <div class="ripple-container"></div>
                        </button>
                    </form>
                </div>
            </div>
        </nav>
        <div class="content">
            <div class="container-fluid">
                <div class="row">
                    <%--<div class="col-md-12">--%>
                        <%--<div class="card">--%>
                            <%--<div class="card-header" data-background-color="purple">--%>
                                <%--<h4 class="title">Simple Table</h4>--%>
                                <%--<p class="category">Here is a subtitle for this table</p>--%>
                            <%--</div>--%>
                            <%--<div class="card-content table-responsive">--%>
                                <%--<table class="table">--%>
                                    <%--<thead class="text-primary">--%>
                                    <%--<th>Name</th>--%>
                                    <%--<th>Country</th>--%>
                                    <%--<th>City</th>--%>
                                    <%--<th>Salary</th>--%>
                                    <%--</thead>--%>
                                    <%--<tbody>--%>
                                    <%--<tr>--%>
                                        <%--<td>Dakota Rice</td>--%>
                                        <%--<td>Niger</td>--%>
                                        <%--<td>Oud-Turnhout</td>--%>
                                        <%--<td class="text-primary">$36,738</td>--%>
                                    <%--</tr>--%>
                                    <%--<tr>--%>
                                        <%--<td>Minerva Hooper</td>--%>
                                        <%--<td>Curaçao</td>--%>
                                        <%--<td>Sinaai-Waas</td>--%>
                                        <%--<td class="text-primary">$23,789</td>--%>
                                    <%--</tr>--%>
                                    <%--<tr>--%>
                                        <%--<td>Sage Rodriguez</td>--%>
                                        <%--<td>Netherlands</td>--%>
                                        <%--<td>Baileux</td>--%>
                                        <%--<td class="text-primary">$56,142</td>--%>
                                    <%--</tr>--%>
                                    <%--<tr>--%>
                                        <%--<td>Philip Chaney</td>--%>
                                        <%--<td>Korea, South</td>--%>
                                        <%--<td>Overland Park</td>--%>
                                        <%--<td class="text-primary">$38,735</td>--%>
                                    <%--</tr>--%>
                                    <%--<tr>--%>
                                        <%--<td>Doris Greene</td>--%>
                                        <%--<td>Malawi</td>--%>
                                        <%--<td>Feldkirchen in Kärnten</td>--%>
                                        <%--<td class="text-primary">$63,542</td>--%>
                                    <%--</tr>--%>
                                    <%--<tr>--%>
                                        <%--<td>Mason Porter</td>--%>
                                        <%--<td>Chile</td>--%>
                                        <%--<td>Gloucester</td>--%>
                                        <%--<td class="text-primary">$78,615</td>--%>
                                    <%--</tr>--%>
                                    <%--</tbody>--%>
                                <%--</table>--%>
                            <%--</div>--%>
                        <%--</div>--%>
                    <%--</div>--%>
                    <div class="col-md-12">
                        <div class="card card-plain">
                            <div class="card-header" data-background-color="purple">
                                <h4 class="title">Table on Plain Background</h4>
                                <p class="category">Here is a subtitle for this table</p>
                            </div>
                            <div class="card-content table-responsive">
                                <table class="table table-hover">
                                    <thead>
                                    <th>Device ID</th>
                                    <th>Device Name</th>
                                    <th>Owner</th>
                                    <th>Token</th>
                                    <th>Analytics</th>
                                    </thead>
                                    <tbody>
                                    <tr>
                                        <td>1</td>
                                        <td>Dakota Rice</td>
                                        <td>$36,738</td>
                                        <td>Niger</td>
                                        <td>Oud-Turnhout</td>
                                    </tr>
                                    <tr>
                                        <td>2</td>
                                        <td>Minerva Hooper</td>
                                        <td>$23,789</td>
                                        <td>Curaçao</td>
                                        <td>Sinaai-Waas</td>
                                    </tr>
                                    <tr>
                                        <td>3</td>
                                        <td>Sage Rodriguez</td>
                                        <td>$56,142</td>
                                        <td>Netherlands</td>
                                        <td>Baileux</td>
                                    </tr>
                                    <tr>
                                        <td>4</td>
                                        <td>Philip Chaney</td>
                                        <td>$38,735</td>
                                        <td>Korea, South</td>
                                        <td>Overland Park</td>
                                    </tr>
                                    <tr>
                                        <td>5</td>
                                        <td>Doris Greene</td>
                                        <td>$63,542</td>
                                        <td>Malawi</td>
                                        <td>Feldkirchen in Kärnten</td>
                                    </tr>
                                    <tr>
                                        <td>6</td>
                                        <td>Mason Porter</td>
                                        <td>$78,615</td>
                                        <td>Chile</td>
                                        <td>Gloucester</td>
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
    $(document).ready(function() {

        // Javascript method's body can be found in assets/js/demos.js
        demo.initDashboardPageCharts();

    });
</script>

</html>
