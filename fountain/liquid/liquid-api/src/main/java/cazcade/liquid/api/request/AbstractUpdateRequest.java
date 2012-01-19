package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidPermission;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractUpdateRequest extends AbstractRequest {
    @Nullable
    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (getUri() != null) {
            return Arrays.asList(new AuthorizationRequest(getUri(), LiquidPermission.EDIT));
        }

        if (getTarget() != null) {
            return Arrays.asList(new AuthorizationRequest(getTarget(), LiquidPermission.EDIT));
        }

        return new ArrayList<AuthorizationRequest>();
    }

    @Nullable
    public List<String> getNotificationLocations() {
        if (getUri() != null) {
            return Arrays.asList(getUri().asReverseDNSString());
        }

        if (getTarget() != null) {
            return Arrays.asList(getTarget().toString());
        }

        return new ArrayList<String>();
    }

    public boolean isMutationRequest() {
        return true;
    }
}
