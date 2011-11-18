package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChatRequest extends AbstractUpdateRequest {

    public ChatRequest() {
    }

    public ChatRequest(LiquidUUID target, LSDEntity entity) {
        this(null, null, target, null, entity);
    }

    public ChatRequest(LiquidSessionIdentifier identity, LiquidUUID target, LSDEntity entity) {
        this(null, identity, target, null, entity);
    }

    public ChatRequest(LiquidSessionIdentifier identity, LiquidURI uri, LSDEntity entity) {
        this(null, identity, null, uri, entity);
    }

    public ChatRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, LiquidURI uri, LSDEntity entity) {
        this.setId(id);
        this.setIdentity(identity);
        this.setTarget(target);
        this.setRequestEntity(entity);
        this.setUri(uri);
    }

    public ChatRequest(LiquidURI uri, String value) {
        this.setRequestEntity(LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.CHAT));
        //Time clocks vary so we don't want this set.
        super.getEntity().remove(LSDAttribute.PUBLISHED);
        super.getEntity().setAttribute(LSDAttribute.TEXT_BRIEF, value);
        this.setUri(uri);
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (getUri().getScheme().equals(LiquidURIScheme.alias)) {
            return Collections.EMPTY_LIST;
        } else {
            return super.getTarget() != null ? Arrays.asList(new AuthorizationRequest(super.getTarget(), LiquidPermission.VIEW)) : Arrays.asList(new AuthorizationRequest(getUri(), LiquidPermission.VIEW));
        }
    }


    @Override
    public LiquidMessage copy() {
        return new ChatRequest(getId(), getSessionIdentifier(), super.getTarget(), getUri(), super.getEntity());
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CHAT;
    }


    @Override
    public void adjustTimeStampForServerTime() {
        super.adjustTimeStampForServerTime();
        if (getEntity() != null) {
            getEntity().setAttribute(LSDAttribute.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        }

    }
}
