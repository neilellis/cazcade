package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;

import java.util.Arrays;
import java.util.List;

public class CreatePoolObjectRequest extends AbstractCreationRequest {


    public CreatePoolObjectRequest() {
    }

    public CreatePoolObjectRequest(LiquidSessionIdentifier authenticatedUser, LiquidURI uri, LSDEntity entity, LiquidURI authorURI) {
        this(authenticatedUser, null, uri, null, entity, authorURI);
    }

    public CreatePoolObjectRequest(LiquidURI uri, LSDEntity entity) {
        this(null, null, uri, null, entity, null);
    }

    public CreatePoolObjectRequest(LiquidSessionIdentifier authenticatedUser, LiquidURI uri, LSDEntity entity) {
        this(authenticatedUser, null, uri, null, entity, authenticatedUser.getAlias());
    }

    public CreatePoolObjectRequest(LiquidSessionIdentifier authenticatedUser, LiquidUUID pool, LSDEntity entity, LiquidURI authorURI) {
        this(authenticatedUser, pool, null, null, entity, authorURI);
    }

    public CreatePoolObjectRequest(LiquidSessionIdentifier identity, LiquidUUID pool, LiquidURI uri, LiquidUUID id, LSDEntity entity, LiquidURI authorURI) {
        this.setUri(uri);
        this.setId(id);
        this.setAuthor(authorURI);
        this.setSessionId(identity);
        this.setPoolUUID(pool);
        this.setRequestEntity(entity);
    }


    public LiquidUUID getPool() {
        return getPoolUUID();
    }

    @Override
    public LiquidMessage copy() {
        return new CreatePoolObjectRequest(getSessionIdentifier(), getPoolUUID(), getUri(), getId(), super.getRequestEntity(), getAuthor());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (getUri() != null) {
            return Arrays.asList(new AuthorizationRequest(getUri(), LiquidPermission.MODIFY));
        } else {
            return Arrays.asList(new AuthorizationRequest(getPoolUUID(), LiquidPermission.MODIFY));
        }
    }

    public List<String> getNotificationLocations() {
        if (super.getRequestEntity() != null) {
            if (getUri() != null) {
                return Arrays.asList(getUri().asReverseDNSString(), getUri().asReverseDNSString() + "." + super.getRequestEntity().getAttribute(LSDAttribute.NAME));
            } else {
                if (super.getRequestEntity().getID() != null) {
                    return Arrays.asList(getPoolUUID().toString(), super.getRequestEntity().getID().toString());
                } else {
                    return Arrays.asList(getPoolUUID().toString());
                }
            }
        } else {
            if (getUri() != null) {
                return Arrays.asList(getUri().asReverseDNSString(), getUri().asReverseDNSString() + "." + getResponse().getAttribute(LSDAttribute.NAME));
            } else {
                return Arrays.asList(getPoolUUID().toString(), getResponse().getID().toString());
            }
        }
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CREATE_POOL_OBJECT;
    }


}
