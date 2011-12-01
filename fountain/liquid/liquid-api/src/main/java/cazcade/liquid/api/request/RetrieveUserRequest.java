package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RetrieveUserRequest extends AbstractRetrievalRequest {


    public RetrieveUserRequest() {
        super();
    }

    public RetrieveUserRequest(final LiquidUUID target) {
        this(null, null, target);
    }

    public RetrieveUserRequest(final LiquidURI uri) {
        this(null, null, uri, false);
    }

    public RetrieveUserRequest(final LiquidSessionIdentifier identity, final LiquidURI uri) {
        this(null, identity, uri, false);
    }

    public RetrieveUserRequest(final LiquidSessionIdentifier identity, final LiquidUUID target) {
        this(null, identity, target);
    }

    public RetrieveUserRequest(final LiquidSessionIdentifier identity, final LiquidURI uri, final boolean internal) {
        this(null, identity, uri, internal);
    }

    public RetrieveUserRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, final LiquidUUID target) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
    }

    public RetrieveUserRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, final LiquidURI uri, final boolean internal) {
        super();
        setId(id);
        setSessionId(identity);
        setUri(uri);
        setInternal(internal);
    }

    private RetrieveUserRequest(final LiquidUUID id, final LiquidSessionIdentifier identity, final LiquidUUID target, final LiquidURI uri) {
        super();
        setTarget(target);
        setId(id);
        setSessionId(identity);
        setUri(uri);
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new RetrieveUserRequest(getId(), getSessionIdentifier(), getTarget(), getUri());
    }


    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_USER;
    }
}
