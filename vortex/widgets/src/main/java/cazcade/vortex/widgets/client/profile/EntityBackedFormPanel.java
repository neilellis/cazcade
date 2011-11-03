package cazcade.vortex.widgets.client.profile;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author neilellis@cazcade.com
 */
public abstract class EntityBackedFormPanel extends EntityBackedPanel {

    private Map<LSDAttribute, Bindable> bindings = new HashMap<LSDAttribute, Bindable>();

    public void addBinding(LSDEntity otherEntity, final Bindable field, final LSDAttribute attribute) {
        field.bind(otherEntity, attribute, getReferenceDataPrefix());
        final Runnable onEnterAction = getUpdateEntityAction(field);
        field.setOnChangeAction(onEnterAction);
        bindings.put(attribute, field);
    }

    public void addBinding(final Bindable field, final LSDAttribute attribute) {
        field.bind(entity, attribute, getReferenceDataPrefix());
        final Runnable onEnterAction = getUpdateEntityAction(field);
        field.setOnChangeAction(onEnterAction);
        bindings.put(attribute, field);
    }


    @Override
    protected void bind(LSDEntity entity) {
        if (entity == null) {
            throw new NullPointerException("Attempted to bind to a null entity.");
        }
        setEntityInternal(entity);
    }

    protected abstract String getReferenceDataPrefix();

    protected abstract Runnable getUpdateEntityAction(Bindable field);

    public boolean isValid() {
        for (Bindable bindable : bindings.values()) {
            if (!bindable.isValid()) {
                return false;
            }
        }
        return true;
    }

}