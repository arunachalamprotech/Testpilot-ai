<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.testpilot.util.Html" %>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Register - TestPilot AI</title></head>
<body>
<%@ include file="header.jspf" %>
<div class="card">
  <h2>Create account</h2>
  <% if (request.getAttribute("error") != null) { %>
    <div class="error"><%= Html.esc(request.getAttribute("error")) %></div>
  <% } %>
  <form method="post" action="<%= request.getContextPath() %>/register">
    <label for="name">Full name</label>
    <input type="text" id="name" name="name" value="<%= Html.esc(request.getAttribute("name")) %>" required>
    <label for="email">Email</label>
    <input type="email" id="email" name="email" value="<%= Html.esc(request.getAttribute("email")) %>" required>
    <label for="password">Password (minimum 8 characters)</label>
    <input type="password" id="password" name="password" required>
    <label for="confirm">Confirm password</label>
    <input type="password" id="confirm" name="confirm" required>
    <button type="submit">Register</button>
  </form>
  <p class="muted">Already registered? <a href="<%= request.getContextPath() %>/login">Login</a></p>
</div>
</body>
</html>
