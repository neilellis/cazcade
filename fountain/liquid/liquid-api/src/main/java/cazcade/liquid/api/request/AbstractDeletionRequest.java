package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidPermission;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractDeletionRequest extends AbstractRequest {


    @Nullable
    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (getTarget() != null) {
            return Arrays.asList(new AuthorizationRequest(getTarget(), LiquidPermission.DELETE));
        } else {
            if (getUri() != null) {
                return Arrays.asList(new AuthorizationRequest(getUri(), LiquidPermission.DELETE));
            } else {
                return Collections.emptyList();
            }
        }
    }


    @Nullable
    public List<String> getNotificationLocations() {
        return null;
    }

    public boolean isMutationRequest() {
        return true;
    }
}
