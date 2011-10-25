package cazcade.fountain.server.rest;

import cazcade.liquid.api.LiquidSessionIdentifier;

/**
 * @author Neil Ellis
 */

public class RestContext {
    private static ThreadLocal<RestContext> context = new ThreadLocal<RestContext>() {
        @Override
        protected RestContext initialValue() {
            return new RestContext();
        }
    };
    private LiquidSessionIdentifier username;
    private String sessionId;

    public static RestContext getContext() {
        return context.get();
    }

    public static void clearContext() {
        context.set(new RestContext());
    }

    public LiquidSessionIdentifier getCredentials() {
        return username;
    }

    public void setCredentials(LiquidSessionIdentifier username) {
        this.username = username;
    }

}
