<%@page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="title" scope="request" type="java.lang.String"/>
<jsp:useBean id="boards" scope="request" type="java.util.List"/>
<html lang="en">
<head>
    <title>Boardcast: Create and Publish Instantly</title>
    <%@ include file="header.jspf" %>
    <link rel="stylesheet" href="./_css/hashbo.less">
    <link rel="stylesheet" href="./_css/hackabout.css">
    <script type="text/javascript">
        function resizeCenter() {
            document.getElementById('boards-list').style.width = (Math.floor(document.body.offsetWidth / 400) * 400) + "px";
        }

        window.onresize = function (event) {
            resizeCenter();
        };

        waitForStylesheetLoad(function () {
            resizeCenter();
        });

    </script>

</head>

<body class="boards-body">
<%@ include file="chrome_frame.jspf" %>

<%@ include file="topbar.jspf" %>
<div class="inner-body">

    <div class="big-text">
        ${title}
    </div>
    <div class="boards-list-container">

        <div class="boards-list" id="boards-list">
            <c:forEach var="board" items="${boards}">

                <div class="boards-list-item">
                    <div class="boards-list-item-inner">
                            <%--<a href="#"><img class="thumbnail" src="http://placehold.it/80x60" alt=""></a>--%>
                        <a href="./<c:url value='${board.shortUrl}'/>" alt="${board.description}"
                           title="${board.description}">
                            <div class="board-image-wrapper">
                                    <%--<img class="thumbnail"--%>
                                    <%--width="300"--%>
                                    <%--height="400"--%>
                                    <%--src='${board.iconUrl}' title='${board.title}'/>--%>

                                <img class="thumbnail"
                                     src='<c:url value="_image-service">
                                    <c:param name="url" value="${board.snapshotUrl}"/>
                                    <c:param name="text" value="${board.title}"/>
                                    <c:param name="size" value="LARGE"/>
                                    <c:param name="width" value="300"/>
                                    <c:param name="height" value="400"/>
                                    <c:param name="delay" value="60"/>
                                    </c:url>'
                                     alt="${board.description}"/>


                            </div>
                        </a>


                        <div class="boards-list-caption-area">
                            <!--
                              ${board}
                             -->
                            <div class="boards-list-board-title"><a
                                    href="./#<c:url value='${board.shortUrl}'/>"><c:out value="${board.title}"
                                                                                        default="#${board.title}"/></a>
                            </div>
                            <div class="boards-list-board-owner"><a href="/~${board.ownerName}">${board.ownerFn}</a>
                            </div>
                            <div class="boards-list-board-comment-count"><c:out value="${board.commentsCount}"
                                                                                default="no"/>&nbsp;comments
                            </div>
                        </div>

                    </div>
                </div>
            </c:forEach>
        </div>

    </div>


    <%@ include file="footer.jspf" %>

    <!-- /container -->
</div>

<%@ include file="bottom-toolbar.jspf" %>

</body>
</html>