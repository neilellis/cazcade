package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDEntity;

import java.util.Arrays;
import java.util.List;

public class UpdatePoolObjectRequest extends AbstractUpdateRequest {

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
        this.setId(id);
        this.setSessionId(identity);
        this.setTarget(target);
        this.setPoolUUID(pool);
        this.setRequestEntity(entity);
    }

    @Deprecated
    public UpdatePoolObjectRequest(LiquidSessionIdentifier identity, LiquidURI objectURI, LSDEntity newEntity) {
        this.setSessionId(identity);
        this.setUri(objectURI);
        this.setRequestEntity(newEntity);
    }

    @Deprecated
    public UpdatePoolObjectRequest(LiquidURI poolURI, LSDEntity newEntity) {
        this.setUri(newEntity.getURI());
        this.setRequestEntity(newEntity);
    }

    public UpdatePoolObjectRequest(LSDEntity newEntity) {
        if (newEntity.getURI() == null) {
            throw new IllegalArgumentException("To update a pool object the entity should have a URI");
        }
        this.setUri(newEntity.getURI());
        if (getUri().equals(getPoolURI())) {
            throw new IllegalArgumentException("To update a pool object the entity supplied should be a pool object and have a pool object URI ending in #<object-name> the URI supplied was " + getUri());
        }
        this.setRequestEntity(newEntity);
    }


    protected UpdatePoolObjectRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID pool, LiquidUUID target, LiquidURI uri, LSDEntity entity) {
        this.setId(id);
        this.setSessionId(identity);
        this.setTarget(target);
        this.setPoolUUID(pool);
        this.setRequestEntity(entity);
        this.setUri(uri);
    }


    @Override
    public LiquidMessage copy() {
        return new UpdatePoolObjectRequest(getId(), getSessionIdentifier(), getPoolUUID(), super.getTarget(), getUri(), super.getRequestEntity());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (getUri() != null) {
            return Arrays.asList(new AuthorizationRequest(getPoolURI(), LiquidPermission.EDIT).or(new AuthorizationRequest(getUri(), LiquidPermission.EDIT).and(new AuthorizationRequest(getPoolURI(), LiquidPermission.MODIFY))));
        } else {
            return Arrays.asList(new AuthorizationRequest(getPoolUUID(), LiquidPermission.EDIT).or(new AuthorizationRequest(super.getTarget(), LiquidPermission.EDIT).and(new AuthorizationRequest(getPoolUUID(), LiquidPermission.MODIFY))));
        }
    }

    public List<String> getNotificationLocations() {
        if (getPoolURI() != null) {
            return Arrays.asList(getPoolURI().asReverseDNSString(), getUri().asReverseDNSString());
        } else {
            return Arrays.asList(getPoolUUID().toString());
        }
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.UPDATE_POOL_OBJECT;
    }

}
