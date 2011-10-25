<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="cazcade.fountain.datastore.api.FountainDataStore" %>
<%@ page import="cazcade.hashbo.util.DataStoreFactory" %>
<%@ page import="cazcade.liquid.api.LiquidMessageState" %>
<%@ page import="static cazcade.common.CommonConstants.IDENTITY_ATTRIBUTE" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <title>Hashbo Monitor Page</title>
    <link rel="stylesheet" href="_css/static.css">
</head>
<body>

<div id="background"></div>


<div class="header-logo-container">
    <div class="header-logo"></div>
</div>

<div id="message-block">
    <%

        FountainDataStore dataStore = DataStoreFactory.getDataStore();
        final cazcade.liquid.api.LiquidMessage retrieveUserResponse = dataStore.process(new cazcade.liquid.api.request.RetrieveUserRequest(new cazcade.liquid.api.LiquidURI(cazcade.liquid.api.LiquidURIScheme.user, "admin")));
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
                                                                     alt="Uptime Report" title="Uptime Report" width="300"
                                                                     height="165"/></a>

</div>
</div>
</body>
</html>

