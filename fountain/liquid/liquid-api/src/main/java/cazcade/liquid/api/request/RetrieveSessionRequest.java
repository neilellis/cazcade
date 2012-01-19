package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class RetrieveSessionRequest extends AbstractRetrievalRequest {
    public RetrieveSessionRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity,
                                  final LiquidUUID target) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
    }

    public RetrieveSessionRequest(final LiquidSessionIdentifier identity, final LiquidUUID target) {
        this(null, identity, target);
    }

    public RetrieveSessionRequest(final LiquidUUID target) {
        this(null, null, target);
    }

    public RetrieveSessionRequest() {
        super();
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new RetrieveSessionRequest(getId(), getSessionIdentifier(), getTarget());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(getTarget(), LiquidPermission.VIEW));
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_SESSION;
    }
}