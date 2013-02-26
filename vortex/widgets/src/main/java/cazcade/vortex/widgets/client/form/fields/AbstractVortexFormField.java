/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.form.fields;

import cazcade.liquid.api.lsd.Attribute;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.SimpleEntity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.vortex.common.client.events.InvalidEvent;
import cazcade.vortex.common.client.events.InvalidHandler;
import cazcade.vortex.common.client.events.ValidEvent;
import cazcade.vortex.common.client.events.ValidHandler;
import cazcade.vortex.dnd.client.browser.BrowserUtil;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.widgets.client.Resources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractVortexFormField extends Composite implements VortexFormField, HasValueChangeHandlers<Object> {
    private final BrowserUtil browserUtil      = GWT.create(BrowserUtil.class);
    protected     boolean     showValidityFlag = true;
    @Nullable
    protected Attribute      boundAttribute;
    @UiField  Label          errorMessage;
    @UiField  Image          validityImage;
    private   TransferEntity entity;

    @Override public void clear() {
        errorMessage.setText("");
        validityImage.setResource(Resources.INSTANCE.validFormValueImage());
        validityImage.setVisible(false);
        entity = SimpleEntity.emptyUnmodifiable();
    }

    @Nullable @Override
    public String getStringValue() {
        throw new UnsupportedOperationException("This widget does not support single string values.");
    }

    @Override
    public Image getValidityImage() {
        return validityImage;
    }

    @Override
    public void setValidityImage(final Image validityImage) {
        this.validityImage = validityImage;
    }

    @Override
    public void setShowValidity(final boolean showValidity) {
        showValidityFlag = showValidity;
    }

    @Override
    public void setErrorMessage(@Nullable final String errorMessage) {
        this.errorMessage.setText(errorMessage);
        if (errorMessage != null && !errorMessage.isEmpty()) {
            this.errorMessage.setVisible(true);
        } else {
            this.errorMessage.setVisible(false);
        }
    }

    @Override
    public void setValue(final String text) {
        throw new UnsupportedOperationException("This widget does not support single string values.");

    }

    @Override
    public boolean isMultiValue() {
        return false;
    }

    @Override
    public boolean isCompoundField() {
        return false;
    }

    @Nonnull @Override
    public List<String> getStringValues() {
        throw new UnsupportedOperationException("This widget does not support multiple values");
    }

    @Override
    public TransferEntity getEntity() {
        return entity;
    }

    @Nonnull @Override
    public TransferEntity getEntityDiff() {
        final TransferEntity newEntity = SimpleEntity.createNewEntity(entity.type());
        if (entity.hasURI()) {
            newEntity.$(Dictionary.URI, entity.uri().toString());
        }
        if (isMultiValue()) {
            newEntity.$(boundAttribute, getStringValues());
        } else {
            String stringValue = getStringValue();
            if (stringValue != null) {
                newEntity.$(boundAttribute, stringValue);
            }
        }
        return newEntity;
    }

    public void setEntity(@Nonnull final TransferEntity entity) {
        if (entity.readonly()) {
            throw new IllegalArgumentException("Cannot accept readonly entities.");
        }
        if (entity.error()) {
            throw new IllegalArgumentException("Cannot accept error entities.");
        }
        this.entity = entity;
    }

    protected boolean isVisibleKeyPress(final int keyCode) {
        return browserUtil.isVisibleKeyPress(keyCode);
    }

    protected void showValidity() {
        if (isValid()) {
            fireEvent(new ValidEvent());
        } else {
            fireEvent(new InvalidEvent());
        }

        if (showValidityFlag) {
            if (isValid()) {
                validityImage.setResource(Resources.INSTANCE.validFormValueImage());
                errorMessage.addStyleName("invisible");
            } else {
                validityImage.setResource(Resources.INSTANCE.invalidFormValueImage());
                errorMessage.removeStyleName("invisible");
            }
        }
    }

    @Override
    public void bind(@Nonnull final TransferEntity entity, @Nullable final Attribute attribute, final String prefix) {
        setEntity(entity);
        if (entity.has$(Dictionary.EDITABLE)) {
            setEditable(entity.$bool(Dictionary.EDITABLE));
        }
        if (attribute != null) {
            if (isMultiValue()) {
                bind(attribute, prefix, entity.$list(attribute));
            } else {
                bind(attribute, prefix, entity.has$(attribute) ? entity.$(attribute) : "");
            }
        }
    }

    @Override public boolean isBound() {
        return boundAttribute != null;
    }

    @Override @Nullable public Attribute getBoundAttribute() {
        return boundAttribute;
    }

    @Override public HandlerRegistration addInvalidHandler(InvalidHandler invalidHandler) {
        return addHandler(invalidHandler, InvalidEvent.TYPE);
    }

    @Override public HandlerRegistration addValidHandler(@Nullable ValidHandler handler) {
        return addHandler(handler, ValidEvent.TYPE);

    }

    @Override
    public HandlerRegistration addChangeHandler(final ValueChangeHandler onChangeAction) {
        return addValueChangeHandler(onChangeAction);
    }

    void setEditable(final boolean editable) {
        /** override this behaviour*/
    }

    public void bind(final Attribute attribute, final String profile, final List<String> initialValues) {

        throw new UnsupportedOperationException("This widget does not support multiple values binding.");
    }

    public void processChange() {
        if (isValid() && entity != null && boundAttribute != null) {
            if (isMultiValue()) {
                entity.$(boundAttribute, getStringValues());
                ClientLog.log(entity.toString());
            } else {
                entity.$(boundAttribute, getStringValue());
            }
        }

        if (isValid()) {
            if (isMultiValue()) {
                ValueChangeEvent.fire(this, getStringValues());
            } else {
                ValueChangeEvent.fire(this, getStringValue());
            }
        }
    }

    public void bind(final Attribute attribute, final String prefix, final String initialValue) {
        boundAttribute = attribute;
        setValue(initialValue);
    }

    @Override public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Object> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }
}
