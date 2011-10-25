
package cazcade.liquid.api.request;

import cazcade.common.CommonConstants;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDEntity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SendRequest extends AbstractRequest {

    private String recipient;

    public SendRequest() {
    }

    public SendRequest(LiquidSessionIdentifier identity, LSDEntity entity, String recipient) {
        this(null, identity, entity, recipient);
    }

    public SendRequest(LiquidUUID id, LiquidSessionIdentifier identity, LSDEntity entity, String recipient) {
        this.id = id;
        this.identity = identity;
        this.recipient = recipient;
        this.entity = entity;
    }

    public SendRequest(LSDEntity entity, String recipient) {
        this(null, null, entity, recipient);
    }


    public Collection<LiquidURI> getAffectedEntities() {
        return Arrays.asList(getRecipientAlias());
    }

    @Override
    public LiquidMessage copy() {
        return new SendRequest(id, identity, entity, recipient);
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Collections.emptyList();
    }

    public LiquidURI getInboxURI() {
        return new LiquidURI("pool:///people/" + recipient + "/.inbox");
    }


    public boolean isMutationRequest() {
        return true;
    }

    @Override
    public List<String> getNotificationLocations() {
        return Arrays.asList(new LiquidURI("alias:cazcade:" + recipient).asReverseDNSString());
    }


    public LiquidRequestType getRequestType() {
        return LiquidRequestType.SEND;
    }

    public String getRecipientAliasName() {
        return recipient;
    }

    public LiquidURI getRecipientAlias() {
        return new LiquidURI("alias:cazcade:" + recipient);
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
