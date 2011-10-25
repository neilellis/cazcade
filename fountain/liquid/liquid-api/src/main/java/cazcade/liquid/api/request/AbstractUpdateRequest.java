package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidPermission;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.LSDEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractUpdateRequest extends AbstractRequest {
    protected LiquidUUID target;


    public LiquidUUID getTarget() {
        return target;
    }

    public LSDEntity getEntity() {
        return entity;
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (uri != null) {
            return Arrays.asList(new AuthorizationRequest(uri, LiquidPermission.EDIT));
        }

        if (target != null) {
            return Arrays.asList(new AuthorizationRequest(target, LiquidPermission.EDIT));
        }

        return new ArrayList<AuthorizationRequest>();
    }

    public List<String> getNotificationLocations() {
        if (uri != null) {
            return Arrays.asList(uri.asReverseDNSString());
        }

        if (target != null) {
            return Arrays.asList(target.toString());
        }

        return new ArrayList<String>();
    }

    public boolean isMutationRequest() {
        return true;
    }


}
