<%@page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%--
  ~ Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
  --%>

<!DOCTYPE HTML>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <meta content="text/html; charset=utf-8" http-equiv="Content-Type"/>
    <title>Change Password</title>
    <link rel="stylesheet" type="text/less" href="/_static/_less/bootstrap/bootstrap-full.less">
    <script src="//cdnjs.cloudflare.com/ajax/libs/less.js/1.3.3/less.min.js" type="text/javascript"></script>
    <%@ include file="analytics.jspf" %>

</head>
<body>
<div class="container" style="width:650px;padding-top:20px  ">
    <section id="forms">
        <div class="row"></div>
        <%@ include file="messages.jspf" %>
        <div class="page-header">
            <h1>Change Password</h1>
        </div>
        <div class="row">
            <div class="span4">
                <p></p>
            </div>
            <div class="span4">
                <form action="/_password-change" method="POST">
                    <fieldset style="border:none;">
                        <div class="clearfix">
                            <label for="xlInput">Username</label>

                            <div class="input">
                                <input type="text" name="username" id="username" readonly="true"
                                       value="${param.username}"/>
                            </div>
                        </div>
                        <!-- /clearfix -->

                        <div class="clearfix">
                            <label for="xlInput">New Password</label>

                            <div class="input">
                                <input type="password" name="password" id="password" value=""/>
                            </div>
                        </div>
                        <div class="clearfix">
                            <label for="xlInput">Confirm Password</label>

                            <div class="input">
                                <input type="password" name="password_confirm" id="password_confirm" value=""/>
                            </div>
                        </div>
                        <div class="input">
                            <input type="hidden" name="hash" id="hash" value="${param.hash}"/>
                            <input type="submit" name="post" value="Change" class="btn primary"/>
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

