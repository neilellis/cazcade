/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.server.rest;

import cazcade.liquid.api.SessionIdentifier;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */

public class RestContext {
    @Nonnull
    private static final ThreadLocal<RestContext> context = new ThreadLocal<RestContext>() {
        @Nonnull @Override
        protected RestContext initialValue() {
            return new RestContext();
        }
    };
    private SessionIdentifier username;
    private String            sessionId;

    @Nonnull
    public static RestContext getContext() {
        return context.get();
    }

    public static void clearContext() {
        context.set(new RestContext());
    }

    public SessionIdentifier getCredentials() {
        return username;
    }

    public void setCredentials(final SessionIdentifier username) {
        this.username = username;
    }
}
