<%@page contentType="text/html;charset=UTF-8" %>
<%--
  ~ Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
  --%>

<%
    //This may look insane but it's for a reason! It's to delayAsync the taking of a snapshot until all GWT has finished.
    Thread.sleep(30 * 1000);
%>
