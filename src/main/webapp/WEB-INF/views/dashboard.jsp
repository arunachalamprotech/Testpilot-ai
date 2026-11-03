<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.testpilot.util.Html" %>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Dashboard - TestPilot AI</title></head>
<body>
<%@ include file="header.jspf" %>
<div class="wrap">
  <h2>Welcome, <%= Html.esc(navUser.getName()) %></h2>
  <p>You are logged in as <strong><%= Html.esc(navUser.getRole()) %></strong>.</p>
  <div class="grid">
    <div class="tile">
      <h3>Requirements</h3>
      <div class="count"><%= Html.esc(request.getAttribute("reqCount")) %></div>
      <a href="<%= request.getContextPath() %>/requirements">Open requirements</a>
    </div>
    <div class="tile">
      <h3>Test Cases</h3>
      <div class="count"><%= Html.esc(request.getAttribute("tcCount")) %></div>
      <a href="<%= request.getContextPath() %>/requirements">Manage from a requirement</a>
    </div>
    <div class="tile"><h3>Bugs</h3><p>Coming soon</p></div>
    <div class="tile"><h3>Test Runs</h3><p>Coming soon</p></div>
  </div>
</div>
</body>
</html>
