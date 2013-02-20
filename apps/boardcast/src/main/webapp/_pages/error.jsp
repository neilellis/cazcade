<%@ page import="java.io.PrintWriter" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%--
  ~ Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
  --%>

<%
    response.setStatus(500);
%>
<%@ page isErrorPage="true" %>
<%@ page language="java" %>
<%
    final Object statusCode = request.getAttribute("javax.servlet.error.status_code");
    final Object exceptionType = request.getAttribute("javax.servlet.error.exception_type");
    final Object message = request.getAttribute("javax.servlet.error.message");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
"http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <%--<link rel="stylesheet" type="text/css" href="style.css">--%>
    <link href='http://fonts.googleapis.com/css?family=Annie+Use+Your+Telescope' rel='stylesheet' type='text/css'>

</head>
<body id="errorpage" style="background-color:rgb(32.6%, 50.8%, 19.7%)">
<div style="width:100%;height:100%;position:absolute;top:0;left:0">
    <div style="text-align:center;margin:20px;;">
        <span style="color:white;font-size: 100px;font-family:'Annie Use Your Telescope', Arial, sans-serif;text-shadow: 0px 0px 6px black;">I'm sorry, we didn't see that coming!</span>
        <img src='/_static/_images/error.jpg' style="width:100%;z-index:-10;margin: 0 auto;display: block;margin-top: 14px"/>
        <a href="/"
           style="color:white;font-size: 100px; font-family:'Annie Use Your Telescope', Arial, sans-serif;text-shadow: 0px 0px 6px black;">Home</a>

    </div>

</div>


<TABLE CELLPADDING="2" CELLSPACING="2" BORDER="1" WIDTH="100%" style="visibility: hidden">
    <TR>
        <TD WIDTH="20%"><B>Status Code</B></TD>
        <TD WIDTH="80%"><%= statusCode %>
        </TD>
    </TR>
    <TR>
        <TD WIDTH="20%"><B>Exception Type</B></TD>
        <TD WIDTH="80%"><%= exceptionType %>
        </TD>
    </TR>
    <TR>
        <TD WIDTH="20%"><B>Message</B></TD>
        <TD WIDTH="80%"><%= message %>
        </TD>
    </TR>
    <TR>
        <TD WIDTH="20%"><B>Exception</B></TD>
        <TD WIDTH="80%">
            <%
                if (exception != null) {
                    out.print("<PRE>");
                    exception.printStackTrace(new PrintWriter(out));
                    out.print("</PRE>");
                }
            %>
        </TD>
    </TR>
    <TR>
        <TD WIDTH="20%"><B>Root Cause</B></TD>
        <TD>
            <%
                if (exception != null && exception instanceof ServletException) {
                    final Throwable cause = ((ServletException) exception).getRootCause();
                    if (cause != null) {
                        out.print("<PRE>");
                        cause.printStackTrace(new PrintWriter(out));
                        out.print("</PRE>");
                    }
                }
            %>
        </TD>
    </TR>
</TABLE>

</body>
<input type="hidden" name="cazcade_status" value="error"/>

</html>