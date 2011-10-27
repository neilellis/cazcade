<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Boardcast</title>

    <script type="text/javascript">
        var _nav;
        var testNoPushState= true;
        if (typeof(window.history.pushState) == "function" && !testNoPushState) {
            _nav = function(href) {
                window.history.pushState(href, window.document.title, "/" + href);
                fireGWTHistoryEvent(href);
            };
        } else {
            _nav = function(href) {
                window.location.href = window.location.href="./"+href;
            };
        }


    </script>

    <%@ include file="header.jspf" %>

</head>

<body>

<div style="opacity: 0" id="top-bar-wrapper">
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


    <!-- /container -->
    <span id="sharethis" style="visibility: hidden">
        <span class='st_twitter'></span>
        <span class='st_email'></span>
        <span class='st_facebook'></span>
        <span class='st_linkedin'></span>
        <span class='st_reddit'></span>
        <%--<span class='st_plusone'></span>--%>
    </span>

</div>

<script type="text/javascript" language="javascript"
        src="_boardcast_gwt/_boardcast_gwt.nocache.js"></script>

</body>
</html>