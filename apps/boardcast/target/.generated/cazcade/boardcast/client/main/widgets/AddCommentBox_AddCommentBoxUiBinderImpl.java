package cazcade.boardcast.client.main.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.HTMLPanel;

public class AddCommentBox_AddCommentBoxUiBinderImpl implements UiBinder<com.google.gwt.user.client.ui.HTMLPanel, cazcade.boardcast.client.main.widgets.AddCommentBox>, cazcade.boardcast.client.main.widgets.AddCommentBox.AddCommentBoxUiBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<p>Tell us what you think, but first you'll need to <span id='{0}'></span> <a class='hashbo-login-link' id='{1}'>or with Boardcast.</a> </p>")
    SafeHtml html1(String arg0, String arg1);
     
    @Template("<span id='{0}'></span> <span id='{1}'></span>")
    SafeHtml html2(String arg0, String arg1);
     
  }

  Template template = GWT.create(Template.class);


  public com.google.gwt.user.client.ui.HTMLPanel createAndBindUi(final cazcade.boardcast.client.main.widgets.AddCommentBox owner) {


    return new Widgets(owner).get_f_HTMLPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final cazcade.boardcast.client.main.widgets.AddCommentBox owner;


    public Widgets(final cazcade.boardcast.client.main.widgets.AddCommentBox owner) {
      this.owner = owner;
      build_domId2();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 3
      build_domId3();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 3
      build_domId0();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId1();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId2Element();  // more than one getter call detected. Type: DEFAULT, precedence: 3
      build_domId0Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId1Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
    }

    SafeHtml template_html1() {
      return template.html1(get_domId2(), get_domId3());
    }
    SafeHtml template_html2() {
      return template.html2(get_domId0(), get_domId1());
    }

    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private cazcade.boardcast.client.main.widgets.AddCommentBox_AddCommentBoxUiBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private cazcade.boardcast.client.main.widgets.AddCommentBox_AddCommentBoxUiBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final cazcade.boardcast.client.main.widgets.AddCommentBox_AddCommentBoxUiBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (cazcade.boardcast.client.main.widgets.AddCommentBox_AddCommentBoxUiBinderImpl_GenBundle) GWT.create(cazcade.boardcast.client.main.widgets.AddCommentBox_AddCommentBoxUiBinderImpl_GenBundle.class);
      // Setup section.


      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for f_HTMLPanel1 called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private com.google.gwt.user.client.ui.HTMLPanel get_f_HTMLPanel1() {
      return build_f_HTMLPanel1();
    }
    private com.google.gwt.user.client.ui.HTMLPanel build_f_HTMLPanel1() {
      // Creation section.
      final com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template_html2().asString());
      // Setup section.
      f_HTMLPanel1.setStyleName("hashbo-add-comment");

      // Attach section.
      UiBinderUtil.TempAttachment attachRecord33 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
      get_domId0Element().get();
      get_domId1Element().get();

      // Detach section.
      attachRecord33.detach();
      f_HTMLPanel1.addAndReplaceElement(get_addCommentBox(), get_domId0Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_loginPanel(), get_domId1Element().get());

      return f_HTMLPanel1;
    }

    /**
     * Getter for domId0 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 2.
     */
    private java.lang.String domId0;
    private java.lang.String get_domId0() {
      return domId0;
    }
    private java.lang.String build_domId0() {
      // Creation section.
      domId0 = com.google.gwt.dom.client.Document.get().createUniqueId();
      // Setup section.


      return domId0;
    }

    /**
     * Getter for addCommentBox called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.vortex.widgets.client.stream.CommentBox get_addCommentBox() {
      return build_addCommentBox();
    }
    private cazcade.vortex.widgets.client.stream.CommentBox build_addCommentBox() {
      // Creation section.
      final cazcade.vortex.widgets.client.stream.CommentBox addCommentBox = (cazcade.vortex.widgets.client.stream.CommentBox) GWT.create(cazcade.vortex.widgets.client.stream.CommentBox.class);
      // Setup section.
      addCommentBox.setVisible(false);
      addCommentBox.setWidth("100%");


      owner.addCommentBox = addCommentBox;

      return addCommentBox;
    }

    /**
     * Getter for domId0Element called 2 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.uibinder.client.LazyDomElement domId0Element;
    private com.google.gwt.uibinder.client.LazyDomElement get_domId0Element() {
      return domId0Element;
    }
    private com.google.gwt.uibinder.client.LazyDomElement build_domId0Element() {
      // Creation section.
      domId0Element = new com.google.gwt.uibinder.client.LazyDomElement<Element>(get_domId0());
      // Setup section.


      return domId0Element;
    }

    /**
     * Getter for domId1 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 2.
     */
    private java.lang.String domId1;
    private java.lang.String get_domId1() {
      return domId1;
    }
    private java.lang.String build_domId1() {
      // Creation section.
      domId1 = com.google.gwt.dom.client.Document.get().createUniqueId();
      // Setup section.


      return domId1;
    }

    /**
     * Getter for loginPanel called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.HTMLPanel get_loginPanel() {
      return build_loginPanel();
    }
    private com.google.gwt.user.client.ui.HTMLPanel build_loginPanel() {
      // Creation section.
      final com.google.gwt.user.client.ui.HTMLPanel loginPanel = new com.google.gwt.user.client.ui.HTMLPanel(template_html1().asString());
      // Setup section.
      loginPanel.setStyleName("hashbo-add-comment-or-login");

      // Attach section.
      UiBinderUtil.TempAttachment attachRecord34 = UiBinderUtil.attachToDom(loginPanel.getElement());
      get_domId2Element().get();
      get_loginLink();

      // Detach section.
      attachRecord34.detach();
      loginPanel.addAndReplaceElement(get_twitterLoginBox(), get_domId2Element().get());

      owner.loginPanel = loginPanel;

      return loginPanel;
    }

    /**
     * Getter for domId2 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 3.
     */
    private java.lang.String domId2;
    private java.lang.String get_domId2() {
      return domId2;
    }
    private java.lang.String build_domId2() {
      // Creation section.
      domId2 = com.google.gwt.dom.client.Document.get().createUniqueId();
      // Setup section.


      return domId2;
    }

    /**
     * Getter for twitterLoginBox called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private cazcade.boardcast.client.main.widgets.login.TwitterLoginBox get_twitterLoginBox() {
      return build_twitterLoginBox();
    }
    private cazcade.boardcast.client.main.widgets.login.TwitterLoginBox build_twitterLoginBox() {
      // Creation section.
      final cazcade.boardcast.client.main.widgets.login.TwitterLoginBox twitterLoginBox = (cazcade.boardcast.client.main.widgets.login.TwitterLoginBox) GWT.create(cazcade.boardcast.client.main.widgets.login.TwitterLoginBox.class);
      // Setup section.


      owner.twitterLoginBox = twitterLoginBox;

      return twitterLoginBox;
    }

    /**
     * Getter for domId2Element called 2 times. Type: DEFAULT. Build precedence: 3.
     */
    private com.google.gwt.uibinder.client.LazyDomElement domId2Element;
    private com.google.gwt.uibinder.client.LazyDomElement get_domId2Element() {
      return domId2Element;
    }
    private com.google.gwt.uibinder.client.LazyDomElement build_domId2Element() {
      // Creation section.
      domId2Element = new com.google.gwt.uibinder.client.LazyDomElement<Element>(get_domId2());
      // Setup section.


      return domId2Element;
    }

    /**
     * Getter for loginLink called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private com.google.gwt.dom.client.AnchorElement get_loginLink() {
      return build_loginLink();
    }
    private com.google.gwt.dom.client.AnchorElement build_loginLink() {
      // Creation section.
      final com.google.gwt.dom.client.AnchorElement loginLink = new com.google.gwt.uibinder.client.LazyDomElement(get_domId3()).get().cast();
      // Setup section.


      owner.loginLink = loginLink;

      return loginLink;
    }

    /**
     * Getter for domId3 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 3.
     */
    private java.lang.String domId3;
    private java.lang.String get_domId3() {
      return domId3;
    }
    private java.lang.String build_domId3() {
      // Creation section.
      domId3 = com.google.gwt.dom.client.Document.get().createUniqueId();
      // Setup section.


      return domId3;
    }

    /**
     * Getter for domId1Element called 2 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.uibinder.client.LazyDomElement domId1Element;
    private com.google.gwt.uibinder.client.LazyDomElement get_domId1Element() {
      return domId1Element;
    }
    private com.google.gwt.uibinder.client.LazyDomElement build_domId1Element() {
      // Creation section.
      domId1Element = new com.google.gwt.uibinder.client.LazyDomElement<Element>(get_domId1());
      // Setup section.


      return domId1Element;
    }
  }
}
