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
        this.id = id;
        this.identity = identity;
        this.target = target;
        this.entity = entity;
        this.uri = uri;
    }

    public ChatRequest(LiquidURI uri, String value) {
        this.entity = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.CHAT);
        //Time clocks vary so we don't want this set.
        entity.remove(LSDAttribute.PUBLISHED);
        this.entity.setAttribute(LSDAttribute.TEXT_BRIEF, value);
        this.uri = uri;
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (uri.getScheme().equals(LiquidURIScheme.alias)) {
            return Collections.EMPTY_LIST;
        } else {
            return target != null ? Arrays.asList(new AuthorizationRequest(target, LiquidPermission.VIEW)) : Arrays.asList(new AuthorizationRequest(uri, LiquidPermission.VIEW));
        }
    }


    @Override
    public LiquidMessage copy() {
        return new ChatRequest(id, identity, target, uri, entity);
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CHAT;
    }

    public LiquidURI getUri() {
        return uri;
    }

    @Override
    public void adjustTimeStampForServerTime() {
        super.adjustTimeStampForServerTime();
        if (getEntity() != null) {
            getEntity().setAttribute(LSDAttribute.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        }

    }
}
