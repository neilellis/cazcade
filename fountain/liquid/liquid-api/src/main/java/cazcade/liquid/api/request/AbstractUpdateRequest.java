package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidPermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractUpdateRequest extends AbstractRequest {


    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (getUri() != null) {
            return Arrays.asList(new AuthorizationRequest(getUri(), LiquidPermission.EDIT));
        }

        if (super.getTarget() != null) {
            return Arrays.asList(new AuthorizationRequest(super.getTarget(), LiquidPermission.EDIT));
        }

        return new ArrayList<AuthorizationRequest>();
    }

    public List<String> getNotificationLocations() {
        if (getUri() != null) {
            return Arrays.asList(getUri().asReverseDNSString());
        }

        if (super.getTarget() != null) {
            return Arrays.asList(super.getTarget().toString());
        }

        return new ArrayList<String>();
    }

    public boolean isMutationRequest() {
        return true;
    }


}
