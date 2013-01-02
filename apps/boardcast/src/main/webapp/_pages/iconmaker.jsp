<%@page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="board" scope="request" type="java.util.Map"/>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Boardcast Icon Snapshot</title>
    <style type="text/css">
        body {
            height: 768px;
            width: 1024px;
            background-color: #111;
        }
    </style>
</head>
<body>
<c:if test="${not empty board.imageUrl}">
    <img src="<c:url value="/_image-scale">
    <c:param name="url" value="${board.imageUrl}"/>
    <%--<c:param name="size" value="LARGE"/>--%>
    <c:param name="width" value="1024"/>
    <c:param name="height" value="768"/>
</c:url>" style="position:absolute; width:1024px;height:768px;z-index:-1; display:block"/>
</c:if>
<div style="font-family: 'Helvetica Neue', Helvetica, sans-serif; font-size:96px; width:1024px;min-height:768px;height:768px; display:table-cell; vertical-align: middle; font-weight: bold; text-align: center; color:white; opacity: 0.95; margin: 0 auto; text-shadow: 4px 4px 2px rgba(0, 0, 0, 0.3)">${board.title}</div>

</body>
</html>