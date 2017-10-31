<%--
  Created by IntelliJ IDEA.
  User: nuwan
  Date: 10/31/17
  Time: 10:40 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login</title>

    <link href="libs/bootstrap/css/bootstrap.min.css" rel="stylesheet"/>
</head>
<body>

<div class="jumbotron" style="">
    <h1>Smart Locker portal</h1>
    <p>Enter Credentials to login</p>
    <%--<p><a class="btn btn-primary btn-lg">Learn more</a></p>--%>

    <form class="form-horizontal">
        <fieldset>
            <div class="form-group">
                <label for="inputEmail" class="col-lg-2 control-label">Email</label>
                <div class="col-lg-10">
                    <input type="text" class="form-control" id="inputEmail" placeholder="Email">
                </div>
            </div>
            <div class="form-group">
                <label for="inputPassword" class="col-lg-2 control-label">Password</label>
                <div class="col-lg-10">
                    <input type="password" class="form-control" id="inputPassword" placeholder="Password">
                </div>
            </div>
            <div class="form-group">
                <div class="col-lg-10 col-lg-offset-2">
                    <button type="reset" class="btn btn-default">Cancel</button>
                    <button type="submit" class="btn btn-primary">Submit</button>
                </div>
            </div>
        </fieldset>
    </form>
</div>
</body>
</html>
