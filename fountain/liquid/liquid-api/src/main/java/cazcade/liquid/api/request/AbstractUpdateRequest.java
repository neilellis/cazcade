package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidPermission;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractUpdateRequest extends AbstractRequest {
    public AbstractUpdateRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    public AbstractUpdateRequest() {
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (hasUri()) {
            return Arrays.asList(new AuthorizationRequest(getSessionIdentifier(), getUri(), LiquidPermission.EDIT));
        }

        if (hasTarget()) {
            return Arrays.asList(new AuthorizationRequest(getSessionIdentifier(), getTarget(), LiquidPermission.EDIT));
        }

        return new ArrayList<AuthorizationRequest>();
    }

    @Nullable
    public List<String> getNotificationLocations() {
        if (hasUri()) {
            return Arrays.asList(getUri().asReverseDNSString());
        }

        if (hasTarget()) {
            return Arrays.asList(getTarget().toString());
        }

        return new ArrayList<String>();
    }

    public boolean isMutationRequest() {
        return true;
    }
}
