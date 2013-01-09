package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CreateUserRequest extends AbstractCreationRequest {
    public CreateUserRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity,
                             final LSDTransferEntity entity) {
        super();
        setId(id);
        setSessionId(identity);
        setRequestEntity(entity);
    }

    public CreateUserRequest(final LiquidSessionIdentifier identity, final LSDTransferEntity entity) {
        this(null, identity, entity);
    }

    public CreateUserRequest() {
        super();
    }

    public CreateUserRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new CreateUserRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Collections.EMPTY_LIST;
    }

    @Nullable
    @Override
    public String getNotificationSession() {
        //Don't notify anyone of a user creation request.
        return null;
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CREATE_USER;
    }

    @Override
    public boolean isAsyncRequest() {
        return false;
    }
}
