package cazcade.vortex.pool.objects.edit;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author neilellis@cazcade.com
 */
public class PoolObjectEditor {

    private Button done;
    private AbstractPoolObjectEditorPanel editorPanel;
    private Runnable onFinishAction;
    private int width;
    private int height;

    public void setOnFinishAction(Runnable onFinishAction) {
        this.onFinishAction = onFinishAction;
    }

    PoolObjectEditor(AbstractPoolObjectEditorPanel editorPanel, Runnable onFinishAction, int width, int height) {
        this.editorPanel = editorPanel;
        this.onFinishAction = onFinishAction;

        this.width = width;
        this.height = height;
    }

    public void edit() {
        final PoolObjectEditorPopup popup = new PoolObjectEditorPopup();
        popup.center();
    }


    public void create() {
        final PoolObjectEditorPopup popup = new PoolObjectEditorPopup();
        popup.center();
    }

    public static void showForCreate(AbstractPoolObjectEditorPanel editorPanel, Runnable onFinishAction) {
        new PoolObjectEditor(editorPanel, onFinishAction, editorPanel.getWidth(), editorPanel.getHeight()).create();
    }

    public static void showForEdit(AbstractPoolObjectEditorPanel editorPanel, Runnable onFinishAction) {
        new PoolObjectEditor(editorPanel, onFinishAction, editorPanel.getWidth(), editorPanel.getHeight()).edit();
    }

    private class PoolObjectEditorPopup extends PopupPanel {

        boolean finished;

        private PoolObjectEditorPopup() {
            super(false, true);
            setHeight(height + "px");
            setWidth(width + "px");
            addStyleName("pool-object-editor-popup");
            setWidget(editorPanel);
            setGlassEnabled(true);
            setGlassStyleName("pool-object-editor-popup-glass");
            addCloseHandler(new CloseHandler<PopupPanel>() {
                @Override
                public void onClose(CloseEvent<PopupPanel> popupPanelCloseEvent) {
                    finish();
                }
            });
            editorPanel.setOnFinishAction(new Runnable() {
                @Override
                public void run() {
                    hide();
                    finish();
                }
            });
        }

        private void finish() {
            if (!finished) {
                finished = true;
                if (onFinishAction != null) {
                    onFinishAction.run();
                }
            }
        }


    }

}
