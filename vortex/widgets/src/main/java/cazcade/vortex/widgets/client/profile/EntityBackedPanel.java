/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.widgets.client.profile;

import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusFactory;
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
public abstract class EntityBackedPanel extends HistoryAwareComposite implements HasValueChangeHandlers<LSDTransferEntity> {
    @Nonnull
    private final Bus bus = BusFactory.getInstance();
    @Nullable
    protected LSDTransferEntity entity;

    protected EntityBackedPanel() {
        addValueChangeHandler(new ValueChangeHandler<LSDTransferEntity>() {
            @Override public void onValueChange(ValueChangeEvent<LSDTransferEntity> event) {
                onChange(event.getValue());
            }
        });
    }

    public void setEntity(@Nonnull final LSDTransferEntity entity) {
        this.entity = entity;
        bindEntity(entity);

    }

    public void setEntityInternal(@Nonnull final LSDTransferEntity entity) {
        if (this.entity == null) {
            this.entity = entity;
        } else {
            this.entity = entity;
            ValueChangeEvent.fire(this, entity);
        }
        onChange(entity);
    }


    protected void onChange(final LSDBaseEntity entity) {

    }


    @Nonnull
    public LSDTransferEntity getEntity() {
        if (entity == null) {
            throw new NullPointerException("Panel has not been initialized yet so the backing entity is null.");
        }
        return entity;
    }

    @Nonnull
    public Bus getBus() {
        return bus;
    }

    protected abstract void bindEntity(LSDTransferEntity entity);


    @Override public HandlerRegistration addValueChangeHandler(ValueChangeHandler<LSDTransferEntity> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }
}


