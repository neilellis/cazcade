package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.Arrays;
import java.util.List;

public class SelectPoolObjectRequest extends AbstractUpdateRequest {
    private boolean selected;

    public SelectPoolObjectRequest() {
    }


    public SelectPoolObjectRequest(LiquidSessionIdentifier identity, LiquidUUID target, boolean selected) {
        this(null, identity, target, selected);
    }

    public SelectPoolObjectRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, boolean selected) {
        this.id = id;
        this.identity = identity;
        this.target = target;
        this.selected = selected;
    }




    @Override
    public LiquidMessage copy() {
        return new SelectPoolObjectRequest(id, identity, target, selected);
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(target, LiquidPermission.MODIFY));
    }

    public List<String> getNotificationLocations() {
        return null;
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.SELECT_POOL_OBJECT;
    }

    public boolean isSelected() {
        return selected;
    }


}
