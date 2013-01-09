package cazcade.boardcast.client.main.widgets.board;

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

public class PublicBoardHeader_PublicBoardHeaderUiBinderImpl implements UiBinder<com.google.gwt.user.client.ui.HTMLPanel, cazcade.boardcast.client.main.widgets.board.PublicBoardHeader>, cazcade.boardcast.client.main.widgets.board.PublicBoardHeader.PublicBoardHeaderUiBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<div class='page-header'> <div class='page-header-inner'> <div class='board-page-header-content' id='{0}'> <span id='{1}'></span> <span id='{2}'></span> <div class='board-page-header-rhs'> <span id='{3}'></span> <div class='board-link-row'> <div class='board-url'> <span id='{4}'></span> </div> <div class='board-tag'> <span id='{5}'></span> </div> </div> </div> </div> </div>     </div>")
    SafeHtml html1(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5);
     
  }

  Template template = GWT.create(Template.class);


  public com.google.gwt.user.client.ui.HTMLPanel createAndBindUi(final cazcade.boardcast.client.main.widgets.board.PublicBoardHeader owner) {


    return new Widgets(owner).get_f_HTMLPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final cazcade.boardcast.client.main.widgets.board.PublicBoardHeader owner;


    public Widgets(final cazcade.boardcast.client.main.widgets.board.PublicBoardHeader owner) {
      this.owner = owner;
      build_domId0();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId1();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId2();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId3();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId4();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId5();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId1Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId2Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId3Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId4Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId5Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
    }

    SafeHtml template_html1() {
      return template.html1(get_domId0(), get_domId1(), get_domId2(), get_domId3(), get_domId4(), get_domId5());
    }

    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private cazcade.boardcast.client.main.widgets.board.PublicBoardHeader_PublicBoardHeaderUiBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private cazcade.boardcast.client.main.widgets.board.PublicBoardHeader_PublicBoardHeaderUiBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final cazcade.boardcast.client.main.widgets.board.PublicBoardHeader_PublicBoardHeaderUiBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (cazcade.boardcast.client.main.widgets.board.PublicBoardHeader_PublicBoardHeaderUiBinderImpl_GenBundle) GWT.create(cazcade.boardcast.client.main.widgets.board.PublicBoardHeader_PublicBoardHeaderUiBinderImpl_GenBundle.class);
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
      UiBinderUtil.TempAttachment attachRecord30 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
      get_contentArea();
      get_domId1Element().get();
      get_domId2Element().get();
      get_domId3Element().get();
      get_domId4Element().get();
      get_domId5Element().get();

      // Detach section.
      attachRecord30.detach();
      f_HTMLPanel1.addAndReplaceElement(get_title(), get_domId1Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_description(), get_domId2Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_boardIcon(), get_domId3Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_url(), get_domId4Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_tag(), get_domId5Element().get());

      return f_HTMLPanel1;
    }

    /**
     * Getter for contentArea called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.dom.client.DivElement get_contentArea() {
      return build_contentArea();
    }
    private com.google.gwt.dom.client.DivElement build_contentArea() {
      // Creation section.
      final com.google.gwt.dom.client.DivElement contentArea = new com.google.gwt.uibinder.client.LazyDomElement(get_domId0()).get().cast();
      // Setup section.


      owner.contentArea = contentArea;

      return contentArea;
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
     * Getter for title called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.vortex.widgets.client.form.fields.VortexEditableLabel get_title() {
      return build_title();
    }
    private cazcade.vortex.widgets.client.form.fields.VortexEditableLabel build_title() {
      // Creation section.
      final cazcade.vortex.widgets.client.form.fields.VortexEditableLabel title = (cazcade.vortex.widgets.client.form.fields.VortexEditableLabel) GWT.create(cazcade.vortex.widgets.client.form.fields.VortexEditableLabel.class);
      // Setup section.
      title.setStyleName("board-title");
      title.setPlaceholder("Click to name");


      owner.title = title;

      return title;
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
     * Getter for description called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.vortex.widgets.client.form.fields.VortexEditableLabel get_description() {
      return build_description();
    }
    private cazcade.vortex.widgets.client.form.fields.VortexEditableLabel build_description() {
      // Creation section.
      final cazcade.vortex.widgets.client.form.fields.VortexEditableLabel description = (cazcade.vortex.widgets.client.form.fields.VortexEditableLabel) GWT.create(cazcade.vortex.widgets.client.form.fields.VortexEditableLabel.class);
      // Setup section.
      description.setStyleName("board-description");
      description.setPlaceholder("Click to edit the short description of the board");


      owner.description = description;

      return description;
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

    /**
     * Getter for domId3 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 2.
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
     * Getter for boardIcon called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.vortex.widgets.client.image.EditableImage get_boardIcon() {
      return build_boardIcon();
    }
    private cazcade.vortex.widgets.client.image.EditableImage build_boardIcon() {
      // Creation section.
      final cazcade.vortex.widgets.client.image.EditableImage boardIcon = (cazcade.vortex.widgets.client.image.EditableImage) GWT.create(cazcade.vortex.widgets.client.image.EditableImage.class);
      // Setup section.
      boardIcon.setStyleName("board-icon");
      boardIcon.setHeight("60px");
      boardIcon.setVisible(false);
      boardIcon.setWidth("90px");
      boardIcon.setSize("CLIPPED_SMALL");


      owner.boardIcon = boardIcon;

      return boardIcon;
    }

    /**
     * Getter for domId3Element called 2 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.uibinder.client.LazyDomElement domId3Element;
    private com.google.gwt.uibinder.client.LazyDomElement get_domId3Element() {
      return domId3Element;
    }
    private com.google.gwt.uibinder.client.LazyDomElement build_domId3Element() {
      // Creation section.
      domId3Element = new com.google.gwt.uibinder.client.LazyDomElement<Element>(get_domId3());
      // Setup section.


      return domId3Element;
    }

    /**
     * Getter for domId4 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 2.
     */
    private java.lang.String domId4;
    private java.lang.String get_domId4() {
      return domId4;
    }
    private java.lang.String build_domId4() {
      // Creation section.
      domId4 = com.google.gwt.dom.client.Document.get().createUniqueId();
      // Setup section.


      return domId4;
    }

    /**
     * Getter for url called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.vortex.widgets.client.form.fields.VortexEditableLabel get_url() {
      return build_url();
    }
    private cazcade.vortex.widgets.client.form.fields.VortexEditableLabel build_url() {
      // Creation section.
      final cazcade.vortex.widgets.client.form.fields.VortexEditableLabel url = (cazcade.vortex.widgets.client.form.fields.VortexEditableLabel) GWT.create(cazcade.vortex.widgets.client.form.fields.VortexEditableLabel.class);
      // Setup section.
      url.setVisible(false);
      url.setReadonly(true);


      owner.url = url;

      return url;
    }

    /**
     * Getter for domId4Element called 2 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.uibinder.client.LazyDomElement domId4Element;
    private com.google.gwt.uibinder.client.LazyDomElement get_domId4Element() {
      return domId4Element;
    }
    private com.google.gwt.uibinder.client.LazyDomElement build_domId4Element() {
      // Creation section.
      domId4Element = new com.google.gwt.uibinder.client.LazyDomElement<Element>(get_domId4());
      // Setup section.


      return domId4Element;
    }

    /**
     * Getter for domId5 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 2.
     */
    private java.lang.String domId5;
    private java.lang.String get_domId5() {
      return domId5;
    }
    private java.lang.String build_domId5() {
      // Creation section.
      domId5 = com.google.gwt.dom.client.Document.get().createUniqueId();
      // Setup section.


      return domId5;
    }

    /**
     * Getter for tag called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.vortex.widgets.client.form.fields.VortexEditableLabel get_tag() {
      return build_tag();
    }
    private cazcade.vortex.widgets.client.form.fields.VortexEditableLabel build_tag() {
      // Creation section.
      final cazcade.vortex.widgets.client.form.fields.VortexEditableLabel tag = (cazcade.vortex.widgets.client.form.fields.VortexEditableLabel) GWT.create(cazcade.vortex.widgets.client.form.fields.VortexEditableLabel.class);
      // Setup section.
      tag.setVisible(false);
      tag.setReadonly(true);


      owner.tag = tag;

      return tag;
    }

    /**
     * Getter for domId5Element called 2 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.uibinder.client.LazyDomElement domId5Element;
    private com.google.gwt.uibinder.client.LazyDomElement get_domId5Element() {
      return domId5Element;
    }
    private com.google.gwt.uibinder.client.LazyDomElement build_domId5Element() {
      // Creation section.
      domId5Element = new com.google.gwt.uibinder.client.LazyDomElement<Element>(get_domId5());
      // Setup section.


      return domId5Element;
    }
  }
}
