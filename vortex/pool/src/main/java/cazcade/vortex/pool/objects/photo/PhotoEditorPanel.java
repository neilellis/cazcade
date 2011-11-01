package cazcade.vortex.pool.objects.photo;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.vortex.pool.objects.edit.AbstractPoolObjectEditorPanel;
import cazcade.vortex.widgets.client.form.fields.ChangeImageUrlPanel;
import cazcade.vortex.widgets.client.form.fields.RegexTextBox;
import cazcade.vortex.widgets.client.form.fields.VortexTextArea;
import cazcade.vortex.widgets.client.profile.Bindable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;


/**
 * @author neilellis@cazcade.com
 */
public class PhotoEditorPanel extends AbstractPoolObjectEditorPanel {

    @UiField
    Button done;
    private Timer timer;

    @UiHandler("done")
    public void doneClicked(ClickEvent e) {
        timer.cancel();
        onFinishAction.run();
    }

    @UiHandler("cancel")
    public void cancelClicked(ClickEvent e) {
        timer.cancel();
        onFinishAction.run();
    }


    @Override
    public void bind(LSDEntity entity) {
        super.bind(entity);
        addBinding(changeImagePanel, LSDAttribute.IMAGE_URL);
        addBinding(description, LSDAttribute.DESCRIPTION);
        addBinding(title, LSDAttribute.TITLE);
    }

    @Override
    public int getHeight() {
        return 400;
    }

    @Override
    public int getWidth() {
        return 700;
    }

    interface PhotoEditorUiBinder extends UiBinder<HTMLPanel, PhotoEditorPanel> {
    }


    private static PhotoEditorUiBinder ourUiBinder = GWT.create(PhotoEditorUiBinder.class);
    @UiField
    ChangeImageUrlPanel changeImagePanel;
    @UiField
    VortexTextArea description;
    @UiField
    RegexTextBox title;
    @UiField
    Button cancel;


    public PhotoEditorPanel(LSDEntity entity) {
        initWidget(ourUiBinder.createAndBindUi(this));
        bind(entity);
        timer = new Timer() {
            @Override
            public void run() {
                done.setEnabled(isValid());
            }

        };
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        timer.schedule(50);
    }

    protected boolean autoCloseField(Bindable field) {
        return field == changeImagePanel;
    }


}