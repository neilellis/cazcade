package cazcade.vortex.widgets.client.stream;

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

public class VortexStatusUpdatePanel_VortexStatusUpdatePanelUiBinderImpl implements UiBinder<com.google.gwt.user.client.ui.HTMLPanel, cazcade.vortex.widgets.client.stream.VortexStatusUpdatePanel>, cazcade.vortex.widgets.client.stream.VortexStatusUpdatePanel.VortexStatusUpdatePanelUiBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<span class='stream-status-update-user-panel'> <span id='{0}'></span> </span> <p class='stream-entry-main'> <span class='stream-entry-message' id='{1}'></span> <span class='stream-entry-details'> <span id='{2}'></span> </span> </p>")
    SafeHtml html1(String arg0, String arg1, String arg2);
     
  }

  Template template = GWT.create(Template.class);


  public com.google.gwt.user.client.ui.HTMLPanel createAndBindUi(final cazcade.vortex.widgets.client.stream.VortexStatusUpdatePanel owner) {


    return new Widgets(owner).get_f_HTMLPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final cazcade.vortex.widgets.client.stream.VortexStatusUpdatePanel owner;


    public Widgets(final cazcade.vortex.widgets.client.stream.VortexStatusUpdatePanel owner) {
      this.owner = owner;
      build_domId0();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId1();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId2();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId0Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId2Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
    }

    SafeHtml template_html1() {
      return template.html1(get_domId0(), get_domId1(), get_domId2());
    }

    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private cazcade.vortex.widgets.client.stream.VortexStatusUpdatePanel_VortexStatusUpdatePanelUiBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private cazcade.vortex.widgets.client.stream.VortexStatusUpdatePanel_VortexStatusUpdatePanelUiBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final cazcade.vortex.widgets.client.stream.VortexStatusUpdatePanel_VortexStatusUpdatePanelUiBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (cazcade.vortex.widgets.client.stream.VortexStatusUpdatePanel_VortexStatusUpdatePanelUiBinderImpl_GenBundle) GWT.create(cazcade.vortex.widgets.client.stream.VortexStatusUpdatePanel_VortexStatusUpdatePanelUiBinderImpl_GenBundle.class);
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
      final com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template_html1().asString());
      // Setup section.
      f_HTMLPanel1.setStyleName("stream-status-update-panel");

      // Attach section.
      UiBinderUtil.TempAttachment attachRecord22 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
      get_domId0Element().get();
      get_text();
      get_domId2Element().get();

      // Detach section.
      attachRecord22.detach();
      f_HTMLPanel1.addAndReplaceElement(get_profileImage(), get_domId0Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_dateTime(), get_domId2Element().get());

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
     * Getter for profileImage called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.vortex.widgets.client.image.UserProfileImage get_profileImage() {
      return build_profileImage();
    }
    private cazcade.vortex.widgets.client.image.UserProfileImage build_profileImage() {
      // Creation section.
      final cazcade.vortex.widgets.client.image.UserProfileImage profileImage = (cazcade.vortex.widgets.client.image.UserProfileImage) GWT.create(cazcade.vortex.widgets.client.image.UserProfileImage.class);
      // Setup section.
      profileImage.setStyleName("stream-author-icon");


      owner.profileImage = profileImage;

      return profileImage;
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
     * Getter for text called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.dom.client.SpanElement get_text() {
      return build_text();
    }
    private com.google.gwt.dom.client.SpanElement build_text() {
      // Creation section.
      final com.google.gwt.dom.client.SpanElement text = new com.google.gwt.uibinder.client.LazyDomElement(get_domId1()).get().cast();
      // Setup section.


      owner.text = text;

      return text;
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
     * Getter for domId2 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 2.
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
     * Getter for dateTime called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.vortex.widgets.client.date.SelfUpdatingRelativeDate get_dateTime() {
      return build_dateTime();
    }
    private cazcade.vortex.widgets.client.date.SelfUpdatingRelativeDate build_dateTime() {
      // Creation section.
      final cazcade.vortex.widgets.client.date.SelfUpdatingRelativeDate dateTime = (cazcade.vortex.widgets.client.date.SelfUpdatingRelativeDate) GWT.create(cazcade.vortex.widgets.client.date.SelfUpdatingRelativeDate.class);
      // Setup section.
      dateTime.setStyleName("stream-entry-time");


      owner.dateTime = dateTime;

      return dateTime;
    }

    /**
     * Getter for domId2Element called 2 times. Type: DEFAULT. Build precedence: 2.
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
  }
}
