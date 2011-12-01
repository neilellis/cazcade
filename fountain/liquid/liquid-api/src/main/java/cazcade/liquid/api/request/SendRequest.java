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
    }

    public SendRequest(LiquidSessionIdentifier identity, LSDEntity entity, String recipient) {
        this(null, identity, entity, recipient);
    }

    public SendRequest(@Nullable LiquidUUID id, @Nullable LiquidSessionIdentifier identity, LSDEntity entity, String recipient) {
        this.setId(id);
        this.setSessionId(identity);
        super.setRecipient(recipient);
        this.setRequestEntity(entity);
    }

    public SendRequest(LSDEntity entity, String recipient) {
        this(null, null, entity, recipient);
    }


    public Collection<LiquidURI> getAffectedEntities() {
        return Arrays.asList(getRecipientAlias());
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new SendRequest(getId(), getSessionIdentifier(), getRequestEntity(), super.getRecipient());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Collections.emptyList();
    }

    @Nonnull
    public LiquidURI getInboxURI() {
        return new LiquidURI("pool:///people/" + super.getRecipient() + "/.inbox");
    }


    public boolean isMutationRequest() {
        return true;
    }

    @Override
    public List<String> getNotificationLocations() {
        return Arrays.asList(new LiquidURI("alias:cazcade:" + super.getRecipient()).asReverseDNSString());
    }


    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.SEND;
    }


    @Nonnull
    public LiquidURI getRecipientAlias() {
        return new LiquidURI("alias:cazcade:" + super.getRecipient());
    }


}
