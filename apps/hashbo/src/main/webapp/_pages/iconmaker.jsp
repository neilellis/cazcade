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
        }
    </style>
</head>
<body
        <c:if test="${board.listed eq 'true' and not empty board.imageUrl}">
            style="background-image: url('<c:url value="_image-service">
            <c:param name="url" value="${board.imageUrl}"/>
            <c:param name="size" value="LARGE"/>
            <c:param name="width" value="1024"/>
            <c:param name="height" value="768"/>
        </c:url>')"
        </c:if>
        >
<div style="font-family: 'Helvetica Neue', Helvetica, sans-serif; font-size:96px; width:1024px;min-height:768px;height:768px; display:table-cell; vertical-align: middle; text-align: center; color:white; opacity:0.9; margin: 0 auto">${board.title}</div>

</body>
</html>