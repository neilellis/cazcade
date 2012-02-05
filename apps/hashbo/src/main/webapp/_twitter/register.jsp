<!DOCTYPE HTML>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <meta content="text/html; charset=utf-8" http-equiv="Content-Type"/>
    <title>Register with Boardcast</title>
    <link rel="stylesheet" type="text/less" href="../_css/bootstrap/bootstrap-full.less">
    <script src="../_js/less-dev.js" type="text/javascript"></script>

    <%--<link rel="stylesheet" href="../_css/boardcast-v2.less">--%>
</head>
<body>

<div class="container" style="width:650px;padding-top:20px">
    <section id="forms">
        <c:if test="${not empty param.username}">
            <div class="alert-message warning">Username ${param.username} is taken, please select another.
            </div>
        </c:if>

        <div class="page-header">
            <h1>Register</h1>
        </div>
        <div class="row">
            <div class="span4">
                <p>We have most of the information we need now to set up your Boardcast account, please just supply
                    <c:if test="${not empty param.username}">
                        a username and
                    </c:if>
                    your email address
                    . If you already have a Boardcast account and would like to use that, click <a
                            href="./link.jsp">here</a>.</p>
            </div>
            <div class="span4">
                <form action="./register" method="GET">
                    <fieldset style="border:none;">
                        <c:if test="${not empty param.username}">

                            <div class="clearfix">
                                <label for="xlInput">Username (on Boardcast)</label>

                                <div class="input">
                                    <input type="text" name="username" id="username"
                                           value="${param.username}"/>
                                </div>
                            </div>
                            <!-- /clearfix -->
                        </c:if>

                        <div class="clearfix">
                            <label for="normalSelect">Email</label>

                            <div class="input">
                                <input type="email" name="email" id="email" value="${param.email}"/>
                            </div>
                        </div>
                        <div class="input">
                            <input type="submit" name="post" value="Done" class="btn primary"/>
                        </div>
                    </fieldset>
                </form>
            </div>
        </div>
    </section>
</div>

</body>
</html>

