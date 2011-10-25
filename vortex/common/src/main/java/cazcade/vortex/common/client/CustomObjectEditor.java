package cazcade.vortex.common.client;

import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;

/**
 * @author neilellis@cazcade.com
 */
public interface CustomObjectEditor {

    void show(LSDEntity object);

     public interface ChangeAction {
        void run(LSDEntity updateEntity);
    }

    void setOnChangeAction(ChangeAction onChangeAction);
}
