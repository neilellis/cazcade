package cazcade.vortex.common.client;

import cazcade.liquid.api.lsd.LSDEntity;

/**
 * @author neilellis@cazcade.com
 */
public interface CustomObjectEditor {

    void show(LSDEntity object);

    interface ChangeAction {
        void run(LSDEntity updateEntity);
    }

    void setOnChangeAction(ChangeAction onChangeAction);
}
