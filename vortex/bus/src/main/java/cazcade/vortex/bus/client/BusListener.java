package cazcade.vortex.bus.client;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequest;
import cazcade.liquid.api.LiquidMessage;

/**
 * @author neilellis@cazcade.com
 */
public interface BusListener<T extends LiquidMessage> {

    void handle(T message);

}
