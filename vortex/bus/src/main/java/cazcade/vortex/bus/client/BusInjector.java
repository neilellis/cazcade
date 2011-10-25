package cazcade.vortex.bus.client;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

/**
 * @author neilellis@cazcade.com
 */
@GinModules(BusClientModule.class)
public interface BusInjector extends Ginjector{

    BusFactory getBusFactory();
}