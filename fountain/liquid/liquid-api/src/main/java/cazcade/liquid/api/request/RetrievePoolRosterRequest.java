package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class RetrievePoolRosterRequest extends AbstractRetrievalRequest {
    private RetrievePoolRosterRequest(final LiquidUUID id, final LiquidSessionIdentifier identity, final LiquidUUID target, final LiquidURI uri) {
        super();
        setTarget(target);
        setId(id);
        setSessionId(identity);
        setUri(uri);
    }

    public RetrievePoolRosterRequest(final LiquidSessionIdentifier identity, final LiquidUUID target) {
        this(null, identity, target);
    }

    public RetrievePoolRosterRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity, final LiquidUUID target) {
        super();
        setTarget(target);
        setId(id);
        setSessionId(identity);
    }

    public RetrievePoolRosterRequest(final LiquidSessionIdentifier identity, final LiquidURI uri) {
        this(null, identity, uri);
    }

    public RetrievePoolRosterRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity, final LiquidURI uri) {
        super();
        setId(id);
        setSessionId(identity);
        setUri(uri);
    }

    public RetrievePoolRosterRequest() {
        super();
    }

    public RetrievePoolRosterRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new RetrievePoolRosterRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (hasTarget()) {
            return Arrays.asList(new AuthorizationRequest(getSessionIdentifier(), getTarget(), LiquidPermission.VIEW));
        }
        else {
            return Arrays.asList(new AuthorizationRequest(getSessionIdentifier(), getUri(), LiquidPermission.VIEW));
        }
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_POOL_ROSTER;
    }
}
