package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SendRequest extends AbstractRequest {


    public SendRequest() {
        super();
    }

    public SendRequest(final LiquidSessionIdentifier identity, final LSDEntity entity, final String recipient) {
        this(null, identity, entity, recipient);
    }

    public SendRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, final LSDEntity entity, final String recipient) {
        super();
        setId(id);
        setSessionId(identity);
        setRecipient(recipient);
        setRequestEntity(entity);
    }

    public SendRequest(final LSDEntity entity, final String recipient) {
        this(null, null, entity, recipient);
    }


    public Collection<LiquidURI> getAffectedEntities() {
        return Arrays.asList(getRecipientAlias());
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new SendRequest(getId(), getSessionIdentifier(), getRequestEntity(), getRecipient());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Collections.emptyList();
    }

    @Nonnull
    public LiquidURI getInboxURI() {
        return new LiquidURI("pool:///people/" + getRecipient() + "/.inbox");
    }


    public boolean isMutationRequest() {
        return true;
    }

    @Override
    public List<String> getNotificationLocations() {
        return Arrays.asList(new LiquidURI("alias:cazcade:" + getRecipient()).asReverseDNSString());
    }


    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.SEND;
    }


    @Nonnull
    public LiquidURI getRecipientAlias() {
        return new LiquidURI("alias:cazcade:" + getRecipient());
    }


}
