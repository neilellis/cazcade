package cazcade.vortex.widgets.client.profile;

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

public class ProfileBoardHeader_PublicBoardHeaderUiBinderImpl implements UiBinder<com.google.gwt.user.client.ui.HTMLPanel, cazcade.vortex.widgets.client.profile.ProfileBoardHeader>, cazcade.vortex.widgets.client.profile.ProfileBoardHeader.PublicBoardHeaderUiBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<a class='profile-board-followers' id='{0}' style='visibility:hidden;'>Profile</a>  <span id='{1}'></span> <span id='{2}'></span> <div class='row profile-board-header-middle-bar'> <div class='button-holder'> <span id='{3}'></span> <span id='{4}'></span> </div> </div>")
    SafeHtml html1(String arg0, String arg1, String arg2, String arg3, String arg4);
     
    @Template("<div id='{0}'> <div class='row profile-board-header-top'> <div class='profile-header-top-left'> <span id='{1}'></span> <span class='profile-board-user-role'>(<span id='{2}'></span>) </span> <span id='{3}'></span> <span id='{4}'></span> </div> <div class='profile-header-top-middle'> <span id='{5}'></span> </div> <div class='profile-header-top-right'> <div> <span id='{6}'></span> </div> <span id='{7}'></span> </div> </div> </div>")
    SafeHtml html2(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7);
     
  }

  Template template = GWT.create(Template.class);


  public com.google.gwt.user.client.ui.HTMLPanel createAndBindUi(final cazcade.vortex.widgets.client.profile.ProfileBoardHeader owner) {


    return new Widgets(owner).get_f_HTMLPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final cazcade.vortex.widgets.client.profile.ProfileBoardHeader owner;


    public Widgets(final cazcade.vortex.widgets.client.profile.ProfileBoardHeader owner) {
      this.owner = owner;
      build_domId6();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 3
      build_domId7();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 3
      build_domId8();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 3
      build_domId9();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 3
      build_domId10();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 3
      build_domId0();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId1();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId2();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId3();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId4();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId5();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId11();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId12();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId7Element();  // more than one getter call detected. Type: DEFAULT, precedence: 3
      build_domId8Element();  // more than one getter call detected. Type: DEFAULT, precedence: 3
      build_domId9Element();  // more than one getter call detected. Type: DEFAULT, precedence: 3
      build_domId10Element();  // more than one getter call detected. Type: DEFAULT, precedence: 3
      build_domId1Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId2Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId3Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId4Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId5Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId11Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
      build_domId12Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
    }

    SafeHtml template_html1() {
      return template.html1(get_domId6(), get_domId7(), get_domId8(), get_domId9(), get_domId10());
    }
    SafeHtml template_html2() {
      return template.html2(get_domId0(), get_domId1(), get_domId2(), get_domId3(), get_domId4(), get_domId5(), get_domId11(), get_domId12());
    }

    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private cazcade.vortex.widgets.client.profile.ProfileBoardHeader_PublicBoardHeaderUiBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private cazcade.vortex.widgets.client.profile.ProfileBoardHeader_PublicBoardHeaderUiBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final cazcade.vortex.widgets.client.profile.ProfileBoardHeader_PublicBoardHeaderUiBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (cazcade.vortex.widgets.client.profile.ProfileBoardHeader_PublicBoardHeaderUiBinderImpl_GenBundle) GWT.create(cazcade.vortex.widgets.client.profile.ProfileBoardHeader_PublicBoardHeaderUiBinderImpl_GenBundle.class);
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
      f_HTMLPanel1.setStyleName("profile-board-header");

      // Attach section.
      UiBinderUtil.TempAttachment attachRecord31 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
      get_contentArea();
      get_domId1Element().get();
      get_domId2Element().get();
      get_domId3Element().get();
      get_domId4Element().get();
      get_domId5Element().get();
      get_domId11Element().get();
      get_domId12Element().get();

      // Detach section.
      attachRecord31.detach();
      f_HTMLPanel1.addAndReplaceElement(get_userFullName(), get_domId1Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_roleFullName(), get_domId2Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_userShortName(), get_domId3Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_description(), get_domId4Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_detailPanel(), get_domId5Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_userImage(), get_domId11Element().get());
      f_HTMLPanel1.addAndReplaceElement(get_directMessagePanel(), get_domId12Element().get());

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
     * Getter for userFullName called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.vortex.widgets.client.form.fields.VortexEditableLabel get_userFullName() {
      return build_userFullName();
    }
    private cazcade.vortex.widgets.client.form.fields.VortexEditableLabel build_userFullName() {
      // Creation section.
      final cazcade.vortex.widgets.client.form.fields.VortexEditableLabel userFullName = (cazcade.vortex.widgets.client.form.fields.VortexEditableLabel) GWT.create(cazcade.vortex.widgets.client.form.fields.VortexEditableLabel.class);
      // Setup section.
      userFullName.setStyleName("profile-board-user-fullname");


      owner.userFullName = userFullName;

      return userFullName;
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
     * Getter for roleFullName called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.Label get_roleFullName() {
      return build_roleFullName();
    }
    private com.google.gwt.user.client.ui.Label build_roleFullName() {
      // Creation section.
      final com.google.gwt.user.client.ui.Label roleFullName = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
      // Setup section.


      owner.roleFullName = roleFullName;

      return roleFullName;
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
     * Getter for userShortName called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.vortex.widgets.client.form.fields.VortexEditableLabel get_userShortName() {
      return build_userShortName();
    }
    private cazcade.vortex.widgets.client.form.fields.VortexEditableLabel build_userShortName() {
      // Creation section.
      final cazcade.vortex.widgets.client.form.fields.VortexEditableLabel userShortName = (cazcade.vortex.widgets.client.form.fields.VortexEditableLabel) GWT.create(cazcade.vortex.widgets.client.form.fields.VortexEditableLabel.class);
      // Setup section.
      userShortName.setStyleName("profile-board-username");
      userShortName.setPrefix("@");
      userShortName.setReadonly(true);


      owner.userShortName = userShortName;

      return userShortName;
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
     * Getter for description called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.vortex.widgets.client.form.fields.VortexEditableLabel get_description() {
      return build_description();
    }
    private cazcade.vortex.widgets.client.form.fields.VortexEditableLabel build_description() {
      // Creation section.
      final cazcade.vortex.widgets.client.form.fields.VortexEditableLabel description = (cazcade.vortex.widgets.client.form.fields.VortexEditableLabel) GWT.create(cazcade.vortex.widgets.client.form.fields.VortexEditableLabel.class);
      // Setup section.
      description.setStyleName("profile-board-user-description");
      description.setWordwrap(false);
      description.setVisibleLength(30);


      owner.description = description;

      return description;
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
     * Getter for detailPanel called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.HTMLPanel get_detailPanel() {
      return build_detailPanel();
    }
    private com.google.gwt.user.client.ui.HTMLPanel build_detailPanel() {
      // Creation section.
      final com.google.gwt.user.client.ui.HTMLPanel detailPanel = new com.google.gwt.user.client.ui.HTMLPanel(template_html1().asString());
      // Setup section.
      detailPanel.setStyleName("profile-header-top-right");

      // Attach section.
      UiBinderUtil.TempAttachment attachRecord32 = UiBinderUtil.attachToDom(detailPanel.getElement());
      get_profileLink();
      get_domId7Element().get();
      get_domId8Element().get();
      get_domId9Element().get();
      get_domId10Element().get();

      // Detach section.
      attachRecord32.detach();
      detailPanel.addAndReplaceElement(get_followersLabel(), get_domId7Element().get());
      detailPanel.addAndReplaceElement(get_followingLabel(), get_domId8Element().get());
      detailPanel.addAndReplaceElement(get_followButton(), get_domId9Element().get());
      detailPanel.addAndReplaceElement(get_dmButton(), get_domId10Element().get());

      owner.detailPanel = detailPanel;

      return detailPanel;
    }

    /**
     * Getter for profileLink called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private com.google.gwt.dom.client.AnchorElement get_profileLink() {
      return build_profileLink();
    }
    private com.google.gwt.dom.client.AnchorElement build_profileLink() {
      // Creation section.
      final com.google.gwt.dom.client.AnchorElement profileLink = new com.google.gwt.uibinder.client.LazyDomElement(get_domId6()).get().cast();
      // Setup section.


      owner.profileLink = profileLink;

      return profileLink;
    }

    /**
     * Getter for domId6 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 3.
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
     * Getter for domId7 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 3.
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
     * Getter for followersLabel called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private com.google.gwt.user.client.ui.Label get_followersLabel() {
      return build_followersLabel();
    }
    private com.google.gwt.user.client.ui.Label build_followersLabel() {
      // Creation section.
      final com.google.gwt.user.client.ui.Label followersLabel = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
      // Setup section.
      followersLabel.setText("X Followers");
      followersLabel.setStyleName("profile-board-followers");


      owner.followersLabel = followersLabel;

      return followersLabel;
    }

    /**
     * Getter for domId7Element called 2 times. Type: DEFAULT. Build precedence: 3.
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
     * Getter for domId8 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 3.
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
     * Getter for followingLabel called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private com.google.gwt.user.client.ui.Label get_followingLabel() {
      return build_followingLabel();
    }
    private com.google.gwt.user.client.ui.Label build_followingLabel() {
      // Creation section.
      final com.google.gwt.user.client.ui.Label followingLabel = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
      // Setup section.
      followingLabel.setText("Following Y");
      followingLabel.setStyleName("profile-board-following");


      owner.followingLabel = followingLabel;

      return followingLabel;
    }

    /**
     * Getter for domId8Element called 2 times. Type: DEFAULT. Build precedence: 3.
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
     * Getter for domId9 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 3.
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
     * Getter for followButton called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private com.google.gwt.user.client.ui.Label get_followButton() {
      return build_followButton();
    }
    private com.google.gwt.user.client.ui.Label build_followButton() {
      // Creation section.
      final com.google.gwt.user.client.ui.Label followButton = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
      // Setup section.
      followButton.setText("Follow");
      followButton.setStyleName("btn large primary");


      owner.followButton = followButton;

      return followButton;
    }

    /**
     * Getter for domId9Element called 2 times. Type: DEFAULT. Build precedence: 3.
     */
    private com.google.gwt.uibinder.client.LazyDomElement domId9Element;
    private com.google.gwt.uibinder.client.LazyDomElement get_domId9Element() {
      return domId9Element;
    }
    private com.google.gwt.uibinder.client.LazyDomElement build_domId9Element() {
      // Creation section.
      domId9Element = new com.google.gwt.uibinder.client.LazyDomElement<Element>(get_domId9());
      // Setup section.


      return domId9Element;
    }

    /**
     * Getter for domId10 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 3.
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
     * Getter for dmButton called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private com.google.gwt.user.client.ui.Label get_dmButton() {
      return build_dmButton();
    }
    private com.google.gwt.user.client.ui.Label build_dmButton() {
      // Creation section.
      final com.google.gwt.user.client.ui.Label dmButton = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
      // Setup section.
      dmButton.setText("DM");
      dmButton.setStyleName("btn large");


      owner.dmButton = dmButton;

      return dmButton;
    }

    /**
     * Getter for domId10Element called 2 times. Type: DEFAULT. Build precedence: 3.
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
     * Getter for domId11 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 2.
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
     * Getter for userImage called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.vortex.widgets.client.image.UserProfileImage get_userImage() {
      return build_userImage();
    }
    private cazcade.vortex.widgets.client.image.UserProfileImage build_userImage() {
      // Creation section.
      final cazcade.vortex.widgets.client.image.UserProfileImage userImage = (cazcade.vortex.widgets.client.image.UserProfileImage) GWT.create(cazcade.vortex.widgets.client.image.UserProfileImage.class);
      // Setup section.
      userImage.setStyleName("profile-board-user-image");
      userImage.setHeight("160px");
      userImage.setWidth("120px");
      userImage.setSize("CLIPPED_MEDIUM");


      owner.userImage = userImage;

      return userImage;
    }

    /**
     * Getter for domId11Element called 2 times. Type: DEFAULT. Build precedence: 2.
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
     * Getter for domId12 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 2.
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
     * Getter for directMessagePanel called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private cazcade.vortex.widgets.client.dm.DirectMessagePanel get_directMessagePanel() {
      return build_directMessagePanel();
    }
    private cazcade.vortex.widgets.client.dm.DirectMessagePanel build_directMessagePanel() {
      // Creation section.
      final cazcade.vortex.widgets.client.dm.DirectMessagePanel directMessagePanel = (cazcade.vortex.widgets.client.dm.DirectMessagePanel) GWT.create(cazcade.vortex.widgets.client.dm.DirectMessagePanel.class);
      // Setup section.
      directMessagePanel.setVisible(false);


      owner.directMessagePanel = directMessagePanel;

      return directMessagePanel;
    }

    /**
     * Getter for domId12Element called 2 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.uibinder.client.LazyDomElement domId12Element;
    private com.google.gwt.uibinder.client.LazyDomElement get_domId12Element() {
      return domId12Element;
    }
    private com.google.gwt.uibinder.client.LazyDomElement build_domId12Element() {
      // Creation section.
      domId12Element = new com.google.gwt.uibinder.client.LazyDomElement<Element>(get_domId12());
      // Setup section.


      return domId12Element;
    }
  }
}
