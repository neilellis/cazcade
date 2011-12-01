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
        super();
    }

    public CreateUserRequest(final LSDEntity entity) {
        this(null, null, entity);
    }

    public CreateUserRequest(final LiquidSessionIdentifier identity, final LSDEntity entity) {
        this(null, identity, entity);
    }

    public CreateUserRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, final LSDEntity entity) {
        super();
        setId(id);
        setSessionId(identity);
        setRequestEntity(entity);
    }


    @Nullable
    @Override
    public LiquidMessage copy() {
        return new CreateUserRequest(getId(), getSessionIdentifier(), getRequestEntity());
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
