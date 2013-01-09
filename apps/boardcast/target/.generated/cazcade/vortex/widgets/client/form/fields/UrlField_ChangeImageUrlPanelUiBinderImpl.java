package cazcade.vortex.widgets.client.form.fields;

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

public class UrlField_ChangeImageUrlPanelUiBinderImpl implements UiBinder<com.google.gwt.user.client.ui.HTMLPanel, cazcade.vortex.widgets.client.form.fields.UrlField>, cazcade.vortex.widgets.client.form.fields.UrlField.ChangeImageUrlPanelUiBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<span id='{0}'></span> <div class='url-field-textbox-area'> <small>You can supply the URL of a web page or of an image. If you supply a web page URL here we will take a snapshot and use that. </small> <label>Url</label> <span id='{1}'></span> </div>")
    SafeHtml html1(String arg0, String arg1);
     
  }

  Template template = GWT.create(Template.class);


  public com.google.gwt.user.client.ui.HTMLPanel createAndBindUi(final cazcade.vortex.widgets.client.form.fields.UrlField owner) {


    return new Widgets(owner).get_f_HTMLPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final cazcade.vortex.widgets.client.form.fields.UrlField owner;


    public Widgets(final cazcade.vortex.widgets.client.form.fields.UrlField owner) {
      this.owner = owner;
      build_domId0();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId1();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId0Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId1Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
    }

    SafeHtml template_html1() {
      return template.html1(get_domId0(), get_domId1());
    }

    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private cazcade.vortex.widgets.client.form.fields.UrlField_ChangeImageUrlPanelUiBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private cazcade.vortex.widgets.client.form.fields.UrlField_ChangeImageUrlPanelUiBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final cazcade.vortex.widgets.client.form.fields.UrlField_ChangeImageUrlPanelUiBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (cazcade.vortex.widgets.client.form.fields.UrlField_ChangeImageUrlPanelUiBinderImpl_GenBundle) GWT.create(cazcade.vortex.widgets.client.form.fields.UrlField_ChangeImageUrlPanelUiBinderImpl_GenBundle.class);
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
      f_HTMLPanel1.setStyleName("url-field");

      // Attach section.
      UiBinderUtil.TempAttachment attachRecord91 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
      get_domId0Element().get();
      get_domId1Element().get();

      // Detach section.
      attachRecord91.detach();
      f_HTMLPanel1.addAndReplaceElement(get_previewImage(), get_domId0Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_urlField(), get_domId1Element().get());

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
     * Getter for previewImage called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.vortex.widgets.client.image.CachedImage get_previewImage() {
      return build_previewImage();
    }
    private cazcade.vortex.widgets.client.image.CachedImage build_previewImage() {
      // Creation section.
      final cazcade.vortex.widgets.client.image.CachedImage previewImage = (cazcade.vortex.widgets.client.image.CachedImage) GWT.create(cazcade.vortex.widgets.client.image.CachedImage.class);
      // Setup section.
      previewImage.setStyleName("url-field-image");
      previewImage.setHeight("240px");
      previewImage.setWidth("320px");


      owner.previewImage = previewImage;

      return previewImage;
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
     * Getter for urlField called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.vortex.widgets.client.form.fields.RegexTextBox get_urlField() {
      return build_urlField();
    }
    private cazcade.vortex.widgets.client.form.fields.RegexTextBox build_urlField() {
      // Creation section.
      final cazcade.vortex.widgets.client.form.fields.RegexTextBox urlField = (cazcade.vortex.widgets.client.form.fields.RegexTextBox) GWT.create(cazcade.vortex.widgets.client.form.fields.RegexTextBox.class);
      // Setup section.
      urlField.setStyleName("url-field-text-box");
      urlField.setRegex("https?://.*");


      owner.urlField = urlField;

      return urlField;
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
