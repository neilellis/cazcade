package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AddCommentRequest extends AbstractUpdateRequest {

    public AddCommentRequest() {
        super();
    }

    public AddCommentRequest(final LiquidUUID target, final LSDEntity entity) {
        this(null, null, target, null, entity);
    }

    public AddCommentRequest(final LiquidSessionIdentifier identity, final LiquidUUID target, final LSDEntity entity) {
        this(null, identity, target, null, entity);
    }

    public AddCommentRequest(final LiquidSessionIdentifier identity, final LiquidURI uri, final LSDEntity entity) {
        this(null, identity, null, uri, entity);
    }

    public AddCommentRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, @Nullable final LiquidUUID target, @Nullable final LiquidURI uri, final LSDEntity entity) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
        setRequestEntity(entity);
        setUri(uri);
    }

    public AddCommentRequest(final LiquidURI uri, final String text) {
        super();
        final LSDSimpleEntity requestEntity = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.COMMENT);
        requestEntity.remove(LSDAttribute.PUBLISHED);
        requestEntity.setAttribute(LSDAttribute.TEXT_BRIEF, text);
        setRequestEntity(requestEntity);
        //Time clocks vary so we don't want this set.
        setUri(uri);
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (getUri().getScheme() == LiquidURIScheme.alias) {
            return Collections.EMPTY_LIST;
        } else {
            return getTarget() != null ? Arrays.asList(new AuthorizationRequest(getTarget(), LiquidPermission.VIEW)) : Arrays.asList(new AuthorizationRequest(getUri(), LiquidPermission.VIEW));
        }
    }


    @Nullable
    @Override
    public LiquidMessage copy() {
        return new AddCommentRequest(getId(), getSessionIdentifier(), getTarget(), getUri(), getRequestEntity());
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.ADD_COMMENT;
    }


    @Override
    public void adjustTimeStampForServerTime() {
        super.adjustTimeStampForServerTime();
        if (getRequestEntity() != null) {
            getEntity().setAttribute(LSDAttribute.REQUEST_ENTITY, LSDAttribute.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        }

    }

}
