package cazcade.vortex.widgets.client.form.fields;

public class UrlField_ChangeImageUrlPanelUiBinderImpl_TemplateImpl implements cazcade.vortex.widgets.client.form.fields.UrlField_ChangeImageUrlPanelUiBinderImpl.Template {
  
  public com.google.gwt.safehtml.shared.SafeHtml html1(java.lang.String arg0,java.lang.String arg1) {
    StringBuilder sb = new java.lang.StringBuilder();
    sb.append("<span id='");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg0));
    sb.append("'></span> <div class='url-field-textbox-area'> <small>You can supply the URL of a web page or of an image. If you supply a web page URL here we will take a snapshot and use that. </small> <label>Url</label> <span id='");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg1));
    sb.append("'></span> </div>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}
}
