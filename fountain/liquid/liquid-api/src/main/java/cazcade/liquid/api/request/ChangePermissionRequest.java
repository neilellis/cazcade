package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class ChangePermissionRequest extends AbstractRequest {


    public ChangePermissionRequest() {
        super();
    }

    public ChangePermissionRequest(final LiquidURI objectURI, final LiquidPermissionChangeType change) {
        this(null, null, objectURI, change);
    }

    public ChangePermissionRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, final LiquidURI objectURI, final LiquidPermissionChangeType change) {
        super();
        setPermission(change);
        setId(id);
        setSessionId(identity);
        setUri(objectURI);
    }


    @Nullable
    @Override
    public LiquidMessage copy() {
        return new ChangePermissionRequest(getId(), getSessionIdentifier(), getUri(), getPermission());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(getUri(), LiquidPermission.SYSTEM));
    }

    public List<String> getNotificationLocations() {
        return Arrays.asList(getUri().getWithoutFragment().asReverseDNSString(), getUri().asReverseDNSString());
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CHANGE_PERMISSION;
    }

    public boolean isMutationRequest() {
        return true;
    }

}