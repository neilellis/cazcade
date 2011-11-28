<%@page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
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
<body style="padding-top:0px;">
<tags:log-panel/>

<script>
    //Deferred loading to improve initial load time.
    loadjs("./_prettify/prettify.js");
</script>

<tags:hashbo-less/>


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

<script type="text/javascript" language="javascript"
        src="_boardcast_gwt/_boardcast_gwt.nocache.js"></script>

</body>
</html>