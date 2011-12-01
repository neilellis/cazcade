package cazcade.liquid.api;

/**
 * @author neilelliz@cazcade.com
 */
public enum LiquidMessageType {

    RESPONSE(LiquidMessage.class),
    REQUEST(LiquidRequest.class);

    private final Class<? extends LiquidMessage> requestClass;


    LiquidMessageType(Class<? extends LiquidMessage> requestClass) {
        this.requestClass = requestClass;
    }

    public Class<? extends LiquidMessage> getRequestClass() {
        return requestClass;
    }
}