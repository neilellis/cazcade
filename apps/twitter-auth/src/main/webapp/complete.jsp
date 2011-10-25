<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta content="text/html; charset=utf-8" http-equiv="Content-Type"/>
    <title>Twitter Credentials Added</title>
</head>
<body bgcolor="#EFF0F3">
<%--<h3>Twitter account ${twitter.screenName} added to Cazcade.</h3>--%>
<input type="hidden" id="cazcade_status" name="cazcade_status" value="success"/>
<input type="hidden" name="cazcade_alias" id="cazcade_alias" value="alias:twitter:${twitter.screenName}"/>
</body>
</html>

