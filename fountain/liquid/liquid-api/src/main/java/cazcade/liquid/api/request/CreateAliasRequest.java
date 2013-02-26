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

public class CreateAliasRequest extends AbstractCreationRequest {
    public CreateAliasRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final TransferEntity entity, final boolean me, final boolean orupdate, final boolean claim) {
        super();
        setClaim(claim);
        id(id);
        setRequestEntity(entity);
        setMe(me);
        setOrCreate(orupdate);
        session(identity);
    }

    public CreateAliasRequest(final SessionIdentifier identity, final TransferEntity alias, final boolean me, final boolean orupdate, final boolean claim) {
        this(null, identity, alias, me, orupdate, claim);
    }

    public CreateAliasRequest(final TransferEntity alias, final boolean me, final boolean orupdate, final boolean claim) {
        this(null, SessionIdentifier.ANON, alias, me, orupdate, claim);
    }

    public CreateAliasRequest() {
        super();
    }

    public CreateAliasRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new CreateAliasRequest(getEntity());
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.CREATE_ALIAS;
    }

    @Override
    public boolean isAsyncRequest() {
        return false;
    }
}