<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="title" scope="request" type="java.lang.String"/>
<jsp:useBean id="boards" scope="request" type="java.util.List"/>
<html lang="en">
<head>
    <title>Boardcast</title>
    <%@ include file="header.jspf" %>
    <link rel="stylesheet" href="./_css/hashbo.less">
    <link rel="stylesheet" href="./_css/hackabout.css">
    <script type="text/javascript">
        function resizeCenter() {
            document.getElementById('boards-list').style.width = (Math.floor(document.body.offsetWidth / 400) * 400) + "px";
        }

        window.onresize = function(event) {
            resizeCenter();
        };

        waitForStylesheetLoad(function() {
            resizeCenter();
        });

    </script>

</head>

<body class="boards-body">


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
                        <a href="./<c:url value='${board.shortUrl}'/>" alt="${board.description}" title="${board.description}">
                            <div class="board-image-wrapper">
                                <img class="thumbnail"
                                     src='image.service?url=<c:url value="${board.iconUrl}"/>&size=CLIPPED_MEDIUM&width=300&height=200'
                                     width="80"
                                     height="60" alt="" >
                            </div>
                        </a>


                        <div class="boards-list-caption-area">

                                <div class="boards-list-board-owner">${board.authorFn}</div>
                            <div class="boards-list-board-title"><a
                                    href="./#<c:url value='${board.shortUrl}'/>"><c:out value="${board.title}" default="#${board.title}"/></a></div>


                            <div class="boards-list-board-comment-count"><c:out value="${board.commentsCount}" default="no"/>&nbsp;comments</div>
                        </div>

                    </div>
                </div>
            </c:forEach>
        </div>

    </div>


    <%@ include file="footer.jspf" %>

    <!-- /container -->
</div>

</body>
</html>