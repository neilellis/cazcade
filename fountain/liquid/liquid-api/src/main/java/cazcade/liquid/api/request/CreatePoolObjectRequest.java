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
        super();
    }

    public CreatePoolObjectRequest(final LiquidSessionIdentifier authenticatedUser, final LiquidURI uri, final LSDEntity entity, final LiquidURI authorURI) {
        this(authenticatedUser, null, uri, null, entity, authorURI);
    }

    public CreatePoolObjectRequest(final LiquidURI uri, final LSDEntity entity) {
        this(null, null, uri, null, entity, null);
    }

    public CreatePoolObjectRequest(@Nonnull final LiquidSessionIdentifier authenticatedUser, final LiquidURI uri, final LSDEntity entity) {
        this(authenticatedUser, null, uri, null, entity, authenticatedUser.getAlias());
    }

    public CreatePoolObjectRequest(final LiquidSessionIdentifier authenticatedUser, final LiquidUUID pool, final LSDEntity entity, final LiquidURI authorURI) {
        this(authenticatedUser, pool, null, null, entity, authorURI);
    }

    public CreatePoolObjectRequest(@Nullable final LiquidSessionIdentifier identity, @Nullable final LiquidUUID pool, @Nullable final LiquidURI uri, @Nullable final LiquidUUID id, final LSDEntity entity, @Nullable final LiquidURI authorURI) {
        super();
        setUri(uri);
        setId(id);
        setAuthor(authorURI);
        setSessionId(identity);
        setPoolUUID(pool);
        setRequestEntity(entity);
    }


    @Nullable
    public LiquidUUID getPool() {
        return getPoolUUID();
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new CreatePoolObjectRequest(getSessionIdentifier(), getPoolUUID(), getUri(), getId(), getRequestEntity(), getAuthor());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (getUri() != null) {
            return Arrays.asList(new AuthorizationRequest(getUri(), LiquidPermission.MODIFY));
        } else {
            return Arrays.asList(new AuthorizationRequest(getPoolUUID(), LiquidPermission.MODIFY));
        }
    }

    public List<String> getNotificationLocations() {
        if (getRequestEntity() != null) {
            if (getUri() != null) {
                return Arrays.asList(getUri().asReverseDNSString(), getUri().asReverseDNSString() + "." + getRequestEntity().getAttribute(LSDAttribute.NAME));
            } else {
                if (getRequestEntity().getUUID() != null) {
                    return Arrays.asList(getPoolUUID().toString(), getRequestEntity().getUUID().toString());
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
