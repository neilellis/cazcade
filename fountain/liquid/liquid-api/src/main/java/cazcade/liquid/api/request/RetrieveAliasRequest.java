package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class RetrieveAliasRequest extends AbstractRetrievalRequest {

    public RetrieveAliasRequest() {
    }

    public RetrieveAliasRequest(LiquidUUID target) {
        this(null, null, target);
    }

    public RetrieveAliasRequest(LiquidURI uri) {
        this(null, null, uri);
    }

    public RetrieveAliasRequest(LiquidSessionIdentifier identity) {
        this.setSessionId(identity);
    }

    public RetrieveAliasRequest(LiquidSessionIdentifier identity, LiquidUUID target) {
        this(null, identity, target);
    }

    public RetrieveAliasRequest(LiquidSessionIdentifier identity, LiquidURI uri) {
        this(null, identity, uri);
    }

    public RetrieveAliasRequest(@Nullable LiquidUUID id, @Nullable LiquidSessionIdentifier identity, LiquidUUID target) {
        this.setId(id);
        this.setSessionId(identity);
        this.setTarget(target);
    }

    public RetrieveAliasRequest(@Nullable LiquidUUID id, @Nullable LiquidSessionIdentifier identity, LiquidURI uri) {
        this.setId(id);
        this.setSessionId(identity);
        this.setUri(uri);
    }

    private RetrieveAliasRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, LiquidURI uri) {
        this.setTarget(target);
        this.setId(id);
        this.setSessionId(identity);
        this.setUri(uri);
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new RetrieveAliasRequest(getId(), getSessionIdentifier(), super.getTarget(), getUri());
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