package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SendRequest extends AbstractRequest {
    public SendRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity,
                       final LSDTransferEntity entity, final String recipient) {
        super();
        setId(id);
        setSessionId(identity);
        setRecipient(recipient);
        setRequestEntity(entity);
    }

    public SendRequest(final LiquidSessionIdentifier identity, final LSDTransferEntity entity, final String recipient) {
        this(null, identity, entity, recipient);
    }

    public SendRequest(final LSDTransferEntity entity, final String recipient) {
        this(null, LiquidSessionIdentifier.ANON, entity, recipient);
    }

    public SendRequest() {
        super();
    }

    public SendRequest(final LSDTransferEntity entity) {
        getEntity();
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new SendRequest(getEntity());
    }

    public Collection<LiquidURI> getAffectedEntities() {
        return Arrays.asList(getRecipientAlias());
    }

    @Nonnull
    public LiquidURI getRecipientAlias() {
        return new LiquidURI("alias:cazcade:" + getRecipient());
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Collections.emptyList();
    }

    @Nonnull
    public LiquidURI getInboxURI() {
        return new LiquidURI("pool:///people/" + getRecipient() + "/.inbox");
    }

    @Override
    public List<String> getNotificationLocations() {
        return Arrays.asList(new LiquidURI("alias:cazcade:" + getRecipient()).asReverseDNSString());
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.SEND;
    }

    public boolean isMutationRequest() {
        return true;
    }
}
