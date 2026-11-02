<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.testpilot.util.Html" %>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Login - TestPilot AI</title></head>
<body>
<%@ include file="header.jspf" %>
<div class="card">
  <h2>Login</h2>
  <% if ("1".equals(request.getParameter("registered"))) { %>
    <div class="success">Registration successful. Please log in.</div>
  <% } %>
  <% if (request.getAttribute("error") != null) { %>
    <div class="error"><%= Html.esc(request.getAttribute("error")) %></div>
  <% } %>
  <form method="post" action="<%= request.getContextPath() %>/login">
    <label for="email">Email</label>
    <input type="email" id="email" name="email" value="<%= Html.esc(request.getAttribute("email")) %>" required>
    <label for="password">Password</label>
    <input type="password" id="password" name="password" required>
    <button type="submit">Login</button>
  </form>
  <p class="muted">No account? <a href="<%= request.getContextPath() %>/register">Register here</a></p>
</div>
</body>
</html>
