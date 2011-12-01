package cazcade.vortex.widgets.client.profile;

import cazcade.liquid.api.lsd.LSDEntity;
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
    protected LSDEntity entity;

    public void setEntity(@Nullable LSDEntity entity) {
        this.entity = entity;
        bind(entity);

    }

    public void setEntityInternal(@Nullable LSDEntity entity) {
        this.entity = entity;
        if (entity == null) {
            onInitial(entity);
        } else {
            onUpdate(entity);
        }
        onChange(entity);
    }


    protected void onChange(LSDEntity entity) {

    }

    protected void onUpdate(LSDEntity entity) {

    }

    protected void onInitial(LSDEntity entity) {

    }

    @Nullable
    public LSDEntity getEntity() {
        return entity;
    }

    @Nonnull
    public Bus getBus() {
        return bus;
    }

    protected abstract void bind(LSDEntity entity);


}


