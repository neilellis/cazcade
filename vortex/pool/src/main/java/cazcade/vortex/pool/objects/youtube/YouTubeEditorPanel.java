package cazcade.vortex.pool.objects.youtube;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.vortex.pool.objects.edit.AbstractPoolObjectEditorPanel;
import cazcade.vortex.widgets.client.form.fields.YouTubeTextBox;
import cazcade.vortex.widgets.client.image.CachedImage;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class YouTubeEditorPanel extends AbstractPoolObjectEditorPanel {

    @UiField
    Button done;

    @UiHandler("done")
    public void doneClicked(ClickEvent e) {
        onFinishAction.run();
    }


    @Override
    public void bind(LSDEntity entity) {
        super.bind(entity);
        //no attribute required for binding YouTube videos
        addBinding(urlTextBox, null);
    }

    @Override
    public int getHeight() {
        return 400;
    }

    @Override
    public int getWidth() {
        return 350;
    }

    interface PhotoEditorUiBinder extends UiBinder<HTMLPanel, YouTubeEditorPanel> {
    }


    private static final PhotoEditorUiBinder ourUiBinder = GWT.create(PhotoEditorUiBinder.class);

    @UiField
    YouTubeTextBox urlTextBox;
    @UiField
    CachedImage image;


    public YouTubeEditorPanel(@Nonnull LSDEntity entity) {
        initWidget(ourUiBinder.createAndBindUi(this));
        setEntity(entity);
        if (entity.hasAttribute(LSDAttribute.EURI)) {
            urlTextBox.setValue(entity.getAttribute(LSDAttribute.EURI).split(":")[1]);
            showPreview();
        }
    }

    @Override
    protected void onChange(LSDEntity entity) {
        super.onChange(entity);
        if (urlTextBox.isValid()) {
            showPreview();
        }
        image.setSize(CachedImage.MEDIUM);

    }

    private void showPreview() {
        image.setUrl("http://img.youtube.com/vi/" + urlTextBox.getStringValue() + "/default.jpg");
    }
}