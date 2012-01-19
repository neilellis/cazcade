package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class SelectPoolObjectRequest extends AbstractUpdateRequest {
    public SelectPoolObjectRequest(@Nullable final LiquidUUID id, final LiquidSessionIdentifier identity, final LiquidUUID target,
                                   final boolean selected) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
        setSelected(selected);
    }

    public SelectPoolObjectRequest(final LiquidSessionIdentifier identity, final LiquidUUID target, final boolean selected) {
        this(null, identity, target, selected);
    }

    public SelectPoolObjectRequest() {
        super();
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new SelectPoolObjectRequest(getId(), getSessionIdentifier(), getTarget(), isSelected());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(getTarget(), LiquidPermission.MODIFY));
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
