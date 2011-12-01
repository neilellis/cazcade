package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    public CreatePoolObjectRequest(@Nonnull LiquidSessionIdentifier authenticatedUser, LiquidURI uri, LSDEntity entity) {
        this(authenticatedUser, null, uri, null, entity, authenticatedUser.getAlias());
    }

    public CreatePoolObjectRequest(LiquidSessionIdentifier authenticatedUser, LiquidUUID pool, LSDEntity entity, LiquidURI authorURI) {
        this(authenticatedUser, pool, null, null, entity, authorURI);
    }

    public CreatePoolObjectRequest(@Nullable LiquidSessionIdentifier identity, @Nullable LiquidUUID pool, @Nullable LiquidURI uri, @Nullable LiquidUUID id, LSDEntity entity, @Nullable LiquidURI authorURI) {
        this.setUri(uri);
        this.setId(id);
        this.setAuthor(authorURI);
        this.setSessionId(identity);
        this.setPoolUUID(pool);
        this.setRequestEntity(entity);
    }


    @Nullable
    public LiquidUUID getPool() {
        return getPoolUUID();
    }

    @Nonnull
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
                if (super.getRequestEntity().getUUID() != null) {
                    return Arrays.asList(getPoolUUID().toString(), super.getRequestEntity().getUUID().toString());
                } else {
                    return Arrays.asList(getPoolUUID().toString());
                }
            }
        } else {
            if (getUri() != null) {
                return Arrays.asList(getUri().asReverseDNSString(), getUri().asReverseDNSString() + "." + getResponse().getAttribute(LSDAttribute.NAME));
            } else {
                return Arrays.asList(getPoolUUID().toString(), getResponse().getUUID().toString());
            }
        }
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CREATE_POOL_OBJECT;
    }


}
