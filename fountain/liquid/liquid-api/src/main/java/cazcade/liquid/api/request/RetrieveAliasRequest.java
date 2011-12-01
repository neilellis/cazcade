package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class RetrieveAliasRequest extends AbstractRetrievalRequest {

    public RetrieveAliasRequest() {
        super();
    }

    public RetrieveAliasRequest(final LiquidUUID target) {
        this(null, null, target);
    }

    public RetrieveAliasRequest(final LiquidURI uri) {
        this(null, null, uri);
    }

    public RetrieveAliasRequest(final LiquidSessionIdentifier identity) {
        super();
        setSessionId(identity);
    }

    public RetrieveAliasRequest(final LiquidSessionIdentifier identity, final LiquidUUID target) {
        this(null, identity, target);
    }

    public RetrieveAliasRequest(final LiquidSessionIdentifier identity, final LiquidURI uri) {
        this(null, identity, uri);
    }

    public RetrieveAliasRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, final LiquidUUID target) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
    }

    public RetrieveAliasRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, final LiquidURI uri) {
        super();
        setId(id);
        setSessionId(identity);
        setUri(uri);
    }

    private RetrieveAliasRequest(final LiquidUUID id, final LiquidSessionIdentifier identity, final LiquidUUID target, final LiquidURI uri) {
        super();
        setTarget(target);
        setId(id);
        setSessionId(identity);
        setUri(uri);
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new RetrieveAliasRequest(getId(), getSessionIdentifier(), getTarget(), getUri());
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_ALIAS;
    }


    @Override
    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Collections.emptyList();
    }
}