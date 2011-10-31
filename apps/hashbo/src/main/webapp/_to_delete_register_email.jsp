<%@ page import="cazcade.liquid.api.lsd.LSDAttribute" %>
<%@ page import="javax.mail.Message" %>
<%@ page import="javax.mail.Session" %>
<%@ page import="javax.mail.Transport" %>
<%@ page import="javax.mail.internet.InternetAddress" %>
<%@ page import="javax.mail.internet.MimeMessage" %>
<%@ page import="java.util.Date" %>
<%@ page import="static cazcade.common.CommonConstants.IDENTITY_ATTRIBUTE" %>
<%@ page import="java.util.Properties" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    cazcade.liquid.api.lsd.LSDEntity user = (cazcade.liquid.api.lsd.LSDEntity) session.getAttribute(cazcade.common.CommonConstants.NEW_USER_ATTRIBUTE);
    if (user == null) {
        throw new RuntimeException("No user in session.");
    }
    String host = "smtp.sendgrid.net";
    String to = user.getAttribute(LSDAttribute.EMAIL_ADDRESS);
    String from = "info@hashbo.com";

    String name = user.getAttribute(LSDAttribute.FULL_NAME);
    String subject = "Welcome!";

    org.jasypt.digest.StandardStringDigester digester = new org.jasypt.digest.StandardStringDigester();
    String messageText = "Please click on this link to register: http://beta.hashbo.com/confirm_reg.jsp?user=" +
            java.net.URLEncoder.encode(user.getAttribute(LSDAttribute.NAME), "utf8") +
            "&hash=" + java.net.URLEncoder.encode(digester.digest(to), "utf8");

    boolean sessionDebug = false;
    Properties props = System.getProperties();
    props.put("mail.host", host);
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    Session mailSession = Session.getDefaultInstance(props, null);
    mailSession.setDebug(sessionDebug);
    Message msg = new MimeMessage(mailSession);
    msg.setFrom(new InternetAddress(from, "Hashbo"));
    InternetAddress[] address = {new InternetAddress(to)};
    msg.setRecipients(Message.RecipientType.TO, address);
    msg.setSubject(subject);
    msg.setSentDate(new Date());
    msg.setText(messageText);

    msg.saveChanges();
    Transport transport = mailSession.getTransport("smtp");
    transport.connect(host, "hashbo", "thx1139");
    transport.sendMessage(msg, msg.getAllRecipients());
    transport.close();

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <title>Hashbo Registration</title>
    <link rel="stylesheet" href="_css/static.css">
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

