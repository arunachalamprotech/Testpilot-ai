<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.testpilot.model.Requirement" %>
<%@ page import="com.testpilot.model.TestCase" %>
<%@ page import="com.testpilot.util.Html" %>
<%@ page import="com.testpilot.util.Csrf" %>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Test cases - TestPilot AI</title></head>
<body>
<%@ include file="header.jspf" %>
<%
    Requirement requirement = (Requirement) request.getAttribute("requirement");
    @SuppressWarnings("unchecked")
    List<TestCase> testCases = (List<TestCase>) request.getAttribute("testCases");
    String msg = request.getParameter("msg");
    String csrf = Csrf.token(session);
    String ctx = request.getContextPath();
%>
<div class="wrap">
  <div class="req-box">
    <h3>Requirement #<%= requirement.getId() %>: <%= Html.esc(requirement.getTitle()) %>
      <span class="badge badge-<%= Html.esc(requirement.getPriority()) %>"><%= Html.esc(requirement.getPriority()) %></span>
    </h3>
    <div class="muted pre"><%= Html.esc(requirement.getDescription()) %></div>
  </div>

  <div class="toolbar">
    <h2>Test cases</h2>
    <div>
      <a class="btn btn-grey" href="<%= ctx %>/requirements">&larr; Requirements</a>
      <a class="btn" href="<%= ctx %>/testcases?requirementId=<%= requirement.getId() %>&action=new">+ Add manually</a>
      <form method="post" action="<%= ctx %>/testcases"
            onsubmit="var b=this.querySelector('button'); b.disabled=true; b.textContent='Generating... please wait (up to 2 minutes)';">
        <input type="hidden" name="action" value="generate">
        <input type="hidden" name="requirementId" value="<%= requirement.getId() %>">
        <input type="hidden" name="csrf" value="<%= Html.esc(csrf) %>">
        <button type="submit" class="btn-ai">Generate with AI</button>
      </form>
    </div>
  </div>

  <% if ("saved".equals(msg)) { %><div class="success">Test case saved.</div><% } %>
  <% if ("deleted".equals(msg)) { %><div class="success">Test case deleted.</div><% } %>
  <% if ("aisaved".equals(msg)) { %><div class="success">Selected AI test cases saved.</div><% } %>
  <% if (request.getAttribute("error") != null) { %>
    <div class="error"><%= Html.esc(request.getAttribute("error")) %></div>
  <% } %>

  <div class="table-box">
    <table>
      <thead>
        <tr><th>ID</th><th>Title</th><th>Steps</th><th>Expected result</th><th>Source</th><th>Actions</th></tr>
      </thead>
      <tbody>
      <% if (testCases == null || testCases.isEmpty()) { %>
        <tr><td colspan="6">No test cases yet. Add one manually or click "Generate with AI".</td></tr>
      <% } else { for (TestCase tc : testCases) { %>
        <tr>
          <td><%= tc.getId() %></td>
          <td><strong><%= Html.esc(tc.getTitle()) %></strong></td>
          <td><div class="pre"><%= Html.esc(tc.getSteps()) %></div></td>
          <td><div class="pre"><%= Html.esc(tc.getExpectedResult()) %></div></td>
          <td><span class="badge badge-<%= Html.esc(tc.getSource()) %>"><%= Html.esc(tc.getSource()) %></span></td>
          <td>
            <a class="btn btn-small" href="<%= ctx %>/testcases?requirementId=<%= requirement.getId() %>&action=edit&id=<%= tc.getId() %>">Edit</a>
            <form method="post" action="<%= ctx %>/testcases" onsubmit="return confirm('Delete this test case?');">
              <input type="hidden" name="action" value="delete">
              <input type="hidden" name="id" value="<%= tc.getId() %>">
              <input type="hidden" name="requirementId" value="<%= requirement.getId() %>">
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
