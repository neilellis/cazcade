package cazcade.fountain.server.rest;

import java.util.Map;

/**
 * @author Neil Ellis
 */

public class RestHandlerFactory {
    private Map<String, RestHandler> handlers;

    public Map<String, RestHandler> getHandlers() {
        return handlers;
    }

    public void setHandlers(final Map handlers) {
        this.handlers = handlers;
    }
}
