package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class UpdateUserRequest extends AbstractUpdateRequest {

    public UpdateUserRequest() {
    }

    @Override
    public boolean isSecureOperation() {
        return true;
    }

    public UpdateUserRequest(LiquidUUID target, LSDEntity entity) {
        this(null, null, target, entity);
    }

    public UpdateUserRequest(LiquidSessionIdentifier identity, LiquidUUID target, LSDEntity entity) {
        this(null, identity, target, entity);
    }

    public UpdateUserRequest(@Nullable LiquidUUID id, @Nullable LiquidSessionIdentifier identity, LiquidUUID target, LSDEntity entity) {
        this.setId(id);
        this.setSessionId(identity);
        this.setTarget(target);
        this.setRequestEntity(entity);
    }


    @Nullable
    @Override
    public LiquidMessage copy() {
        return new UpdateUserRequest(getId(), getSessionIdentifier(), super.getTarget(), super.getRequestEntity());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(super.getTarget(), LiquidPermission.EDIT));
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.UPDATE_USER;
    }

    public boolean isMutationRequest() {
        return true;
    }
}
