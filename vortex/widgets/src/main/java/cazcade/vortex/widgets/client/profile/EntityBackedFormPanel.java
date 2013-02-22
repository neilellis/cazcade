/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.profile;

import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.vortex.common.client.events.InvalidEvent;
import cazcade.vortex.common.client.events.ValidEvent;
import cazcade.vortex.gwt.util.client.ClientLog;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;

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


    @Override public void setEntity(@Nonnull LSDTransferEntity entity) {
        super.setEntity(entity);

    }

    public void setAndBindEntity(@Nonnull LSDTransferEntity entity) {
        super.setEntity(entity);
        bindEntity(entity);

    }

    public void addBinding(final LSDTransferEntity otherEntity, @Nonnull final Bindable field, final LSDAttribute attribute) {
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

    public void addBinding(@Nonnull final Bindable field, @Nullable final LSDAttribute attribute) {
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
                    Window.alert("Sending update for " + attribute + " (" + field + ")");
                    if (ClientLog.isDebugMode()) {
                        ClientLog.log("Sending update for " + attribute + " (" + field + ")");
                    }
                    getUpdateEntityAction(field).run();
                }
            });
        }
        bindings.put(attribute, field);
    }


    private void bindEntity(@Nullable final LSDTransferEntity entity) {
        if (entity == null) {
            throw new NullPointerException("Attempted to bind to a null entity.");
        }
        for (Map.Entry<LSDAttribute, Bindable> entry : bindings.entrySet()) {
            final Bindable field = entry.getValue();
            final LSDAttribute attribute = entry.getKey();
            field.bind(entity, attribute, getReferenceDataPrefix());

        }
        if (isValid()) {
            fireEvent(new ValidEvent());
        } else {
            fireEvent(new InvalidEvent());
        }
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
            } else {
                final String stringValue = bindable.getStringValue();
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