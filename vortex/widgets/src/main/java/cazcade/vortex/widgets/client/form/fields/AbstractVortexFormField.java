/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.form.fields;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.vortex.dnd.client.browser.BrowserUtil;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.widgets.client.Resources;
import com.google.gwt.core.client.GWT;
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
public abstract class AbstractVortexFormField extends Composite implements VortexFormField {
    private final BrowserUtil browserUtil = GWT.create(BrowserUtil.class);
    protected Runnable onChangeAction;
    protected boolean showValidityFlag = true;
    @Nullable
    protected LSDAttribute      boundAttribute;
    @UiField  Label             errorMessage;
    @UiField  Image             validityImage;
    private   LSDTransferEntity entity;
    @Nullable
    protected Runnable          onValid;

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
        }
        else {
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
    public LSDTransferEntity getEntity() {
        return entity;
    }

    public void setEntity(@Nonnull final LSDTransferEntity entity) {
        if (entity.isReadonly()) {
            throw new IllegalArgumentException("Cannot accept readonly entities.");
        }
        if (entity.isError()) {
            throw new IllegalArgumentException("Cannot accept error entities.");
        }
        this.entity = entity;
    }

    @Nonnull @Override
    public LSDTransferEntity getEntityDiff() {
        final LSDTransferEntity newEntity = LSDSimpleEntity.createNewEntity(entity.getTypeDef());
        if (entity.hasURI()) {
            newEntity.setAttribute(LSDAttribute.URI, entity.getURI().toString());
        }
        if (isMultiValue()) {
            newEntity.setValues(boundAttribute, getStringValues());
        }
        else {
            String stringValue = getStringValue();
            if (stringValue != null) {
                newEntity.setAttribute(boundAttribute, stringValue);
            }
        }
        return newEntity;
    }

    @Override public void setOnValid(@Nullable Runnable runnable) {
        onValid = runnable;
    }

    @Override public boolean isBound() {
        return boundAttribute != null;
    }

    protected boolean isVisibleKeyPress(final int keyCode) {
        return browserUtil.isVisibleKeyPress(keyCode);
    }

    protected void showValidity() {
        if (isValid() && onValid != null) {
            onValid.run();
        }

        if (showValidityFlag) {
            if (isValid()) {
                validityImage.setResource(Resources.INSTANCE.validFormValueImage());
                errorMessage.addStyleName("invisible");
            }
            else {
                validityImage.setResource(Resources.INSTANCE.invalidFormValueImage());
                errorMessage.removeStyleName("invisible");
            }
        }
    }

    @Override
    public void bind(@Nonnull final LSDTransferEntity entity, @Nullable final LSDAttribute attribute, final String prefix) {
        setEntity(entity);
        if (entity.hasAttribute(LSDAttribute.EDITABLE)) {
            setEditable(entity.getBooleanAttribute(LSDAttribute.EDITABLE));
        }
        if (attribute != null) {
            if (isMultiValue()) {
                bind(attribute, prefix, entity.getAttributeAsList(attribute));
            }
            else {
                bind(attribute, prefix, entity.hasAttribute(attribute) ? entity.getAttribute(attribute) : "");
            }
        }
    }

    @Override
    public void setOnChangeAction(final Runnable onChangeAction) {
        this.onChangeAction = onChangeAction;
    }

    void setEditable(final boolean editable) {
        /** override this behaviour*/
    }

    public void bind(final LSDAttribute attribute, final String profile, final List<String> initialValues) {

        throw new UnsupportedOperationException("This widget does not support multiple values binding.");
    }

    protected void callOnChangeAction() {
        if (onChangeAction != null && isValid()) {
            if (entity != null && boundAttribute != null) {
                if (isMultiValue()) {
                    entity.setValues(boundAttribute, getStringValues());
                    ClientLog.log(entity.toString());
                }
                else {
                    entity.setAttribute(boundAttribute, getStringValue());
                }
            }
            onChangeAction.run();
        }
    }

    void bind(final LSDAttribute attribute, final String prefix, final String initialValue) {
        throw new UnsupportedOperationException("This widget does not support single value binding.");
    }

}
