package cazcade.liquid.api;

import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public interface LiquidMessageHandler<T extends LiquidMessage> {

    @Nullable
    T handle(T message) throws Exception;

}
