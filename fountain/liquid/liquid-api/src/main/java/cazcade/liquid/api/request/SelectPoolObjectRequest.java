package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.Arrays;
import java.util.List;

public class SelectPoolObjectRequest extends AbstractUpdateRequest {

    public SelectPoolObjectRequest() {
    }


    public SelectPoolObjectRequest(LiquidSessionIdentifier identity, LiquidUUID target, boolean selected) {
        this(null, identity, target, selected);
    }

    public SelectPoolObjectRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, boolean selected) {
        this.setId(id);
        this.setSessionId(identity);
        this.setTarget(target);
        this.setSelected(selected);
    }


    @Override
    public LiquidMessage copy() {
        return new SelectPoolObjectRequest(getId(), getSessionIdentifier(), super.getTarget(), isSelected());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(super.getTarget(), LiquidPermission.MODIFY));
    }

    public List<String> getNotificationLocations() {
        return null;
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.SELECT_POOL_OBJECT;
    }


}
