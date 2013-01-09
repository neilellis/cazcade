package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class DeletePoolObjectRequest extends AbstractDeletionRequest {
    public DeletePoolObjectRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity,
                                   @Nullable final LiquidUUID pool, @Nullable final LiquidUUID target,
                                   @Nullable final LiquidURI uri) {
        super();
        setId(id);
        setSessionId(identity);
        setPoolUUID(pool);
        setTarget(target);
        setUri(uri);
    }

    public DeletePoolObjectRequest(final LiquidSessionIdentifier identity, final LiquidUUID pool, final LiquidUUID target) {
        this(null, identity, pool, target, null);
    }

    public DeletePoolObjectRequest(final LiquidURI uri) {
        this(null, LiquidSessionIdentifier.ANON, null, null, uri);
    }

    public DeletePoolObjectRequest() {
        super();
    }

    public DeletePoolObjectRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new DeletePoolObjectRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (hasUri()) {
            return Arrays.asList(new AuthorizationRequest(getSessionIdentifier(), getUri(), LiquidPermission.DELETE).or(new AuthorizationRequest(getSessionIdentifier(),
                    getUri().getWithoutFragment(), LiquidPermission.EDIT
            )
                                                                                               ), new AuthorizationRequest(getSessionIdentifier(),
                    getUri().getWithoutFragment(), LiquidPermission.MODIFY
            )
                                );
        }
        else {
            return Arrays.asList(new AuthorizationRequest(getSessionIdentifier(), getTarget(), LiquidPermission.DELETE).or(new AuthorizationRequest(getSessionIdentifier(),
                    getPoolUUID(), LiquidPermission.EDIT
            )
                                                                                                  ), new AuthorizationRequest(getSessionIdentifier(),
                    getPoolUUID(), LiquidPermission.MODIFY
            )
                                );
        }
    }

    public List<String> getNotificationLocations() {
        if (hasUri()) {
            return Arrays.asList(getUri().asReverseDNSString(), getUri().getWithoutFragment().asReverseDNSString());
        }
        else {
            return Arrays.asList(getPoolUUID().toString(), getTarget().toString());
        }
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.DELETE_POOL_OBJECT;
    }
}
