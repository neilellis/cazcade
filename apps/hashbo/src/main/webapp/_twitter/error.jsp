<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.util.Enumeration" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%
    response.setStatus(500);
%>
%>

<%@ page isErrorPage="true" %>
<%@ page language="java" %>
<%
    final Object statusCode = request.getAttribute("javax.servlet.error.status_code");
    final Object exceptionType = request.getAttribute("javax.servlet.error.exception_type");
    final Object message = request.getAttribute("javax.servlet.error.message");
%>

<html>
<head>
    <title>Error Page</title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
<body bgcolor="#FFFFFF">
<P><A href="<%=response.encodeURL(request.getContextPath()) %>">Home</A></P>
<hr>
<H2>An Error has occured:</H2>

<TABLE CELLPADDING="2" CELLSPACING="2" BORDER="1" WIDTH="100%">
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

<hr>
Header List
<table border=3>
    <tr>
        <td>Name</td>
        <td>Value</td>
    </tr>
    <%
        String name = "";
        String value = "";

        final Enumeration headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            name = (String) headers.nextElement();
            value = request.getHeader(name);
    %>
    <tr>
        <td><%=name%>
        </td>
        <td><%=value%>
        </td>
    </tr>
    <%
        }
    %>
</table>

Attribute List
<!-- "javax.servlet.jsp.jspException" for getting an Exception -->
<table border=3>
    <%
        final Enumeration attributes = request.getAttributeNames();
        while (attributes.hasMoreElements()) {
            name = (String) attributes.nextElement();

            if (request.getAttribute(name) == null) {
                value = "null";
            } else {
                value = request.getAttribute(name).toString();
            }
    %>
    <tr>
        <td><%=name%>
        </td>
        <td><%=value%>
        </td>
    </tr>
    <%
        }
    %>
</table>
<input type="hidden" name="cazcade_status" value="error"/>
</body>
</html>


