<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Boardcast</title>
    <c:if test="${not empty requestScope.board}">
        <script type="text/javascript">
            if (typeof(window.history.pushState) == "function") {
                var board= '${requestScope.board}';
                if(window.location.href.indexOf('?') > 0) {
                    board=board+"?"+window.history.substring(window.location.href.indexOf('?'));
                }
                window.history.replaceState('${requestScope.board}', '${requestScope.board}', board);
            } else {
                window.location.replace(window.location.href.substring(0, window.location.href.lastIndexOf('/'))+'#' + '${requestScope.board}');
            }


        </script>
    </c:if>
    <c:if test="${empty requestScope.board}">
    <script type="text/javascript">
        if (window.location.href.indexOf('#') < 0 && window.location.href.indexOf('?') < 0) {
            window.location.href = './site/';
        }
    </script>
    </c:if>
    <%@ include file="header.jspf" %>

</head>

<body>

<div style="opacity: 0" id="top-bar-wrapper">
    <%@ include file="topbar.jspf" %>
</div>

<div class="inner-body">

    <div class="container">
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


    </div>




    <!-- /container -->
<span id="sharethis" style="visibility: hidden">
 <span class='st_twitter_large' displayText='Tweet'></span><span class='st_email_large'
                                                                 displayText='Email'></span><span
        class='st_facebook_large' displayText='Facebook'></span>
    <span class='st_linkedin_large'></span>
    <span class='st_reddit_large'></span>
    <%--<span class='st_plusone_large'></span>--%>
    </span>
</div>

<script type="text/javascript" language="javascript"
        src="cazcade.hashbo.HashBo/cazcade.hashbo.HashBo.nocache.js"></script>

</body>
</html>