<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="cazcade.boardcast.util.DataStoreFactory" %>
<%@ page import="cazcade.fountain.datastore.api.FountainDataStore" %>
<%@ page import="cazcade.fountain.datastore.impl.email.EmailUtil" %>
<%@ page import="cazcade.liquid.api.LiquidSessionIdentifier" %>
<%@ page import="cazcade.liquid.api.lsd.LSDAttribute" %>
<%@ page import="cazcade.liquid.api.lsd.LSDEntity" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%

    FountainDataStore dataStore = DataStoreFactory.getDataStore();
    LiquidSessionIdentifier admin = new LiquidSessionIdentifier("admin", null);
    final cazcade.liquid.api.LiquidRequest retrieveUserResponse = dataStore.process(new cazcade.liquid.api.request.RetrieveUserRequest(admin, new cazcade.liquid.api.LiquidURI(request.getParameter("user"))));
    cazcade.liquid.api.lsd.LSDEntity user = retrieveUserResponse.getResponse();
    if (retrieveUserResponse.getResponse().isError()) {
        response.sendRedirect("/_pages/failed.jsp?message=" + URLEncoder.encode(retrieveUserResponse.getResponse().getAttribute(LSDAttribute.DESCRIPTION), "utf8"));
    } else if (!EmailUtil.confirmEmailHash(user.getAttribute(LSDAttribute.EMAIL_ADDRESS), request.getParameter("hash"))) {
        response.sendRedirect("/_pages/failed.jsp?message=Incorrect+URL");
    } else {
        final LSDEntity update = user.asUpdateEntity();
        update.setAttribute(LSDAttribute.EMAIL_UPDATE_FREQUENCY, request.getParameter("frequency"));
        dataStore.process(new cazcade.liquid.api.request.UpdateUserRequest(admin, user.getID(), update));
    }


%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <title>Boardcast Email Update Frequency</title>
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
    <p>Thank you, your email frequency has been changed successfully.</p>

</div>

</body>
</html>

