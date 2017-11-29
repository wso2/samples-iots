<%@ page import="org.wso2.iot.locker.portal.LoginController" %>
<%@ page import="java.net.URLEncoder" %>
<%
    String requestUri = request.getRequestURI();
    String queryString = request.getQueryString();

    if (!("login.jsp").equals(requestUri)) {
        if (request.getSession(false) == null) {
            if (queryString != null && !queryString.isEmpty()) {
                response.sendRedirect("login.jsp?ret=" + requestUri + "&q=" + URLEncoder
                        .encode(request.getQueryString(), "UTF-8"));
            } else {
                response.sendRedirect("login.jsp?ret=" + requestUri);
            }
        }
        Object token = request.getSession(false).getAttribute(LoginController.ATTR_ACCESS_TOKEN);
        if (token == null) {
            if (queryString != null && !queryString.isEmpty()) {
                response.sendRedirect("login.jsp?ret=" + requestUri + "&q=" + URLEncoder
                        .encode(request.getQueryString(), "UTF-8"));
            } else {
                response.sendRedirect("login.jsp?ret=" + requestUri);
            }
        }
    }
%>