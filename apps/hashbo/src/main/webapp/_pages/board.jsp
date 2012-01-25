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


    <!-- /container -->
    <span id="sharethisbutton" style="visibility: hidden">
        <%--<span class='st_twitter'></span>--%>
        <span class='st_sharethis_hcount'></span>
        <%--<span class='st_facebook_large'></span>--%>
        <%--<span class='st_reddit_large'></span>--%>
        <%--<span class='st_plusone'></span>--%>
    </span>


</div>


<script type="text/javascript" language="javascript"
        src="_boardcast_gwt/_boardcast_gwt.nocache.js"></script>

<script>
    var options = { "publisher":"c2dcef1f-34f2-4795-b2c4-d55b9ff9528d", "logo":{ "visible":false, "url":"", "img":"http://sd.sharethis.com/disc/images/demo_logo.png", "height":45}, "ad":{ "visible":false, "openDelay":"10", "closeDelay":"0"}, "livestream":{ "domain":"boardcast.it", "type":"sharethis", "src":""}, "ticker":{ "visible":false, "domain":"", "title":"Most Shared", "type":"sharethis", "src":""}, "facebook":{ "visible":true, "profile":"boardcast"}, "fblike":{ "visible":true, "url":""}, "twitter":{ "visible":true, "user":"boardcast_it"}, "twfollow":{ "visible":true, "url":"http://twitter.com/boardcast_it"}, "custom":[
        { "visible":false, "title":"Popular", "url":"http://boardcast.it/_query-popular", "img":"", "popup":false, "popupCustom":{ "width":300, "height":250}},
        { "visible":false, "title":"Custom 2", "url":"", "img":"", "popup":false, "popupCustom":{ "width":300, "height":250}},
        { "visible":false, "title":"Custom 3", "url":"", "img":"", "popup":false, "popupCustom":{ "width":300, "height":250}}
    ]};
    var st_bar_widget = new sharethis.widgets.sharebar(options);
</script>
</body>
</html>