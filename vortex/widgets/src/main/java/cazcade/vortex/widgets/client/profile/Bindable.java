/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.profile;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.vortex.common.client.events.InvalidHandler;
import cazcade.vortex.common.client.events.ValidHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public interface Bindable {

    void bind(LSDTransferEntity entity, @Nullable LSDAttribute attribute, String referenceDataPrefix);

    void setErrorMessage(String message);

    LSDTransferEntity getEntityDiff();

    boolean isValid();

    boolean isBound();

    boolean isMultiValue();

    LSDAttribute getBoundAttribute();

    List getStringValues();

    String getStringValue();

    HandlerRegistration addInvalidHandler(InvalidHandler invalidHandler);

    HandlerRegistration addValidHandler(ValidHandler validHandler);

    HandlerRegistration addChangeHandler(ValueChangeHandler onChangeAction);

    void clear();
}
