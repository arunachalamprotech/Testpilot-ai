<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.testpilot.model.Requirement" %>
<%@ page import="com.testpilot.model.TestCase" %>
<%@ page import="com.testpilot.util.Html" %>
<%@ page import="com.testpilot.util.Csrf" %>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Test case - TestPilot AI</title></head>
<body>
<%@ include file="header.jspf" %>
<%
    Requirement requirement = (Requirement) request.getAttribute("requirement");
    TestCase tc = (TestCase) request.getAttribute("testCase");
    boolean editing = (tc != null && tc.getId() > 0);
    String title = (tc == null) ? "" : tc.getTitle();
    String steps = (tc == null) ? "" : tc.getSteps();
    String expected = (tc == null) ? "" : tc.getExpectedResult();
    String ctx = request.getContextPath();
%>
<div class="card wide">
  <h2><%= editing ? "Edit test case" : "Add test case" %></h2>
  <p class="muted">Requirement #<%= requirement.getId() %>: <%= Html.esc(requirement.getTitle()) %></p>
  <% if (request.getAttribute("error") != null) { %>
    <div class="error"><%= Html.esc(request.getAttribute("error")) %></div>
  <% } %>
  <form method="post" action="<%= ctx %>/testcases">
    <input type="hidden" name="action" value="save">
    <input type="hidden" name="requirementId" value="<%= requirement.getId() %>">
    <input type="hidden" name="csrf" value="<%= Html.esc(Csrf.token(session)) %>">
    <% if (editing) { %><input type="hidden" name="id" value="<%= tc.getId() %>"><% } %>

    <label for="title">Title</label>
    <input type="text" id="title" name="title" maxlength="200" value="<%= Html.esc(title) %>" required>

    <label for="steps">Steps (one per line)</label>
    <textarea id="steps" name="steps" maxlength="5000"><%= Html.esc(steps) %></textarea>

    <label for="expectedResult">Expected result</label>
    <textarea id="expectedResult" name="expectedResult" maxlength="5000"><%= Html.esc(expected) %></textarea>

    <button type="submit">Save</button>
  </form>
  <p class="muted"><a href="<%= ctx %>/testcases?requirementId=<%= requirement.getId() %>">Back to test cases</a></p>
</div>
</body>
</html>
