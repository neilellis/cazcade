package cazcade.liquid.api.request;

import cazcade.liquid.api.lsd.LSDAttribute;

import java.util.Collections;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractCreationRequest extends AbstractRequest {


    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Collections.EMPTY_LIST;
    }

    public List<String> getNotificationLocations() {
        return null;
    }

    public boolean isMutationRequest() {
        return true;
    }

    @Override
    public boolean isAsyncRequest() {
        return true;
    }

    @Override
    public void adjustTimeStampForServerTime() {
        super.adjustTimeStampForServerTime();
        if (getEntity() != null) {
            getEntity().setAttribute(LSDAttribute.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        }
    }
}
