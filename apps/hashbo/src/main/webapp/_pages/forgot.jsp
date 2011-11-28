<!DOCTYPE HTML>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <meta content="text/html; charset=utf-8" http-equiv="Content-Type"/>
    <title>Forgot Password</title>
    <link rel="stylesheet" type="text/less" href="/_css/bootstrap/bootstrap-full.less">
    <script src="/_js/less-1.1.3.min.js" type="text/javascript"></script>
    <%@ include file="analytics.jspf" %>

</head>
<body>
<div class="container" style="width:650px;padding-top:20px  ">
    <section id="forms">
        <div class="row"></div>
        <%@ include file="messages.jspf" %>

        <div class="page-header">
            <h1>Forgot Password</h1>
        </div>
        <div class="row">
            <div class="span4">
                <p>Just enter your username and we'll send you a link to reset your password.</p>
            </div>
            <div class="span4">
                <form action="/_password-change" method="POST">
                    <fieldset style="border:none;">
                        <div class="clearfix">
                            <label for="xlInput">Username</label>

                            <div class="input">
                                <input type="text" name="username" id="username"
                                       value="${param.username}"/>
                            </div>
                        </div>
                        <!-- /clearfix -->

                        <div class="input">
                            <input type="submit" name="post" value="Done" class="btn primary"/>
                            <%--<a href="./signin" class="btn">Cancel</a>--%>
                        </div>
                    </fieldset>
                </form>
            </div>
        </div>
    </section>
</div>

</body>
</html>

