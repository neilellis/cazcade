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

<tags:hashbo-less/>


<div id="top-bar-wrapper">
    <%@ include file="topbar.jspf" %>
</div>

<div class="container">

    <div class="content span16">

        <div class="row">
            <div class="span16 ">
                <div class="big-text">
                    Let's go create ...
                </div>
            </div>
        </div>
        <div class="row">


            <div class="span9 ">
                <div class="welcome-block left ">

                    <div class="welcome-block-inner">

                        <p><span class="larger">Realtime Noticeboards</span> help you keep everyone up to date with
                            relevant information without them having to sift through your entire Twitter timeline. Our Realtime
                            Noticeboards show the bigger picture of what's going with photo, videos notes and more. Maybe you'd
                            like to provide information during an event,
                            keep the rest of your club informed or share your thoughts with others. It's simple, social,
                            instant and visual.
                        </p>


                        <a href="./#::createPublic" class="create-board-button drop-shadow curved curved-vt-2">
                            <h1>Create Notice Board</h1>

                            <p>Create a public notice board.</p>

                        </a>
                    </div>
                </div>
            </div>


            <div class="span7">
                <div class="welcome-block right">
                    <div class="welcome-block-inner">
                        <p>
                            <span class="larger">Unlisted Boards</span> are great for collaboration such as
                            planning a holiday with friends, organising a wedding with family or working
                            on a project with teammates. Create a board and
                            then just share the URL with those you'd like to see it.
                            Only those with the URL can access your board.
                        </p>

                        <a href="./#::createCollab" class="create-board-button drop-shadow curved curved-vt-2">
                            <h1>Create Unlisted Board</h1>

                            <p>Create an unlisted board for collaboration.</p>
                        </a>
                    </div>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="span16 ">
                <div class="big-text bottom">
                    ... something amazing!
                </div>
            </div>
        </div>


        <%@ include file="footer.jspf" %>

    </div>


</div>

<%--<script type="text/javascript" language="javascript"--%>
<%--src="cazcade.hashbo.BoardcastMain/cazcade.hashbo.BoardcastMain.nocache.js"></script>--%>


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