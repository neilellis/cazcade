/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.form.fields;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.vortex.common.client.events.EditFinishEvent;
import cazcade.vortex.common.client.events.EditFinishHandler;
import cazcade.vortex.common.client.events.EditStartEvent;
import cazcade.vortex.common.client.events.EditStartHandler;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.widgets.client.image.ImageUploader;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class ChangeImageUrlPanel extends AbstractVortexFormField implements VortexFormField {


    interface ChangeImageUrlPanelUiBinder extends UiBinder<HTMLPanel, ChangeImageUrlPanel> {}

    private static final ChangeImageUrlPanelUiBinder ourUiBinder = GWT.create(ChangeImageUrlPanelUiBinder.class);
    private              String                      url         = "";

    @Override
    public boolean isValid() {
        return !url.isEmpty();
    }


    @UiField ImageUploader imageUploader;


    public ChangeImageUrlPanel() {
        super();

        try {
            initWidget(ourUiBinder.createAndBindUi(this));
            imageUploader.setOnFinishHandler(new EditFinishHandler() {
                @Override public void onEditFinish(EditFinishEvent event) {
                    if (imageUploader.getStatus() == ImageUploader.Status.SUCCESS) {
                        setValue(imageUploader.getImageUrl());
                        processChange();
                    } else {
                        Window.alert("Failed to upload image.");
                    }
                }
            });


            imageUploader.setOnStartUploadHandler(new EditStartHandler() {
                @Override public void onEditStart(EditStartEvent event) {
                    setValue("");
                }
            });
        } catch (Exception e) {
            ClientLog.log(e);
        }

    }


    @Override
    public void setValue(final String imageUrl) {
        url = imageUrl;
    }


    @Override public void clear() {
        super.clear();
        setValue("");
        imageUploader.clear();
    }


    @Override
    public void bind(@Nonnull final LSDTransferEntity entity, final LSDAttribute attribute, final String prefix) {
        super.bind(entity, attribute, prefix);
        imageUploader.setImageUrl(getValue());
    }


    public String getValue() {
        return url;
    }
}