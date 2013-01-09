package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidPermission;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractDeletionRequest extends AbstractRequest {
    public AbstractDeletionRequest(final LSDTransferEntity entity) {
        super(entity);
    }


    protected AbstractDeletionRequest() {
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (hasTarget()) {
            return Arrays.asList(new AuthorizationRequest(getSessionIdentifier(), getTarget(), LiquidPermission.DELETE));
        }
        else {
            if (hasUri()) {
                return Arrays.asList(new AuthorizationRequest(getSessionIdentifier(), getUri(), LiquidPermission.DELETE));
            }
            else {
                return Collections.emptyList();
            }
        }
    }


    @Nullable
    public List<String> getNotificationLocations() {
        return null;
    }

    public boolean isMutationRequest() {
        return true;
    }
}
