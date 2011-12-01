package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DeletePoolRequest extends AbstractDeletionRequest {

    public DeletePoolRequest() {
        super();
    }

    public DeletePoolRequest(final LiquidUUID target) {
        this(null, null, target);
    }

    public DeletePoolRequest(final LiquidSessionIdentifier identity, final LiquidUUID target) {
        this(null, identity, target);
    }

    public DeletePoolRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, final LiquidUUID target) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
    }

    public DeletePoolRequest(final LiquidSessionIdentifier identity, final LiquidURI poolURI) {
        super();
        setSessionId(identity);
        setUri(poolURI);
    }

    public DeletePoolRequest(final LiquidUUID id, final LiquidSessionIdentifier identity, final LiquidUUID target, final LiquidURI uri) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
        setUri(uri);
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new DeletePoolRequest(getId(), getSessionIdentifier(), getTarget(), getUri());
    }


    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.DELETE_POOL;
    }

    @Override
    public boolean isSecureOperation() {
        return true;
    }
}
