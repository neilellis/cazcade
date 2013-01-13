/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.form.fields;

import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.vortex.widgets.client.profile.Bindable;
import com.google.gwt.user.client.ui.Image;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public interface VortexFormField extends Bindable {

    @Nullable String getStringValue();


    boolean isValid();

    Image getValidityImage();

    void setValidityImage(Image validityImage);

    void setShowValidity(boolean showValidity);

    void setErrorMessage(String errorMessage);

    void setValue(String text);

    boolean isMultiValue();

    boolean isCompoundField();

    @Nonnull List<String> getStringValues();

    LSDTransferEntity getEntity();

    /**
     * Returns a stripped down entity with only the information required for an update.
     *
     * @return a lightweight entity.
     */
    @Nonnull LSDTransferEntity getEntityDiff();

    void setOnValid(Runnable runnable);
}
