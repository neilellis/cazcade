/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.Permission;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractUpdateRequest extends AbstractRequest {
    public AbstractUpdateRequest(final TransferEntity entity) {
        super(entity);
    }

    public AbstractUpdateRequest() {
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        if (hasUri()) {
            return Arrays.asList(new AuthorizationRequest(session(), uri(), Permission.EDIT_PERM));
        }

        if (hasTarget()) {
            return Arrays.asList(new AuthorizationRequest(session(), getTarget(), Permission.EDIT_PERM));
        }

        return new ArrayList<AuthorizationRequest>();
    }

    @Nullable
    public List<String> notificationLocations() {
        if (hasUri()) {
            return Arrays.asList(uri().asReverseDNSString());
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
