package cazcade.vortex.bus.client;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/**
 * @author neilellis@cazcade.com
 */
public class BusClientModule extends AbstractGinModule{
    @Override
    protected void configure() {
        bind(BusImpl.class).in(Singleton.class);

    }
}