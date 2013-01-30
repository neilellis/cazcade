<%@page contentType="text/html;charset=UTF-8" %>
<%@ page import="cazcade.boardcast.util.DataStoreFactory" %>
<%@ page import="cazcade.fountain.datastore.api.FountainDataStore" %>
<%@ page import="cazcade.fountain.datastore.impl.email.EmailUtil" %>
<%@ page import="cazcade.liquid.api.LiquidMessage" %>
<%@ page import="cazcade.liquid.api.LiquidSessionIdentifier" %>
<%@ page import="cazcade.liquid.api.LiquidURI" %>
<%@ page import="cazcade.liquid.api.LiquidURIScheme" %>
<%@ page import="static cazcade.common.CommonConstants.IDENTITY_ATTRIBUTE" %>
<%@ page import="cazcade.liquid.api.lsd.LSDAttribute" %>
<%@ page import="cazcade.liquid.api.lsd.LSDTransferEntity" %>
<%@ page import="cazcade.liquid.api.request.RetrieveUserRequest" %>
<%@ page import="cazcade.liquid.api.request.UpdateUserRequest" %>
<%@ page import="org.jasypt.digest.StandardStringDigester" %>
<%@ page import="javax.mail.Message" %>
<%@ page import="javax.mail.Session" %>
<%@ page import="javax.mail.Transport" %>
<%@ page import="javax.mail.internet.InternetAddress" %>
<%@ page import="javax.mail.internet.MimeMessage" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Properties" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
  --%>

<%

    final FountainDataStore dataStore = DataStoreFactory.getDataStore();
    final LiquidSessionIdentifier admin = new LiquidSessionIdentifier("admin");
    final LiquidMessage retrieveUserResponse = dataStore.process(new RetrieveUserRequest(admin, new LiquidURI(LiquidURIScheme.user, request
            .getParameter("user")
            .toLowerCase())));
    final LSDTransferEntity user = retrieveUserResponse.getResponse();

    final String host = "smtp.postmarkapp.com";
    final String to = user.getAttribute(LSDAttribute.EMAIL_ADDRESS);
    final String from = "support@boardcast.it";

    final String name = user.getAttribute(LSDAttribute.FULL_NAME);
    final String subject = "Welcome!";

    final StandardStringDigester digester = new StandardStringDigester();

    final String url = "http://boardcast.it/welcome";
    final String messageText =
            "Welcome aboard! We're a pretty young application, so we're really looking for your feedback and help. Please feel free to email us at support@boardcast.it and let us know what we can do for you. You can now click on this link "
            + url
            + " and sign in using the username and password you supplied.\n";

    if (!EmailUtil.confirmEmailHash(to, request.getParameter("hash"))) {
        response.sendRedirect("failed.jsp?message=Confirm+Failed");
    }
    else {
        user.setAttribute(LSDAttribute.SECURITY_RESTRICTED, "false");
        dataStore.process(new UpdateUserRequest(admin, user.getUUID(), user));
        final boolean sessionDebug = false;
        final Properties props = System.getProperties();
        props.setProperty("mail.host", host);
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.auth", "true");
        final Session mailSession = Session.getDefaultInstance(props, null);
        mailSession.setDebug(sessionDebug);
        final Message msg = new MimeMessage(mailSession);
        msg.setFrom(new InternetAddress(from, "Boardcast"));
        final InternetAddress[] address = {new InternetAddress(to)};
        msg.setRecipients(Message.RecipientType.TO, address);
        msg.setSubject(subject);
        msg.setSentDate(new Date());
        msg.setText(messageText);

        msg.saveChanges();
        final Transport transport = mailSession.getTransport("smtp");
        transport.connect(host, "20d930a8-c079-43f6-9022-880156538a40", "20d930a8-c079-43f6-9022-880156538a40");
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
        response.sendRedirect(url);

    }


%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <title>Boardcast Registration</title>
    <link rel="stylesheet" href="../_css/static.css">
    <%@ include file="analytics.jspf" %>

</head>
<body>

<div id="background"></div>


<div class="header-logo-container">
    <div class="header-logo"></div>
</div>

<div id="message-block">
    <p>Great stuff! Please copy this link into your browser:</p>
    <br/>

    <p><%=url%>
    </p>
    <br/>


</div>

</body>
</html>

