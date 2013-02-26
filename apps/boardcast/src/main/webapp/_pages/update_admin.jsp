<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="cazcade.boardcast.util.DataStoreFactory" %>
<%@ page import="cazcade.fountain.datastore.api.FountainDataStore" %>
<%@ page import="cazcade.fountain.datastore.impl.email.EmailUtil" %>
<%@ page import="cazcade.liquid.api.LiquidRequest" %>
<%@ page import="cazcade.liquid.api.LiquidURI" %>
<%@ page import="cazcade.liquid.api.SessionIdentifier" %>
<%@ page import="cazcade.liquid.api.lsd.Dictionary" %>
<%@ page import="cazcade.liquid.api.lsd.TransferEntity" %>
<%@ page import="cazcade.liquid.api.request.RetrieveUserRequest" %>
<%@ page import="cazcade.liquid.api.request.UpdateUserRequest" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
  --%>

<%

    final FountainDataStore dataStore = DataStoreFactory.getDataStore();
    final SessionIdentifier admin = new SessionIdentifier("admin", null);
    final LiquidRequest retrieveUserResponse = dataStore.process(new RetrieveUserRequest(admin, new LiquidURI(request.getParameter("user"))));
    final TransferEntity user = retrieveUserResponse.response();
    if (retrieveUserResponse.response().error()) {
        response.sendRedirect("/_pages/failed.jsp?message=" + URLEncoder.encode(retrieveUserResponse.response()
                                                                                                    .$(Dictionary.DESCRIPTION), "utf8"));
    } else if (!EmailUtil.confirmEmailHash(user.$(Dictionary.EMAIL_ADDRESS), request.getParameter("hash"))) {
        response.sendRedirect("/_pages/failed.jsp?message=Incorrect+URL");
    } else {
        final TransferEntity update = user.asUpdateEntity();
        update.$(Dictionary.EMAIL_UPDATE_FREQUENCY, request.getParameter("frequency"));
        dataStore.process(new UpdateUserRequest(admin, user.id(), update));
    }


%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <title>Boardcast Email Update Frequency</title>
    <link rel="stylesheet" href="/_static/css/static.css">
    <%@ include file="analytics.jspf" %>
</head>
<body>

<div id="background"></div>


<div class="header-logo-container">
    <div class="header-logo"></div>
</div>

<div id="message-block">
    <p>Thank you, your email frequency has been changed successfully.</p>

</div>

</body>
</html>

