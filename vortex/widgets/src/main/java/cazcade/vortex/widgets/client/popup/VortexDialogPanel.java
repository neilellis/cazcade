/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * @author neilellis@cazcade.com
 */
public class VortexDialogPanel extends DialogBox {

    interface VortexPopupPanelUiBinder extends UiBinder<HTMLPanel, VortexDialogPanel> {}

    private static final VortexPopupPanelUiBinder ourUiBinder       = GWT.create(VortexPopupPanelUiBinder.class);
    private static final String                   POPUP_READY_STYLE = "vtx-dialog-popup-ready";
    @UiField public Button    cancel;
    @UiField public Button    done;
    @UiField public HTMLPanel mainArea;
    @Nullable
    private         Timer     timer;
    @Nullable
    private         Widget    widget;
    @Nullable
    private         Runnable  onFinishAction;
    private         Runnable  onCancelAction;

    public VortexDialogPanel() {
        super();
        setWidget(ourUiBinder.createAndBindUi(this));
        setModal(true);
        setGlassEnabled(true);
        addStyleName("vtx-dialog-popup");

    }

    @UiHandler("done")
    public void doneClicked(final ClickEvent e) {
        if (onFinishAction != null) {
            onFinishAction.run();
        }
    }

    @UiHandler("cancel")
    public void cancelClicked(final ClickEvent e) {
        if (onCancelAction != null) {
            onCancelAction.run();
        }
    }

    @Override
    public void hide(final boolean autoClosed) {
        super.hide(autoClosed);
        if (timer != null) {
            timer.cancel();
        }
    }

    public void setMainPanel(@Nonnull final Widget w) {
        widget = w;
        mainArea.add(w);
    }

    public void showDown() {
        setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                setPopupPosition((Window.getClientWidth() - offsetWidth) / 2, 0);
                getElement().getStyle().setPosition(Style.Position.FIXED);
                addStyleName(POPUP_READY_STYLE);
            }
        });

    }

    @Override public void hide() {
        removeStyleName(POPUP_READY_STYLE);
        super.hide();
    }

    @Override
    public void show() {
        super.show();
        if (widget instanceof PopupEditPanel) {
            timer = new Timer() {
                @Override
                public void run() {
                    done.setEnabled(((PopupEditPanel) widget).isValid());
                }
            };
            timer.scheduleRepeating(50);
        }
    }

    public void setOnFinishAction(@Nullable final Runnable onFinishAction) {
        this.onFinishAction = onFinishAction;
    }


    public void setOnCancelAction(final Runnable onCancelAction) {
        this.onCancelAction = onCancelAction;
    }
}
