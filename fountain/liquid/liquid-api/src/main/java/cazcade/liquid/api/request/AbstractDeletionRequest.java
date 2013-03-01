/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.Permission;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractDeletionRequest extends AbstractRequest {
    public AbstractDeletionRequest(@Nonnull final TransferEntity entity) {
        super(entity);
    }


    protected AbstractDeletionRequest() {
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        if (hasTarget()) {
            return Arrays.asList(new AuthorizationRequest(session(), getTarget(), Permission.P_DELETE));
        } else {
            if (hasUri()) {
                return Arrays.asList(new AuthorizationRequest(session(), uri(), Permission.P_DELETE));
            } else {
                return Collections.emptyList();
            }
        }
    }


    @Nullable
    public List<String> notificationLocations() {
        return null;
    }

    public boolean isMutationRequest() {
        return true;
    }
}
