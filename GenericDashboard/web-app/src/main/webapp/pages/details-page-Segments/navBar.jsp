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
            </strong> Device Statistics
            <ul class="nav navbar-nav navbar-right">
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