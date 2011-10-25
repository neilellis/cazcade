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
public abstract class AbstractRetrievalRequest extends AbstractRequest {

    ;

    protected LiquidUUID target;
    protected boolean historical;


    public LiquidUUID getTarget() {
        return target;
    }


    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (target != null) {
            return Arrays.asList(new AuthorizationRequest(target, LiquidPermission.VIEW));
        } else {
            if (uri != null) {
                return Arrays.asList(new AuthorizationRequest(uri, LiquidPermission.VIEW));
            } else {
                return Collections.emptyList();
            }
        }
    }


    @Override
    public String getCacheIdentifier() {
        return getRequestType().name() + ":" + getState().name() + ":" + getDetail() + ":" + ((uri != null) ? uri : target) + ":" + (historical ? "historical" : "latest");
    }

    public List<String> getNotificationLocations() {
        return null;
    }

    public boolean isMutationRequest() {
        return false;
    }

    public boolean isHistorical() {
        return historical;
    }

    public boolean getHistorical() {
        return historical;
    }

    @Override
    public boolean isCacheable() {
        return true;
    }



}
