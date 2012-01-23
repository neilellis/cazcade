package cazcade.vortex.widgets.client.profile;

import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.gwt.util.client.history.HistoryAwareComposite;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public abstract class EntityBackedPanel extends HistoryAwareComposite {
    @Nonnull
    private final Bus bus = BusFactory.getInstance();
    @Nullable
    protected LSDTransferEntity entity;

    public void setEntity(@Nonnull final LSDTransferEntity entity) {
        this.entity = entity;
        bind(entity);

    }

    public void setEntityInternal(@Nonnull final LSDTransferEntity entity) {
        if (this.entity == null) {
            this.entity = entity;
            onInitial(entity);
        }
        else {
            this.entity = entity;
            onUpdate(entity);
        }
        onChange(entity);
    }


    protected void onChange(final LSDBaseEntity entity) {

    }

    protected void onUpdate(final LSDBaseEntity entity) {

    }

    protected void onInitial(final LSDBaseEntity entity) {

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

    protected abstract void bind(LSDTransferEntity entity);


}


