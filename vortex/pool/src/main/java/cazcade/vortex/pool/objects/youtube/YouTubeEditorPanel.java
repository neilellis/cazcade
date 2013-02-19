/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects.youtube;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.vortex.common.client.events.EditFinishEvent;
import cazcade.vortex.common.client.events.ValidEvent;
import cazcade.vortex.common.client.events.ValidHandler;
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

    interface PhotoEditorUiBinder extends UiBinder<HTMLPanel, YouTubeEditorPanel> {}

    private static final PhotoEditorUiBinder ourUiBinder = GWT.create(PhotoEditorUiBinder.class);
    @UiField Button         done;
    @UiField YouTubeTextBox urlTextBox;
    @UiField CachedImage    image;

    public YouTubeEditorPanel(@Nonnull final LSDTransferEntity entity) {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        if (entity.hasAttribute(LSDAttribute.EURI)) {
            urlTextBox.setValue(entity.getAttribute(LSDAttribute.EURI).split(":")[1]);
            showPreview();
        }
        urlTextBox.addValidHandler(new ValidHandler() {
            @Override public void onValid(ValidEvent event) {
                showPreview();
            }
        });
        addBinding(urlTextBox, LSDAttribute.MEDIA_ID);
        setEntity(entity);

    }

    @UiHandler("done")
    public void doneClicked(final ClickEvent e) {
        fireEvent(new EditFinishEvent());
    }

    @Override
    public void bindEntity(final LSDTransferEntity entity) {
        super.bindEntity(entity);
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

    @Override public LSDTransferEntity getEntityForCreation() {
        return getEntity().merge(urlTextBox.getEntityDiff(), true);
    }

    @Override
    protected void onChange(final LSDBaseEntity entity) {
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