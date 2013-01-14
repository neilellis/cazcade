/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.edit;

import cazcade.vortex.widgets.client.popup.VortexPopupPanel;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public class PoolObjectEditor {

    private final AbstractPoolObjectEditorPanel editorPanel;
    private final Runnable                      onFinishAction;
    private final int                           width;
    private final int                           height;


    PoolObjectEditor(final AbstractPoolObjectEditorPanel editorPanel, final Runnable onFinishAction, final int width, final int height) {
        this.editorPanel = editorPanel;
        this.onFinishAction = onFinishAction;

        this.width = width;
        this.height = height;
    }

    public void edit() {
        final PoolObjectEditorPopup popup = new PoolObjectEditorPopup();
        popup.showDown();
    }


    public void create() {
        final PoolObjectEditorPopup popup = new PoolObjectEditorPopup();
        popup.showDown();
    }

    public static void showForCreate(@Nonnull final AbstractPoolObjectEditorPanel editorPanel, @Nullable final Runnable onFinishAction) {
        new PoolObjectEditor(editorPanel, onFinishAction, editorPanel.getWidth(), editorPanel.getHeight()).create();
    }

    public static void showForEdit(@Nonnull final AbstractPoolObjectEditorPanel editorPanel, @Nullable final Runnable onFinishAction) {
        new PoolObjectEditor(editorPanel, onFinishAction, editorPanel.getWidth(), editorPanel.getHeight()).edit();
    }

    private class PoolObjectEditorPopup extends VortexPopupPanel {

        boolean finished;

        private PoolObjectEditorPopup() {
            super();
            setAutoHideEnabled(true);
            setAutoHideOnHistoryEventsEnabled(true);
            setHeight(height + "px");
            setWidth(width + "px");
            addStyleName("pool-object-editor-popup");
            setWidget(editorPanel);
            setGlassEnabled(true);
            setText(editorPanel.getCaption());

            //            setGlassStyleName("pool-object-editor-popup-glass");
            addCloseHandler(new CloseHandler<PopupPanel>() {
                @Override
                public void onClose(final CloseEvent<PopupPanel> popupPanelCloseEvent) {
                    finish();
                }
            });
            setOnFinishAction(new Runnable() {
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
