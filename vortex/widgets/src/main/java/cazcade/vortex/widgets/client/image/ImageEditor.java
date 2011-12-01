package cazcade.vortex.widgets.client.image;

import cazcade.vortex.widgets.client.form.fields.ChangeImageUrlPanel;
import cazcade.vortex.widgets.client.popup.PopupEditPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public class ImageEditor extends Composite implements PopupEditPanel {
    @Nullable
    public String getUrl() {
        return changeImagePanel.getStringValue();
    }

    @Override
    public boolean isValid() {
        return changeImagePanel.isValid();
    }

    interface ImageEditorUiBinder extends UiBinder<HTMLPanel, ImageEditor> {
    }

    private static final ImageEditorUiBinder ourUiBinder = GWT.create(ImageEditorUiBinder.class);
    @UiField
    ChangeImageUrlPanel changeImagePanel;

    public ImageEditor(@Nonnull final CachedImage displayImage) {
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        initWidget(rootElement);
        changeImagePanel.setValue(displayImage.getUnCachedUrl());
        changeImagePanel.setOnChangeAction(new Runnable() {
            @Override
            public void run() {
                displayImage.setUrl(changeImagePanel.getStringValue());
            }
        });
    }
}