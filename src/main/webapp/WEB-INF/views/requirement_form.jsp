<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.testpilot.model.Requirement" %>
<%@ page import="com.testpilot.util.Html" %>
<%@ page import="com.testpilot.util.Csrf" %>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Requirement - TestPilot AI</title></head>
<body>
<%@ include file="header.jspf" %>
<%
    Requirement r = (Requirement) request.getAttribute("requirement");
    boolean editing = (r != null && r.getId() > 0);
    String title = (r == null) ? "" : r.getTitle();
    String description = (r == null) ? "" : r.getDescription();
    String priority = (r == null || r.getPriority() == null || r.getPriority().isEmpty()) ? "MEDIUM" : r.getPriority();
%>
<div class="card wide">
  <h2><%= editing ? "Edit requirement" : "Add requirement" %></h2>
  <% if (request.getAttribute("error") != null) { %>
    <div class="error"><%= Html.esc(request.getAttribute("error")) %></div>
  <% } %>
  <form method="post" action="<%= request.getContextPath() %>/requirements">
    <input type="hidden" name="action" value="save">
    <input type="hidden" name="csrf" value="<%= Html.esc(Csrf.token(session)) %>">
    <% if (editing) { %><input type="hidden" name="id" value="<%= r.getId() %>"><% } %>

    <label for="title">Title</label>
    <input type="text" id="title" name="title" maxlength="200" value="<%= Html.esc(title) %>" required>

    <label for="description">Description</label>
    <textarea id="description" name="description" maxlength="5000"><%= Html.esc(description) %></textarea>

    <label for="priority">Priority</label>
    <select id="priority" name="priority">
      <option value="LOW"    <%= "LOW".equals(priority)    ? "selected" : "" %>>Low</option>
      <option value="MEDIUM" <%= "MEDIUM".equals(priority) ? "selected" : "" %>>Medium</option>
      <option value="HIGH"   <%= "HIGH".equals(priority)   ? "selected" : "" %>>High</option>
    </select>

    <button type="submit">Save</button>
  </form>
  <p class="muted"><a href="<%= request.getContextPath() %>/requirements">Back to requirements</a></p>
</div>
</body>
</html>
