package cazcade.vortex.widgets.client.stream;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CommentPanel_VortexStreamPanelUiBinderImpl implements UiBinder<com.google.gwt.user.client.ui.VerticalPanel, cazcade.vortex.widgets.client.stream.CommentPanel>, cazcade.vortex.widgets.client.stream.CommentPanel.VortexStreamPanelUiBinder {


  public com.google.gwt.user.client.ui.VerticalPanel createAndBindUi(final cazcade.vortex.widgets.client.stream.CommentPanel owner) {


    return new Widgets(owner).get_f_VerticalPanel1();
  }

  /**
   * Encapsulates the access to all inner widgets
   */
  class Widgets {
    private final cazcade.vortex.widgets.client.stream.CommentPanel owner;


    public Widgets(final cazcade.vortex.widgets.client.stream.CommentPanel owner) {
      this.owner = owner;
    }


    /**
     * Getter for clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay called 0 times. Type: GENERATED_BUNDLE. Build precedence: 1.
     */
    private cazcade.vortex.widgets.client.stream.CommentPanel_VortexStreamPanelUiBinderImpl_GenBundle get_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      return build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay();
    }
    private cazcade.vortex.widgets.client.stream.CommentPanel_VortexStreamPanelUiBinderImpl_GenBundle build_clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay() {
      // Creation section.
      final cazcade.vortex.widgets.client.stream.CommentPanel_VortexStreamPanelUiBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (cazcade.vortex.widgets.client.stream.CommentPanel_VortexStreamPanelUiBinderImpl_GenBundle) GWT.create(cazcade.vortex.widgets.client.stream.CommentPanel_VortexStreamPanelUiBinderImpl_GenBundle.class);
      // Setup section.


      return clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay;
    }

    /**
     * Getter for f_VerticalPanel1 called 1 times. Type: DEFAULT. Build precedence: 1.
     */
    private com.google.gwt.user.client.ui.VerticalPanel get_f_VerticalPanel1() {
      return build_f_VerticalPanel1();
    }
    private com.google.gwt.user.client.ui.VerticalPanel build_f_VerticalPanel1() {
      // Creation section.
      final com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel1 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
      // Setup section.
      f_VerticalPanel1.setStyleName("comment-panel");


      return f_VerticalPanel1;
    }
  }
}
