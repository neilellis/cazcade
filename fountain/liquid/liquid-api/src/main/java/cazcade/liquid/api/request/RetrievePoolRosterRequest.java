package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class RetrievePoolRosterRequest extends AbstractRetrievalRequest {

    public RetrievePoolRosterRequest() {
        super();
    }

    public RetrievePoolRosterRequest(final LiquidUUID target) {
        this(null, null, target);
    }

    public RetrievePoolRosterRequest(final LiquidURI uri) {
        this(null, null, uri);
    }

    public RetrievePoolRosterRequest(final LiquidSessionIdentifier identity, final LiquidUUID target) {
        this(null, identity, target);
    }

    public RetrievePoolRosterRequest(final LiquidSessionIdentifier identity, final LiquidURI uri) {
        this(null, identity, uri);
    }

    public RetrievePoolRosterRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, final LiquidUUID target) {
        super();
        setTarget(target);
        setId(id);
        setSessionId(identity);
    }

    public RetrievePoolRosterRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, final LiquidURI uri) {
        super();
        setId(id);
        setSessionId(identity);
        setUri(uri);
    }

    private RetrievePoolRosterRequest(final LiquidUUID id, final LiquidSessionIdentifier identity, final LiquidUUID target, final LiquidURI uri) {
        super();
        setTarget(target);
        setId(id);
        setSessionId(identity);
        setUri(uri);
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new RetrievePoolRosterRequest(getId(), getSessionIdentifier(), getTarget(), getUri());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (getTarget() != null) {
            return Arrays.asList(new AuthorizationRequest(getTarget(), LiquidPermission.VIEW));
        } else {
            return Arrays.asList(new AuthorizationRequest(getUri(), LiquidPermission.VIEW));
        }
    }


    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_POOL_ROSTER;
    }


}
