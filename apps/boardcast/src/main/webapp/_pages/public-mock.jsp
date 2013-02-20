<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%--
  ~ Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
  --%>

<!DOCTYPE html>
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

<div class="container" style="width:1020px;">


<script>
    //Deferred loading to improve initial load time.
    loadjs("../_prettify/prettify.js");
    loadcss("/_static/less/boardcast.less", "boardcast", function () {
        document.getElementById('loading-panel').style.opacity = 0.0;
        document.getElementById('cache-panel').style.opacity = 1.0;
        document.getElementById('top-bar-wrapper').style.opacity = 1.0;
    });
    loadcss("/_static/css/hackabout.css", "hack", null);
</script>

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
        <div style="width:1014px; height:760px; background-color:#eee;border:1px solid #aaa;">
            <tags:loading-bar/>
        </div>
    </div>
</div>
<div class="row">
<div class="span12">
<h2>Comments</h2>

<div class="comment-panel">
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/8ef412c0-2b86-4019-946e-1db1e8f5e857.gif&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">test</div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@admin</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-23</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/8ef412c0-2b86-4019-946e-1db1e8f5e857.gif&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Testing</div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@admin</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-23</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/8ef412c0-2b86-4019-946e-1db1e8f5e857.gif&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">xyz</div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@admin</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-23</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/2887737c-874c-450b-aa9e-fb072887efae.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">test</div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@neil</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-21</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/2887737c-874c-450b-aa9e-fb072887efae.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message"><a class="board-link"
                                             href="#FollowMeNickM">#FollowMeNickM</a>

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@neil</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-16</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/2887737c-874c-450b-aa9e-fb072887efae.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Like&nbsp;

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@neil</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-16</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/2887737c-874c-450b-aa9e-fb072887efae.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Okay so the idea is that you can create a board just by
            saying it's name.

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@neil</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-16</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"></div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Yes please

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@nick</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-16</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/2887737c-874c-450b-aa9e-fb072887efae.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Would you like me to give you a quick tour of Boardcast Nick?

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@neil</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-16</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"></div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Hello

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@nick</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-16</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/2887737c-874c-450b-aa9e-fb072887efae.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Hey Nick

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@neil</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-16</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/8ef412c0-2b86-4019-946e-1db1e8f5e857.gif&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Hey Ian

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@admin</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-14</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/8ef412c0-2b86-4019-946e-1db1e8f5e857.gif&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">For update.

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@admin</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-13</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/8ef412c0-2b86-4019-946e-1db1e8f5e857.gif&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">System going down.

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@admin</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-13</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/8ef412c0-2b86-4019-946e-1db1e8f5e857.gif&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">System going down for update.

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@admin</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-13</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/8ef412c0-2b86-4019-946e-1db1e8f5e857.gif&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Hey Michael

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@admin</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-13</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/8ef412c0-2b86-4019-946e-1db1e8f5e857.gif&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">(i.e. private boards)

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@admin</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-13</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/8ef412c0-2b86-4019-946e-1db1e8f5e857.gif&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">By end of the week we'll have full support for inviting
            people to boards.

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@admin</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-13</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/8ef412c0-2b86-4019-946e-1db1e8f5e857.gif&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Then by Tues/Wed we'll have follow/unfollow + f
            riends list.
        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@admin</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-13</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/8ef412c0-2b86-4019-946e-1db1e8f5e857.gif&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Hey Cody, sorry about the radio silence, a lot of work
            going into these new features ? From tomorrow clicking on a user photo in chat will
            provide a pop-up with a little more information about them.

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@admin</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-13</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/42106730-8a12-462c-95de-0217e4e6f13b.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Can't wait to test it out

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@belthesar</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-12</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/42106730-8a12-462c-95de-0217e4e6f13b.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Hey Neil - Sounds awesome.

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@belthesar</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-12</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/2887737c-874c-450b-aa9e-fb072887efae.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Just a status update, I'm currently working on private
            boards and the mechanisms required to invite people into them for a chat etc.

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@neil</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-12</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/2887737c-874c-450b-aa9e-fb072887efae.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message"></div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@neil</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-12</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/2887737c-874c-450b-aa9e-fb072887efae.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Hey Holly! Welcome aboard!

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@neil</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-12</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/2887737c-874c-450b-aa9e-fb072887efae.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Would you like me to show you around at all Justin:?

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@neil</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-12</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"></div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">howdy!

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@jmeader</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-12</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/2887737c-874c-450b-aa9e-fb072887efae.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Hey Justin

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@neil</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-12</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"></div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Thanks Neil!

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@sarahcc</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-12</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"></div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Hey, super new to this...need some direction, STAT!

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@sarahcc</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-12</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/fe3b3cf4-9896-4fc2-8d46-964d44280f2d.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Hi Sarah, Hi Neil. I'll be offline now until tomorrow
            evening. Have fun on Boardcast! ?

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@dimple</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-12</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/2887737c-874c-450b-aa9e-fb072887efae.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Hey D

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@neil</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-12</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/2887737c-874c-450b-aa9e-fb072887efae.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Hey Sarah

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@neil</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-12</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/fe3b3cf4-9896-4fc2-8d46-964d44280f2d.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Hi Kevin

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@dimple</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-11</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/42106730-8a12-462c-95de-0217e4e6f13b.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Happy Friday indeed!

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@belthesar</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-09</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/fe3b3cf4-9896-4fc2-8d46-964d44280f2d.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Happy Friday!

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@dimple</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-09</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/42106730-8a12-462c-95de-0217e4e6f13b.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Heya Dimple.

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@belthesar</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-09</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/fe3b3cf4-9896-4fc2-8d46-964d44280f2d.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">hey cody

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@dimple</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-09</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/2446cdda-083e-440d-9752-fddd09649d1e.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">I do indeed, took me a while to upload it. Just sending
            off some more videos for TCTV.

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@chris</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-09</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/2887737c-874c-450b-aa9e-fb072887efae.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Okay <a class="board-link" href="#FollowMeChrisL">#FollowMeChrisL</a>

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@neil</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-09</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/2446cdda-083e-440d-9752-fddd09649d1e.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">It's a bit like Google Wave on Steroids?

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@chris</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-09</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/2887737c-874c-450b-aa9e-fb072887efae.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Hooray Chris you have a photo ?

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@neil</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-09</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/2446cdda-083e-440d-9752-fddd09649d1e.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Please do!

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@chris</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-09</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/fe3b3cf4-9896-4fc2-8d46-964d44280f2d.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Hello everyone!

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@dimple</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-09</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/42106730-8a12-462c-95de-0217e4e6f13b.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Heya Neil.&nbsp;

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@belthesar</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-09</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/2887737c-874c-450b-aa9e-fb072887efae.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">So Chris can I give you a quick tour?

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@neil</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-09</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/2887737c-874c-450b-aa9e-fb072887efae.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Hey Cody!

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@neil</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-09</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/2887737c-874c-450b-aa9e-fb072887efae.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Yep click on your photo on the top bar ?

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@neil</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-09</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/42106730-8a12-462c-95de-0217e4e6f13b.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Hey there, Chris. Yeah, I said the same thing my when I
            first joined. ?

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@belthesar</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-09</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"
                                             src="../_website-snapshot?url=http://c0021791.cdn1.cloudfiles.rackspacecloud.com/2887737c-874c-450b-aa9e-fb072887efae.jpg&amp;size=PROFILE_SMALL">
        </div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">I'll send it and tell you the rest here ?

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@neil</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-09</div>
        </div>
    </div>
</div>
<div class="stream-entry-panel" style="opacity: 1; height: 100%;">
    <div class="stream-entry-user-panel">
        <div class="stream-author-icon"><img class="gwt-Image"></div>
    </div>
    <div class="stream-entry-main">
        <div class="stream-entry-message">Ew that's a horrible avatar, going to need to change that!

        </div>
        <div class="stream-entry-details">
            <div class="stream-author-label">@chris</div>
            <div class="stream-entry-location"></div>
            <div class="stream-entry-time">2011-09-09</div>
        </div>
    </div>
</div>
<div class="alert-message info stream-status-update-panel" style="height: 100%; opacity: 1;"><span
        class="stream-status-update-user-panel"> <div class="stream-author-icon"><img
        class="gwt-Image"></div> </span>

    <p class="stream-entry-main"><span
            class="stream-entry-message">Anonymous has just entered.</span> <span
            class="stream-entry-details"> <div class="stream-entry-time">1 min ago</div> </span></p>
</div>
</div>
</div>
<div class="span4">
    <h3>Board Information</h3>
</div>
</div>
</div>

<div id="public-board-panel">

</div>

<%@ include file="footer.jspf" %>


</div>

<%--<script type="text/javascript" language="javascript"--%>
<%--src="../_boardcast_gwtMain/_boardcast_gwtMain.nocache.js"></script>--%>


<!-- /container -->
<script type="text/javascript" language="javascript">
    //Deferred loading to improve initial load time.
    loadjs("http://w.sharethis.com/button/buttons.js");
</script>
<span id="sharethis" class='st_sharethis' displayText='' style="visibility: hidden"></span>

</body>
</html>