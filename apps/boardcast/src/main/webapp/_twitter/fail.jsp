<!DOCTYPE HTML>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <meta content="text/html; charset=utf-8" http-equiv="Content-Type"/>
    <title>Failed</title>
    <link rel="stylesheet" type="text/less" href="../_less/bootstrap/bootstrap-full.less">
    <script src="../_js/less-dev.js" type="text/javascript"></script>

</head>
<body>
<div class="container" style="width:650px;padding-top:20px">
    <section id="forms">
        <div class="row"></div>
        <c:if test="${not empty param.message}">
            <div class="alert-message error">${param.message}</div>
        </c:if>

        <div class="page-header">
            <h1>Twitter Registration Failed</h1>
        </div>
        <div class="row">
            <div class="span8">
                <p>Unfortunately there was a problem signing you in with Twitter, you could <a href="./signin">try
                    again</a> or <a href="../login.html">sign in</a> with us.</p>
            </div>
        </div>
    </section>
</div>

</body>
</html>


