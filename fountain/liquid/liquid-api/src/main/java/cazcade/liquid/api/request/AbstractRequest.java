package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDEntity;

import java.io.Serializable;
import java.util.*;

/**
 * @author neilelliz@cazcade.com
 */
public abstract class AbstractRequest implements Serializable, LiquidRequest {
    protected LiquidSessionIdentifier identity;
    protected LiquidUUID id;
    protected boolean internal;
    protected LiquidURI uri;
    private LiquidCachingScope cachingScope = LiquidCachingScope.NONE;
    private LiquidMessageOrigin origin = LiquidMessageOrigin.UNASSIGNED;
    private LiquidMessageState state = LiquidMessageState.INITIAL;
    private LSDEntity response;
    private Boolean rpc;
    protected LSDEntity entity;

    protected LiquidRequestDetailLevel detail = LiquidRequestDetailLevel.NORMAL;
    private String reply;

    protected AbstractRequest() {
    }

    public LiquidUUID getId() {
        return id;
    }


    public void setId(LiquidUUID id) {
        this.id = id;
    }

    public LiquidURI getUri() {
        return uri;
    }

    public boolean shouldNotify() {
        return isAsyncRequest();
    }

    @Override
    public long getCacheExpiry() {
        return -1;
    }

    @Override
    public boolean isSecureOperation() {
        return false;
    }


    public boolean isInternal() {
        return internal;
    }

    public boolean getInternal() {
        return internal;
    }

    public LiquidSessionIdentifier getSessionIdentifier() {
        return identity;
    }

    public void setIdentity(LiquidSessionIdentifier identity) {
        if (identity != null && identity.equals(this.identity)) {
            return;
        }
        if (this.identity != null && identity != null) {
            throw new IllegalStateException("Cannot change the identity on a request was " + this.identity.getAliasURL() + " tried to change to " + identity.getAliasURL());
        }
        this.identity = identity;
    }

    public LiquidMessageOrigin getOrigin() {
        return origin;
    }

    public void setOrigin(LiquidMessageOrigin origin) {
        this.origin = origin;
    }

    public abstract LiquidMessage copy();

    public LiquidMessageState getState() {
        return state;
    }


    public LiquidMessageType getMessageType() {
        return LiquidMessageType.REQUEST;
    }

    public String getNotificationSession() {
        if (getSessionIdentifier() == null || getSessionIdentifier().getSession() == null) {
            return null;
        } else {
            return getSessionIdentifier().getSession().toString();
        }
    }

    public boolean isAsyncRequest() {
        if (rpc != null) {
            return !rpc;
        } else {
            return isMutationRequest();
        }
    }

    public void setState(LiquidMessageState state) {
        this.state = state;
    }


    public void setResponse(LSDEntity response) {
        this.response = response;
    }

    public LSDEntity getEntity() {
        return entity;
    }


    public LSDEntity getResponse() {
        return response;
    }

    @Override
    public String getCacheIdentifier() {
        return id.toString() + ":" + getState() + ":" + getOrigin();
    }

    @Override
    public boolean isCacheable() {
        return false;
    }

    public LiquidURI getAlias() {
        return identity.getAlias();
    }

    public LiquidRequestDetailLevel getDetail() {
        return detail;
    }

    @Override
    public LiquidCachingScope getCachingScope() {
        return cachingScope;
    }

    @Override
    public void setCachingScope(LiquidCachingScope cachingScope) {
        this.cachingScope = cachingScope;
    }

    @Override
    public String getDeduplicationIdentifier() {
        return id.toString() + ":" + getState() + ":" + getOrigin();
    }

    @Override
    public LSDEntity getResponseOrRequestEntity() {
        if (getResponse() != null) {
            return getResponse();
        } else {
            return getEntity();
        }
    }


    public Collection<LiquidURI> getAffectedEntities() {
        return getStandardAffectedEntitiesInternalPlus();
    }

    protected Set<LiquidURI> getStandardAffectedEntitiesInternalPlus(LiquidURI... uris) {
        final Set<LiquidURI> result = new HashSet<LiquidURI>();
        if (getEntity() != null) {
            result.addAll(getAffectedEntitiesInternal(getEntity().getURI()));
        }
        if (getResponse() != null) {
            result.addAll(getAffectedEntitiesInternal(getResponse().getURI()));
        }
        if (uri != null) {
            result.addAll(getAffectedEntitiesInternal(uri));
        }
        result.addAll(getAffectedEntitiesInternal(uris));
        return result;

    }

    protected Set<LiquidURI> getAffectedEntitiesInternal(LiquidURI... uris) {
        final Set<LiquidURI> result = new HashSet<LiquidURI>();
        for (LiquidURI uri : uris) {
            if (uri != null) {
                result.addAll(uriSplitToParent(uri));
            }
        }
        return result;
    }

    private List<LiquidURI> uriSplitToParent(LiquidURI theURI) {
        if (theURI.hasFragment()) {
            return Arrays.asList(theURI, theURI.getWithoutFragment());
        } else {
            return Arrays.asList(theURI);
        }
    }

    public void setRpc(Boolean rpc) {
        this.rpc = rpc;
    }

    @Override
    public void setReplyTo(String name) {
        this.reply = name;
    }

    @Override
    public void adjustTimeStampForServerTime() {
        if(getEntity() != null) {
            getEntity().timestamp();
        }
    }

    @Override
    public boolean shouldSendProvisional() {
        return false;
    }

    public String getReplyTo() {
        return reply;
    }

    public void setEntity(LSDEntity entity) {
        this.entity = entity;
    }


}
