<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="cazcade.liquid.api.lsd.LSDAttribute" %>
<%@ page import="cazcade.liquid.api.lsd.LSDDictionaryTypes" %>
<%@ page import="java.util.Arrays" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>

    <meta name="viewport" content="width = device-width">
    <title>Server Dictionary</title>
    <style type="text/css">
        .keys thead tr td {
            font-size: larger;
            text-decoration: underline;
        }

        .types thead tr td {
            font-size: larger;
            text-decoration: underline;
        }
    </style>
</head>
<body>


<%
    LSDAttribute[] keys = LSDAttribute.values();
    Arrays.sort(keys);
    request.setAttribute("keys", keys);
    request.setAttribute("types", LSDDictionaryTypes.values());
%>

<c:out value="${requestScope.identities}"/>

<h1>Keys</h1>

<div class="mainColumn">

    <table class="keys">
        <thead>
        <tr>
            <td>
                Key
            </td>
            <td>
                Format
            </td>
            <td>
                Description
            </td>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="key" items="${keys}">
            <tr>
                <td>
                    <c:out value='${key.keyName}'/>
                </td>
                <td>
                    <c:out value='${key.formatValidationString}'/>
                </td>
                <td>
                    <c:out value='${key.description}'/>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

</div>

<h1>Types</h1>

<div class="mainColumn">

    <table class="types">
        <thead>
        <tr>
            <td>
                Value
            </td>
            <td>
                Description
            </td>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="type" items="${types}">
            <tr>
                <td>
                    <c:out value='${type.value}'/>
                </td>
                <td>
                    <c:out value='${type.description}'/>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

</div>
</body>
</html>
