package cazcade.fountain.notification.client.api;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequest;

/**
 * @author neilelliz@cazcade.com
 */
public class RequestNotification {
    private RequestState state;
    private final LiquidRequest request;
    private LiquidMessage response;

    public RequestNotification(final RequestState state, final LiquidRequest request, final LiquidMessage response) {
        this.state = state;
        this.request = request;
        this.response = response;
    }

    public RequestNotification(final RequestState state, final LiquidRequest request) {
        this.request = request;
    }

    public LiquidRequest getRequest() {
        return request;
    }

    public LiquidMessage getResponse() {
        return response;
    }

    public RequestState getState() {
        return state;
    }

    public void setState(final RequestState state) {
        this.state = state;
    }
}
