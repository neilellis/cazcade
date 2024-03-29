<%@page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%--
  ~ Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
  --%>

<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Boardcast Snapshot</title>
    <%@ include file="navfunc.jspf" %>
    <%@ include file="header.jspf" %>
</head>
<body
        style="padding-top:0px;max-width:1024px;max-height:1365px;height:auto;min-height:636px;background-color:white;overflow:hidden;">
<tags:log-panel/>


<div id="cache-panel">
    <div id="loading-panel">
        <div class="loading-bar-outer">
            <tags:loading-bar/>
        </div>
    </div>
</div>

<div id="snapshot-panel">
</div>

<%@ include file="footer.jspf" %>

<%@ include file="libs.jspf" %>

<script type="text/javascript" language="javascript"
        src="_boardcast_gwt/_boardcast_gwt.nocache.js"></script>


<%--&lt;%&ndash;//This may look insane but it's for a reason! It's to delayAsync the taking of a snapshot until all GWT has finished.&ndash;%&gt;--%>
<%--<img width="0" height="0" border="0" src="/_pages/delayAsync.jsp"/>--%>
</body>
</html>