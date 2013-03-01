/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SendRequest extends AbstractRequest {
    public SendRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final TransferEntity entity, final String recipient) {
        super();
        id(id);
        session(identity);
        setRecipient(recipient);
        setRequestEntity(entity);
    }

    public SendRequest(final SessionIdentifier identity, final TransferEntity entity, final String recipient) {
        this(null, identity, entity, recipient);
    }

    public SendRequest(final TransferEntity entity, final String recipient) {
        this(null, SessionIdentifier.ANON, entity, recipient);
    }

    public SendRequest() {
        super();
    }

    public SendRequest(final TransferEntity entity) {
        getEntity();
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new SendRequest(getEntity());
    }

    public Collection<LURI> affectedEntities() {
        return Arrays.asList(getRecipientAlias());
    }

    @Nonnull
    public LURI getRecipientAlias() {
        return new LURI("alias:cazcade:" + getRecipient());
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        return Collections.emptyList();
    }

    @Nonnull
    public LURI getInboxURI() {
        return new LURI("pool:///people/" + getRecipient() + "/.inbox");
    }

    @Override
    public List<String> notificationLocations() {
        return Arrays.asList(new LURI("alias:cazcade:" + getRecipient()).asReverseDNSString());
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.R_SEND;
    }

    public boolean isMutationRequest() {
        return true;
    }
}
