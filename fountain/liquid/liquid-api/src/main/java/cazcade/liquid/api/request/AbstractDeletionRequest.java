package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidPermission;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.LSDEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractDeletionRequest extends AbstractRequest {

    protected LiquidUUID target;
    protected LiquidURI uri;


    public LiquidUUID getTarget() {
        return target;
    }

    public LiquidURI getUri() {
        return uri;
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (target != null) {
            return Arrays.asList(new AuthorizationRequest(target, LiquidPermission.DELETE));
        } else {
            if (uri != null) {
                return Arrays.asList(new AuthorizationRequest(uri, LiquidPermission.DELETE));
            } else {
                return Collections.emptyList();
            }
        }
    }


    public LSDEntity getEntity() {
        return null;
    }

    public List<String> getNotificationLocations() {
        return null;
    }

    public boolean isMutationRequest() {
        return true;
    }
}
