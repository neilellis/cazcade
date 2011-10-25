package cazcade.vortex.widgets.client.profile;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;

/**
 * @author neilellis@cazcade.com
 */
public interface Bindable {

    void bind(LSDEntity entity, LSDAttribute attribute, String referenceDataPrefix);

    void setOnChangeAction(Runnable onEnterAction);

    void setErrorMessage(String message);

    LSDEntity getEntityDiff();
}
