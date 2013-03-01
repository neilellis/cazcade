/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.profile;

import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.EntityAware;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusService;
import cazcade.vortex.gwt.util.client.history.HistoryAwareComposite;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public abstract class EntityBackedPanel extends HistoryAwareComposite implements EntityAware<TransferEntity>, HasValueChangeHandlers<TransferEntity> {
    @Nullable
    protected TransferEntity entity;

    protected EntityBackedPanel() {
        addValueChangeHandler(new ValueChangeHandler<TransferEntity>() {
            @Override public void onValueChange(ValueChangeEvent<TransferEntity> event) {
                onChange(event.getValue());
            }
        });
    }

    public void setEntity(@Nonnull final TransferEntity entity) {
        if (this.entity == null) {
            this.entity = entity;
        } else {
            this.entity = entity;
        }
        ValueChangeEvent.fire(this, entity);
    }


    protected void onChange(final Entity entity) {

    }


    @Nonnull
    public TransferEntity $() {
        if (entity == null) {
            throw new NullPointerException("Panel has not been initialized yet so the backing entity is null.");
        }
        return entity;
    }


    @Override public HandlerRegistration addValueChangeHandler(ValueChangeHandler<TransferEntity> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }
}


