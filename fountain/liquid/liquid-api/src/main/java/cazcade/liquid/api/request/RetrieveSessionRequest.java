package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class RetrieveSessionRequest extends AbstractRetrievalRequest {
    public RetrieveSessionRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity,
                                  final LiquidUUID target) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
    }

    public RetrieveSessionRequest(final LiquidSessionIdentifier identity, final LiquidUUID target) {
        this(null, identity, target);
    }


    public RetrieveSessionRequest() {
        super();
    }

    public RetrieveSessionRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new RetrieveSessionRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(getSessionIdentifier(), getTarget(), LiquidPermission.VIEW));
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_SESSION;
    }
}