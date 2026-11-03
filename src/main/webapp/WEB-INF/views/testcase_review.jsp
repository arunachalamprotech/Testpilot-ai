<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.testpilot.model.Requirement" %>
<%@ page import="com.testpilot.model.TestCase" %>
<%@ page import="com.testpilot.util.Html" %>
<%@ page import="com.testpilot.util.Csrf" %>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Review AI test cases - TestPilot AI</title></head>
<body>
<%@ include file="header.jspf" %>
<%
    Requirement requirement = (Requirement) request.getAttribute("requirement");
    @SuppressWarnings("unchecked")
    List<TestCase> proposed = (List<TestCase>) request.getAttribute("proposed");
    String csrf = Csrf.token(session);
    String ctx = request.getContextPath();
%>
<div class="wrap">
  <div class="req-box">
    <h3>Requirement #<%= requirement.getId() %>: <%= Html.esc(requirement.getTitle()) %></h3>
  </div>

  <h2>Review AI-generated test cases</h2>
  <div class="notice">
    These test cases were written by an AI model and are <strong>not saved yet</strong>.
    AI can make mistakes, so read each one and untick the ones that are wrong or not useful.
  </div>

  <form method="post" action="<%= ctx %>/testcases">
    <input type="hidden" name="action" value="saveProposed">
    <input type="hidden" name="requirementId" value="<%= requirement.getId() %>">
    <input type="hidden" name="csrf" value="<%= Html.esc(csrf) %>">

    <div class="table-box">
      <table>
        <thead>
          <tr><th>Save?</th><th>Title</th><th>Steps</th><th>Expected result</th></tr>
        </thead>
        <tbody>
        <% for (int i = 0; i < proposed.size(); i++) { TestCase tc = proposed.get(i); %>
          <tr>
            <td><input type="checkbox" name="pick" value="<%= i %>" checked></td>
            <td><strong><%= Html.esc(tc.getTitle()) %></strong></td>
            <td><div class="pre"><%= Html.esc(tc.getSteps()) %></div></td>
            <td><div class="pre"><%= Html.esc(tc.getExpectedResult()) %></div></td>
          </tr>
        <% } %>
        </tbody>
      </table>
    </div>

    <p>
      <button type="submit">Save selected test cases</button>
    </p>
  </form>

  <form method="post" action="<%= ctx %>/testcases">
    <input type="hidden" name="action" value="discard">
    <input type="hidden" name="requirementId" value="<%= requirement.getId() %>">
    <input type="hidden" name="csrf" value="<%= Html.esc(csrf) %>">
    <button type="submit" class="btn-grey">Discard all</button>
  </form>
</div>
</body>
</html>
