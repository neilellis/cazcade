<!DOCTYPE HTML>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <meta content="text/html; charset=utf-8" http-equiv="Content-Type"/>
    <title>Link Twitter to Boardcast Account</title>
    <link rel="stylesheet" href="../_css/bootstrap/bootstrap-full.less">
</head>
<body>
<div class="container" style="width:650px;padding-top:20px  ">
    <section id="forms">
        <div class="row"></div>
        <c:if test="${not empty param.message}">
            <div class="alert-message error">${param.message}</div>
        </c:if>

        <div class="page-header">
            <h1>Link Twitter Account</h1>
        </div>
        <div class="row">
            <div class="span4">
                <p>Please sign into Boardcast to link your Twitter account to your existing Boardcast account, alternatively
                    click <a href="./signin">here</a> to create a new account.</p>
            </div>
            <div class="span4">
                <form action="./link" method="POST">
                    <fieldset style="border:none;">
                        <div class="clearfix">
                            <label for="xlInput">Boardcast Username</label>

                            <div class="input">
                                <input type="text" name="username" id="username"
                                       value="${param.username}"/>
                            </div>
                        </div>
                        <!-- /clearfix -->

                        <div class="clearfix">
                            <label for="xlInput">Password</label>

                            <div class="input">
                                <input type="password" name="password" id="password"
                                       value=""/>
                            </div>
                        </div>
                        <div class="input">
                            <input type="submit" name="post" value="Done" class="btn primary"/>
                            <a href="./signin" class="btn">Cancel</a>
                        </div>
                    </fieldset>
                </form>
            </div>
        </div>
    </section>
</div>

</body>
</html>

