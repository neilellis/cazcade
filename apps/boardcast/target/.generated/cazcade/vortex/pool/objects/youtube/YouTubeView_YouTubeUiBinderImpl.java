package cazcade.vortex.pool.objects.youtube;

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

public class YouTubeView_YouTubeUiBinderImpl implements UiBinder<com.google.gwt.user.client.ui.HTMLPanel, cazcade.vortex.pool.objects.youtube.YouTubeView>, cazcade.vortex.pool.objects.youtube.YouTubeView.YouTubeUiBinder {

  interface Template extends SafeHtmlTemplates {
    @Template("<span id='{0}'></span>")
    SafeHtml html1(String arg0);
     
    @Template("<iframe allowfullscreen='true' frameborder='0' height='219' id='{0}' src='' width='366'></iframe>")
    SafeHtml html2(String arg0);
     
    @Template("<span id='{0}'></span>")
    SafeHtml html3(String arg0);
     
  }

  Template template = GWT.create(Template.class);


  public com.google.gwt.user.client.ui.HTMLPanel createAndBindUi(final cazcade.vortex.pool.objects.youtube.YouTubeView owner) {


    return new Widgets(owner).get_f_HTMLPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final cazcade.vortex.pool.objects.youtube.YouTubeView owner;


    public Widgets(final cazcade.vortex.pool.objects.youtube.YouTubeView owner) {
      this.owner = owner;
      build_domId1();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 4
      build_domId2();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 4
      build_domId0();  // more than one getter call detected. Type: DOM_ID_HOLDER, precedence: 2
      build_domId1Element();  // more than one getter call detected. Type: DEFAULT, precedence: 4
      build_domId0Element();  // more than one getter call detected. Type: DEFAULT, precedence: 2
    }

    SafeHtml template_html1() {
      return template.html1(get_domId1());
    }
    SafeHtml template_html2() {
      return template.html2(get_domId2());
    }
    SafeHtml template_html3() {
      return template.html3(get_domId0());
    }

    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private cazcade.vortex.pool.objects.youtube.YouTubeView_YouTubeUiBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private cazcade.vortex.pool.objects.youtube.YouTubeView_YouTubeUiBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final cazcade.vortex.pool.objects.youtube.YouTubeView_YouTubeUiBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (cazcade.vortex.pool.objects.youtube.YouTubeView_YouTubeUiBinderImpl_GenBundle) GWT.create(cazcade.vortex.pool.objects.youtube.YouTubeView_YouTubeUiBinderImpl_GenBundle.class);
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
      final com.google.gwt.user.client.ui.HTMLPanel f_HTMLPanel1 = new com.google.gwt.user.client.ui.HTMLPanel(template_html3().asString());
      // Setup section.
      f_HTMLPanel1.setStyleName("pool-object-youtube drop-shadow raised");

      // Attach section.
      UiBinderUtil.TempAttachment attachRecord46 = UiBinderUtil.attachToDom(f_HTMLPanel1.getElement());
      get_domId0Element().get();

      // Detach section.
      attachRecord46.detach();
      f_HTMLPanel1.addAndReplaceElement(get_f_AbsolutePanel2(), get_domId0Element().get());

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
     * Getter for f_AbsolutePanel2 called 1 times. Type: DEFAULT. Build precedence: 2.
     */
    private com.google.gwt.user.client.ui.AbsolutePanel get_f_AbsolutePanel2() {
      return build_f_AbsolutePanel2();
    }
    private com.google.gwt.user.client.ui.AbsolutePanel build_f_AbsolutePanel2() {
      // Creation section.
      final com.google.gwt.user.client.ui.AbsolutePanel f_AbsolutePanel2 = (com.google.gwt.user.client.ui.AbsolutePanel) GWT.create(com.google.gwt.user.client.ui.AbsolutePanel.class);
      // Setup section.
      f_AbsolutePanel2.add(get_f_HTMLPanel3());
      f_AbsolutePanel2.add(get_videoFrameHolder());


      return f_AbsolutePanel2;
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
      f_HTMLPanel3.setStyleName("pool-object-youtube-preview");

      // Attach section.
      UiBinderUtil.TempAttachment attachRecord47 = UiBinderUtil.attachToDom(f_HTMLPanel3.getElement());
      get_domId1Element().get();

      // Detach section.
      attachRecord47.detach();
      f_HTMLPanel3.addAndReplaceElement(get_image(), get_domId1Element().get());

      return f_HTMLPanel3;
    }

    /**
     * Getter for domId1 called 2 times. Type: DOM_ID_HOLDER. Build precedence: 4.
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
     * Getter for image called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private cazcade.vortex.widgets.client.image.CachedImage get_image() {
      return build_image();
    }
    private cazcade.vortex.widgets.client.image.CachedImage build_image() {
      // Creation section.
      final cazcade.vortex.widgets.client.image.CachedImage image = (cazcade.vortex.widgets.client.image.CachedImage) GWT.create(cazcade.vortex.widgets.client.image.CachedImage.class);
      // Setup section.
      image.setVisible(false);
      image.setSize("CLIPPED_MEDIUM");


      owner.image = image;

      return image;
    }

    /**
     * Getter for domId1Element called 2 times. Type: DEFAULT. Build precedence: 4.
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
     * Getter for videoFrameHolder called 1 times. Type: DEFAULT. Build precedence: 3.
     */
    private com.google.gwt.user.client.ui.HTMLPanel get_videoFrameHolder() {
      return build_videoFrameHolder();
    }
    private com.google.gwt.user.client.ui.HTMLPanel build_videoFrameHolder() {
      // Creation section.
      final com.google.gwt.user.client.ui.HTMLPanel videoFrameHolder = new com.google.gwt.user.client.ui.HTMLPanel(template_html2().asString());
      // Setup section.
      videoFrameHolder.setStyleName("pool-object-youtube-video");

      // Attach section.
      UiBinderUtil.TempAttachment attachRecord48 = UiBinderUtil.attachToDom(videoFrameHolder.getElement());
      get_videoFrame();

      // Detach section.
      attachRecord48.detach();

      owner.videoFrameHolder = videoFrameHolder;

      return videoFrameHolder;
    }

    /**
     * Getter for videoFrame called 1 times. Type: DEFAULT. Build precedence: 4.
     */
    private com.google.gwt.dom.client.IFrameElement get_videoFrame() {
      return build_videoFrame();
    }
    private com.google.gwt.dom.client.IFrameElement build_videoFrame() {
      // Creation section.
      final com.google.gwt.dom.client.IFrameElement videoFrame = new com.google.gwt.uibinder.client.LazyDomElement(get_domId2()).get().cast();
      // Setup section.


      owner.videoFrame = videoFrame;

      return videoFrame;
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
