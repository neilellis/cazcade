package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.LSDEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    public CreateUserRequest(@Nullable LiquidUUID id, @Nullable LiquidSessionIdentifier identity, LSDEntity entity) {
        this.setId(id);
        this.setSessionId(identity);
        this.setRequestEntity(entity);
    }


    @Nullable
    @Override
    public LiquidMessage copy() {
        return new CreateUserRequest(getId(), getSessionIdentifier(), super.getRequestEntity());
    }

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
