package cazcade.liquid.api;

/**
 * @author neilellis@cazcade.com
 */
public interface LiquidMessageHandler<T extends LiquidMessage> {

    T handle(T message) throws Exception;

}
