package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChatRequest extends AbstractUpdateRequest {

    public ChatRequest() {
        super();
    }

    public ChatRequest(final LiquidUUID target, final LSDTransferEntity entity) {
        this(null, null, target, null, entity);
    }

    public ChatRequest(final LiquidSessionIdentifier identity, final LiquidUUID target, final LSDTransferEntity entity) {
        this(null, identity, target, null, entity);
    }

    public ChatRequest(final LiquidSessionIdentifier identity, final LiquidURI uri, final LSDTransferEntity entity) {
        this(null, identity, null, uri, entity);
    }

    public ChatRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, @Nullable final LiquidUUID target, @Nullable final LiquidURI uri, final LSDTransferEntity entity) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
        setRequestEntity(entity);
        setUri(uri);
    }

    public ChatRequest(final LiquidURI uri, final String value) {
        super();
        setRequestEntity(LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.CHAT));
        //Time clocks vary so we don't want this set.
        getRequestEntity().remove(LSDAttribute.PUBLISHED);
        getRequestEntity().setAttribute(LSDAttribute.TEXT_BRIEF, value);
        setUri(uri);
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (getUri().getScheme() == LiquidURIScheme.alias) {
            return Collections.EMPTY_LIST;
        } else {
            return getTarget() != null ? Arrays.asList(new AuthorizationRequest(getTarget(), LiquidPermission.VIEW)) : Arrays.asList(new AuthorizationRequest(getUri(), LiquidPermission.VIEW));
        }
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new ChatRequest(getId(), getSessionIdentifier(), getTarget(), getUri(), getRequestEntity());
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CHAT;
    }


    @Override
    public void adjustTimeStampForServerTime() {
        super.adjustTimeStampForServerTime();
        if (getRequestEntity() != null) {
            getEntity().setAttribute(LSDAttribute.REQUEST_ENTITY, LSDAttribute.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        }

    }
}
