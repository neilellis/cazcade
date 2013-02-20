<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="cazcade.boardcast.util.DataStoreFactory" %>
<%@ page import="cazcade.fountain.datastore.api.FountainDataStore" %>
<%@ page import="cazcade.liquid.api.LiquidMessage" %>
<%@ page import="static cazcade.common.CommonConstants.IDENTITY_ATTRIBUTE" %>
<%@ page import="cazcade.liquid.api.LiquidMessageState" %>
<%@ page import="cazcade.liquid.api.LiquidURI" %>
<%@ page import="cazcade.liquid.api.LiquidURIScheme" %>
<%@ page import="cazcade.liquid.api.request.RetrieveUserRequest" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
  --%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <title>Boardcast Monitor Page</title>
    <link rel="stylesheet" href="/_static/css/static.css">
</head>
<body>

<div id="background"></div>


<div class="header-logo-container">
    <div class="header-logo"></div>
</div>

<div id="message-block">
    <%

        final FountainDataStore dataStore = DataStoreFactory.getDataStore();
        final LiquidMessage retrieveUserResponse = dataStore.process(new RetrieveUserRequest(new LiquidURI(LiquidURIScheme.user, "admin")));
        if (retrieveUserResponse.getState() == LiquidMessageState.SUCCESS) {
    %>
    RETRIEVE_USER: SUCCESS
    <%
    } else {
    %>
    RETRIEVE_USER: FAIL
    <%
        }
    %>

    <br/>
    <br/>
    <br/>

    <div><a href="http://stats.pingdom.com/7ap4sdhxa9m1/391238"><img src="http://share.pingdom.com/banners/15809bd2"
                                                                     alt="Uptime Report" title="Uptime Report"
                                                                     width="300"
                                                                     height="165"/></a>

    </div>
</div>
</body>
</html>

