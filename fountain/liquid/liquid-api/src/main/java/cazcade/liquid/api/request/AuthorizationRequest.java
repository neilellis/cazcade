package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuthorizationRequest extends AbstractRequest {
    @Nonnull
    private final List<AuthorizationRequest> or = new ArrayList<AuthorizationRequest>();
    @Nonnull
    private final List<AuthorizationRequest> and = new ArrayList<AuthorizationRequest>();
    private LiquidPermission[] permission;

    public AuthorizationRequest() {
    }

    public AuthorizationRequest(LiquidUUID resource, LiquidPermission... permission) {
        this(null, null, resource, null, permission);
    }

    public AuthorizationRequest(LiquidURI uri, LiquidPermission... permission) {
        this(null, null, null, uri, permission);
    }


    public AuthorizationRequest(LiquidSessionIdentifier identity, LiquidUUID resource, LiquidPermission... permission) {
        this(null, identity, resource, null, permission);
    }

    public AuthorizationRequest(LiquidSessionIdentifier identity, LiquidURI uri, LiquidPermission... permission) {
        this(null, identity, null, uri, permission);
    }

    public AuthorizationRequest(@Nullable LiquidUUID id, @Nullable LiquidSessionIdentifier identity, @Nullable LiquidUUID resource, @Nullable LiquidURI uri, LiquidPermission... permission) {
        if (resource == null && uri == null) {
            throw new NullPointerException("Cannot create an authorization request with a null resource id and uri.");
        }
        this.setId(id);
        this.setUri(uri);
        this.setTarget(resource);
        this.permission = permission;
        this.setSessionId(identity);
    }

    public AuthorizationRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID resource, LiquidURI uri, LiquidPermission[] permission, List<AuthorizationRequest> or, List<AuthorizationRequest> and) {
        this(id, identity, resource, uri, permission);
        this.or.addAll(or);
        this.and.addAll(and);
    }


    @Nonnull
    public AuthorizationRequest or(AuthorizationRequest... authorizationRequests) {
        Collections.addAll(or, authorizationRequests);
        return this;
    }

    @Nonnull
    public AuthorizationRequest and(AuthorizationRequest... authorizationRequests) {
        Collections.addAll(and, authorizationRequests);
        return this;
    }


    @Nonnull
    public List<AuthorizationRequest> getOr() {
        return or;
    }

    @Nonnull
    public List<AuthorizationRequest> getAnd() {
        return and;
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new AuthorizationRequest(getId(), getSessionIdentifier(), super.getTarget(), super.getUri(), permission, or, and);
    }

    public LiquidPermission[] getActions() {
        return permission;
    }


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


}