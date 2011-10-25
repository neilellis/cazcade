package cazcade.vortex.pool.objects.photo;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.vortex.pool.objects.edit.AbstractPoolObjectEditorPanel;
import cazcade.vortex.widgets.client.form.fields.ChangeImageUrlPanel;
import cazcade.vortex.widgets.client.form.fields.RegexTextBox;
import cazcade.vortex.widgets.client.form.fields.VortexTextArea;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author neilellis@cazcade.com
 */
public class PhotoEditorPanel extends AbstractPoolObjectEditorPanel {

    @UiField
    Button done;

    @UiHandler("done")
    public void doneClicked(ClickEvent e) {
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


    public PhotoEditorPanel(LSDEntity entity) {
        initWidget(ourUiBinder.createAndBindUi(this));
        bind(entity);
    }
}