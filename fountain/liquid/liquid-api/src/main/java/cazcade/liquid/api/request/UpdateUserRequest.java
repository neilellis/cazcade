package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDEntity;

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

    public UpdateUserRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, LSDEntity entity) {
        this.setId(id);
        this.setIdentity(identity);
        this.setTarget(target);
        this.setRequestEntity(entity);
    }


    @Override
    public LiquidMessage copy() {
        return new UpdateUserRequest(getId(), getSessionIdentifier(), super.getTarget(), super.getEntity());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(super.getTarget(), LiquidPermission.EDIT));
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.UPDATE_USER;
    }

    public boolean isMutationRequest() {
        return true;
    }
}
