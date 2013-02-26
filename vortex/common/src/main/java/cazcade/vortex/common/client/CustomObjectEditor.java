/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.common.client;

import cazcade.liquid.api.lsd.TransferEntity;

/**
 * @author neilellis@cazcade.com
 */
public interface CustomObjectEditor {

    void show(TransferEntity object);

    interface ChangeAction {
        void run(TransferEntity updateEntity);
    }

    void setOnChangeAction(ChangeAction onChangeAction);
}
