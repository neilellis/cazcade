/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.edit;

import cazcade.vortex.common.client.events.*;
import cazcade.vortex.widgets.client.popup.VortexDialogPanel;

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


    PoolObjectEditor(final AbstractPoolObjectEditorPanel editorPanel, @Nullable final Runnable onFinishAction, final int width, final int height) {
        this.editorPanel = editorPanel;
        this.onFinishAction = onFinishAction;
        this.width = width;
        this.height = height;
    }

    public static void showForCreate(@Nonnull final AbstractPoolObjectEditorPanel editorPanel, @Nullable final Runnable onFinishAction) {
        PoolObjectEditor poolObjectEditor = new PoolObjectEditor(editorPanel, onFinishAction, editorPanel.getWidth(), editorPanel.getHeight());
        poolObjectEditor.create(onFinishAction);
    }

    public static void showForEdit(@Nonnull final AbstractPoolObjectEditorPanel editorPanel, @Nullable final Runnable onFinishAction) {
        new PoolObjectEditor(editorPanel, onFinishAction, editorPanel.getWidth(), editorPanel.getHeight()).edit(onFinishAction);
    }

    public void edit(final Runnable onFinishAction) {
        final PoolObjectEditorPopup popup = new PoolObjectEditorPopup(false);
        popup.addEditFinishHandler(new EditFinishHandler() {
            @Override public void onEditFinish(EditFinishEvent event) {
                onFinishAction.run();
            }
        });
        popup.showDown();
    }

    public void create(final Runnable onFinishAction) {
        final PoolObjectEditorPopup popup = new PoolObjectEditorPopup(true);
        popup.addEditFinishHandler(new EditFinishHandler() {
            @Override public void onEditFinish(EditFinishEvent event) {
                onFinishAction.run();
            }
        });
        popup.showDown();
    }

    private class PoolObjectEditorPopup extends VortexDialogPanel {

        boolean finished;
        private boolean create;

        private PoolObjectEditorPopup(boolean create) {
            super();
            this.create = create;
            setAutoHideOnHistoryEventsEnabled(true);
            setHeight(height + "px");
            setWidth(width + "px");
            addStyleName("pool-object-editor-popup");
            setMainPanel(editorPanel);
            setText(editorPanel.getCaption());

            //            setGlassStyleName("pool-object-editor-popup-glass");
            //            addCloseHandler(new CloseHandler<PopupPanel>() {
            //                @Override
            //                public void onClose(final CloseEvent<PopupPanel> popupPanelCloseEvent) {
            //                    finish();
            //                }
            //            });
            done.setEnabled(false);
            editorPanel.addEditFinishHandler(new EditFinishHandler() {
                @Override public void onEditFinish(EditFinishEvent event) {
                    hide();
                    finish();
                }
            });
            editorPanel.addValidHandler(new ValidHandler() {
                @Override public void onValid(ValidEvent event) {
                    done.setEnabled(true);
                }
            });
            editorPanel.addInvalidHandler(new InvalidHandler() {
                @Override public void onInvalid(InvalidEvent event) {
                    done.setEnabled(false);
                }
            });
            addEditFinishHandler(new EditFinishHandler() {
                @Override public void onEditFinish(EditFinishEvent event) {
                    hide();
                    finish();
                }
            });

            addEditCancelHandler(new EditCancelHandler() {
                @Override public void onEditCancel(EditCancelEvent event) {
                    finished = true;
                    hide();
                }
            });

        }

        private void finish() {
            if (!finished) {
                if (!create) {
                    editorPanel.save();
                }
                finished = true;
                if (onFinishAction != null) {
                    onFinishAction.run();
                }
            }
        }


    }

}
