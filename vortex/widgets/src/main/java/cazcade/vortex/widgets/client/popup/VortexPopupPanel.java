package cazcade.vortex.widgets.client.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author neilellis@cazcade.com
 */
public class VortexPopupPanel extends PopupPanel {

    private Timer timer;
    private Widget widget;

    interface VortexPopupPanelUiBinder extends UiBinder<HTMLPanel, VortexPopupPanel> {
    }

    private static final VortexPopupPanelUiBinder ourUiBinder = GWT.create(VortexPopupPanelUiBinder.class);


    @UiField
    Button cancel;

    @UiField
    Button done;
    @UiField
    HTMLPanel mainArea;

    public VortexPopupPanel() {
        super();
        super.setWidget(ourUiBinder.createAndBindUi(this));
    }

    private Runnable onFinishAction;

    @UiHandler("done")
    public void doneClicked(final ClickEvent e) {
        onFinishAction.run();
    }

    @UiHandler("cancel")
    public void cancelClicked(final ClickEvent e) {
        onFinishAction.run();
    }

    @Override
    public void hide(final boolean autoClosed) {
        super.hide(autoClosed);
        timer.cancel();
    }

    public void setOnFinishAction(final Runnable onFinishAction) {
        this.onFinishAction = onFinishAction;
    }

    @Override
    public void setWidget(final Widget w) {
        widget = w;
        mainArea.add(w);
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
}
