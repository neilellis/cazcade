/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

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
public abstract class AbstractRetrievalRequest extends AbstractRequest {
    public AbstractRetrievalRequest(final LSDTransferEntity entity) {
        super(entity);
    }


    public AbstractRetrievalRequest() {
        super();
    }

    @Nonnull @Override
    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (hasTarget()) {
            return Arrays.asList(new AuthorizationRequest(getSessionIdentifier(), getTarget(), LiquidPermission.VIEW));
        }
        else {
            if (hasUri()) {
                return Arrays.asList(new AuthorizationRequest(getSessionIdentifier(), getUri(), LiquidPermission.VIEW));
            }
            else {
                return Collections.emptyList();
            }
        }
    }

    @Nonnull @Override
    public String getCacheIdentifier() {
        return getRequestType().name() +
               ":" +
               getState().name() +
               ":" +
               getDetail() +
               ":" +
               (hasUri() ? getUri() : getTarget()) +
               ":" +
               (isHistorical() ? "historical" : "latest");
    }

    @Nullable
    public List<String> getNotificationLocations() {
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
