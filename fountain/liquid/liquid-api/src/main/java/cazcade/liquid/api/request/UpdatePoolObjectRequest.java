package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class UpdatePoolObjectRequest extends AbstractUpdateRequest {
    protected UpdatePoolObjectRequest(final LiquidUUID id, final LiquidSessionIdentifier identity, final LiquidUUID pool,
                                      final LiquidUUID target, final LiquidURI uri, final LSDTransferEntity entity) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
        setPoolUUID(pool);
        setRequestEntity(entity);
        setUri(uri);
    }

    /**
     * @deprecated use URIs where possible.
     */

    public UpdatePoolObjectRequest(@Nullable final LiquidUUID id, final LiquidSessionIdentifier identity, final LiquidUUID pool,
                                   final LiquidUUID target, final LSDTransferEntity entity) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
        setPoolUUID(pool);
        setRequestEntity(entity);
    }

    /**
     * @deprecated use URIs where possible.
     */

    public UpdatePoolObjectRequest(final LiquidSessionIdentifier identity, final LiquidUUID pool, final LiquidUUID target,
                                   final LSDTransferEntity entity) {
        this(null, identity, pool, target, entity);
    }

    @Deprecated
    public UpdatePoolObjectRequest(final LiquidSessionIdentifier identity, final LiquidURI objectURI,
                                   final LSDTransferEntity newEntity) {
        super();
        setSessionId(identity);
        setUri(objectURI);
        setRequestEntity(newEntity);
    }

    @Deprecated
    public UpdatePoolObjectRequest(final LiquidURI poolURI, @Nonnull final LSDTransferEntity request) {
        super();
        setUri(request.getURI());
        setRequestEntity(request);
    }

    public UpdatePoolObjectRequest(@Nonnull final LSDTransferEntity request) {
        super();
        if (!request.hasURI()) {
            throw new IllegalArgumentException("To update a pool object the entity should have a URI");
        }
        setUri(request.getURI());
        if (getUri().equals(getPoolURI())) {
            throw new IllegalArgumentException(
                    "To update a pool object the entity supplied should be a pool object and have a pool object URI ending in #<object-name> the URI supplied was " +
                    getUri()
            );
        }
        setRequestEntity(request);
    }

    public UpdatePoolObjectRequest() {
        super();
    }

     UpdatePoolObjectRequest(final LSDTransferEntity entity, String marker) {
        super(entity);
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new UpdatePoolObjectRequest(getEntity(), "copy constructor");
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        final LiquidSessionIdentifier sessionIdentifier = getSessionIdentifier();
        if (hasUri()) {
            return Arrays.asList(new AuthorizationRequest(sessionIdentifier, getPoolURI(), LiquidPermission.EDIT).or(new AuthorizationRequest(sessionIdentifier, getUri(),
                                                                                                                           LiquidPermission.EDIT
            ).and(new AuthorizationRequest(sessionIdentifier, getPoolURI(), LiquidPermission.MODIFY))
                                                                                                 )
                                );
        }
        else {
            return Arrays.asList(new AuthorizationRequest(sessionIdentifier, getPoolUUID(), LiquidPermission.EDIT).or(new AuthorizationRequest(
                    sessionIdentifier, getTarget(), LiquidPermission.EDIT
            ).and(new AuthorizationRequest(sessionIdentifier, getPoolUUID(), LiquidPermission.MODIFY))
                                                                                                  )
                                );
        }
    }

    public List<String> getNotificationLocations() {
        if (hasUri()) {
            return Arrays.asList(getPoolURI().asReverseDNSString(), getUri().asReverseDNSString());
        }
        else {
            return Arrays.asList(getPoolUUID().toString());
        }
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.UPDATE_POOL_OBJECT;
    }
}
