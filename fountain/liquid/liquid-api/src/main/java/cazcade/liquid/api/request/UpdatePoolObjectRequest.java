package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDEntity;

import java.util.Arrays;
import java.util.List;

public class UpdatePoolObjectRequest extends AbstractUpdateRequest {
    private LiquidUUID pool;
    private LiquidURI poolURI;

    public UpdatePoolObjectRequest() {
    }


    /**
     * @deprecated use URIs where possible.
     */

    public UpdatePoolObjectRequest(LiquidSessionIdentifier identity, LiquidUUID pool, LiquidUUID target, LSDEntity entity) {
        this(null, identity, pool, target, entity);
    }

    /**
     * @deprecated use URIs where possible.
     */

    public UpdatePoolObjectRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID pool, LiquidUUID target, LSDEntity entity) {
        this.id = id;
        this.identity = identity;
        this.target = target;
        this.pool = pool;
        this.entity = entity;
    }

    @Deprecated
    public UpdatePoolObjectRequest(LiquidSessionIdentifier identity, LiquidURI poolURI, LiquidURI objectURI, LSDEntity newEntity) {
        this.identity = identity;
        this.poolURI = poolURI;
        this.uri = objectURI;
        this.entity = newEntity;
    }

     @Deprecated
    public UpdatePoolObjectRequest(LiquidURI poolURI, LSDEntity newEntity) {
        this.poolURI = poolURI;
        this.uri = newEntity.getURI();
        this.entity = newEntity;
    }

    public UpdatePoolObjectRequest(LSDEntity newEntity) {
        if(newEntity.getURI() == null) {
            throw new IllegalArgumentException("To update a pool object the entity should have a URI");
        }
        this.uri = newEntity.getURI();
        this.poolURI = this.uri.getWithoutFragment();
        if(uri.equals(poolURI)) {
            throw new IllegalArgumentException("To update a pool object the entity supplied should be a pool object and have a pool object URI ending in #<object-name> the URI supplied was "+uri);
        }
        this.entity = newEntity;
    }


    protected UpdatePoolObjectRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID pool, LiquidURI poolURI, LiquidUUID target, LiquidURI uri, LSDEntity entity) {
        this.id = id;
        this.identity = identity;
        this.target = target;
        this.pool = pool;
        this.entity = entity;
        this.poolURI = poolURI;
        this.uri = uri;
    }


    @Override
    public LiquidMessage copy() {
        return new UpdatePoolObjectRequest(id, identity, pool, poolURI, target, uri, entity);
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (uri != null) {
            return Arrays.asList(new AuthorizationRequest(poolURI, LiquidPermission.EDIT).or(new AuthorizationRequest(uri, LiquidPermission.EDIT).and(new AuthorizationRequest(poolURI, LiquidPermission.MODIFY))));
        } else {
            return Arrays.asList(new AuthorizationRequest(pool, LiquidPermission.EDIT).or(new AuthorizationRequest(target, LiquidPermission.EDIT).and(new AuthorizationRequest(pool, LiquidPermission.MODIFY))));
        }
    }

    public List<String> getNotificationLocations() {
        if (poolURI != null) {
            return Arrays.asList(poolURI.asReverseDNSString(), uri.asReverseDNSString());
        } else {
            return Arrays.asList(pool.toString());
        }
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.UPDATE_POOL_OBJECT;
    }

}
