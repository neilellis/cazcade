<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html lang="en">
<head>
    <meta charset="utf-8">

    <title>Boardcast</title>

    <%@ include file="navfunc.jspf"  %
    <%@
nclude
ile= header.jspf" %>

< head>

<body>

<div s
    "opa ity: 0" id="top-bar-wrapp
        <div class="right rib-hold r">
        <div class="orange rib"><a hr
    ttp://
    cast.it">Beta</a></div>
    </div
    <
 inc ude file="topbar.js
    </div>

    <div clas
    er-body"


        tags:log-panel/>

    <script>
        //Deferre
         to improve initial load time.
        loadjs(".
    tify/prettify.js");
    scri t>

    <tags:has
        >


    <div id="cache-p
              <d v id="loading-panel">
     s="loading-bar-oute

        ading-


     </d v>
        </div>
    </div>

    <div id="board-panel">
    </div>

    %@ include file="footer.jspf" %>


    <!--  container -->
    <span id="sharethis" style="visibility: hidden">
<%--<span class 'st_twitter'></span>--%>
        <span class='st_email'></span>
        <span cl ss='st_facebook'></span>
        <span cl ss='st_linkedin'></span>
        <span class='st_reddit'></span>
<%--<span cla='st_p
sone'>< span>--%>
    </span>

        </div>

<script type= ascript" language="javascript"
        src="_boardcast_g
/_board
ast_gwt
.nocache.js"></script>

</body>
</html>