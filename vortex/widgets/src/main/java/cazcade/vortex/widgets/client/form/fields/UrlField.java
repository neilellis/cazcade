/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.form.fields;

import cazcade.liquid.api.lsd.Attribute;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.vortex.common.client.events.InvalidHandler;
import cazcade.vortex.common.client.events.ValidEvent;
import cazcade.vortex.common.client.events.ValidHandler;
import cazcade.vortex.widgets.client.image.CachedImage;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
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

    @UiField UrlTextBox  urlField;
    @UiField CachedImage previewImage;

    public UrlField() {
        super();
        initWidget(ourUiBinder.createAndBindUi(this));
        urlField.addValidHandler(new ValidHandler() {
            @Override public void onValid(ValidEvent event) {
                previewImage.setUrl(urlField.getValue());
            }
        });
    }


    @Override public void clear() {
        urlField.clear();
        previewImage.clear();
    }


    @Override
    public void bind(@Nonnull final TransferEntity entity, final Attribute attribute, final String prefix) {
        urlField.bind(entity, attribute, prefix);
    }

    @Override public boolean isBound() {
        return urlField.isBound();
    }

    @Override public Attribute getBoundAttribute() {
        return urlField.getBoundAttribute();
    }

    @Override public HandlerRegistration addInvalidHandler(InvalidHandler invalidHandler) {
        return urlField.addInvalidHandler(invalidHandler);
    }

    @Override public HandlerRegistration addValidHandler(ValidHandler handler) {
        return urlField.addValidHandler(handler);
    }

    @Override
    public HandlerRegistration addChangeHandler(@Nullable final ValueChangeHandler onChangeAction) {
        return urlField.addChangeHandler(onChangeAction);
    }


    @Override
    public String getStringValue() {
        return urlField.getValue();
    }

    @Override
    public boolean isValid() {
        return urlField.isValid();
    }

    @Override
    public Image getValidityImage() {
        return urlField.getValidityImage();
    }

    @Override
    public void setValidityImage(final Image validityImage) {
        urlField.setValidityImage(validityImage);
    }
    //
    //    public void setEditable(boolean editable) {
    //        urlField.setEditable(editable);
    //    }

    @Override
    public void setShowValidity(final boolean showValidity) {
        urlField.setShowValidity(showValidity);
    }

    @Override
    public void setErrorMessage(final String errorMessage) {
        urlField.setErrorMessage(errorMessage);
    }

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
    public TransferEntity getEntity() {
        return urlField.getEntity();
    }

    @Nonnull @Override
    public TransferEntity getEntityDiff() {
        return urlField.getEntityDiff();
    }


}