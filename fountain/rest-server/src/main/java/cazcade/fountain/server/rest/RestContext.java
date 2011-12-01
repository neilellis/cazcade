package cazcade.fountain.server.rest;

import cazcade.liquid.api.LiquidSessionIdentifier;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */

public class RestContext {
    @Nonnull
    private static final ThreadLocal<RestContext> context = new ThreadLocal<RestContext>() {
        @Nonnull
        @Override
        protected RestContext initialValue() {
            return new RestContext();
        }
    };
    private LiquidSessionIdentifier username;
    private String sessionId;

    @Nonnull
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
