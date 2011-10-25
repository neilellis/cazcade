package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;

import java.util.Arrays;
import java.util.List;

public class CreatePoolObjectRequest extends AbstractCreationRequest {
    private LiquidUUID pool;
    private LiquidURI author;


    public CreatePoolObjectRequest() {
    }

    public CreatePoolObjectRequest(LiquidSessionIdentifier authenticatedUser, LiquidURI poolURI, LSDEntity entity, LiquidURI authorURI) {
        this(authenticatedUser, null, poolURI, null, entity, authorURI);
    }

    public CreatePoolObjectRequest(LiquidURI poolURI, LSDEntity entity) {
        this(null, null, poolURI, null, entity, null);
    }

    public CreatePoolObjectRequest(LiquidSessionIdentifier authenticatedUser, LiquidURI poolURI, LSDEntity entity) {
        this(authenticatedUser, null, poolURI, null, entity, authenticatedUser.getAlias());
    }

    public CreatePoolObjectRequest(LiquidSessionIdentifier authenticatedUser, LiquidUUID pool, LSDEntity entity, LiquidURI authorURI) {
        this(authenticatedUser, pool, null, null, entity, authorURI);
    }

    public CreatePoolObjectRequest(LiquidSessionIdentifier identity, LiquidUUID pool, LiquidURI poolURI, LiquidUUID id, LSDEntity entity, LiquidURI authorURI) {
        this.uri = poolURI;
        this.id = id;
        this.author = authorURI;
        this.identity = identity;
        this.pool = pool;
        this.entity = entity;
    }


    public LiquidURI getAuthor() {
        if (author == null) {
            return getAlias();
        } else {
            return author;
        }
    }


    public LiquidUUID getPool() {
        return pool;
    }

    @Override
    public LiquidMessage copy() {
        return new CreatePoolObjectRequest(identity, pool, uri, id, entity, author);
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (uri != null) {
            return Arrays.asList(new AuthorizationRequest(uri, LiquidPermission.MODIFY));
        } else {
            return Arrays.asList(new AuthorizationRequest(pool, LiquidPermission.MODIFY));
        }
    }

    public List<String> getNotificationLocations() {
        if (entity != null) {
            if (uri != null) {
                return Arrays.asList(uri.asReverseDNSString(), uri.asReverseDNSString() + "." + entity.getAttribute(LSDAttribute.NAME));
            } else {
                if (entity.getID() != null) {
                    return Arrays.asList(pool.toString(), entity.getID().toString());
                } else {
                    return Arrays.asList(pool.toString());
                }
            }
        } else {
            if (uri != null) {
                return Arrays.asList(uri.asReverseDNSString(), uri.asReverseDNSString() + "." + getResponse().getAttribute(LSDAttribute.NAME));
            } else {
                return Arrays.asList(pool.toString(), getResponse().getID().toString());
            }
        }
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CREATE_POOL_OBJECT;
    }


    public LiquidURI getPoolURI() {
        return uri;
    }
}
