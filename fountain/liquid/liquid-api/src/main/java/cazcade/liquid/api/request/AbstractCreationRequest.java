/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractCreationRequest extends AbstractRequest {
    public AbstractCreationRequest(final TransferEntity entity) {
        super(entity);
    }


    protected AbstractCreationRequest() {
    }

    @Override
    public void adjustTimeStampForServerTime() {
        super.adjustTimeStampForServerTime();
        if (hasRequestEntity()) {
            getEntity().$(Dictionary.REQUEST_ENTITY, Dictionary.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        }
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        return Collections.EMPTY_LIST;
    }

    @Nullable
    public List<String> notificationLocations() {
        return null;
    }

    @Override
    public boolean isAsyncRequest() {
        return true;
    }

    public boolean isMutationRequest() {
        return true;
    }
}
