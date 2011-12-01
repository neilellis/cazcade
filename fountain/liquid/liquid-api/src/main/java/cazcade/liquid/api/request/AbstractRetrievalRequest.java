package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidPermission;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractRetrievalRequest extends AbstractRequest {


    @Nullable
    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (super.getTarget() != null) {
            return Arrays.asList(new AuthorizationRequest(super.getTarget(), LiquidPermission.VIEW));
        } else {
            if (getUri() != null) {
                return Arrays.asList(new AuthorizationRequest(getUri(), LiquidPermission.VIEW));
            } else {
                return Collections.emptyList();
            }
        }
    }


    @Nonnull
    @Override
    public String getCacheIdentifier() {
        return getRequestType().name() + ":" + getState().name() + ":" + getDetail() + ":" + ((getUri() != null) ? getUri() : super.getTarget()) + ":" + (super.isHistorical() ? "historical" : "latest");
    }

    @Nullable
    public List<String> getNotificationLocations() {
        return null;
    }

    public boolean isMutationRequest() {
        return false;
    }


    @Override
    public boolean isCacheable() {
        return true;
    }


}
