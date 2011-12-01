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

    public void setEntity(@Nullable final LSDTransferEntity entity) {
        this.entity = entity;
        bind(entity);

    }

    public void setEntityInternal(@Nullable final LSDTransferEntity entity) {
        this.entity = entity;
        if (entity == null) {
            onInitial(entity);
        } else {
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

    @Nullable
    public LSDTransferEntity getEntity() {
        return entity;
    }

    @Nonnull
    public Bus getBus() {
        return bus;
    }

    protected abstract void bind(LSDTransferEntity entity);


}


