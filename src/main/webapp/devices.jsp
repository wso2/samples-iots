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
            <a href="#" class="simple-text">
                SmartLocker
            </a>
        </div>
        <div class="sidebar-wrapper">
            <ul class="nav">
                <li class="active">
                    <a href="./table.html">
                        <i class="material-icons">content_paste</i>
                        <p>Locker List</p>
                    </a>
                </li>
                <li>
                    <a href="./icons.html">
                        <i class="material-icons">bubble_chart</i>
                        <p>Historical Data</p>
                    </a>
                </li>
                <li>
                    <a href="./user.html">
                        <i class="material-icons">person</i>
                        <p>User Profile</p>
                    </a>
                </li>
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
                    <a class="navbar-brand" href="#"></a>
                </div>
                <div class="collapse navbar-collapse">
                    <ul class="nav navbar-nav navbar-right">

                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                <i class="material-icons">person</i>
                                <%--<span class="notification">5</span>--%>
                                <p class="hidden-lg hidden-md">Profile</p>
                            </a>
                            <ul class="dropdown-menu">
                                <li>
                                    <a href="#">Logout</a>
                                </li>
                                <li>
                                    <a href="#">Account Settings</a>
                                </li>
                            </ul>
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
                    <div class="col-md-12">
                        <div class="card card-plain">
                            <div class="card-header" data-background-color="purple">
                                <h4 class="title">Lockers enrolled</h4>
                                <p class="category">Below are the list of lockers enrolled with the server</p>
                                <button class="btn btn-white">Add Locker</button>
                            </div>
                            <div class="card-content table-responsive">
                                <table class="table table-hover">
                                    <thead>
                                    <th>Device ID</th>
                                    <th>Device Name</th>
                                    <th>Owner</th>
                                    <th>Token</th>
                                    <th></th>
                                    <th></th>
                                    </thead>
                                    <tbody>
                                    <tr>
                                        <td>l1</td>
                                        <td>Locker 1</td>
                                        <td>John</td>
                                        <td>1234</td>
                                        <td><button class="btn btn-primary btn-fab btn-fab-mini btn-round">
                                            <i class="material-icons">refresh</i>
                                        </button></td>
                                        <td><button class="btn btn-info">Analytics</button></td>
                                    </tr>
                                    <tr>
                                        <td>l2</td>
                                        <td>Locker 2</td>
                                        <td>Adam</td>
                                        <td>4567</td>
                                        <td><button class="btn btn-primary btn-fab btn-fab-mini btn-round">
                                            <i class="material-icons">refresh</i>
                                        </button></td>
                                        <td><button class="btn btn-info">Analytics</button></td>
                                    </tr>
                                    <tr>
                                        <td>l3</td>
                                        <td>Locker 3</td>
                                        <td>Sarah</td>
                                        <td>8901</td>
                                        <td><button class="btn btn-primary btn-fab btn-fab-mini btn-round">
                                            <i class="material-icons">refresh</i>
                                        </button></td>
                                        <td><button class="btn btn-info">Analytics</button></td>
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
    $(document).ready(function() {

        // Javascript method's body can be found in assets/js/demos.js
        demo.initDashboardPageCharts();

    });
</script>

</html>
