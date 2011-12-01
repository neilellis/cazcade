package cazcade.vortex.common.client;

import cazcade.liquid.api.lsd.LSDTransferEntity;

/**
 * @author neilellis@cazcade.com
 */
public interface CustomObjectEditor {

    void show(LSDTransferEntity object);

    interface ChangeAction {
        void run(LSDTransferEntity updateEntity);
    }

    void setOnChangeAction(ChangeAction onChangeAction);
}
