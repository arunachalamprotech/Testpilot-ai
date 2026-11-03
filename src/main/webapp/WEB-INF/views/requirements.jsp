<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.testpilot.model.Requirement" %>
<%@ page import="com.testpilot.util.Html" %>
<%@ page import="com.testpilot.util.Csrf" %>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Requirements - TestPilot AI</title></head>
<body>
<%@ include file="header.jspf" %>
<%
    @SuppressWarnings("unchecked")
    List<Requirement> requirements = (List<Requirement>) request.getAttribute("requirements");
    String msg = request.getParameter("msg");
    String csrf = Csrf.token(session);
    String ctx = request.getContextPath();
%>
<div class="wrap">
  <div class="toolbar">
    <h2>Requirements</h2>
    <a class="btn" href="<%= ctx %>/requirements?action=new">+ Add requirement</a>
  </div>

  <% if ("saved".equals(msg)) { %><div class="success">Requirement saved.</div><% } %>
  <% if ("deleted".equals(msg)) { %><div class="success">Requirement deleted.</div><% } %>
  <% if (request.getAttribute("error") != null) { %>
    <div class="error"><%= Html.esc(request.getAttribute("error")) %></div>
  <% } %>

  <div class="table-box">
    <table>
      <thead>
        <tr><th>ID</th><th>Title</th><th>Priority</th><th>Created by</th><th>Created</th><th>Actions</th></tr>
      </thead>
      <tbody>
      <% if (requirements == null || requirements.isEmpty()) { %>
        <tr><td colspan="6">No requirements yet. Click "Add requirement" to create the first one.</td></tr>
      <% } else { for (Requirement r : requirements) {
            String created = r.getCreatedAt() == null ? "" : r.getCreatedAt().toString();
            if (created.length() > 16) { created = created.substring(0, 16); }
      %>
        <tr>
          <td><%= r.getId() %></td>
          <td>
            <strong><%= Html.esc(r.getTitle()) %></strong>
            <% if (r.getDescription() != null && !r.getDescription().isEmpty()) { %>
              <div class="muted"><%= Html.esc(r.getDescription()) %></div>
            <% } %>
          </td>
          <td><span class="badge badge-<%= Html.esc(r.getPriority()) %>"><%= Html.esc(r.getPriority()) %></span></td>
          <td><%= Html.esc(r.getCreatedByName()) %></td>
          <td><%= Html.esc(created) %></td>
          <td>
            <a class="btn btn-small" href="<%= ctx %>/requirements?action=edit&id=<%= r.getId() %>">Edit</a>
            <form method="post" action="<%= ctx %>/requirements" onsubmit="return confirm('Delete this requirement?');">
              <input type="hidden" name="action" value="delete">
              <input type="hidden" name="id" value="<%= r.getId() %>">
              <input type="hidden" name="csrf" value="<%= Html.esc(csrf) %>">
              <button type="submit" class="btn-small btn-danger">Delete</button>
            </form>
          </td>
        </tr>
      <% } } %>
      </tbody>
    </table>
  </div>
</div>
</body>
</html>
