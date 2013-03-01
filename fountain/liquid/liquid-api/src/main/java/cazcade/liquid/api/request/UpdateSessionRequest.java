/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.RequestType;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class UpdateSessionRequest extends AbstractUpdateRequest {
    public UpdateSessionRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final LiquidUUID target, final TransferEntity entity, final boolean internal) {
        super();
        id(id);
        session(identity);
        setTarget(target);
        setRequestEntity(entity);
        setInternal(internal);
    }

    public UpdateSessionRequest(final SessionIdentifier identity, final LiquidUUID target, final TransferEntity entity, final boolean internal) {
        this(null, identity, target, entity, internal);
    }


    public UpdateSessionRequest() {
        super();
    }

    public UpdateSessionRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new UpdateSessionRequest(getEntity());
    }

    @Nullable @Override
    public List<String> notificationLocations() {
        return null;
    }

    @Nullable @Override
    public String notificationSession() {
        //Don't notify anyone of a session update.
        return null;
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.R_UPDATE_SESSION;
    }
}