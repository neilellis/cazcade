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

    <title>Boardcast: The Internet's Corkboard </title>
    <%@ include file="navfunc.jspf" %>
    <%@ include file="header.jspf" %>
    <%@ include file="analytics.jspf" %>

</head>

<body>


<%@ include file="chrome_frame.jspf" %>

<div style="opacity: 0" id="top-bar-wrapper">
    <div class="right rib-holder">
        <div class="black rib"><a href="http://en.wikipedia.org/wiki/Software_release_life_cycle#Beta">Beta</a></div>
    </div>
    <%@ include file="topbar.jspf" %>
</div>

<div class="inner-body">


    <tags:log-panel/>

    <script>
        //Deferred loading to improve initial load time.
        loadjs("./_prettify/prettify.js");
    </script>


    <tags:boardcast-less/>


    <div id="cache-panel">
        <div id="loading-panel">
            <div class="loading-bar-outer">
                <tags:loading-bar/>
            </div>
        </div>
    </div>

    <div id="board-panel">
    </div>

    <%@ include file="footer.jspf" %>


    <%--<!-- /container -->--%>
    <%--<span id="sharethis" style="visibility: hidden">--%>
    <%--&lt;%&ndash;<span class='st_twitter'></span>&ndash;%&gt;--%>
    <%--<span class='st_sharethis_hcount'></span>--%>
    <%--&lt;%&ndash;<span class='st_facebook_large'></span>&ndash;%&gt;--%>
    <%--&lt;%&ndash;<span class='st_reddit_large'></span>&ndash;%&gt;--%>
    <%--&lt;%&ndash;<span class='st_plusone'></span>&ndash;%&gt;--%>
    <%--</span>--%>


</div>


<script type="text/javascript" language="javascript"
        src="_boardcast_gwt/_boardcast_gwt.nocache.js"></script>

</body>
</html>