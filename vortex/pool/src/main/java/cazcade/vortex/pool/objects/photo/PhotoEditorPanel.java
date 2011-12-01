package cazcade.vortex.pool.objects.photo;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.vortex.pool.objects.edit.AbstractPoolObjectEditorPanel;
import cazcade.vortex.widgets.client.form.fields.ChangeImageUrlPanel;
import cazcade.vortex.widgets.client.form.fields.RegexTextBox;
import cazcade.vortex.widgets.client.form.fields.VortexTextArea;
import cazcade.vortex.widgets.client.popup.PopupEditPanel;
import cazcade.vortex.widgets.client.profile.Bindable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;


/**
 * @author neilellis@cazcade.com
 */
public class PhotoEditorPanel extends AbstractPoolObjectEditorPanel implements PopupEditPanel {


    @Override
    public void bind(final LSDEntity entity) {
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


    private static final PhotoEditorUiBinder ourUiBinder = GWT.create(PhotoEditorUiBinder.class);
    @UiField
    ChangeImageUrlPanel changeImagePanel;
    @UiField
    VortexTextArea description;
    @UiField
    RegexTextBox title;


    public PhotoEditorPanel(final LSDEntity entity) {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        bind(entity);

    }

    @Override
    protected void onLoad() {
        super.onLoad();
    }

    @SuppressWarnings({"ObjectEquality"})
    protected boolean autoCloseField(final Bindable field) {
        return field == changeImagePanel;
    }


}