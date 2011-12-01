package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RetrieveUserRequest extends AbstractRetrievalRequest {


    public RetrieveUserRequest() {
    }

    public RetrieveUserRequest(LiquidUUID target) {
        this(null, null, target);
    }

    public RetrieveUserRequest(LiquidURI uri) {
        this(null, null, uri, false);
    }

    public RetrieveUserRequest(LiquidSessionIdentifier identity, LiquidURI uri) {
        this(null, identity, uri, false);
    }

    public RetrieveUserRequest(LiquidSessionIdentifier identity, LiquidUUID target) {
        this(null, identity, target);
    }

    public RetrieveUserRequest(LiquidSessionIdentifier identity, LiquidURI uri, boolean internal) {
        this(null, identity, uri, internal);
    }

    public RetrieveUserRequest(@Nullable LiquidUUID id, @Nullable LiquidSessionIdentifier identity, LiquidUUID target) {
        this.setId(id);
        this.setSessionId(identity);
        this.setTarget(target);
    }

    public RetrieveUserRequest(@Nullable LiquidUUID id, @Nullable LiquidSessionIdentifier identity, LiquidURI uri, boolean internal) {
        this.setId(id);
        this.setSessionId(identity);
        this.setUri(uri);
        this.setInternal(internal);
    }

    private RetrieveUserRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, LiquidURI uri) {
        this.setTarget(target);
        this.setId(id);
        this.setSessionId(identity);
        this.setUri(uri);
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new RetrieveUserRequest(getId(), getSessionIdentifier(), super.getTarget(), getUri());
    }


    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_USER;
    }
}
