package cazcade.vortex.pool.widgets;

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

public class PoolContentArea_PoolContentAreaUiBinderImpl implements UiBinder<com.google.gwt.user.client.ui.HTMLPanel, cazcade.vortex.pool.widgets.PoolContentArea>, cazcade.vortex.pool.widgets.PoolContentArea.PoolContentAreaUiBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<span id='{0}'></span> <span id='{1}'></span>")
    SafeHtml html1(String arg0, String arg1);
     
  }

  Template template = GWT.create(Template.class);


  public com.google.gwt.user.client.ui.HTMLPanel createAndBindUi(final cazcade.vortex.pool.widgets.PoolContentArea owner) {


    return new Widgets(owner).get_poolContentArea();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final cazcade.vortex.pool.widgets.PoolContentArea owner;


    public Widgets(final cazcade.vortex.pool.widgets.PoolContentArea owner) {
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
    private cazcade.vortex.pool.widgets.PoolContentArea_PoolContentAreaUiBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private cazcade.vortex.pool.widgets.PoolContentArea_PoolContentAreaUiBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final cazcade.vortex.pool.widgets.PoolContentArea_PoolContentAreaUiBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (cazcade.vortex.pool.widgets.PoolContentArea_PoolContentAreaUiBinderImpl_GenBundle) GWT.create(cazcade.vortex.pool.widgets.PoolContentArea_PoolContentAreaUiBinderImpl_GenBundle.class);
      // Setup section.


      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for poolContentArea called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private com.google.gwt.user.client.ui.HTMLPanel get_poolContentArea() {
      return build_poolContentArea();
    }
    private com.google.gwt.user.client.ui.HTMLPanel build_poolContentArea() {
      // Creation section.
      final com.google.gwt.user.client.ui.HTMLPanel poolContentArea = new com.google.gwt.user.client.ui.HTMLPanel(template_html1().asString());
      // Setup section.
      poolContentArea.setStyleName("pool-area");
      poolContentArea.setHeight("100%");
      poolContentArea.setWidth("1024px");

      // Attach section.
      UiBinderUtil.TempAttachment attachRecord23 = UiBinderUtil.attachToDom(poolContentArea.getElement());
      get_domId0Element().get();
      get_domId1Element().get();

      // Detach section.
      attachRecord23.detach();
      poolContentArea.addAndReplaceElement(get_container(), get_domId0Element().get());
      poolContentArea.addAndReplaceElement(get_visibilityStatus(), get_domId1Element().get());

      return poolContentArea;
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
     * Getter for container called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.AbsolutePanel get_container() {
      return build_container();
    }
    private com.google.gwt.user.client.ui.AbsolutePanel build_container() {
      // Creation section.
      final com.google.gwt.user.client.ui.AbsolutePanel container = (com.google.gwt.user.client.ui.AbsolutePanel) GWT.create(com.google.gwt.user.client.ui.AbsolutePanel.class);
      // Setup section.
      container.setStyleName("pool-area-content");
      container.setHeight("100%");
      container.setWidth("100%");


      owner.container = container;

      return container;
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
     * Getter for visibilityStatus called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.Label get_visibilityStatus() {
      return build_visibilityStatus();
    }
    private com.google.gwt.user.client.ui.Label build_visibilityStatus() {
      // Creation section.
      final com.google.gwt.user.client.ui.Label visibilityStatus = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
      // Setup section.
      visibilityStatus.setStyleName("pool-visibility-ribbon");


      owner.visibilityStatus = visibilityStatus;

      return visibilityStatus;
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
