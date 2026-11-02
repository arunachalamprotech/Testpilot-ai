<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session.getAttribute("user") != null) {
        response.sendRedirect(request.getContextPath() + "/dashboard");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>TestPilot AI</title></head>
<body>
<%@ include file="/WEB-INF/views/header.jspf" %>
<div class="card" style="max-width:640px">
  <h2>TestPilot AI</h2>
  <p>Intelligent Software Testing &amp; Bug Prediction Platform</p>
  <p>
    <a href="<%= request.getContextPath() %>/login">Login</a> |
    <a href="<%= request.getContextPath() %>/register">Register</a> |
    <a href="<%= request.getContextPath() %>/dbtest">Check database connection</a>
  </p>
</div>
</body>
</html>
