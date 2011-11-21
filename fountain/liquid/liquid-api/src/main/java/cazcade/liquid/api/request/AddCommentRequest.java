package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AddCommentRequest extends AbstractUpdateRequest {

    public AddCommentRequest() {
    }

    public AddCommentRequest(LiquidUUID target, LSDEntity entity) {
        this(null, null, target, null, entity);
    }

    public AddCommentRequest(LiquidSessionIdentifier identity, LiquidUUID target, LSDEntity entity) {
        this(null, identity, target, null, entity);
    }

    public AddCommentRequest(LiquidSessionIdentifier identity, LiquidURI uri, LSDEntity entity) {
        this(null, identity, null, uri, entity);
    }

    public AddCommentRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, LiquidURI uri, LSDEntity entity) {
        this.setId(id);
        this.setIdentity(identity);
        this.setTarget(target);
        this.setRequestEntity(entity);
        this.setUri(uri);
    }

    public AddCommentRequest(LiquidURI uri, String text) {
        final LSDSimpleEntity requestEntity = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.COMMENT);
        requestEntity.remove(LSDAttribute.PUBLISHED);
        requestEntity.setAttribute(LSDAttribute.TEXT_BRIEF, text);
        this.setRequestEntity(requestEntity);
        //Time clocks vary so we don't want this set.
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
        return new AddCommentRequest(getId(), getSessionIdentifier(), super.getTarget(), getUri(), super.getRequestEntity());
    }

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
