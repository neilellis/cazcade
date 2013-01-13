/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.form.fields;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.vortex.widgets.client.image.ImageUploader;
import cazcade.vortex.widgets.client.image.OnFinishUploadHandler;
import cazcade.vortex.widgets.client.image.OnStartUploadHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class ChangeImageUrlPanel extends Composite implements VortexFormField {


    interface ChangeImageUrlPanelUiBinder extends UiBinder<HTMLPanel, ChangeImageUrlPanel> {}

    private static final ChangeImageUrlPanelUiBinder ourUiBinder = GWT.create(ChangeImageUrlPanelUiBinder.class);

    @Override
    public boolean isValid() {
        return urlField.isValid();
    }

    @Override public boolean isBound() {
        return urlField.isBound();
    }

    @UiField ImageUploader imageUploader;
    @UiField RegexTextBox  urlField;

    public ChangeImageUrlPanel() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));

        imageUploader.setOnFinishHandler(new OnFinishUploadHandler() {
            @Override
            public void onFinish(@Nonnull ImageUploader uploader) {
                if (uploader.getStatus() == ImageUploader.Status.SUCCESS) {
                    setValue(uploader.getImageUrl());
                    callOnChangeAction();
                }
                else {
                    Window.alert("Failed to upload image.");
                }
            }


        });

        imageUploader.setOnStartUploadHandler(new OnStartUploadHandler() {
            @Override
            public void onStart(@Nonnull ImageUploader uploader) {
                urlField.setValue("");
            }
        });
        urlField.addHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(final KeyUpEvent event) {
                imageUploader.setImageUrl(urlField.getValue());
            }
        }, KeyUpEvent.getType());

    }


    @Override
    public void setValue(final String imageUrl) {
        imageUploader.setImageUrl(imageUrl);
        urlField.setValue(imageUrl);
    }


    public void callOnChangeAction() {
        urlField.callOnChangeAction();
    }

    @Override
    public void setOnChangeAction(@Nonnull final Runnable onChangeAction) {
        urlField.setOnChangeAction(new Runnable() {
            @Override
            public void run() {
                onChangeAction.run();
                imageUploader.setImageUrl(urlField.getValue());
            }
        });
    }


    @Override
    public Image getValidityImage() {
        return urlField.getValidityImage();
    }

    @Override
    public void setValidityImage(final Image validityImage) {
        urlField.setValidityImage(validityImage);
    }

    @Override
    public void setShowValidity(final boolean showValidity) {
        urlField.setShowValidity(showValidity);
    }

    @Override
    public void setErrorMessage(final String errorMessage) {
        urlField.setErrorMessage(errorMessage);
    }

    @Override
    public void bind(@Nonnull final LSDTransferEntity entity, final LSDAttribute attribute, final String prefix) {
        urlField.bind(entity, attribute, prefix);
        imageUploader.setImageUrl(urlField.getValue());
    }
    //
    //    public void setEditable(boolean editable) {
    //        urlField.setEditable(editable);
    //    }

    @Override
    public boolean isMultiValue() {
        return urlField.isMultiValue();
    }

    @Override
    public boolean isCompoundField() {
        return urlField.isCompoundField();
    }

    @Nonnull @Override
    public List<String> getStringValues() {
        return urlField.getStringValues();
    }


    @Override
    public LSDTransferEntity getEntity() {
        return urlField.getEntity();
    }

    @Nonnull @Override
    public LSDTransferEntity getEntityDiff() {
        return urlField.getEntityDiff();
    }

    @Override
    public String getStringValue() {
        return urlField.getValue();
    }


}