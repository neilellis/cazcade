package cazcade.vortex.bus.client;

import cazcade.liquid.api.LiquidMessage;

/**
 * @author neilellis@cazcade.com
 */
public class AbstractBusListener<T extends LiquidMessage> implements BusListener<T> {
    
    public void handle(T message) {
    }

}
