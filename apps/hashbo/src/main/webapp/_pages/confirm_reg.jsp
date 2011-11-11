<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="cazcade.boardcast.util.DataStoreFactory" %>
<%@ page import="cazcade.fountain.datastore.api.FountainDataStore" %>
<%@ page import="cazcade.liquid.api.LiquidSessionIdentifier" %>
<%@ page import="cazcade.liquid.api.lsd.LSDAttribute" %>
<%@ page import="javax.mail.Message" %>
<%@ page import="javax.mail.Session" %>
<%@ page import="javax.mail.Transport" %>
<%@ page import="static cazcade.common.CommonConstants.IDENTITY_ATTRIBUTE" %>
<%@ page import="javax.mail.internet.InternetAddress" %>
<%@ page import="javax.mail.internet.MimeMessage" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Properties" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%

    FountainDataStore dataStore = DataStoreFactory.getDataStore();
    LiquidSessionIdentifier admin = new LiquidSessionIdentifier("admin");
    final cazcade.liquid.api.LiquidMessage retrieveUserResponse = dataStore.process(new cazcade.liquid.api.request.RetrieveUserRequest(admin, new cazcade.liquid.api.LiquidURI(cazcade.liquid.api.LiquidURIScheme.user, request.getParameter("user").toLowerCase())));
    cazcade.liquid.api.lsd.LSDEntity user = retrieveUserResponse.getResponse();

    String host = "smtp.postmarkapp.com";
    String to = user.getAttribute(LSDAttribute.EMAIL_ADDRESS);
    String from = "support@boardcast.it";

    String name = user.getAttribute(LSDAttribute.FULL_NAME);
    String subject = "Welcome!";

    org.jasypt.digest.StandardStringDigester digester = new org.jasypt.digest.StandardStringDigester();

    final String url = "http://boardcast.it/_welcome";
    String messageText = "Welcome aboard! Please click on this link " + url + " and sign in using the username and password you supplied.\n";

    if (!digester.matches(to, request.getParameter("hash"))) {
        response.sendRedirect("confirm_failed.jsp");
    } else {
        user.setAttribute(LSDAttribute.SECURITY_RESTRICTED, "false");
        dataStore.process(new cazcade.liquid.api.request.UpdateUserRequest(admin, user.getID(), user));
        boolean sessionDebug = false;
        Properties props = System.getProperties();
        props.put("mail.host", host);
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        Session mailSession = Session.getDefaultInstance(props, null);
        mailSession.setDebug(sessionDebug);
        Message msg = new MimeMessage(mailSession);
        msg.setFrom(new InternetAddress(from, "Boardcast"));
        InternetAddress[] address = {new InternetAddress(to)};
        msg.setRecipients(Message.RecipientType.TO, address);
        msg.setSubject(subject);
        msg.setSentDate(new Date());
        msg.setText(messageText);

        msg.saveChanges();
        Transport transport = mailSession.getTransport("smtp");
        transport.connect(host, "20d930a8-c079-43f6-9022-880156538a40", "20d930a8-c079-43f6-9022-880156538a40");
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
        response.sendRedirect(url);

    }


%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <title>Hashbo Registration</title>
    <link rel="stylesheet" href="../_css/static.css">
    <script type="text/javascript">

        var _gaq = _gaq || [];
        _gaq.push(['_setAccount', 'UA-25104667-1']);
        _gaq.push(['_trackPageview']);

        (function() {
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

