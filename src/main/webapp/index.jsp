<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>TestPilot AI</title>
  <style>
    body { font-family: Arial, sans-serif; background:#f4f6f9; margin:0; }
    .box { max-width:640px; margin:80px auto; background:#fff; padding:32px; border-radius:8px; }
    h1 { margin-top:0; }
    a { color:#1a56db; }
  </style>
</head>
<body>
  <div class="box">
    <h1>TestPilot AI</h1>
    <p>Intelligent Software Testing &amp; Bug Prediction Platform</p>
    <p>The server is running.</p>
    <p><a href="<%= request.getContextPath() %>/dbtest">Check database connection</a></p>
  </div>
</body>
</html>
