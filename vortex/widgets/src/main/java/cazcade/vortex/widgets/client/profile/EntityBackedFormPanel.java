/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.profile;

import cazcade.liquid.api.lsd.Attribute;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.vortex.common.client.events.InvalidEvent;
import cazcade.vortex.common.client.events.ValidEvent;
import cazcade.vortex.gwt.util.client.ClientLog;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author neilellis@cazcade.com
 */
public abstract class EntityBackedFormPanel extends EntityBackedPanel {

    @Nonnull
    private final Map<Attribute, Bindable> bindings = new HashMap<Attribute, Bindable>();


    @Override public void setEntity(@Nonnull TransferEntity entity) {
        super.setEntity(entity);

    }

    @Override
    public void $(TransferEntity entity) {
        super.setEntity(entity);
        bindEntity(entity);

    }

    public void bind(final TransferEntity otherEntity, @Nonnull final Bindable field, final Attribute attribute) {
        field.bind(otherEntity, attribute, getReferenceDataPrefix());
        if (!isSaveOnExit()) {
            field.addChangeHandler(new ValueChangeHandler() {
                @Override public void onValueChange(ValueChangeEvent event) {
                    getUpdateEntityAction(field).run();
                }
            });
        }
        bindings.put(attribute, field);

    }

    public void bind(@Nonnull final Bindable field, @Nullable final Attribute attribute) {
        if (bindings.containsKey(attribute)) {
            throw new IllegalStateException("Attribute " + attribute + " is already bound.");
        }
        if (isSaveOnExit()) {
            field.addChangeHandler(new ValueChangeHandler() {
                @Override public void onValueChange(ValueChangeEvent event) {
                    if (isValid()) {
                        fireEvent(new ValidEvent());
                    } else {
                        fireEvent(new InvalidEvent());
                    }
                }
            });

        } else {
            field.addChangeHandler(new ValueChangeHandler() {
                @Override public void onValueChange(ValueChangeEvent event) {
                    if (ClientLog.isDebugMode()) {
                        ClientLog.log("Sending update for " + attribute + " (" + field + ")");
                    }
                    getUpdateEntityAction(field).run();
                }
            });
        }
        bindings.put(attribute, field);
    }


    private void bindEntity(@Nullable final TransferEntity entity) {
        if (entity == null) {
            throw new NullPointerException("Attempted to bind to a null entity.");
        }
        for (Map.Entry<Attribute, Bindable> entry : bindings.entrySet()) {
            final Bindable field = entry.getValue();
            final Attribute attribute = entry.getKey();
            field.bind(entity, attribute, getReferenceDataPrefix());

        }
        if (isValid()) {
            fireEvent(new ValidEvent());
        } else {
            fireEvent(new InvalidEvent());
        }
    }

    @Nonnull
    public TransferEntity getEntityDiff() {
        final TransferEntity newEntity = entity.asUpdateEntity();
        if (entity.hasURI()) {
            newEntity.$(Dictionary.URI, entity.uri().toString());
        }
        for (final Bindable bindable : bindings.values()) {
            if (bindable.isMultiValue()) {
                newEntity.$(bindable.getBoundAttribute(), bindable.getStringValues());
            } else {
                final String stringValue = bindable.getStringValue();
                if (stringValue != null) {
                    newEntity.$(bindable.getBoundAttribute(), stringValue);
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