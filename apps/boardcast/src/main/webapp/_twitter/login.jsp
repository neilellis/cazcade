<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
  --%>

<html>
<head>
    <meta content="text/html; charset=utf-8" http-equiv="Content-Type"/>
    <title>Login Successful</title>
    <link rel="stylesheet" href="/_static/css/boardcast-v2.css">
    <script src="/_static/_js/less-dev.js" type="text/javascript"></script>

</head>
<body bgcolor="#EFF0F3">
<script>
    //let's the parent window know we're logged in on refresh.
    window.opener.sessionStorage.setItem('boardcast.identity', '${sessionScope.sessionId}');
    window.close();
    if (window.opener && !window.opener.closed) {
        window.opener.location.reload();
    }
</script>
<input type="hidden" id="status" name="status" value="success"/>

</body>
</html>

