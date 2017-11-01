<%@ page import="org.wso2.iot.locker.portal.LoginController" %><%
    if (!"/login.jsp".equals(request.getRequestURI())) {
        if (request.getSession(false) == null) {
            response.sendRedirect("/login.jsp?ret=" + request.getRequestURI());
        }
        Object token = request.getSession(false).getAttribute(LoginController.ATTR_ACCESS_TOKEN);
        if (token == null) {
            response.sendRedirect("/login.jsp?ret=" + request.getRequestURI());
        }
    }
%>