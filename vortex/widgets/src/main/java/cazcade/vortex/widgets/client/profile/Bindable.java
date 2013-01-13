/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.profile;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDTransferEntity;

/**
 * @author neilellis@cazcade.com
 */
public interface Bindable {

    void bind(LSDTransferEntity entity, LSDAttribute attribute, String referenceDataPrefix);

    void setOnChangeAction(Runnable onEnterAction);

    void setErrorMessage(String message);

    LSDTransferEntity getEntityDiff();

    boolean isValid();

    boolean isBound();
}
