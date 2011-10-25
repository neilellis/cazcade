package cazcade.vortex.widgets.client.profile;

import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.vortex.bus.client.Bus;
import cazcade.vortex.bus.client.BusFactory;
import cazcade.vortex.gwt.util.client.history.HistoryAwareComposite;
import com.google.gwt.user.client.ui.Composite;

/**
 * @author neilellis@cazcade.com
 */
public abstract class EntityBackedPanel extends HistoryAwareComposite {
    private final Bus bus = BusFactory.getInstance();
    protected LSDEntity entity;

    public void setEntity(LSDEntity entity) {
        this.entity = entity;
        bind(entity);

    }

    public void setEntityInternal(LSDEntity entity) {
        this.entity= entity;
        if(entity == null) {
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

    public LSDEntity getEntity() {
        return entity;
    }

    public Bus getBus() {
        return bus;
    }

    protected abstract void bind(LSDEntity entity);


}


