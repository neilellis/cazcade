<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Boardcast</title>
    <%@ include file="header.jspf" %>

</head>

<body>


<div style="opacity: 0" id="top-bar-wrapper">
    <%@ include file="topbar.jspf" %>
</div>

<div class="container" style="width:1000px;">
    <tags:log-panel/>

    <script>
        //Deferred loading to improve initial load time.
        loadjs("./_prettify/prettify.js");
    </script>

    <tags:hashbo-less/>

    <div id="loading-panel">
        <div class="loading-bar-outer">
            <tags:loading-bar/>
        </div>
    </div>

    <div id="cache-panel" class="content" style="opacity: 0">
        <div class="page-header">
            <h1>Board Title
                <small>Board Description</small>
            </h1>
        </div>
        <div class="row">
            <div class="span14">
                <div style="width:1014px; height:760px; background-color:#eee;border:1px solid #aaa;position:relative;">
                    <tags:loading-indicator/>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="span12">
                <h2>Comments</h2>
            </div>
            <div class="span4">
                <h3>Board Information</h3>
            </div>
        </div>
    </div>

    <div id="profile-panel">

    </div>

    <%@ include file="footer.jspf" %>


</div>

<%--<script type="text/javascript" language="javascript"--%>
        <%--src="cazcade.hashbo.HashboMain/cazcade.hashbo.HashboMain.nocache.js"></script>--%>


<!-- /container -->
<span id="sharethis" style="visibility: hidden">
 <span class='st_twitter_large' displayText='Tweet'></span><span class='st_email_large'
                                                                  displayText='Email'></span><span
        class='st_facebook_large' displayText='Facebook'></span>
    <span class='st_linkedin_large'></span>
    <span class='st_reddit_large'></span>
    <span class='st_plusone_large'></span>
    </span>
</body>
</html>