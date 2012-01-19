package cazcade.liquid.api.request;

import cazcade.liquid.api.lsd.LSDAttribute;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractCreationRequest extends AbstractRequest {
    @Override
    public void adjustTimeStampForServerTime() {
        super.adjustTimeStampForServerTime();
        if (getRequestEntity() != null) {
            getEntity().setAttribute(LSDAttribute.REQUEST_ENTITY, LSDAttribute.PUBLISHED, String.valueOf(System.currentTimeMillis()
                                                                                                        )
                                    );
        }
    }

    @Nullable
    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Collections.EMPTY_LIST;
    }

    @Nullable
    public List<String> getNotificationLocations() {
        return null;
    }

    @Override
    public boolean isAsyncRequest() {
        return true;
    }

    public boolean isMutationRequest() {
        return true;
    }
}
