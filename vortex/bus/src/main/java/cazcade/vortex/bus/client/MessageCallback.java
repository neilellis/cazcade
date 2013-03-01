package cazcade.vortex.bus.client;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequest;
import cazcade.liquid.api.LiquidMessage;

/**
 * @author neilellis@cazcade.com
 */
public interface MessageCallback<T extends LiquidMessage> {

    void onSuccess(T request, T response);
    void onFailure(T request, T response);
    void onException(T request, Throwable error);
}
