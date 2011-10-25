package cazcade.fountain.notification.client.api;

import cazcade.liquid.api.LiquidRequest;
import cazcade.liquid.api.LiquidMessage;

/**
 * @author neilelliz@cazcade.com
 */
public class RequestNotification {

    private RequestState state;
    private LiquidRequest request;
    private LiquidMessage response;

    public RequestNotification(RequestState state, LiquidRequest request, LiquidMessage response) {
        this.state = state;
        this.request = request;
        this.response = response;
    }

    public RequestNotification(RequestState state, LiquidRequest request) {
        this.request = request;
    }

    public void setState(RequestState state) {
        this.state = state;
    }

    public RequestState getState() {
        return state;
    }

    public LiquidRequest getRequest() {
        return request;
    }

    public LiquidMessage getResponse() {
        return response;
    }
}
