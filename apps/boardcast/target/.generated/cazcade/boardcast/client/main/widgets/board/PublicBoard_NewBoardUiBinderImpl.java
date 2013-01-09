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

public class PublicBoard_NewBoardUiBinderImpl implements UiBinder<com.google.gwt.user.client.ui.HTMLPanel, cazcade.boardcast.client.main.widgets.board.PublicBoard>, cazcade.boardcast.client.main.widgets.board.PublicBoard.NewBoardUiBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<div class='board-author-info-box'> <h3>About the author</h3> <span id='{0}'></span> </div>")
    SafeHtml html1(String arg0);
     
    @Template("<h3>Board Information</h3> <p> This board was created at <span id='{0}'></span> by <span id='{1}'></span>. <span id='{2}'></span> </p>")
    SafeHtml html2(String arg0, String arg1, String arg2);
     
    @Template("<h3>About Boardcast</h3>   <p>Boardcast is still in the Alpha stage of development which is a technical way of saying there are still a few bugs kicking around and we're still trying to make things look nice. We'd love you to have a play, look around and tell us how we can make your life easier! </p> <p>We have a <a href='http://boardcast.posterous.com'>blog</a>, and a <a href='http://twitter.com/boardcast_it'>Twitter account</a> where you can keep up to date with the latest improvements. </p> <p>If you have any suggestions for us or find something that doesn't work, please drop by our <a href='http://cazcade.zendesk.com'>support website</a>. </p>")
    SafeHtml html3();
     
    @Template("<span id='{0}'></span>")
    SafeHtml html4(String arg0);
     
    @Template("<div class='container' id='{0}'> <div class='content'> <div class='board-share-surround'> <div class='board-share'> <iframe allowtransparency='true' class='tweet-button' frameborder='0' id='{1}' scrolling='no' style='width:100px; height:20px;'></iframe>  </div> </div> <span id='{2}'></span> <span id='{3}'></span> <span id='{4}'></span> <div class='row board-top-menu-row'> <span id='{5}'></span> </div> <hr> <div class='board-content' style=''> <span id='{6}'></span>   </div> <div class='row profile-board-header-bottom'> </div> <hr> <div class='row'> <h2>Comments</h2> <span id='{7}'></span> <span id='{8}'></span> </div> </div> </div> <div class='board-footer' id='{9}'> <div class='container'> <div class='content'> <span id='{10}'></span> </div> </div> </div> <div class='hashbo-chat'> <span id='{11}'></span> </div>")
    SafeHtml html5(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7, String arg8, String arg9, String arg10, String arg11);
     
  }

  Template template = GWT.create(Template.class);


  public com.google.gwt.user.client.ui.HTMLPanel createAndBindUi(final cazcade.boardcast.client.main.widgets.board.PublicBoard owner) {


    return new Widgets(owner).get_f_HTMLPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final cazcade.boardcast.client.main.widgets.board.PublicBoard owner;


    public Widgets(final cazcade.boardcast.client.main.widgets.board.PublicBoard owner) {
      this.owner = owner;
      build_domId11();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 4
      build_domId12();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 4
      build_domId13();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 4
      build_domId14();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 4
      build_domId16();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 4
      build_domId0();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId1();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId2();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId3();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId4();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId5();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId6();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId7();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId8();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId9();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId10();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId15();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId11Element();  // more than one getter call detected. Type: DEFAULT, precedence: 4
      build_domId16Element();  // more than one getter call detected. Type: DEFAULT, precedence: 4
      build_domId2Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId3Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId4Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId5Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId6Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId7Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId8Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId10Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId15Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
    }

    SafeHtml template_html1() {
      return template.html1(get_domId11());
    }
    SafeHtml template_html2() {
      return template.html2(get_domId12(), get_domId13(), get_domId14());
    }
    SafeHtml template_html3() {
      return template.html3();
    }
    SafeHtml template_html4() {
      return template.html4(get_domId16());
    }
    SafeHtml template_html5() {
      return template.html5(get_domId0(), get_domId1(), get_domId2(), get_domId3(), get_domId4(), get_domId5(), get_domId6(), get_domId7(), get_domId8(), get_domId9(), get_domId10(), get_domId15());
    }

    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private cazcade.boardcast.client.main.widgets.board.PublicBoard_NewBoardUiBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private cazcade.boardcast.client.main.widgets.board.PublicBoard_NewBoardUiBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final cazcade.boardcast.client.main.widgets.board.PublicBoard_NewBoardUiBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (cazcade.boardcast.client.main.widgets.board.PublicBoard_NewBoardUiBinderImpl_GenBundle) GWT.create(cazcade.boardcast.client.main.widgets.board.PublicBoard_NewBoardUiBinderImpl_GenBundle.class);
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
      final com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template_html5().asString());
      // Setup section.

      // Attach section.
      UiBinderUtil.TempAttachment attachRecord9 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
      get_containerDiv();
      get_tweetButton();
      get_domId2Element().get();
      get_domId3Element().get();
      get_domId4Element().get();
      get_domId5Element().get();
      get_domId6Element().get();
      get_domId7Element().get();
      get_domId8Element().get();
      get_footer();
      get_domId10Element().get();
      get_domId15Element().get();

      // Detach section.
      attachRecord9.detach();
      f_HTMLPanel1.addAndReplaceElement(get_notificationPanel(), get_domId2Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_publicBoardHeader(), get_domId3Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_profileBoardHeader(), get_domId4Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_menuBar(), get_domId5Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_contentArea(), get_domId6Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_addCommentBox(), get_domId7Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_comments(), get_domId8Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_f_DockLayoutPanel2(), get_domId10Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_f_DockLayoutPanel6(), get_domId15Element().get());

      return f_HTMLPanel1;
    }

    /**
     * Getter for containerDiv called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.dom.client.DivElement get_containerDiv() {
      return build_containerDiv();
    }
    private com.google.gwt.dom.client.DivElement build_containerDiv() {
      // Creation section.
      final com.google.gwt.dom.client.DivElement containerDiv = new com.google.gwt.uibinder.client.LazyDomElement(get_domId0()).get().cast();
      // Setup section.


      owner.containerDiv = containerDiv;

      return containerDiv;
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
     * Getter for tweetButton called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.dom.client.IFrameElement get_tweetButton() {
      return build_tweetButton();
    }
    private com.google.gwt.dom.client.IFrameElement build_tweetButton() {
      // Creation section.
      final com.google.gwt.dom.client.IFrameElement tweetButton = new com.google.gwt.uibinder.client.LazyDomElement(get_domId1()).get().cast();
      // Setup section.


      owner.tweetButton = tweetButton;

      return tweetButton;
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
     * Getter for notificationPanel called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.vortex.widgets.client.stream.NotificationPanel get_notificationPanel() {
      return build_notificationPanel();
    }
    private cazcade.vortex.widgets.client.stream.NotificationPanel build_notificationPanel() {
      // Creation section.
      final cazcade.vortex.widgets.client.stream.NotificationPanel notificationPanel = (cazcade.vortex.widgets.client.stream.NotificationPanel) GWT.create(cazcade.vortex.widgets.client.stream.NotificationPanel.class);
      // Setup section.


      owner.notificationPanel = notificationPanel;

      return notificationPanel;
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
     * Getter for publicBoardHeader called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.boardcast.client.main.widgets.board.PublicBoardHeader get_publicBoardHeader() {
      return build_publicBoardHeader();
    }
    private cazcade.boardcast.client.main.widgets.board.PublicBoardHeader build_publicBoardHeader() {
      // Creation section.
      final cazcade.boardcast.client.main.widgets.board.PublicBoardHeader publicBoardHeader = (cazcade.boardcast.client.main.widgets.board.PublicBoardHeader) GWT.create(cazcade.boardcast.client.main.widgets.board.PublicBoardHeader.class);
      // Setup section.
      publicBoardHeader.setVisible(false);


      owner.publicBoardHeader = publicBoardHeader;

      return publicBoardHeader;
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
     * Getter for profileBoardHeader called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.vortex.widgets.client.profile.ProfileBoardHeader get_profileBoardHeader() {
      return build_profileBoardHeader();
    }
    private cazcade.vortex.widgets.client.profile.ProfileBoardHeader build_profileBoardHeader() {
      // Creation section.
      final cazcade.vortex.widgets.client.profile.ProfileBoardHeader profileBoardHeader = (cazcade.vortex.widgets.client.profile.ProfileBoardHeader) GWT.create(cazcade.vortex.widgets.client.profile.ProfileBoardHeader.class);
      // Setup section.
      profileBoardHeader.setVisible(false);


      owner.profileBoardHeader = profileBoardHeader;

      return profileBoardHeader;
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

    /**
     * Getter for domId6 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 2.
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
     * Getter for contentArea called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.vortex.pool.widgets.PoolContentArea get_contentArea() {
      return build_contentArea();
    }
    private cazcade.vortex.pool.widgets.PoolContentArea build_contentArea() {
      // Creation section.
      final cazcade.vortex.pool.widgets.PoolContentArea contentArea = new cazcade.vortex.pool.widgets.PoolContentArea(false, false, true);
      // Setup section.


      owner.contentArea = contentArea;

      return contentArea;
    }

    /**
     * Getter for domId6Element called 2 times. Type: DEFAULT. Build precedence: 2.
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
     * Getter for domId7 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 2.
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
     * Getter for addCommentBox called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.boardcast.client.main.widgets.AddCommentBox get_addCommentBox() {
      return build_addCommentBox();
    }
    private cazcade.boardcast.client.main.widgets.AddCommentBox build_addCommentBox() {
      // Creation section.
      final cazcade.boardcast.client.main.widgets.AddCommentBox addCommentBox = (cazcade.boardcast.client.main.widgets.AddCommentBox) GWT.create(cazcade.boardcast.client.main.widgets.AddCommentBox.class);
      // Setup section.


      owner.addCommentBox = addCommentBox;

      return addCommentBox;
    }

    /**
     * Getter for domId7Element called 2 times. Type: DEFAULT. Build precedence: 2.
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
     * Getter for comments called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.vortex.widgets.client.stream.CommentPanel get_comments() {
      return build_comments();
    }
    private cazcade.vortex.widgets.client.stream.CommentPanel build_comments() {
      // Creation section.
      final cazcade.vortex.widgets.client.stream.CommentPanel comments = (cazcade.vortex.widgets.client.stream.CommentPanel) GWT.create(cazcade.vortex.widgets.client.stream.CommentPanel.class);
      // Setup section.


      owner.comments = comments;

      return comments;
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
     * Getter for footer called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.dom.client.DivElement get_footer() {
      return build_footer();
    }
    private com.google.gwt.dom.client.DivElement build_footer() {
      // Creation section.
      final com.google.gwt.dom.client.DivElement footer = new com.google.gwt.uibinder.client.LazyDomElement(get_domId9()).get().cast();
      // Setup section.


      owner.footer = footer;

      return footer;
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
     * Getter for f_DockLayoutPanel2 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.DockLayoutPanel get_f_DockLayoutPanel2() {
      return build_f_DockLayoutPanel2();
    }
    private com.google.gwt.user.client.ui.DockLayoutPanel build_f_DockLayoutPanel2() {
      // Creation section.
      final com.google.gwt.user.client.ui.DockLayoutPanel f_DockLayoutPanel2 = new com.google.gwt.user.client.ui.DockLayoutPanel(com.google.gwt.dom.client.Style.Unit.PX);
      // Setup section.
      f_DockLayoutPanel2.addWest(get_f_HTMLPanel3(), 380);
      f_DockLayoutPanel2.addEast(get_f_HTMLPanel5(), 380);
      f_DockLayoutPanel2.add(get_f_HTMLPanel4());
      f_DockLayoutPanel2.setHeight("300px");


      return f_DockLayoutPanel2;
    }

    /**
     * Getter for f_HTMLPanel3 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private com.google.gwt.user.client.ui.HTMLPanel get_f_HTMLPanel3() {
      return build_f_HTMLPanel3();
    }
    private com.google.gwt.user.client.ui.HTMLPanel build_f_HTMLPanel3() {
      // Creation section.
      final com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel3 = new com.google.gwt.user.client.ui.HTMLPanel(template_html1().asString());
      // Setup section.
      f_HTMLPanel3.setStyleName("footer-lhs");

      // Attach section.
      UiBinderUtil.TempAttachment attachRecord10 = UiBinderUtil.attachToDom(f_HTMLPanel3.getElement());
      get_domId11Element().get();

      // Detach section.
      attachRecord10.detach();
      f_HTMLPanel3.addAndReplaceElement(get_ownerDetailPanel(), get_domId11Element().get());

      return f_HTMLPanel3;
    }

    /**
     * Getter for domId11 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 4.
     */
    private java.lang.String domId11;
    private java.lang.String get_domId11() {
      return domId11;
    }
    private java.lang.String build_domId11() {
      // Creation section.
      domId11 = com.google.gwt.dom.client.Document.get().createUniqueId();
      // Setup section.


      return domId11;
    }

    /**
     * Getter for ownerDetailPanel called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private cazcade.vortex.widgets.client.profile.AliasDetailFlowPanel get_ownerDetailPanel() {
      return build_ownerDetailPanel();
    }
    private cazcade.vortex.widgets.client.profile.AliasDetailFlowPanel build_ownerDetailPanel() {
      // Creation section.
      final cazcade.vortex.widgets.client.profile.AliasDetailFlowPanel ownerDetailPanel = (cazcade.vortex.widgets.client.profile.AliasDetailFlowPanel) GWT.create(cazcade.vortex.widgets.client.profile.AliasDetailFlowPanel.class);
      // Setup section.


      owner.ownerDetailPanel = ownerDetailPanel;

      return ownerDetailPanel;
    }

    /**
     * Getter for domId11Element called 2 times. Type: DEFAULT. Build precedence: 4.
     */
    private com.google.gwt.uibinder.client.LazyDomElement domId11Element;
    private com.google.gwt.uibinder.client.LazyDomElement get_domId11Element() {
      return domId11Element;
    }
    private com.google.gwt.uibinder.client.LazyDomElement build_domId11Element() {
      // Creation section.
      domId11Element = new com.google.gwt.uibinder.client.LazyDomElement<Element>(get_domId11());
      // Setup section.


      return domId11Element;
    }

    /**
     * Getter for f_HTMLPanel4 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private com.google.gwt.user.client.ui.HTMLPanel get_f_HTMLPanel4() {
      return build_f_HTMLPanel4();
    }
    private com.google.gwt.user.client.ui.HTMLPanel build_f_HTMLPanel4() {
      // Creation section.
      final com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel4 = new com.google.gwt.user.client.ui.HTMLPanel(template_html2().asString());
      // Setup section.
      f_HTMLPanel4.setStyleName("footer-middle");

      // Attach section.
      UiBinderUtil.TempAttachment attachRecord11 = UiBinderUtil.attachToDom(f_HTMLPanel4.getElement());
      get_publishDate();
      get_authorFullname();
      get_visibilityDescription();

      // Detach section.
      attachRecord11.detach();

      return f_HTMLPanel4;
    }

    /**
     * Getter for publishDate called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private com.google.gwt.dom.client.SpanElement get_publishDate() {
      return build_publishDate();
    }
    private com.google.gwt.dom.client.SpanElement build_publishDate() {
      // Creation section.
      final com.google.gwt.dom.client.SpanElement publishDate = new com.google.gwt.uibinder.client.LazyDomElement(get_domId12()).get().cast();
      // Setup section.


      owner.publishDate = publishDate;

      return publishDate;
    }

    /**
     * Getter for domId12 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 4.
     */
    private java.lang.String domId12;
    private java.lang.String get_domId12() {
      return domId12;
    }
    private java.lang.String build_domId12() {
      // Creation section.
      domId12 = com.google.gwt.dom.client.Document.get().createUniqueId();
      // Setup section.


      return domId12;
    }

    /**
     * Getter for authorFullname called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private com.google.gwt.dom.client.SpanElement get_authorFullname() {
      return build_authorFullname();
    }
    private com.google.gwt.dom.client.SpanElement build_authorFullname() {
      // Creation section.
      final com.google.gwt.dom.client.SpanElement authorFullname = new com.google.gwt.uibinder.client.LazyDomElement(get_domId13()).get().cast();
      // Setup section.


      owner.authorFullname = authorFullname;

      return authorFullname;
    }

    /**
     * Getter for domId13 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 4.
     */
    private java.lang.String domId13;
    private java.lang.String get_domId13() {
      return domId13;
    }
    private java.lang.String build_domId13() {
      // Creation section.
      domId13 = com.google.gwt.dom.client.Document.get().createUniqueId();
      // Setup section.


      return domId13;
    }

    /**
     * Getter for visibilityDescription called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private com.google.gwt.dom.client.SpanElement get_visibilityDescription() {
      return build_visibilityDescription();
    }
    private com.google.gwt.dom.client.SpanElement build_visibilityDescription() {
      // Creation section.
      final com.google.gwt.dom.client.SpanElement visibilityDescription = new com.google.gwt.uibinder.client.LazyDomElement(get_domId14()).get().cast();
      // Setup section.


      owner.visibilityDescription = visibilityDescription;

      return visibilityDescription;
    }

    /**
     * Getter for domId14 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 4.
     */
    private java.lang.String domId14;
    private java.lang.String get_domId14() {
      return domId14;
    }
    private java.lang.String build_domId14() {
      // Creation section.
      domId14 = com.google.gwt.dom.client.Document.get().createUniqueId();
      // Setup section.


      return domId14;
    }

    /**
     * Getter for f_HTMLPanel5 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private com.google.gwt.user.client.ui.HTMLPanel get_f_HTMLPanel5() {
      return build_f_HTMLPanel5();
    }
    private com.google.gwt.user.client.ui.HTMLPanel build_f_HTMLPanel5() {
      // Creation section.
      final com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel5 = new com.google.gwt.user.client.ui.HTMLPanel(template_html3().asString());
      // Setup section.
      f_HTMLPanel5.setStyleName("footer-rhs");


      return f_HTMLPanel5;
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

    /**
     * Getter for domId15 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 2.
     */
    private java.lang.String domId15;
    private java.lang.String get_domId15() {
      return domId15;
    }
    private java.lang.String build_domId15() {
      // Creation section.
      domId15 = com.google.gwt.dom.client.Document.get().createUniqueId();
      // Setup section.


      return domId15;
    }

    /**
     * Getter for f_DockLayoutPanel6 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.DockLayoutPanel get_f_DockLayoutPanel6() {
      return build_f_DockLayoutPanel6();
    }
    private com.google.gwt.user.client.ui.DockLayoutPanel build_f_DockLayoutPanel6() {
      // Creation section.
      final com.google.gwt.user.client.ui.DockLayoutPanel f_DockLayoutPanel6 = new com.google.gwt.user.client.ui.DockLayoutPanel(com.google.gwt.dom.client.Style.Unit.PX);
      // Setup section.
      f_DockLayoutPanel6.addSouth(get_f_HTMLPanel7(), 60);
      f_DockLayoutPanel6.add(get_stream());
      f_DockLayoutPanel6.setHeight("100%");


      return f_DockLayoutPanel6;
    }

    /**
     * Getter for stream called 1 times. Type: DEFAULT. Build precedence: 3.
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
     * Getter for f_HTMLPanel7 called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private com.google.gwt.user.client.ui.HTMLPanel get_f_HTMLPanel7() {
      return build_f_HTMLPanel7();
    }
    private com.google.gwt.user.client.ui.HTMLPanel build_f_HTMLPanel7() {
      // Creation section.
      final com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel7 = new com.google.gwt.user.client.ui.HTMLPanel(template_html4().asString());
      // Setup section.
      f_HTMLPanel7.setStyleName("board-status-panel-create-panel");

      // Attach section.
      UiBinderUtil.TempAttachment attachRecord12 = UiBinderUtil.attachToDom(f_HTMLPanel7.getElement());
      get_domId16Element().get();

      // Detach section.
      attachRecord12.detach();
      f_HTMLPanel7.addAndReplaceElement(get_addChatBox(), get_domId16Element().get());

      return f_HTMLPanel7;
    }

    /**
     * Getter for domId16 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 4.
     */
    private java.lang.String domId16;
    private java.lang.String get_domId16() {
      return domId16;
    }
    private java.lang.String build_domId16() {
      // Creation section.
      domId16 = com.google.gwt.dom.client.Document.get().createUniqueId();
      // Setup section.


      return domId16;
    }

    /**
     * Getter for addChatBox called 1 times. Type: DEFAULT. Build precedence: 4.
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
     * Getter for domId16Element called 2 times. Type: DEFAULT. Build precedence: 4.
     */
    private com.google.gwt.uibinder.client.LazyDomElement domId16Element;
    private com.google.gwt.uibinder.client.LazyDomElement get_domId16Element() {
      return domId16Element;
    }
    private com.google.gwt.uibinder.client.LazyDomElement build_domId16Element() {
      // Creation section.
      domId16Element = new com.google.gwt.uibinder.client.LazyDomElement<Element>(get_domId16());
      // Setup section.


      return domId16Element;
    }

    /**
     * Getter for domId15Element called 2 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.uibinder.client.LazyDomElement domId15Element;
    private com.google.gwt.uibinder.client.LazyDomElement get_domId15Element() {
      return domId15Element;
    }
    private com.google.gwt.uibinder.client.LazyDomElement build_domId15Element() {
      // Creation section.
      domId15Element = new com.google.gwt.uibinder.client.LazyDomElement<Element>(get_domId15());
      // Setup section.


      return domId15Element;
    }
  }
}
