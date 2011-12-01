package cazcade.vortex.widgets.client.profile;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author neilellis@cazcade.com
 */
public abstract class EntityBackedFormPanel extends EntityBackedPanel {

    @Nonnull
    private final Map<LSDAttribute, Bindable> bindings = new HashMap<LSDAttribute, Bindable>();

    public void addBinding(final LSDTransferEntity otherEntity, @Nonnull final Bindable field, final LSDAttribute attribute) {
        field.bind(otherEntity, attribute, getReferenceDataPrefix());
        final Runnable onEnterAction = getUpdateEntityAction(field);
        field.setOnChangeAction(onEnterAction);
        bindings.put(attribute, field);
    }

    public void addBinding(@Nonnull final Bindable field, @Nullable final LSDAttribute attribute) {
        field.bind(entity, attribute, getReferenceDataPrefix());
        final Runnable onEnterAction = getUpdateEntityAction(field);
        field.setOnChangeAction(onEnterAction);
        bindings.put(attribute, field);
    }


    @Override
    protected void bind(@Nullable final LSDTransferEntity entity) {
        if (entity == null) {
            throw new NullPointerException("Attempted to bind to a null entity.");
        }
        setEntityInternal(entity);
    }

    @Nonnull
    protected abstract String getReferenceDataPrefix();

    @Nonnull
    protected abstract Runnable getUpdateEntityAction(Bindable field);

    public boolean isValid() {
        for (final Bindable bindable : bindings.values()) {
            if (!bindable.isValid()) {
                return false;
            }
        }
        return true;
    }

}