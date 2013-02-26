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
public abstract class AbstractRetrievalRequest extends AbstractRequest {
    public AbstractRetrievalRequest(final TransferEntity entity) {
        super(entity);
    }


    public AbstractRetrievalRequest() {
        super();
    }

    @Nonnull @Override
    public List<AuthorizationRequest> authorizationRequests() {
        if (hasTarget()) {
            return Arrays.asList(new AuthorizationRequest(session(), getTarget(), Permission.VIEW_PERM));
        } else {
            if (hasUri()) {
                return Arrays.asList(new AuthorizationRequest(session(), uri(), Permission.VIEW_PERM));
            } else {
                return Collections.emptyList();
            }
        }
    }

    @Nonnull @Override
    public String cacheIdentifier() {
        return requestType().name() +
               ":" +
               getState().name() +
               ":" +
               detail() +
               ":" +
               (hasUri() ? uri() : getTarget()) +
               ":" +
               (historical() ? "historical" : "latest");
    }

    @Nullable
    public List<String> notificationLocations() {
        return null;
    }

    @Override
    public boolean isCacheable() {
        return true;
    }

    public boolean isMutationRequest() {
        return false;
    }
}
