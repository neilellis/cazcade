package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class SelectPoolObjectRequest extends AbstractUpdateRequest {

    public SelectPoolObjectRequest() {
    }


    public SelectPoolObjectRequest(LiquidSessionIdentifier identity, LiquidUUID target, boolean selected) {
        this(null, identity, target, selected);
    }

    public SelectPoolObjectRequest(@Nullable LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, boolean selected) {
        this.setId(id);
        this.setSessionId(identity);
        this.setTarget(target);
        this.setSelected(selected);
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new SelectPoolObjectRequest(getId(), getSessionIdentifier(), super.getTarget(), isSelected());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(super.getTarget(), LiquidPermission.MODIFY));
    }

    @Nullable
    public List<String> getNotificationLocations() {
        return null;
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.SELECT_POOL_OBJECT;
    }


}
