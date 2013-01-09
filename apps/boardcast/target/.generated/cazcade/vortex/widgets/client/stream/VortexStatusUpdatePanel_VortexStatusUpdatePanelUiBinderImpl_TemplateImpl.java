package cazcade.vortex.widgets.client.stream;

public class VortexStatusUpdatePanel_VortexStatusUpdatePanelUiBinderImpl_TemplateImpl implements cazcade.vortex.widgets.client.stream.VortexStatusUpdatePanel_VortexStatusUpdatePanelUiBinderImpl.Template {
  
  public com.google.gwt.safehtml.shared.SafeHtml html1(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2) {
    StringBuilder sb = new java.lang.StringBuilder();
    sb.append("<span class='stream-status-update-user-panel'> <span id='");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg0));
    sb.append("'></span> </span> <p class='stream-entry-main'> <span class='stream-entry-message' id='");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg1));
    sb.append("'></span> <span class='stream-entry-details'> <span id='");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg2));
    sb.append("'></span> </span> </p>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}
}
