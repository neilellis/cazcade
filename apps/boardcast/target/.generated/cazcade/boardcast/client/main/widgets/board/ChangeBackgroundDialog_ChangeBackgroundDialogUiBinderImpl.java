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

public class ChangeBackgroundDialog_ChangeBackgroundDialogUiBinderImpl implements UiBinder<com.google.gwt.user.client.ui.HTMLPanel, cazcade.boardcast.client.main.widgets.board.ChangeBackgroundDialog>, cazcade.boardcast.client.main.widgets.board.ChangeBackgroundDialog.ChangeBackgroundDialogUiBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<strong>Change Background</strong> <span id='{0}'></span> <span id='{1}'></span>")
    SafeHtml html1(String arg0, String arg1);
     
  }

  Template template = GWT.create(Template.class);


  public com.google.gwt.user.client.ui.HTMLPanel createAndBindUi(final cazcade.boardcast.client.main.widgets.board.ChangeBackgroundDialog owner) {


    return new Widgets(owner).get_f_HTMLPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final cazcade.boardcast.client.main.widgets.board.ChangeBackgroundDialog owner;


    public Widgets(final cazcade.boardcast.client.main.widgets.board.ChangeBackgroundDialog owner) {
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
    private cazcade.boardcast.client.main.widgets.board.ChangeBackgroundDialog_ChangeBackgroundDialogUiBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private cazcade.boardcast.client.main.widgets.board.ChangeBackgroundDialog_ChangeBackgroundDialogUiBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final cazcade.boardcast.client.main.widgets.board.ChangeBackgroundDialog_ChangeBackgroundDialogUiBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (cazcade.boardcast.client.main.widgets.board.ChangeBackgroundDialog_ChangeBackgroundDialogUiBinderImpl_GenBundle) GWT.create(cazcade.boardcast.client.main.widgets.board.ChangeBackgroundDialog_ChangeBackgroundDialogUiBinderImpl_GenBundle.class);
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
      f_HTMLPanel1.setStyleName("change-background-panel");

      // Attach section.
      UiBinderUtil.TempAttachment attachRecord24 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
      get_domId0Element().get();
      get_domId1Element().get();

      // Detach section.
      attachRecord24.detach();
      f_HTMLPanel1.addAndReplaceElement(get_changeBackgroundPanel(), get_domId0Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_imageSelector(), get_domId1Element().get());

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
     * Getter for changeBackgroundPanel called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.vortex.widgets.client.form.fields.ChangeImageUrlPanel get_changeBackgroundPanel() {
      return build_changeBackgroundPanel();
    }
    private cazcade.vortex.widgets.client.form.fields.ChangeImageUrlPanel build_changeBackgroundPanel() {
      // Creation section.
      final cazcade.vortex.widgets.client.form.fields.ChangeImageUrlPanel changeBackgroundPanel = (cazcade.vortex.widgets.client.form.fields.ChangeImageUrlPanel) GWT.create(cazcade.vortex.widgets.client.form.fields.ChangeImageUrlPanel.class);
      // Setup section.


      owner.changeBackgroundPanel = changeBackgroundPanel;

      return changeBackgroundPanel;
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
     * Getter for imageSelector called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.vortex.widgets.client.image.ImageSelection get_imageSelector() {
      return build_imageSelector();
    }
    private cazcade.vortex.widgets.client.image.ImageSelection build_imageSelector() {
      // Creation section.
      final cazcade.vortex.widgets.client.image.ImageSelection imageSelector = (cazcade.vortex.widgets.client.image.ImageSelection) GWT.create(cazcade.vortex.widgets.client.image.ImageSelection.class);
      // Setup section.
      imageSelector.addOption(get_f_ImageOption2());
      imageSelector.addOption(get_f_ImageOption3());
      imageSelector.addOption(get_f_ImageOption4());
      imageSelector.addOption(get_f_ImageOption5());
      imageSelector.addOption(get_f_ImageOption6());
      imageSelector.addOption(get_f_ImageOption7());
      imageSelector.addOption(get_f_ImageOption8());
      imageSelector.addOption(get_f_ImageOption9());
      imageSelector.addOption(get_f_ImageOption10());
      imageSelector.addOption(get_f_ImageOption11());
      imageSelector.addOption(get_f_ImageOption12());
      imageSelector.addOption(get_f_ImageOption13());
      imageSelector.addOption(get_f_ImageOption14());


      owner.imageSelector = imageSelector;

      return imageSelector;
    }

    /**
     * Getter for f_ImageOption2 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private cazcade.vortex.widgets.client.image.ImageOption get_f_ImageOption2() {
      return build_f_ImageOption2();
    }
    private cazcade.vortex.widgets.client.image.ImageOption build_f_ImageOption2() {
      // Creation section.
      final cazcade.vortex.widgets.client.image.ImageOption f_ImageOption2 = (cazcade.vortex.widgets.client.image.ImageOption) GWT.create(cazcade.vortex.widgets.client.image.ImageOption.class);
      // Setup section.
      f_ImageOption2.setThumbnail("_background/thumb/misc/corkboard.jpg");
      f_ImageOption2.setUrl("_background/misc/corkboard.jpg");


      return f_ImageOption2;
    }

    /**
     * Getter for f_ImageOption3 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private cazcade.vortex.widgets.client.image.ImageOption get_f_ImageOption3() {
      return build_f_ImageOption3();
    }
    private cazcade.vortex.widgets.client.image.ImageOption build_f_ImageOption3() {
      // Creation section.
      final cazcade.vortex.widgets.client.image.ImageOption f_ImageOption3 = (cazcade.vortex.widgets.client.image.ImageOption) GWT.create(cazcade.vortex.widgets.client.image.ImageOption.class);
      // Setup section.
      f_ImageOption3.setThumbnail("_background/thumb/misc/canvas.jpg");
      f_ImageOption3.setUrl("_background/misc/canvas.jpg");


      return f_ImageOption3;
    }

    /**
     * Getter for f_ImageOption4 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private cazcade.vortex.widgets.client.image.ImageOption get_f_ImageOption4() {
      return build_f_ImageOption4();
    }
    private cazcade.vortex.widgets.client.image.ImageOption build_f_ImageOption4() {
      // Creation section.
      final cazcade.vortex.widgets.client.image.ImageOption f_ImageOption4 = (cazcade.vortex.widgets.client.image.ImageOption) GWT.create(cazcade.vortex.widgets.client.image.ImageOption.class);
      // Setup section.
      f_ImageOption4.setThumbnail("_background/thumb/misc/linen-black.jpg");
      f_ImageOption4.setUrl("_background/misc/linen-black.jpg");


      return f_ImageOption4;
    }

    /**
     * Getter for f_ImageOption5 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private cazcade.vortex.widgets.client.image.ImageOption get_f_ImageOption5() {
      return build_f_ImageOption5();
    }
    private cazcade.vortex.widgets.client.image.ImageOption build_f_ImageOption5() {
      // Creation section.
      final cazcade.vortex.widgets.client.image.ImageOption f_ImageOption5 = (cazcade.vortex.widgets.client.image.ImageOption) GWT.create(cazcade.vortex.widgets.client.image.ImageOption.class);
      // Setup section.
      f_ImageOption5.setThumbnail("_background/thumb/misc/linen-gray.jpg");
      f_ImageOption5.setUrl("_background/misc/linen-gray.jpg");


      return f_ImageOption5;
    }

    /**
     * Getter for f_ImageOption6 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private cazcade.vortex.widgets.client.image.ImageOption get_f_ImageOption6() {
      return build_f_ImageOption6();
    }
    private cazcade.vortex.widgets.client.image.ImageOption build_f_ImageOption6() {
      // Creation section.
      final cazcade.vortex.widgets.client.image.ImageOption f_ImageOption6 = (cazcade.vortex.widgets.client.image.ImageOption) GWT.create(cazcade.vortex.widgets.client.image.ImageOption.class);
      // Setup section.
      f_ImageOption6.setThumbnail("_background/thumb/misc/linen-white-light.jpg");
      f_ImageOption6.setUrl("_background/misc/linen-white-light.jpg");


      return f_ImageOption6;
    }

    /**
     * Getter for f_ImageOption7 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private cazcade.vortex.widgets.client.image.ImageOption get_f_ImageOption7() {
      return build_f_ImageOption7();
    }
    private cazcade.vortex.widgets.client.image.ImageOption build_f_ImageOption7() {
      // Creation section.
      final cazcade.vortex.widgets.client.image.ImageOption f_ImageOption7 = (cazcade.vortex.widgets.client.image.ImageOption) GWT.create(cazcade.vortex.widgets.client.image.ImageOption.class);
      // Setup section.
      f_ImageOption7.setThumbnail("_background/thumb/misc/linen-white.jpg");
      f_ImageOption7.setUrl("_background/misc/linen-white.jpg");


      return f_ImageOption7;
    }

    /**
     * Getter for f_ImageOption8 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private cazcade.vortex.widgets.client.image.ImageOption get_f_ImageOption8() {
      return build_f_ImageOption8();
    }
    private cazcade.vortex.widgets.client.image.ImageOption build_f_ImageOption8() {
      // Creation section.
      final cazcade.vortex.widgets.client.image.ImageOption f_ImageOption8 = (cazcade.vortex.widgets.client.image.ImageOption) GWT.create(cazcade.vortex.widgets.client.image.ImageOption.class);
      // Setup section.
      f_ImageOption8.setThumbnail("_background/thumb/misc/noise-black.jpg");
      f_ImageOption8.setUrl("_background/misc/noise-black.jpg");


      return f_ImageOption8;
    }

    /**
     * Getter for f_ImageOption9 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private cazcade.vortex.widgets.client.image.ImageOption get_f_ImageOption9() {
      return build_f_ImageOption9();
    }
    private cazcade.vortex.widgets.client.image.ImageOption build_f_ImageOption9() {
      // Creation section.
      final cazcade.vortex.widgets.client.image.ImageOption f_ImageOption9 = (cazcade.vortex.widgets.client.image.ImageOption) GWT.create(cazcade.vortex.widgets.client.image.ImageOption.class);
      // Setup section.
      f_ImageOption9.setThumbnail("_background/thumb/misc/noise-grey.jpg");
      f_ImageOption9.setUrl("_background/misc/noise-grey.jpg");


      return f_ImageOption9;
    }

    /**
     * Getter for f_ImageOption10 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private cazcade.vortex.widgets.client.image.ImageOption get_f_ImageOption10() {
      return build_f_ImageOption10();
    }
    private cazcade.vortex.widgets.client.image.ImageOption build_f_ImageOption10() {
      // Creation section.
      final cazcade.vortex.widgets.client.image.ImageOption f_ImageOption10 = (cazcade.vortex.widgets.client.image.ImageOption) GWT.create(cazcade.vortex.widgets.client.image.ImageOption.class);
      // Setup section.
      f_ImageOption10.setThumbnail("_background/thumb/misc/noise-light-grey.jpg");
      f_ImageOption10.setUrl("_background/misc/noise-light-grey.jpg");


      return f_ImageOption10;
    }

    /**
     * Getter for f_ImageOption11 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private cazcade.vortex.widgets.client.image.ImageOption get_f_ImageOption11() {
      return build_f_ImageOption11();
    }
    private cazcade.vortex.widgets.client.image.ImageOption build_f_ImageOption11() {
      // Creation section.
      final cazcade.vortex.widgets.client.image.ImageOption f_ImageOption11 = (cazcade.vortex.widgets.client.image.ImageOption) GWT.create(cazcade.vortex.widgets.client.image.ImageOption.class);
      // Setup section.
      f_ImageOption11.setThumbnail("_background/thumb/misc/noise-vlight-grey.jpg");
      f_ImageOption11.setUrl("_background/misc/noise-vlight-grey.jpg");


      return f_ImageOption11;
    }

    /**
     * Getter for f_ImageOption12 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private cazcade.vortex.widgets.client.image.ImageOption get_f_ImageOption12() {
      return build_f_ImageOption12();
    }
    private cazcade.vortex.widgets.client.image.ImageOption build_f_ImageOption12() {
      // Creation section.
      final cazcade.vortex.widgets.client.image.ImageOption f_ImageOption12 = (cazcade.vortex.widgets.client.image.ImageOption) GWT.create(cazcade.vortex.widgets.client.image.ImageOption.class);
      // Setup section.
      f_ImageOption12.setThumbnail("_background/thumb/misc/noise-white.jpg");
      f_ImageOption12.setUrl("_background/misc/noise-white.jpg");


      return f_ImageOption12;
    }

    /**
     * Getter for f_ImageOption13 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private cazcade.vortex.widgets.client.image.ImageOption get_f_ImageOption13() {
      return build_f_ImageOption13();
    }
    private cazcade.vortex.widgets.client.image.ImageOption build_f_ImageOption13() {
      // Creation section.
      final cazcade.vortex.widgets.client.image.ImageOption f_ImageOption13 = (cazcade.vortex.widgets.client.image.ImageOption) GWT.create(cazcade.vortex.widgets.client.image.ImageOption.class);
      // Setup section.
      f_ImageOption13.setThumbnail("_background/thumb/misc/retro-sunbeam-1.jpg");
      f_ImageOption13.setUrl("_background/misc/retro-sunbeam-1.jpg");


      return f_ImageOption13;
    }

    /**
     * Getter for f_ImageOption14 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private cazcade.vortex.widgets.client.image.ImageOption get_f_ImageOption14() {
      return build_f_ImageOption14();
    }
    private cazcade.vortex.widgets.client.image.ImageOption build_f_ImageOption14() {
      // Creation section.
      final cazcade.vortex.widgets.client.image.ImageOption f_ImageOption14 = (cazcade.vortex.widgets.client.image.ImageOption) GWT.create(cazcade.vortex.widgets.client.image.ImageOption.class);
      // Setup section.
      f_ImageOption14.setThumbnail("_background/thumb/misc/ios-linen-light-tan.png");
      f_ImageOption14.setUrl("_background/misc/ios-linen-light-tan.png");


      return f_ImageOption14;
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
