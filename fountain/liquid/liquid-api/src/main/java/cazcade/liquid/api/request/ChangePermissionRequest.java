package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.Arrays;
import java.util.List;

public class ChangePermissionRequest extends AbstractRequest {


    public ChangePermissionRequest() {
    }

    public ChangePermissionRequest(LiquidURI objectURI, LiquidPermissionChangeType change) {
        this(null, null, objectURI, change);
    }

    public ChangePermissionRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidURI objectURI, LiquidPermissionChangeType change) {
        this.setPermission(change);
        this.setId(id);
        this.setIdentity(identity);
        this.setUri(objectURI);
    }


    @Override
    public LiquidMessage copy() {
        return new ChangePermissionRequest(getId(), getSessionIdentifier(), getUri(), this.getPermission());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(getUri(), LiquidPermission.SYSTEM));
    }

    public List<String> getNotificationLocations() {
        return Arrays.asList(getUri().getWithoutFragment().asReverseDNSString(), getUri().asReverseDNSString());
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CHANGE_PERMISSION;
    }

    public boolean isMutationRequest() {
        return true;
    }

}