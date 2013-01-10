/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AuthorizationRequest extends AbstractRequest {
    @Nonnull
    private final List<AuthorizationRequest> or  = new ArrayList<AuthorizationRequest>();
    @Nonnull
    private final List<AuthorizationRequest> and = new ArrayList<AuthorizationRequest>();
    private LiquidPermission[] permission;

    public AuthorizationRequest(@Nullable final LiquidUUID id, final LiquidSessionIdentifier identity, @Nullable final LiquidUUID resource, @Nullable final LiquidURI uri, final LiquidPermission[] permission, final List<AuthorizationRequest> or, final List<AuthorizationRequest> and) {
        this(id, identity, resource, uri, permission);
        this.or.addAll(or);
        this.and.addAll(and);
    }

    public AuthorizationRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier session, @Nullable final LiquidUUID resource, @Nullable final LiquidURI uri, final LiquidPermission... permission) {
        super();
        if (resource == null && uri == null) {
            throw new IllegalArgumentException("Cannot create an authorization request with a null resource id and uri.");
        }
        setId(id);
        setUri(uri);
        setTarget(resource);
        this.permission = permission;
        setSessionId(session);
    }

    public AuthorizationRequest(final LiquidSessionIdentifier identity, final LiquidUUID resource, final LiquidPermission... permission) {
        this(null, identity, resource, null, permission);
    }

    public AuthorizationRequest(final LiquidSessionIdentifier identity, final LiquidURI uri, final LiquidPermission... permission) {
        this(null, identity, null, uri, permission);
    }

    //    public AuthorizationRequest(final LiquidUUID resource, final LiquidPermission... permission) {
    //        this(null, null, resource, null, permission);
    //    }
    //
    //    public AuthorizationRequest(final LiquidURI uri, final LiquidPermission... permission) {
    //        this(null, null, null, uri, permission);
    //    }

    public AuthorizationRequest() {
        super();
    }

    @Nonnull
    public AuthorizationRequest and(final AuthorizationRequest... authorizationRequests) {
        Collections.addAll(and, authorizationRequests);
        return this;
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new AuthorizationRequest(hasId() ? getId() : null, getSessionIdentifier(), hasTarget() ? getTarget() : null, hasUri()
                                                                                                                            ? getUri()
                                                                                                                            : null, permission, or, and);
    }

    public LiquidPermission[] getActions() {
        return permission;
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Collections.EMPTY_LIST;
    }

    @Nullable
    public List<String> getNotificationLocations() {
        return null;
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.AUTHORIZATION_REQUEST;
    }

    public boolean isMutationRequest() {
        return false;
    }

    @Nonnull
    public AuthorizationRequest or(final AuthorizationRequest... authorizationRequests) {
        Collections.addAll(or, authorizationRequests);
        return this;
    }

    @Nonnull
    public List<AuthorizationRequest> getAnd() {
        return and;
    }

    @Nonnull
    public List<AuthorizationRequest> getOr() {
        return or;
    }

    @Nonnull @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("AuthorizationRequest");
        sb.append("{or=").append(or);
        sb.append(", and=").append(and);
        sb.append(", permission=").append(permission == null ? "null" : Arrays.asList(permission).toString());
        sb.append('}');
        return sb.toString();
    }
}