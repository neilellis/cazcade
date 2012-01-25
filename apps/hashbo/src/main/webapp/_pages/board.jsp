<%@page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html lang="en">
<head>
    <meta charset="utf-8">

    <title>Boardcast: Create and Publish Instantly</title>
    <%@ include file="navfunc.jspf" %>
    <%@ include file="header.jspf" %>
</head>

<body>


<%@ include file="chrome_frame.jspf" %>

<div style="opacity: 0" id="top-bar-wrapper">
    <div class="right rib-holder">
        <div class="orange rib"><a href="http://en.wikipedia.org/wiki/Software_release_life_cycle#Alpha">Alpha</a></div>
    </div>
    <%@ include file="topbar.jspf" %>
</div>

<div class="inner-body">


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

    <div id="board-panel">
    </div>

    <%@ include file="footer.jspf" %>


    <%--<!-- /container -->--%>
    <%--<span id="sharethisbutton" style="visibility: hidden">--%>
    <%--&lt;%&ndash;<span class='st_twitter'></span>&ndash;%&gt;--%>
    <%--<span class='st_sharethis_hcount'></span>--%>
    <%--&lt;%&ndash;<span class='st_facebook_large'></span>&ndash;%&gt;--%>
    <%--&lt;%&ndash;<span class='st_reddit_large'></span>&ndash;%&gt;--%>
    <%--&lt;%&ndash;<span class='st_plusone'></span>&ndash;%&gt;--%>
    <%--</span>--%>


</div>


<script type="text/javascript" language="javascript"
        src="_boardcast_gwt/_boardcast_gwt.nocache.js"></script>
    <span id="sharethis">
<%@ include file="bottom-toolbar.jspf" %>
</span>
</body>
</html>