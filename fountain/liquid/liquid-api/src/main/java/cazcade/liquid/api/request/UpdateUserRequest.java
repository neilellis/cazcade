package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class UpdateUserRequest extends AbstractUpdateRequest {

    public UpdateUserRequest() {
        super();
    }

    @Override
    public boolean isSecureOperation() {
        return true;
    }

    public UpdateUserRequest(final LiquidUUID target, final LSDTransferEntity entity) {
        this(null, null, target, entity);
    }

    public UpdateUserRequest(final LiquidSessionIdentifier identity, final LiquidUUID target, final LSDTransferEntity entity) {
        this(null, identity, target, entity);
    }

    public UpdateUserRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, final LiquidUUID target, final LSDTransferEntity entity) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
        setRequestEntity(entity);
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new UpdateUserRequest(getId(), getSessionIdentifier(), getTarget(), getRequestEntity());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(getTarget(), LiquidPermission.EDIT));
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.UPDATE_USER;
    }

    public boolean isMutationRequest() {
        return true;
    }
}
