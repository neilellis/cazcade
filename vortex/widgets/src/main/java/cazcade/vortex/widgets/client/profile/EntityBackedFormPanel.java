/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

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
        if (!isSaveOnExit()) {
            final Runnable onEnterAction = getUpdateEntityAction(field);
            field.setOnChangeAction(onEnterAction);
        }
        bindings.put(attribute, field);
        onChange(field, attribute);
    }

    public void addBinding(@Nonnull final Bindable field, @Nullable final LSDAttribute attribute) {
        field.bind(entity, attribute, getReferenceDataPrefix());
        if (!isSaveOnExit()) {
            field.setOnChangeAction(new Runnable() {
                @Override public void run() {
                    if (field.isValid()) { onChange(field, attribute); }
                    getUpdateEntityAction(field).run();
                }
            });
        }
        else {
            field.setOnChangeAction(new Runnable() {
                @Override public void run() {
                    if (field.isValid()) { onChange(field, attribute); }
                }
            });
        }
        onChange(field, attribute);
        bindings.put(attribute, field);
    }

    protected void onChange(Bindable field, @Nullable LSDAttribute attribute) {
        //Override to participate in binding lifecycle
    }

    @Override
    protected void bind(@Nullable final LSDTransferEntity entity) {
        if (entity == null) {
            throw new NullPointerException("Attempted to bind to a null entity.");
        }
        setEntityInternal(entity);
    }

    @Nonnull
    public LSDTransferEntity getEntityDiff() {
        final LSDTransferEntity newEntity = entity.asUpdateEntity();
        if (entity.hasURI()) {
            newEntity.setAttribute(LSDAttribute.URI, entity.getURI().toString());
        }
        for (final Bindable bindable : bindings.values()) {
            if (bindable.isMultiValue()) {
                newEntity.setValues(bindable.getBoundAttribute(), bindable.getStringValues());
            }
            else {
                String stringValue = bindable.getStringValue();
                if (stringValue != null) {
                    newEntity.setAttribute(bindable.getBoundAttribute(), stringValue);
                }
            }
        }
        return newEntity;
    }

    public void save() {

    }

    protected abstract boolean isSaveOnExit();

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