package cazcade.liquid.api;

import cazcade.liquid.api.request.*;

/**
 * @author neilelliz@cazcade.com
 */
public enum LiquidMessageType {

    RESPONSE(LiquidMessage.class),
    REQUEST(LiquidRequest.class);

    private Class<? extends LiquidMessage> requestClass;


    LiquidMessageType(Class<? extends LiquidMessage> requestClass) {
        this.requestClass = requestClass;
    }

    public Class<? extends LiquidMessage> getRequestClass() {
        return requestClass;
    }
}