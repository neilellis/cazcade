<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html lang="en">
<head>
    <meta charset="utf-8">
    title>Boardcast Snapshot
    e>
    <%@  nclude file="navfun c.
pf" %>

  <%@
clud  file="header.jspf"
    ead>
    < dy s yle=
    ng-top:0
        <tags:log-panel/>

    <script>
        //Defer
        ng to improve initial load time.
        loadjs(
    ettify/prettify.js"
    </sc ipt>

    <tags:h
        s/>




    <div id="cac
                  <div id="loading-panel">
 class="loading-bar-

        s:load
    ar/>

         </div>
        </
        </

         div id="snapshot- ">
    </div>

    <%@ include file="footer.j pf" %>

<script type= ascript" language="javascript"
        src="_boardcast_g
/_board
ast_gwt
.nocache.js"></script>

</body>
</html>