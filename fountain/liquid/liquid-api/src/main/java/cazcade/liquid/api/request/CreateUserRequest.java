package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.LSDEntity;

import java.util.Collections;
import java.util.List;

public class CreateUserRequest extends AbstractCreationRequest {

    public CreateUserRequest() {
    }

    public CreateUserRequest(LSDEntity entity) {
        this(null, null, entity);
    }

    public CreateUserRequest(LiquidSessionIdentifier identity, LSDEntity entity) {
        this(null, identity, entity);
    }

    public CreateUserRequest(LiquidUUID id, LiquidSessionIdentifier identity, LSDEntity entity) {
        this.setId(id);
        this.setIdentity(identity);
        this.setRequestEntity(entity);
    }


    @Override
    public LiquidMessage copy() {
        return new CreateUserRequest(getId(), getSessionIdentifier(), super.getRequestEntity());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Collections.EMPTY_LIST;
    }


    @Override
    public String getNotificationSession() {
        //Don't notify anyone of a user creation request.
        return null;
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CREATE_USER;
    }

    @Override
    public boolean isAsyncRequest() {
        return false;
    }

}
