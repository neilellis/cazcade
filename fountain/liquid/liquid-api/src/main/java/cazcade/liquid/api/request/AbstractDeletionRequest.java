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
        if (super.getTarget() != null) {
            return Arrays.asList(new AuthorizationRequest(super.getTarget(), LiquidPermission.DELETE));
        } else {
            if (super.getUri() != null) {
                return Arrays.asList(new AuthorizationRequest(super.getUri(), LiquidPermission.DELETE));
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
