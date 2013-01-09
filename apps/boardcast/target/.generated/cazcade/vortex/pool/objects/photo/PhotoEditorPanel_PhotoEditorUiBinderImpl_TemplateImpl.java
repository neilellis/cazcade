package cazcade.vortex.pool.objects.photo;

public class PhotoEditorPanel_PhotoEditorUiBinderImpl_TemplateImpl implements cazcade.vortex.pool.objects.photo.PhotoEditorPanel_PhotoEditorUiBinderImpl.Template {
  
  public com.google.gwt.safehtml.shared.SafeHtml html1(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2) {
    StringBuilder sb = new java.lang.StringBuilder();
    sb.append("<div class='span12'> <div class='form-stacked'> <div class='row'> <div class='span5'> <h3>Create/Edit Image</h3> <label>Title <small>(optional)</small> </label> <span id='");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg0));
    sb.append("'></span> <label>Description <small>(optional)</small> </label> <span id='");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg1));
    sb.append("'></span> </div> <div class='span6'> <span id='");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg2));
    sb.append("'></span> </div> </div> </div> </div>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}
}
