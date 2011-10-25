package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuthorizationRequest extends AbstractRequest {
    private LiquidUUID resource;
    private LiquidURI uri;
    private List<AuthorizationRequest> or = new ArrayList<AuthorizationRequest>();
    private List<AuthorizationRequest> and = new ArrayList<AuthorizationRequest>();

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

    public AuthorizationRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID resource, LiquidURI uri, LiquidPermission... permission) {
        if (resource == null && uri == null) {
            throw new NullPointerException("Cannot create an authorization request with a null resource id and uri.");
        }
        this.id = id;
        this.uri = uri;
        this.resource = resource;
        this.permission = permission;
        this.identity = identity;
    }

    public AuthorizationRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID resource, LiquidURI uri, LiquidPermission[] permission, List<AuthorizationRequest> or, List<AuthorizationRequest> and) {
        this(id, identity, resource, uri, permission);
        this.or.addAll(or);
        this.and.addAll(and);
    }

    public LiquidUUID getResource() {
        return resource;
    }


    public AuthorizationRequest or(AuthorizationRequest ... authorizationRequests) {
        for (AuthorizationRequest authorizationRequest : authorizationRequests) {
            or.add(authorizationRequest);
        }
        return this;
    }

    public AuthorizationRequest and(AuthorizationRequest ... authorizationRequests) {
        for (AuthorizationRequest authorizationRequest : authorizationRequests) {
            and.add(authorizationRequest);
        }
        return this;
    }


    public List<AuthorizationRequest> getOr() {
        return or;
    }

    public List<AuthorizationRequest> getAnd() {
        return and;
    }

    @Override
    public LiquidMessage copy() {
        return new AuthorizationRequest(id, identity, resource, uri, permission, or, and);
    }

    public LiquidPermission[] getActions() {
        return permission;
    }


    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Collections.EMPTY_LIST;
    }

    public LSDEntity getEntity() {
        return null;
    }

    public List<String> getNotificationLocations() {
        return null;
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.AUTHORIZATION_REQUEST;
    }

    public boolean isMutationRequest() {
        return false;
    }

    public LiquidURI getUri() {
        return uri;
    }

}