/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.form.fields;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.vortex.widgets.client.image.CachedImage;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class UrlField extends Composite implements VortexFormField {


    interface ChangeImageUrlPanelUiBinder extends UiBinder<HTMLPanel, UrlField> {}

    private static final ChangeImageUrlPanelUiBinder ourUiBinder = GWT.create(ChangeImageUrlPanelUiBinder.class);
    @UiField RegexTextBox urlField;
    @UiField CachedImage  previewImage;
    private  boolean      validUrl;

    public UrlField() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        //
        //        imageUploader.setOnFinishHandler(new IUploader.OnFinishUploaderHandler() {
        //            @Override
        //            public void onFinish(IUploader uploader) {
        //                if (uploader.getStatus().equals(IUploadStatus.Status.SUCCESS)) {
        //                    setValue(uploader.getServerInfo().message);
        //                    callOnChangeAction();
        //                    IUploader.UploadedInfo info = uploader.getServerInfo();
        //                } else {
        //                    Window.alert("Failed to upload image.");
        //                }
        //            }
        //
        //
        //        });
        //        urlField.setOnValid(new Runnable() {
        //            @Override public void run() {
        //                previewImage.setUrl(urlField.getValue());
        //            }
        //        });
        setOnChangeAction(null);
    }

    public void callOnChangeAction() {
        urlField.callOnChangeAction();
    }


    @Override
    public void bind(@Nonnull final LSDTransferEntity entity, final LSDAttribute attribute, final String prefix) {
        urlField.bind(entity, attribute, prefix);
    }

    @Override
    public void setOnChangeAction(@Nullable final Runnable onChangeAction) {
        urlField.setOnChangeAction(new Runnable() {
            @Override
            public void run() {
                RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(urlField.getValue()));

                try {
                    Request request = builder.sendRequest(null, new RequestCallback() {
                        @Override public void onError(final Request request, final Throwable exception) {
                            // Couldn't connect to server (could be timeout, SOP violation, etc.)
                        }

                        @Override public void onResponseReceived(final Request request, final Response response) {
                            if (200 == response.getStatusCode()) {
                                previewImage.setUrl(urlField.getValue());
                                validUrl = true;
                                **Need to create a URLBox that extends RegexBox to do { this }
                                if (onChangeAction != null) { onChangeAction.run(); }
                            }
                            else {
                                validUrl = false;
                            }
                        }
                    });
                } catch (RequestException e) {
                    validUrl = false;
                }
            }
        });
    }

    @Override public boolean isBound() {
        return urlField.isBound();
    }

    @Override
    public String getStringValue() {
        return urlField.getValue();
    }

    @Override
    public boolean isValid() {
        return urlField.isValid() && isValidURL();
    }

    private boolean isValidURL() {
        return validUrl;
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
    //
    //    public void setEditable(boolean editable) {
    //        urlField.setEditable(editable);
    //    }

    @Override
    public void setValue(final String imageUrl) {
        previewImage.setUrl(imageUrl);
        urlField.setValue(imageUrl);
    }

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

    @Override public void setOnValid(Runnable runnable) {
        urlField.setOnValid(runnable);
    }


}