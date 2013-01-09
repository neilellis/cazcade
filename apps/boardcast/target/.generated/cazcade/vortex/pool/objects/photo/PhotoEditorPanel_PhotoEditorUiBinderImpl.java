package cazcade.vortex.pool.objects.photo;

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

public class PhotoEditorPanel_PhotoEditorUiBinderImpl implements UiBinder<com.google.gwt.user.client.ui.HTMLPanel, cazcade.vortex.pool.objects.photo.PhotoEditorPanel>, cazcade.vortex.pool.objects.photo.PhotoEditorPanel.PhotoEditorUiBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<div class='span12'> <div class='form-stacked'> <div class='row'> <div class='span5'> <h3>Create/Edit Image</h3> <label>Title <small>(optional)</small> </label> <span id='{0}'></span> <label>Description <small>(optional)</small> </label> <span id='{1}'></span> </div> <div class='span6'> <span id='{2}'></span> </div> </div> </div> </div>")
    SafeHtml html1(String arg0, String arg1, String arg2);
     
  }

  Template template = GWT.create(Template.class);


  public com.google.gwt.user.client.ui.HTMLPanel createAndBindUi(final cazcade.vortex.pool.objects.photo.PhotoEditorPanel owner) {


    return new Widgets(owner).get_f_HTMLPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final cazcade.vortex.pool.objects.photo.PhotoEditorPanel owner;


    public Widgets(final cazcade.vortex.pool.objects.photo.PhotoEditorPanel owner) {
      this.owner = owner;
      build_domId0();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId1();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId2();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId0Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId1Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId2Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
    }

    SafeHtml template_html1() {
      return template.html1(get_domId0(), get_domId1(), get_domId2());
    }

    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private cazcade.vortex.pool.objects.photo.PhotoEditorPanel_PhotoEditorUiBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private cazcade.vortex.pool.objects.photo.PhotoEditorPanel_PhotoEditorUiBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final cazcade.vortex.pool.objects.photo.PhotoEditorPanel_PhotoEditorUiBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (cazcade.vortex.pool.objects.photo.PhotoEditorPanel_PhotoEditorUiBinderImpl_GenBundle) GWT.create(cazcade.vortex.pool.objects.photo.PhotoEditorPanel_PhotoEditorUiBinderImpl_GenBundle.class);
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

      // Attach section.
      UiBinderUtil.TempAttachment attachRecord70 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
      get_domId0Element().get();
      get_domId1Element().get();
      get_domId2Element().get();

      // Detach section.
      attachRecord70.detach();
      f_HTMLPanel1.addAndReplaceElement(get_title(), get_domId0Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_description(), get_domId1Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_changeImagePanel(), get_domId2Element().get());

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
     * Getter for title called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.vortex.widgets.client.form.fields.RegexTextBox get_title() {
      return build_title();
    }
    private cazcade.vortex.widgets.client.form.fields.RegexTextBox build_title() {
      // Creation section.
      final cazcade.vortex.widgets.client.form.fields.RegexTextBox title = (cazcade.vortex.widgets.client.form.fields.RegexTextBox) GWT.create(cazcade.vortex.widgets.client.form.fields.RegexTextBox.class);
      // Setup section.


      owner.title = title;

      return title;
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
     * Getter for description called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.vortex.widgets.client.form.fields.VortexTextArea get_description() {
      return build_description();
    }
    private cazcade.vortex.widgets.client.form.fields.VortexTextArea build_description() {
      // Creation section.
      final cazcade.vortex.widgets.client.form.fields.VortexTextArea description = (cazcade.vortex.widgets.client.form.fields.VortexTextArea) GWT.create(cazcade.vortex.widgets.client.form.fields.VortexTextArea.class);
      // Setup section.


      owner.description = description;

      return description;
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
     * Getter for changeImagePanel called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.vortex.widgets.client.form.fields.ChangeImageUrlPanel get_changeImagePanel() {
      return build_changeImagePanel();
    }
    private cazcade.vortex.widgets.client.form.fields.ChangeImageUrlPanel build_changeImagePanel() {
      // Creation section.
      final cazcade.vortex.widgets.client.form.fields.ChangeImageUrlPanel changeImagePanel = (cazcade.vortex.widgets.client.form.fields.ChangeImageUrlPanel) GWT.create(cazcade.vortex.widgets.client.form.fields.ChangeImageUrlPanel.class);
      // Setup section.


      owner.changeImagePanel = changeImagePanel;

      return changeImagePanel;
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
