package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class UpdatePoolObjectRequest extends AbstractUpdateRequest {

    public UpdatePoolObjectRequest() {
        super();
    }


    /**
     * @deprecated use URIs where possible.
     */

    public UpdatePoolObjectRequest(final LiquidSessionIdentifier identity, final LiquidUUID pool, final LiquidUUID target, final LSDEntity entity) {
        this(null, identity, pool, target, entity);
    }

    /**
     * @deprecated use URIs where possible.
     */

    public UpdatePoolObjectRequest(@Nullable final LiquidUUID id, final LiquidSessionIdentifier identity, final LiquidUUID pool, final LiquidUUID target, final LSDEntity entity) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
        setPoolUUID(pool);
        setRequestEntity(entity);
    }

    @Deprecated
    public UpdatePoolObjectRequest(final LiquidSessionIdentifier identity, final LiquidURI objectURI, final LSDEntity newEntity) {
        super();
        setSessionId(identity);
        setUri(objectURI);
        setRequestEntity(newEntity);
    }

    @Deprecated
    public UpdatePoolObjectRequest(final LiquidURI poolURI, @Nonnull final LSDEntity newEntity) {
        super();
        setUri(newEntity.getURI());
        setRequestEntity(newEntity);
    }

    public UpdatePoolObjectRequest(@Nonnull final LSDEntity newEntity) {
        super();
        if (newEntity.getURI() == null) {
            throw new IllegalArgumentException("To update a pool object the entity should have a URI");
        }
        setUri(newEntity.getURI());
        if (getUri().equals(getPoolURI())) {
            throw new IllegalArgumentException("To update a pool object the entity supplied should be a pool object and have a pool object URI ending in #<object-name> the URI supplied was " + getUri());
        }
        setRequestEntity(newEntity);
    }


    protected UpdatePoolObjectRequest(final LiquidUUID id, final LiquidSessionIdentifier identity, final LiquidUUID pool, final LiquidUUID target, final LiquidURI uri, final LSDEntity entity) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
        setPoolUUID(pool);
        setRequestEntity(entity);
        setUri(uri);
    }


    @Nullable
    @Override
    public LiquidMessage copy() {
        return new UpdatePoolObjectRequest(getId(), getSessionIdentifier(), getPoolUUID(), getTarget(), getUri(), getRequestEntity());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (getUri() != null) {
            return Arrays.asList(new AuthorizationRequest(getPoolURI(), LiquidPermission.EDIT).or(new AuthorizationRequest(getUri(), LiquidPermission.EDIT).and(new AuthorizationRequest(getPoolURI(), LiquidPermission.MODIFY))));
        } else {
            return Arrays.asList(new AuthorizationRequest(getPoolUUID(), LiquidPermission.EDIT).or(new AuthorizationRequest(getTarget(), LiquidPermission.EDIT).and(new AuthorizationRequest(getPoolUUID(), LiquidPermission.MODIFY))));
        }
    }

    public List<String> getNotificationLocations() {
        if (getPoolURI() != null) {
            return Arrays.asList(getPoolURI().asReverseDNSString(), getUri().asReverseDNSString());
        } else {
            return Arrays.asList(getPoolUUID().toString());
        }
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.UPDATE_POOL_OBJECT;
    }

}
