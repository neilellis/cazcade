package cazcade.boardcast.client.main.widgets.board;

public class PublicBoard_NewBoardUiBinderImpl_TemplateImpl implements cazcade.boardcast.client.main.widgets.board.PublicBoard_NewBoardUiBinderImpl.Template {
  
  public com.google.gwt.safehtml.shared.SafeHtml html1(java.lang.String arg0) {
    StringBuilder sb = new java.lang.StringBuilder();
    sb.append("<div class='board-author-info-box'> <h3>About the author</h3> <span id='");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg0));
    sb.append("'></span> </div>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}

public com.google.gwt.safehtml.shared.SafeHtml html2(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2) {
StringBuilder sb = new java.lang.StringBuilder();
sb.append("<h3>Board Information</h3> <p> This board was created at <span id='");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg0));
sb.append("'></span> by <span id='");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg1));
sb.append("'></span>. <span id='");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg2));
sb.append("'></span> </p>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}

public com.google.gwt.safehtml.shared.SafeHtml html3() {
StringBuilder sb = new java.lang.StringBuilder();
sb.append("<h3>About Boardcast</h3>   <p>Boardcast is still in the Alpha stage of development which is a technical way of saying there are still a few bugs kicking around and we're still trying to make things look nice. We'd love you to have a play, look around and tell us how we can make your life easier! </p> <p>We have a <a href='http://boardcast.posterous.com'>blog</a>, and a <a href='http://twitter.com/boardcast_it'>Twitter account</a> where you can keep up to date with the latest improvements. </p> <p>If you have any suggestions for us or find something that doesn't work, please drop by our <a href='http://cazcade.zendesk.com'>support website</a>. </p>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}

public com.google.gwt.safehtml.shared.SafeHtml html4(java.lang.String arg0) {
StringBuilder sb = new java.lang.StringBuilder();
sb.append("<span id='");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg0));
sb.append("'></span>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}

public com.google.gwt.safehtml.shared.SafeHtml html5(java.lang.String arg0,java.lang.String arg1,java.lang.String arg2,java.lang.String arg3,java.lang.String arg4,java.lang.String arg5,java.lang.String arg6,java.lang.String arg7,java.lang.String arg8,java.lang.String arg9,java.lang.String arg10,java.lang.String arg11) {
StringBuilder sb = new java.lang.StringBuilder();
sb.append("<div class='container' id='");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg0));
sb.append("'> <div class='content'> <div class='board-share-surround'> <div class='board-share'> <iframe allowtransparency='true' class='tweet-button' frameborder='0' id='");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg1));
sb.append("' scrolling='no' style='width:100px; height:20px;'></iframe>  </div> </div> <span id='");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg2));
sb.append("'></span> <span id='");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg3));
sb.append("'></span> <span id='");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg4));
sb.append("'></span> <div class='row board-top-menu-row'> <span id='");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg5));
sb.append("'></span> </div> <hr> <div class='board-content' style=''> <span id='");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg6));
sb.append("'></span>   </div> <div class='row profile-board-header-bottom'> </div> <hr> <div class='row'> <h2>Comments</h2> <span id='");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg7));
sb.append("'></span> <span id='");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg8));
sb.append("'></span> </div> </div> </div> <div class='board-footer' id='");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg9));
sb.append("'> <div class='container'> <div class='content'> <span id='");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg10));
sb.append("'></span> </div> </div> </div> <div class='hashbo-chat'> <span id='");
sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg11));
sb.append("'></span> </div>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}
}
