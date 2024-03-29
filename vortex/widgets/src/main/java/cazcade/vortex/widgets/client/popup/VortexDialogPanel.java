/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.popup;

import cazcade.vortex.common.client.events.EditCancelEvent;
import cazcade.vortex.common.client.events.EditCancelHandler;
import cazcade.vortex.common.client.events.EditFinishEvent;
import cazcade.vortex.common.client.events.EditFinishHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
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

    @UiField public   Button              cancel;
    @UiField public   Button              done;
    @UiField public   HTMLPanel           mainArea;
    @Nullable
    private           Timer               timer;
    @Nullable
    private           Widget              mainPanel;
    @Nullable private HandlerRegistration antiScrollHandler;

    public VortexDialogPanel() {
        super();
        setWidget(ourUiBinder.createAndBindUi(this));
        setModal(true);
        setGlassEnabled(true);
        addStyleName("vtx-dialog-popup");
        addStyleName("non-glassed");
        setGlassStyleName("vtx-dialog-glass");

    }

    @UiHandler("done")
    public void doneClicked(final ClickEvent e) {
        fireEvent(new EditFinishEvent());
    }

    @UiHandler("cancel")
    public void cancelClicked(final ClickEvent e) {
        fireEvent(new EditCancelEvent());
    }

    @Override
    public void hide(final boolean autoClosed) {
        super.hide(autoClosed);
        if (timer != null) {
            timer.cancel();
        }

        //        removeStyleName(POPUP_READY_STYLE);
        //        new Timer() {
        //            @Override public void run() {
        //                VortexDialogPanel.super.hide(autoClosed);
        //            }
        //        }.schedule(750);

    }

    public void setMainPanel(@Nonnull final Widget w) {
        mainPanel = w;
        mainArea.add(w);
    }

    public void showDown() {
        getGlassElement().getStyle().setOpacity(0.0);
        setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                setPopupPosition((Window.getClientWidth() - offsetWidth) / 2, 0);
                getElement().getStyle().setPosition(Style.Position.FIXED);
                addStyleName(POPUP_READY_STYLE);
                antiScrollHandler = Window.addWindowScrollHandler(new Window.ScrollHandler() {
                    @Override public void onWindowScroll(Window.ScrollEvent event) {
                        //This is a hack to deal with some elemnts propagating events badly, like iFrames
                        //Here we ensure that while the popup is visible the window won't scroll.
                        Window.scrollTo(Window.getScrollLeft(), Window.getScrollTop() - event.getScrollTop());
                    }
                });
                new Timer() {
                    @Override public void run() {
                        getGlassElement().getStyle().setOpacity(0.85);
                    }
                }.schedule(300);

            }
        });

    }

    @Override public void hide() {
        if (antiScrollHandler != null) {
            antiScrollHandler.removeHandler();
            antiScrollHandler = null;
        }
        removeStyleName(POPUP_READY_STYLE);
        new Timer() {
            @Override public void run() {
                VortexDialogPanel.super.hide();
            }
        }.schedule(750);
    }

    @Override
    public void show() {
        super.show();
        if (mainPanel instanceof PopupEditPanel) {
            timer = new Timer() {
                @Override
                public void run() {
                    done.setEnabled(((PopupEditPanel) mainPanel).isValid());
                }
            };
            timer.scheduleRepeating(50);
        }
    }

    public HandlerRegistration addEditFinishHandler(@Nullable final EditFinishHandler onFinishAction) {
        return addHandler(onFinishAction, EditFinishEvent.TYPE);
    }


    public HandlerRegistration addEditCancelHandler(final EditCancelHandler onCancelAction) {
        return addHandler(onCancelAction, EditCancelEvent.TYPE);
    }
}
