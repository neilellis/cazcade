<%@page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Boardcast</title>
    <%@ include file="header.jspf" %>
    <%@ include file="analytics.jspf" %>

    <style type="text/css">
        p {
            font-size: 20px;
            line-height: 24px;
        }

        h1 {
            font-size: 32px;
            color: #444;
            text-align: center;
        }

        h2 {
            font-size: 28px;
            color: #666;
        }

        h3 {
            font-size: 22px;
        }
    </style>

</head>

<body>
<%@ include file="chrome_frame.jspf" %>
<tags:hashbo-less/>


<div id="top-bar-wrapper">
    <%@ include file="topbar.jspf" %>
</div>


<div class="container">

    <div class="content">

        <div class="row">
            <div class="span16">
                <div class="big-text">
                    Get Started!
                </div>
            </div>
        </div>
        <div class="row">
            <div class="span16">
                <h1>Two Great Uses</h1>

                <h2>To show the world!</h2>

                <p><span class="larger">Listed boards</span> allow you to instantly publish an idea, some photos, a
                    poem or the latest news about something.
                    They can be creative, simple and visual - like a poem, some photos or a a tribute to your favourite
                    band. They can also be informational,
                    rich and realtime like a gig information board, or a charity notice board.
                    <a href="/_create-listed" class="call-to-action ">
                        Create listed board
                    </a>
                </p>
            </div>
            <div class="span16">
                <h2>To collaborate with friends, family or colleagues</h2>

                <p><span class="larger">Unlisted Boards</span> are great for collaboration such as
                    planning a holiday with friends, organising a wedding with family or working
                    on a project with teammates. Create a board and
                    then just share the URL with those you'd like to see it.
                    Only those with the URL can access your board.
                    <a href="/_create-unlisted" class="call-to-action ">
                        Create unlisted board
                    </a>
                </p>

            </div>

        </div>
        <hr/>
        <div class="row">
            <div class="span16 ">
                <h1>Three Easy Steps</h1>

                <h3>Step One</h3>

                <p>Create a board. If it's listed, optionally give it a short name.</p>
                <br/>

                <h3>Step Two</h3>

                <p>Optionally add a background. Then add some content, we're continually adding new content types, right
                    now
                    you can add text, photos, videos, websites and annotations.</p>
                <br/>

                <h3>Step Three</h3>

                <p>Share the board with your friends - they can comment and see changes as they happen. If it's an
                    unlisted
                    board they can also add content to the board.</p>

                <br/>
                <a href="/_create-listed" class="call-to-action primary">
                    Create a board now
                </a>

                <a href="/_query-popular" class="call-to-action secondary">
                    Look at some examples
                </a>

            </div>
        </div>


        <%@ include file="footer.jspf" %>

    </div>


</div>

</body>
</html>