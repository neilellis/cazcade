<%@ page import="cazcade.common.CommonConstants" %>
<%@ page import="cazcade.liquid.api.lsd.Dictionary" %>
<%@ page import="cazcade.liquid.api.lsd.Entity" %>
<%@ page import="org.jasypt.digest.StandardStringDigester" %>
<%@ page import="javax.mail.Message" %>
<%@ page import="javax.mail.Session" %>
<%@ page import="javax.mail.Transport" %>
<%@ page import="static cazcade.common.CommonConstants.IDENTITY_ATTRIBUTE" %>
<%@ page import="javax.mail.internet.InternetAddress" %>
<%@ page import="javax.mail.internet.MimeMessage" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Properties" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
  --%>

<%
    final Entity user = (Entity) session.getAttribute(CommonConstants.NEW_USER_ATTRIBUTE);
    if (user == null) {
        throw new RuntimeException("No user in session.");
    }
    final String host = "smtp.sendgrid.net";
    final String to = user.$(Dictionary.EMAIL_ADDRESS);
    final String from = "info@boardcast.com";

    final String name = user.$(Dictionary.FULL_NAME);
    final String subject = "Welcome!";

    final StandardStringDigester digester = new StandardStringDigester();
    final String messageText = "Please click on this link to register: http://beta.boardcast.com/confirm_reg.jsp?user=" +
                               URLEncoder.encode(user.$(Dictionary.NAME), "utf8") +
                               "&hash=" + URLEncoder.encode(digester.digest(to), "utf8");

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
    transport.connect(host, "boardcast", "thx1139");
    transport.sendMessage(msg, msg.getAllRecipients());
    transport.close();

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <title>Boardcast Registration</title>
    <link rel="stylesheet" href="/_static/css/static.css">
    <script type="text/javascript">

        var _gaq = _gaq || [];
        _gaq.push(['_setAccount', 'UA-25104667-1']);
        _gaq.push(['_trackPageview']);

        (function () {
            var ga = document.createElement('script');
            ga.type = 'text/javascript';
            ga.async = true;
            ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
            var s = document.getElementsByTagName('script')[0];
            s.parentNode.insertBefore(ga, s);
        })();

    </script>
</head>
<body>

<%@ include file="_pages/topbar.jspf" %>

<div class="container">

    <div class="content">
        <div class="row">
            <h3>Thanks for registering we've sent you an email with a link to confirm, please click on that link to
                continue
                the
                registration process.</h3>
        </div>
    </div>
</div>

</div>

</body>
</html>

