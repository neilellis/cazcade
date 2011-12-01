package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class RetrievePoolRosterRequest extends AbstractRetrievalRequest {

    public RetrievePoolRosterRequest() {
    }

    public RetrievePoolRosterRequest(LiquidUUID target) {
        this(null, null, target);
    }

    public RetrievePoolRosterRequest(LiquidURI uri) {
        this(null, null, uri);
    }

    public RetrievePoolRosterRequest(LiquidSessionIdentifier identity, LiquidUUID target) {
        this(null, identity, target);
    }

    public RetrievePoolRosterRequest(LiquidSessionIdentifier identity, LiquidURI uri) {
        this(null, identity, uri);
    }

    public RetrievePoolRosterRequest(@Nullable LiquidUUID id, @Nullable LiquidSessionIdentifier identity, LiquidUUID target) {
        this.setTarget(target);
        this.setId(id);
        this.setSessionId(identity);
    }

    public RetrievePoolRosterRequest(@Nullable LiquidUUID id, @Nullable LiquidSessionIdentifier identity, LiquidURI uri) {
        this.setId(id);
        this.setSessionId(identity);
        this.setUri(uri);
    }

    private RetrievePoolRosterRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, LiquidURI uri) {
        this.setTarget(target);
        this.setId(id);
        this.setSessionId(identity);
        this.setUri(uri);
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new RetrievePoolRosterRequest(getId(), getSessionIdentifier(), super.getTarget(), getUri());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (super.getTarget() != null) {
            return Arrays.asList(new AuthorizationRequest(super.getTarget(), LiquidPermission.VIEW));
        } else {
            return Arrays.asList(new AuthorizationRequest(getUri(), LiquidPermission.VIEW));
        }
    }


    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_POOL_ROSTER;
    }


}
