<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    session.invalidate();
    String referer = request.getHeader("referer");
    String redirect = (referer == null || referer.isEmpty()) ? "/" : referer;
    response.sendRedirect(redirect);
%>