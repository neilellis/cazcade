package cazcade.liquid.api;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public interface LiquidMessageHandler<T extends LiquidMessage> {
    @Nonnull
    T handle(T message) throws Exception;
}
