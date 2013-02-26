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

public class UnlinkAliasRequest extends AbstractDeletionRequest {

    public UnlinkAliasRequest(final SessionIdentifier identity, final LiquidUUID target) {
        this(null, identity, target);
    }

    public UnlinkAliasRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final LiquidUUID target) {
        super();
        id(id);
        session(identity);
        setTarget(target);
    }

    public UnlinkAliasRequest(final LiquidUUID target) {
        this(null, SessionIdentifier.ANON, target);
    }

    public UnlinkAliasRequest() {
        super();
    }

    public UnlinkAliasRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new UnlinkAliasRequest(getEntity());
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.DELETE_ALIAS;
    }
}