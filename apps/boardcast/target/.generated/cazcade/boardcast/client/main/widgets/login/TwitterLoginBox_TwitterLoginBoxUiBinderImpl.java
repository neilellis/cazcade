package cazcade.boardcast.client.main.widgets.login;

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

public class TwitterLoginBox_TwitterLoginBoxUiBinderImpl implements UiBinder<com.google.gwt.user.client.ui.HTMLPanel, cazcade.boardcast.client.main.widgets.login.TwitterLoginBox>, cazcade.boardcast.client.main.widgets.login.TwitterLoginBox.TwitterLoginBoxUiBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<span id='{0}'></span>")
    SafeHtml html1(String arg0);
     
  }

  Template template = GWT.create(Template.class);


  public com.google.gwt.user.client.ui.HTMLPanel createAndBindUi(final cazcade.boardcast.client.main.widgets.login.TwitterLoginBox owner) {


    return new Widgets(owner).get_panel();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final cazcade.boardcast.client.main.widgets.login.TwitterLoginBox owner;


    public Widgets(final cazcade.boardcast.client.main.widgets.login.TwitterLoginBox owner) {
      this.owner = owner;
      build_domId0();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId0Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
    }

    SafeHtml template_html1() {
      return template.html1(get_domId0());
    }

    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private cazcade.boardcast.client.main.widgets.login.TwitterLoginBox_TwitterLoginBoxUiBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private cazcade.boardcast.client.main.widgets.login.TwitterLoginBox_TwitterLoginBoxUiBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final cazcade.boardcast.client.main.widgets.login.TwitterLoginBox_TwitterLoginBoxUiBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (cazcade.boardcast.client.main.widgets.login.TwitterLoginBox_TwitterLoginBoxUiBinderImpl_GenBundle) GWT.create(cazcade.boardcast.client.main.widgets.login.TwitterLoginBox_TwitterLoginBoxUiBinderImpl_GenBundle.class);
      // Setup section.


      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for panel called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private com.google.gwt.user.client.ui.HTMLPanel get_panel() {
      return build_panel();
    }
    private com.google.gwt.user.client.ui.HTMLPanel build_panel() {
      // Creation section.
      final com.google.gwt.user.client.ui.HTMLPanel panel = new com.google.gwt.user.client.ui.HTMLPanel(template_html1().asString());
      // Setup section.
      panel.setStyleName("twitter-signin");

      // Attach section.
      UiBinderUtil.TempAttachment attachRecord61 = UiBinderUtil.attachToDom(panel.getElement());
      get_domId0Element().get();

      // Detach section.
      attachRecord61.detach();
      panel.addAndReplaceElement(get_imageButton(), get_domId0Element().get());

      owner.panel = panel;

      return panel;
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
     * Getter for imageButton called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.Image get_imageButton() {
      return build_imageButton();
    }
    private com.google.gwt.user.client.ui.Image build_imageButton() {
      // Creation section.
      final com.google.gwt.user.client.ui.Image imageButton = (com.google.gwt.user.client.ui.Image) GWT.create(com.google.gwt.user.client.ui.Image.class);
      // Setup section.
      imageButton.setUrl("./_images/Sign-in-with-Twitter-darker.png");


      owner.imageButton = imageButton;

      return imageButton;
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
  }
}
