/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.youtube;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.vortex.pool.objects.edit.AbstractPoolObjectEditorPanel;
import cazcade.vortex.widgets.client.form.fields.YouTubeTextBox;
import cazcade.vortex.widgets.client.image.CachedImage;
import cazcade.vortex.widgets.client.profile.Bindable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public class YouTubeEditorPanel extends AbstractPoolObjectEditorPanel {

    @UiField Button done;

    @UiHandler("done")
    public void doneClicked(final ClickEvent e) {
        if (onFinishAction != null) {
            onFinishAction.run();
        }
    }


    @Override
    public void bind(final LSDTransferEntity entity) {
        super.bind(entity);
        addBinding(urlTextBox, LSDAttribute.MEDIA_ID);
    }

    @Override
    public int getHeight() {
        return 480;
    }

    @Override
    public int getWidth() {
        return 400;
    }

    @Override public String getCaption() {
        return "Choose Video";
    }

    interface PhotoEditorUiBinder extends UiBinder<HTMLPanel, YouTubeEditorPanel> {}


    private static final PhotoEditorUiBinder ourUiBinder = GWT.create(PhotoEditorUiBinder.class);

    @UiField YouTubeTextBox urlTextBox;
    @UiField CachedImage    image;


    public YouTubeEditorPanel(@Nonnull final LSDTransferEntity entity) {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        setEntity(entity);
        if (entity.hasAttribute(LSDAttribute.EURI)) {
            urlTextBox.setValue(entity.getAttribute(LSDAttribute.EURI).split(":")[1]);
            showPreview();
        }
        urlTextBox.setOnValid(new Runnable() {
            @Override public void run() {
                showPreview();
            }
        });
    }

    @Override
    protected void onChange(final LSDBaseEntity entity) {
        super.onChange(entity);
        if (urlTextBox.isValid()) {
            showPreview();
        }
        image.setSize(CachedImage.MEDIUM);

    }

    @Override protected void onChange(Bindable field, @Nullable LSDAttribute attribute) {
        super.onChange(field, attribute);


    }


    @Override public LSDTransferEntity getEntityForCreation() {
        return getEntity().merge(urlTextBox.getEntityDiff(), true);
    }

    private void showPreview() {
        image.setUrl("http://img.youtube.com/vi/" + urlTextBox.getStringValue() + "/default.jpg");
    }
}