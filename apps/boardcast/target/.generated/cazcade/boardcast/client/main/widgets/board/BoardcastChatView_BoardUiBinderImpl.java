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

public class BoardcastChatView_BoardUiBinderImpl implements UiBinder<com.google.gwt.user.client.ui.HTMLPanel, cazcade.boardcast.client.main.widgets.board.BoardcastChatView>, cazcade.boardcast.client.main.widgets.board.BoardcastChatView.BoardUiBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<span id='{0}'></span>")
    SafeHtml html1(String arg0);
     
    @Template("<span id='{0}'></span>")
    SafeHtml html2(String arg0);
     
    @Template("<img src='_images/toolbar/hide-rhs.png'>")
    SafeHtml html3();
     
    @Template("<img src='_images/toolbar/reveal-rhs.png'>")
    SafeHtml html4();
     
    @Template("<img src='_images/toolbar/hide-rhs-hover.png'>")
    SafeHtml html5();
     
    @Template("<img src='_images/toolbar/reveal-rhs-hover.png'>")
    SafeHtml html6();
     
    @Template("<span id='{0}'></span>")
    SafeHtml html7(String arg0);
     
    @Template("<span id='{0}'></span> <div class='hashbo-chat'> <span id='{1}'></span> </div>")
    SafeHtml html8(String arg0, String arg1);
     
    @Template("<span id='{0}'></span>")
    SafeHtml html9(String arg0);
     
    @Template("<span id='{0}'></span> <span id='{1}'></span> <span id='{2}'></span>        <div class='board-locked' id='{3}' title='You don&#39;t have permission to edit this board.'></div> <span id='{4}'></span>")
    SafeHtml html10(String arg0, String arg1, String arg2, String arg3, String arg4);
     
  }

  Template template = GWT.create(Template.class);


  public com.google.gwt.user.client.ui.HTMLPanel createAndBindUi(final cazcade.boardcast.client.main.widgets.board.BoardcastChatView owner) {


    return new Widgets(owner).get_board();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final cazcade.boardcast.client.main.widgets.board.BoardcastChatView owner;


    public Widgets(final cazcade.boardcast.client.main.widgets.board.BoardcastChatView owner) {
      this.owner = owner;
      build_domId7();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 6
      build_domId2();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 4
      build_domId5();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 4
      build_domId6();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 4
      build_domId1();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 3
      build_domId4();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 3
      build_domId0();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId3();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId8();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId9();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId10();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId7Element();  // more than one getter call detected. Type: DEFAULT, precedence: 6
      build_domId2Element();  // more than one getter call detected. Type: DEFAULT, precedence: 4
      build_domId5Element();  // more than one getter call detected. Type: DEFAULT, precedence: 4
      build_domId6Element();  // more than one getter call detected. Type: DEFAULT, precedence: 4
      build_domId1Element();  // more than one getter call detected. Type: DEFAULT, precedence: 3
      build_domId4Element();  // more than one getter call detected. Type: DEFAULT, precedence: 3
      build_domId0Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId3Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId8Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId10Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
    }

    SafeHtml template_html1() {
      return template.html1(get_domId2());
    }
    SafeHtml template_html2() {
      return template.html2(get_domId1());
    }
    SafeHtml template_html3() {
      return template.html3();
    }
    SafeHtml template_html4() {
      return template.html4();
    }
    SafeHtml template_html5() {
      return template.html5();
    }
    SafeHtml template_html6() {
      return template.html6();
    }
    SafeHtml template_html7() {
      return template.html7(get_domId7());
    }
    SafeHtml template_html8() {
      return template.html8(get_domId5(), get_domId6());
    }
    SafeHtml template_html9() {
      return template.html9(get_domId4());
    }
    SafeHtml template_html10() {
      return template.html10(get_domId0(), get_domId3(), get_domId8(), get_domId9(), get_domId10());
    }

    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private cazcade.boardcast.client.main.widgets.board.BoardcastChatView_BoardUiBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private cazcade.boardcast.client.main.widgets.board.BoardcastChatView_BoardUiBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final cazcade.boardcast.client.main.widgets.board.BoardcastChatView_BoardUiBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (cazcade.boardcast.client.main.widgets.board.BoardcastChatView_BoardUiBinderImpl_GenBundle) GWT.create(cazcade.boardcast.client.main.widgets.board.BoardcastChatView_BoardUiBinderImpl_GenBundle.class);
      // Setup section.


      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for board called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private com.google.gwt.user.client.ui.HTMLPanel get_board() {
      return build_board();
    }
    private com.google.gwt.user.client.ui.HTMLPanel build_board() {
      // Creation section.
      final com.google.gwt.user.client.ui.HTMLPanel board = new com.google.gwt.user.client.ui.HTMLPanel(template_html10().asString());
      // Setup section.
      board.setStyleName("hashbo-app");

      // Attach section.
      UiBinderUtil.TempAttachment attachRecord3 = UiBinderUtil.attachToDom(board.getElement());
      get_domId0Element().get();
      get_domId3Element().get();
      get_domId8Element().get();
      get_boardLockedIcon();
      get_domId10Element().get();

      // Detach section.
      attachRecord3.detach();
      board.addAndReplaceElement(get_f_HTMLPanel1(), get_domId0Element().get());
      board.addAndReplaceElement(get_rhs(), get_domId3Element().get());
      board.addAndReplaceElement(get_menuBar(), get_domId8Element().get());
      board.addAndReplaceElement(get_returnFromChatButton(), get_domId10Element().get());

      owner.board = board;

      return board;
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
     * Getter for f_HTMLPanel1 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.HTMLPanel get_f_HTMLPanel1() {
      return build_f_HTMLPanel1();
    }
    private com.google.gwt.user.client.ui.HTMLPanel build_f_HTMLPanel1() {
      // Creation section.
      final com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template_html2().asString());
      // Setup section.
      f_HTMLPanel1.setStyleName("hashbo-main");

      // Attach section.
      UiBinderUtil.TempAttachment attachRecord4 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
      get_domId1Element().get();

      // Detach section.
      attachRecord4.detach();
      f_HTMLPanel1.addAndReplaceElement(get_f_HTMLPanel2(), get_domId1Element().get());

      return f_HTMLPanel1;
    }

    /**
     * Getter for domId1 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 3.
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
     * Getter for f_HTMLPanel2 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private com.google.gwt.user.client.ui.HTMLPanel get_f_HTMLPanel2() {
      return build_f_HTMLPanel2();
    }
    private com.google.gwt.user.client.ui.HTMLPanel build_f_HTMLPanel2() {
      // Creation section.
      final com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel2 = new com.google.gwt.user.client.ui.HTMLPanel(template_html1().asString());
      // Setup section.
      f_HTMLPanel2.setStyleName("board-current-work-area");

      // Attach section.
      UiBinderUtil.TempAttachment attachRecord5 = UiBinderUtil.attachToDom(f_HTMLPanel2.getElement());
      get_domId2Element().get();

      // Detach section.
      attachRecord5.detach();
      f_HTMLPanel2.addAndReplaceElement(get_contentArea(), get_domId2Element().get());

      return f_HTMLPanel2;
    }

    /**
     * Getter for domId2 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 4.
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
     * Getter for contentArea called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private cazcade.vortex.pool.widgets.PoolContentArea get_contentArea() {
      return build_contentArea();
    }
    private cazcade.vortex.pool.widgets.PoolContentArea build_contentArea() {
      // Creation section.
      final cazcade.vortex.pool.widgets.PoolContentArea contentArea = new cazcade.vortex.pool.widgets.PoolContentArea(true, false, true);
      // Setup section.


      owner.contentArea = contentArea;

      return contentArea;
    }

    /**
     * Getter for domId2Element called 2 times. Type: DEFAULT. Build precedence: 4.
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
     * Getter for domId1Element called 2 times. Type: DEFAULT. Build precedence: 3.
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
     * Getter for rhs called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.HTMLPanel get_rhs() {
      return build_rhs();
    }
    private com.google.gwt.user.client.ui.HTMLPanel build_rhs() {
      // Creation section.
      final com.google.gwt.user.client.ui.HTMLPanel rhs = new com.google.gwt.user.client.ui.HTMLPanel(template_html9().asString());
      // Setup section.
      rhs.setStyleName("hashbo-rhs");

      // Attach section.
      UiBinderUtil.TempAttachment attachRecord6 = UiBinderUtil.attachToDom(rhs.getElement());
      get_domId4Element().get();

      // Detach section.
      attachRecord6.detach();
      rhs.addAndReplaceElement(get_f_HTMLPanel3(), get_domId4Element().get());

      owner.rhs = rhs;

      return rhs;
    }

    /**
     * Getter for domId4 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 3.
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
     * Getter for f_HTMLPanel3 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private com.google.gwt.user.client.ui.HTMLPanel get_f_HTMLPanel3() {
      return build_f_HTMLPanel3();
    }
    private com.google.gwt.user.client.ui.HTMLPanel build_f_HTMLPanel3() {
      // Creation section.
      final com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel3 = new com.google.gwt.user.client.ui.HTMLPanel(template_html8().asString());
      // Setup section.
      f_HTMLPanel3.setStyleName("hashbo-rhs-inner");

      // Attach section.
      UiBinderUtil.TempAttachment attachRecord7 = UiBinderUtil.attachToDom(f_HTMLPanel3.getElement());
      get_domId5Element().get();
      get_domId6Element().get();

      // Detach section.
      attachRecord7.detach();
      f_HTMLPanel3.addAndReplaceElement(get_hideReveal(), get_domId5Element().get());
      f_HTMLPanel3.addAndReplaceElement(get_f_DockLayoutPanel4(), get_domId6Element().get());

      return f_HTMLPanel3;
    }

    /**
     * Getter for domId5 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 4.
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
     * Getter for hideReveal called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private com.google.gwt.user.client.ui.ToggleButton get_hideReveal() {
      return build_hideReveal();
    }
    private com.google.gwt.user.client.ui.ToggleButton build_hideReveal() {
      // Creation section.
      final com.google.gwt.user.client.ui.ToggleButton hideReveal = (com.google.gwt.user.client.ui.ToggleButton) GWT.create(com.google.gwt.user.client.ui.ToggleButton.class);
      // Setup section.
      hideReveal.getUpFace().setHTML(template_html3().asString());
      hideReveal.getDownFace().setHTML(template_html4().asString());
      hideReveal.getUpHoveringFace().setHTML(template_html5().asString());
      hideReveal.getDownHoveringFace().setHTML(template_html6().asString());
      hideReveal.setStyleName("board-rhs-reveal-hide");


      owner.hideReveal = hideReveal;

      return hideReveal;
    }

    /**
     * Getter for domId5Element called 2 times. Type: DEFAULT. Build precedence: 4.
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

    /**
     * Getter for domId6 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 4.
     */
    private java.lang.String domId6;
    private java.lang.String get_domId6() {
      return domId6;
    }
    private java.lang.String build_domId6() {
      // Creation section.
      domId6 = com.google.gwt.dom.client.Document.get().createUniqueId();
      // Setup section.


      return domId6;
    }

    /**
     * Getter for f_DockLayoutPanel4 called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private com.google.gwt.user.client.ui.DockLayoutPanel get_f_DockLayoutPanel4() {
      return build_f_DockLayoutPanel4();
    }
    private com.google.gwt.user.client.ui.DockLayoutPanel build_f_DockLayoutPanel4() {
      // Creation section.
      final com.google.gwt.user.client.ui.DockLayoutPanel f_DockLayoutPanel4 = new com.google.gwt.user.client.ui.DockLayoutPanel(com.google.gwt.dom.client.Style.Unit.PX);
      // Setup section.
      f_DockLayoutPanel4.addSouth(get_f_HTMLPanel5(), 60);
      f_DockLayoutPanel4.add(get_stream());
      f_DockLayoutPanel4.setHeight("100%");


      return f_DockLayoutPanel4;
    }

    /**
     * Getter for stream called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private cazcade.vortex.widgets.client.stream.ChatStreamPanel get_stream() {
      return build_stream();
    }
    private cazcade.vortex.widgets.client.stream.ChatStreamPanel build_stream() {
      // Creation section.
      final cazcade.vortex.widgets.client.stream.ChatStreamPanel stream = (cazcade.vortex.widgets.client.stream.ChatStreamPanel) GWT.create(cazcade.vortex.widgets.client.stream.ChatStreamPanel.class);
      // Setup section.
      stream.setStyleName("board-stream-panel");


      owner.stream = stream;

      return stream;
    }

    /**
     * Getter for f_HTMLPanel5 called 1 times. Type: DEFAULT. Build precedence: 5.
     */
    private com.google.gwt.user.client.ui.HTMLPanel get_f_HTMLPanel5() {
      return build_f_HTMLPanel5();
    }
    private com.google.gwt.user.client.ui.HTMLPanel build_f_HTMLPanel5() {
      // Creation section.
      final com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel5 = new com.google.gwt.user.client.ui.HTMLPanel(template_html7().asString());
      // Setup section.
      f_HTMLPanel5.setStyleName("board-status-panel-create-panel");

      // Attach section.
      UiBinderUtil.TempAttachment attachRecord8 = UiBinderUtil.attachToDom(f_HTMLPanel5.getElement());
      get_domId7Element().get();

      // Detach section.
      attachRecord8.detach();
      f_HTMLPanel5.addAndReplaceElement(get_addChatBox(), get_domId7Element().get());

      return f_HTMLPanel5;
    }

    /**
     * Getter for domId7 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 6.
     */
    private java.lang.String domId7;
    private java.lang.String get_domId7() {
      return domId7;
    }
    private java.lang.String build_domId7() {
      // Creation section.
      domId7 = com.google.gwt.dom.client.Document.get().createUniqueId();
      // Setup section.


      return domId7;
    }

    /**
     * Getter for addChatBox called 1 times. Type: DEFAULT. Build precedence: 6.
     */
    private cazcade.boardcast.client.main.widgets.AddChatBox get_addChatBox() {
      return build_addChatBox();
    }
    private cazcade.boardcast.client.main.widgets.AddChatBox build_addChatBox() {
      // Creation section.
      final cazcade.boardcast.client.main.widgets.AddChatBox addChatBox = (cazcade.boardcast.client.main.widgets.AddChatBox) GWT.create(cazcade.boardcast.client.main.widgets.AddChatBox.class);
      // Setup section.


      owner.addChatBox = addChatBox;

      return addChatBox;
    }

    /**
     * Getter for domId7Element called 2 times. Type: DEFAULT. Build precedence: 6.
     */
    private com.google.gwt.uibinder.client.LazyDomElement domId7Element;
    private com.google.gwt.uibinder.client.LazyDomElement get_domId7Element() {
      return domId7Element;
    }
    private com.google.gwt.uibinder.client.LazyDomElement build_domId7Element() {
      // Creation section.
      domId7Element = new com.google.gwt.uibinder.client.LazyDomElement<Element>(get_domId7());
      // Setup section.


      return domId7Element;
    }

    /**
     * Getter for domId6Element called 2 times. Type: DEFAULT. Build precedence: 4.
     */
    private com.google.gwt.uibinder.client.LazyDomElement domId6Element;
    private com.google.gwt.uibinder.client.LazyDomElement get_domId6Element() {
      return domId6Element;
    }
    private com.google.gwt.uibinder.client.LazyDomElement build_domId6Element() {
      // Creation section.
      domId6Element = new com.google.gwt.uibinder.client.LazyDomElement<Element>(get_domId6());
      // Setup section.


      return domId6Element;
    }

    /**
     * Getter for domId4Element called 2 times. Type: DEFAULT. Build precedence: 3.
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
     * Getter for domId8 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 2.
     */
    private java.lang.String domId8;
    private java.lang.String get_domId8() {
      return domId8;
    }
    private java.lang.String build_domId8() {
      // Creation section.
      domId8 = com.google.gwt.dom.client.Document.get().createUniqueId();
      // Setup section.


      return domId8;
    }

    /**
     * Getter for menuBar called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.boardcast.client.main.widgets.BoardMenuBar get_menuBar() {
      return build_menuBar();
    }
    private cazcade.boardcast.client.main.widgets.BoardMenuBar build_menuBar() {
      // Creation section.
      final cazcade.boardcast.client.main.widgets.BoardMenuBar menuBar = (cazcade.boardcast.client.main.widgets.BoardMenuBar) GWT.create(cazcade.boardcast.client.main.widgets.BoardMenuBar.class);
      // Setup section.
      menuBar.setStyleName("hashbo-board-menu");
      menuBar.setFocusOnHoverEnabled(true);
      menuBar.setAutoOpen(true);


      owner.menuBar = menuBar;

      return menuBar;
    }

    /**
     * Getter for domId8Element called 2 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.uibinder.client.LazyDomElement domId8Element;
    private com.google.gwt.uibinder.client.LazyDomElement get_domId8Element() {
      return domId8Element;
    }
    private com.google.gwt.uibinder.client.LazyDomElement build_domId8Element() {
      // Creation section.
      domId8Element = new com.google.gwt.uibinder.client.LazyDomElement<Element>(get_domId8());
      // Setup section.


      return domId8Element;
    }

    /**
     * Getter for boardLockedIcon called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.dom.client.DivElement get_boardLockedIcon() {
      return build_boardLockedIcon();
    }
    private com.google.gwt.dom.client.DivElement build_boardLockedIcon() {
      // Creation section.
      final com.google.gwt.dom.client.DivElement boardLockedIcon = new com.google.gwt.uibinder.client.LazyDomElement(get_domId9()).get().cast();
      // Setup section.


      owner.boardLockedIcon = boardLockedIcon;

      return boardLockedIcon;
    }

    /**
     * Getter for domId9 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 2.
     */
    private java.lang.String domId9;
    private java.lang.String get_domId9() {
      return domId9;
    }
    private java.lang.String build_domId9() {
      // Creation section.
      domId9 = com.google.gwt.dom.client.Document.get().createUniqueId();
      // Setup section.


      return domId9;
    }

    /**
     * Getter for domId10 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 2.
     */
    private java.lang.String domId10;
    private java.lang.String get_domId10() {
      return domId10;
    }
    private java.lang.String build_domId10() {
      // Creation section.
      domId10 = com.google.gwt.dom.client.Document.get().createUniqueId();
      // Setup section.


      return domId10;
    }

    /**
     * Getter for returnFromChatButton called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.Label get_returnFromChatButton() {
      return build_returnFromChatButton();
    }
    private com.google.gwt.user.client.ui.Label build_returnFromChatButton() {
      // Creation section.
      final com.google.gwt.user.client.ui.Label returnFromChatButton = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
      // Setup section.
      returnFromChatButton.setStyleName("btn large primary return-from-chat-button");
      returnFromChatButton.setText("Leave Chat");


      owner.returnFromChatButton = returnFromChatButton;

      return returnFromChatButton;
    }

    /**
     * Getter for domId10Element called 2 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.uibinder.client.LazyDomElement domId10Element;
    private com.google.gwt.uibinder.client.LazyDomElement get_domId10Element() {
      return domId10Element;
    }
    private com.google.gwt.uibinder.client.LazyDomElement build_domId10Element() {
      // Creation section.
      domId10Element = new com.google.gwt.uibinder.client.LazyDomElement<Element>(get_domId10());
      // Setup section.


      return domId10Element;
    }
  }
}
