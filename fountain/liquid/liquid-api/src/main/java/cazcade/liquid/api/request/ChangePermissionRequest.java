package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDEntity;

import java.util.Arrays;
import java.util.List;

public class ChangePermissionRequest extends AbstractRequest {

    private LiquidPermissionChangeType change;

    public ChangePermissionRequest() {
    }

    public ChangePermissionRequest(LiquidURI objectURI, LiquidPermissionChangeType change) {
        this(null, null, objectURI, change);
    }

    public ChangePermissionRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidURI objectURI, LiquidPermissionChangeType change) {
        this.change = change;
        this.id = id;
        this.identity = identity;
        this.uri = objectURI;
    }




    @Override
    public LiquidMessage copy() {
        return new ChangePermissionRequest(id, identity, uri, change);
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(uri, LiquidPermission.SYSTEM));
    }

    public List<String> getNotificationLocations() {
        return Arrays.asList(uri.getWithoutFragment().asReverseDNSString(), uri.asReverseDNSString());
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CHANGE_PERMISSION;
    }

    public boolean isMutationRequest() {
        return true;
    }

    public LiquidPermissionChangeType getChange() {
        return change;
    }
}