package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RetrievePoolObjectRequest extends AbstractRetrievalRequest {
    public RetrievePoolObjectRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier authenticatedUser,
                                     final LiquidUUID pool, final LiquidUUID target, final boolean historical) {
        super();
        setSessionId(authenticatedUser);
        setPoolUUID(pool);
        setTarget(target);
        setHistorical(historical);
    }

    private RetrievePoolObjectRequest(final LiquidUUID id, final LiquidSessionIdentifier authenticatedUser, final LiquidUUID pool,
                                      final LiquidUUID target, final LiquidURI uri) {
        super();
        setId(id);
        setSessionId(authenticatedUser);
        setPoolUUID(pool);
        setTarget(target);
        setUri(uri);
    }

    public RetrievePoolObjectRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity,
                                     final LiquidURI uri, final boolean historical) {
        super();
        setId(id);
        setSessionId(identity);
        setUri(uri);
        setHistorical(historical);
    }

    public RetrievePoolObjectRequest(final LiquidSessionIdentifier identity, final LiquidUUID pool, final LiquidUUID target,
                                     final boolean historical) {
        this(null, identity, pool, target, historical);
    }

    public RetrievePoolObjectRequest(final LiquidSessionIdentifier identity, final LiquidURI uri, final boolean historical) {
        this(null, identity, uri, historical);
    }

    public RetrievePoolObjectRequest(final LiquidUUID pool, final LiquidUUID target, final boolean historical) {
        this(null, null, pool, target, historical);
    }

    public RetrievePoolObjectRequest(final LiquidURI uri, final boolean historical) {
        this(null, null, uri, historical);
    }

    public RetrievePoolObjectRequest() {
        super();
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new RetrievePoolObjectRequest(getId(), getSessionIdentifier(), getPoolUUID(), getTarget(), getUri());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (getPoolUUID() != null) {
            return Arrays.asList(new AuthorizationRequest(getTarget(), LiquidPermission.VIEW), new AuthorizationRequest(
                    getPoolUUID(), LiquidPermission.VIEW
            )
                                );
        }
        else {
            return Collections.EMPTY_LIST;
        }
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_POOL_OBJECT;
    }
}
