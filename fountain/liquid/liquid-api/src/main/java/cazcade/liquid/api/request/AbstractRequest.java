package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.*;

import java.io.Serializable;
import java.util.*;

/**
 * @author neilelliz@cazcade.com
 */
public abstract class AbstractRequest implements Serializable, LiquidRequest {


    private LSDEntity entity = LSDSimpleEntity.createNewEntity(new LSDTypeDefImpl(LSDDictionaryTypes.REQUEST, getClass().getName().substring(getClass().getName().lastIndexOf('.') + 1)));

    public void setEntity(LSDEntity entity) {
        this.entity = entity;
    }

    public final long getSince() {
        return entity.getLongAttribute(LSDAttribute.SINCE);
    }

    public final void setSince(long since) {
        entity.setAttribute(LSDAttribute.SINCE, since);
    }

    public final boolean isContents() {
        return entity.getBooleanAttribute(LSDAttribute.CONTENTS, true);
    }

    public final void setContents(boolean contents) {
        entity.setAttribute(LSDAttribute.CONTENTS, contents);
    }

    public final boolean isOrCreate() {
        return entity.getBooleanAttribute(LSDAttribute.CREATE_OR_UPDATE);
    }

    public final void setOrCreate(boolean orCreate) {
        entity.setAttribute(LSDAttribute.CREATE_OR_UPDATE, orCreate);
    }

    public final ChildSortOrder getOrder() {
        return ChildSortOrder.valueOf(entity.getAttribute(LSDAttribute.SORT_BY));
    }

    public final void setOrder(ChildSortOrder order) {
        entity.setAttribute(LSDAttribute.SORT_BY, order != null ? order.name() : ChildSortOrder.NONE.name());
    }

    public final Category getCategory() {
        return Category.valueOf(entity.getAttribute(LSDAttribute.DICTIONARY_CATEGORY));
    }

    public final void setCategory(Category category) {
        entity.setAttribute(LSDAttribute.DICTIONARY_CATEGORY, category.name());
    }

    public final LiquidSessionIdentifier getSessionIdentifier() {
        return LiquidSessionIdentifier.fromString(entity.getAttribute(LSDAttribute.REQUEST_SESSION_ID));
    }

    public final void setInternal(boolean internal) {
        entity.setAttribute(LSDAttribute.INTERNAL_REQUEST, internal);
    }

    public final void setUri(LiquidURI uri) {
        if (uri != null) {
            entity.setAttribute(LSDAttribute.REQUEST_URI, uri);
        }
    }

    public final LiquidUUID getTarget() {
        return entity.getUUIDAttribute(LSDAttribute.REQUEST_UUID);
    }

    public final void setTarget(LiquidUUID target) {
        if (target != null && target.toString() != null) {
            entity.setAttribute(LSDAttribute.REQUEST_UUID, target);
        }
    }

    public final boolean isHistorical() {
        return entity.getBooleanAttribute(LSDAttribute.HISTORICAL_REQUEST);
    }

    public final void setHistorical(boolean historical) {
        entity.setAttribute(LSDAttribute.HISTORICAL_REQUEST, historical);
    }


    public final QueryType getQueryType() {
        return QueryType.valueOf(entity.getAttribute(LSDAttribute.BOARD_QUERY_TYPE));
    }

    public final void setQueryType(QueryType queryType) {
        entity.setAttribute(LSDAttribute.BOARD_QUERY_TYPE, queryType.name());
    }

    public final void setAlias(LiquidURI alias) {
        if (alias != null) {
            entity.setAttribute(LSDAttribute.REQUEST_ALIAS, alias.asString());
        }
    }

    public final Integer getStart() {
        return entity.getIntegerAttribute(LSDAttribute.QUERY_START_OFFSET, 0);
    }

    public final void setStart(int start) {
        entity.setAttribute(LSDAttribute.QUERY_START_OFFSET, start);
    }

    public final int getMax() {
        return entity.getIntegerAttribute(LSDAttribute.QUERY_MAX, 60);
    }

    public final void setMax(int max) {
        entity.setAttribute(LSDAttribute.QUERY_MAX, max);
    }


    public final String getPassword() {
        return entity.getAttribute(LSDAttribute.PLAIN_PASSWORD);
    }

    public final void setPassword(String password) {
        entity.setAttribute(LSDAttribute.PLAIN_PASSWORD, password);
    }

    public final boolean isMe() {
        return entity.getBooleanAttribute(LSDAttribute.IS_ME);
    }

    public final void setMe(boolean me) {
        entity.setAttribute(LSDAttribute.IS_ME, me);
    }

    public final boolean isClaim() {
        return entity.getBooleanAttribute(LSDAttribute.IS_CLAIM);
    }

    public final void setClaim(boolean claim) {
        entity.setAttribute(LSDAttribute.IS_CLAIM, claim);
    }

    public final LiquidUUID getPoolUUID() {
        return entity.getUUIDAttribute(LSDAttribute.REQUEST_POOL_UUID);
    }

    public final void setPoolUUID(LiquidUUID poolUUID) {
        if (poolUUID != null) {
            entity.setAttribute(LSDAttribute.REQUEST_POOL_UUID, poolUUID);
        }
    }

    public final LiquidURI getAuthor() {
        return entity.getURIAttribute(LSDAttribute.REQUEST_AUTHOR);
    }

    public final void setAuthor(LiquidURI author) {
        if (author != null) {
            entity.setAttribute(LSDAttribute.REQUEST_AUTHOR, author);
        }
    }

    public final LiquidURI getParent() {
        return entity.getURIAttribute(LSDAttribute.REQUEST_PARENT_URI);
    }

    public final void setParent(LiquidURI parent) {
        if (parent != null) {
            entity.setAttribute(LSDAttribute.REQUEST_PARENT_URI, parent);
        }
    }

    public final LSDDictionaryTypes getType() {
        return LSDDictionaryTypes.valueOf(LSDDictionaryTypes.getNameForValue(entity.getAttribute(LSDAttribute.REQUEST_RESOURCE_TYPE)));
    }

    public final void setType(LSDDictionaryTypes type) {
        entity.setAttribute(LSDAttribute.REQUEST_RESOURCE_TYPE, type.asString());
    }

    public final String getName() {
        return entity.getAttribute(LSDAttribute.NAME);
    }

    public final void setName(String name) {
        entity.setAttribute(LSDAttribute.NAME, name);
    }

    public final String getTitle() {
        return entity.getAttribute(LSDAttribute.TITLE);
    }

    public final void setTitle(String title) {
        entity.setAttribute(LSDAttribute.TITLE, title);
    }

    public final String getDescription() {
        return entity.getAttribute(LSDAttribute.DESCRIPTION);
    }

    public final void setDescription(String description) {
        entity.setAttribute(LSDAttribute.DESCRIPTION, description);
    }

    public final boolean isListed() {
        return entity.getBooleanAttribute(LSDAttribute.LISTED);
    }

    public final void setListed(boolean listed) {
        entity.setAttribute(LSDAttribute.LISTED, listed);
    }

    public final ClientApplicationIdentifier getClient() {
        return ClientApplicationIdentifier.valueOf(entity.getAttribute(LSDAttribute.CLIENT_APPLICATION_IDENTIFIER));
    }

    public final void setClient(ClientApplicationIdentifier client) {
        entity.setAttribute(LSDAttribute.CLIENT_APPLICATION_IDENTIFIER, client.toString());
    }

    public final boolean isFollow() {
        return entity.getBooleanAttribute(LSDAttribute.FOLLOW);
    }

    public final void setFollow(boolean follow) {
        entity.setAttribute(LSDAttribute.FOLLOW, follow);
    }

    public final LiquidUUID getFrom() {
        return entity.getUUIDAttribute(LSDAttribute.FROM);
    }

    public final void setFrom(LiquidUUID from) {
        if (from != null) {
            entity.setAttribute(LSDAttribute.FROM, from);
        }
    }

    public final LiquidUUID getTo() {
        return entity.getUUIDAttribute(LSDAttribute.TO);
    }

    public final void setTo(LiquidUUID to) {
        if (to != null) {
            entity.setAttribute(LSDAttribute.TO, to);
        }
    }

    public final boolean isUnlink() {
        return entity.getBooleanAttribute(LSDAttribute.UNLINK);
    }

    public final void setUnlink(boolean unlink) {
        entity.setAttribute(LSDAttribute.UNLINK, unlink);
    }

    public final LiquidUUID getObjectUUID() {
        return entity.getUUIDAttribute(LSDAttribute.REQUEST_OBJECT_UUID);
    }

    public final void setObjectUUID(LiquidUUID objectUUID) {
        if (objectUUID != null) {
            entity.setAttribute(LSDAttribute.REQUEST_OBJECT_UUID, objectUUID);
        }
    }

    public final Double getX() {
        return entity.getDoubleAttribute(LSDAttribute.VIEW_X);
    }

    public final void setX(Double x) {
        entity.setAttribute(LSDAttribute.VIEW_X, x);
    }

    public final Double getY() {
        return entity.getDoubleAttribute(LSDAttribute.VIEW_Y);
    }

    public final void setY(Double y) {
        entity.setAttribute(LSDAttribute.VIEW_Y, y);
    }

    public final Double getZ() {
        return entity.getDoubleAttribute(LSDAttribute.VIEW_Z);
    }

    public final void setZ(Double z) {
        entity.setAttribute(LSDAttribute.VIEW_Z, z);
    }

    public final Integer getWidth() {
        return entity.getIntegerAttribute(LSDAttribute.VIEW_WIDTH);
    }

    public final void setWidth(Integer width) {
        entity.setAttribute(LSDAttribute.VIEW_WIDTH, width);
    }

    public final Integer getHeight() {
        return entity.getIntegerAttribute(LSDAttribute.VIEW_WIDTH);
    }

    public final void setHeight(Integer height) {
        entity.setAttribute(LSDAttribute.VIEW_HEIGHT, height);
    }

    public final Double getAngle() {
        return entity.getDoubleAttribute(LSDAttribute.VIEW_ROTATE_XY);
    }

    public final void setAngle(Double angle) {
        entity.setAttribute(LSDAttribute.VIEW_ROTATE_XY, angle);
    }

    public final String getSearchText() {
        return entity.getAttribute(LSDAttribute.TEXT_BRIEF);
    }

    public final void setSearchText(String searchText) {
        entity.setAttribute(LSDAttribute.TEXT_BRIEF, searchText);
    }

    public final boolean isSelected() {
        return entity.getBooleanAttribute(LSDAttribute.SELECTED);
    }

    public final void setSelected(boolean selected) {
        entity.setAttribute(LSDAttribute.SELECTED, selected);
    }

    public final String getRecipient() {
        return entity.getAttribute(LSDAttribute.RECIPIENT);
    }

    public final void setRecipient(String recipient) {
        entity.setAttribute(LSDAttribute.RECIPIENT, recipient);
    }

    public final LiquidURI getPoolURI() {
        if (getUri() != null) {
            return getUri().getWithoutFragment();
        } else {
            return null;
        }
    }

    public final LiquidURI getPreviousPool() {
        return entity.getURIAttribute(LSDAttribute.REQUEST_PREVIOUS_POOL_URI);
    }

    public final void setPreviousPool(LiquidURI previousPool) {
        if (previousPool != null) {
            entity.setAttribute(LSDAttribute.REQUEST_PREVIOUS_POOL_URI, previousPool);
        }
    }

    public final void setPoolType(LSDType poolType) {
        entity.setAttribute(LSDAttribute.REQUEST_RESOURCE_TYPE, poolType.asString());
    }

    public final LiquidPermissionChangeType getPermission() {
        return LiquidPermissionChangeType.valueOf(entity.getAttribute(LSDAttribute.PERMISSION_CHANGE));
    }

    public final void setPermission(LiquidPermissionChangeType permission) {
        if (permission != null) {
            entity.setAttribute(LSDAttribute.PERMISSION_CHANGE, permission.name());
        }
    }


    public final Boolean getRpc() {
        if (entity.hasAttribute(LSDAttribute.REQUEST_EXPLICIT_RPC)) {
            return entity.getBooleanAttribute(LSDAttribute.REQUEST_EXPLICIT_RPC);
        } else {
            return null;
        }
    }

    public final void setDetail(LiquidRequestDetailLevel detail) {
        entity.setAttribute(LSDAttribute.QUERY_DETAIL, detail.name());
    }

    public enum QueryType {
        MY, USERS_BOARDS, RECENT, HISTORY, POPULAR
    }

    public static enum Category {
        KEYS, TYPES
    }


    protected AbstractRequest() {
    }

    public final LiquidUUID getId() {
        return entity.getID();
    }


    public final void setId(LiquidUUID id) {
        if (id != null && id.toString() != null) {
            entity.setId(id.toString());
        } else {
            entity.removeCompletely(LSDAttribute.ID);
        }
    }

    public final LiquidURI getUri() {
        return entity.getURIAttribute(LSDAttribute.REQUEST_URI);
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
        return entity.getBooleanAttribute(LSDAttribute.INTERNAL_REQUEST);
    }

    public boolean getInternal() {
        return isInternal();
    }


    public final void setIdentity(LiquidSessionIdentifier sessionId) {
        if (sessionId != null && sessionId.equals(getSessionIdentifier())) {
            return;
        }
        if (getSessionIdentifier() != null && sessionId != null) {
            throw new IllegalStateException("Cannot change the identity on a request was " + getSessionIdentifier().getAliasURL() + " tried to change to " + sessionId.getAliasURL());
        }
        entity.setAttribute(LSDAttribute.REQUEST_SESSION_ID, sessionId == null ? "" : sessionId.toString());
    }

    public final LiquidMessageOrigin getOrigin() {
        return LiquidMessageOrigin.valueOf(entity.getAttribute(LSDAttribute.REQUEST_ORIGIN, LiquidMessageOrigin.UNASSIGNED.name()));
    }

    public final void setOrigin(LiquidMessageOrigin origin) {
        entity.setAttribute(LSDAttribute.REQUEST_ORIGIN, origin.name());
    }

    public abstract LiquidMessage copy();

    public final LiquidMessageState getState() {
        return LiquidMessageState.valueOf(entity.getAttribute(LSDAttribute.REQUEST_STATE, LiquidMessageState.INITIAL.name()));
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
        if (getRpc() != null) {
            return !getRpc();
        } else {
            return isMutationRequest();
        }
    }

    public final void setState(LiquidMessageState state) {
        entity.setAttribute(LSDAttribute.REQUEST_STATE, state.name());
    }


    public final void setResponse(LSDEntity response) {
        entity.addSubEntity(LSDAttribute.REQUEST_RESULT, response, false);
    }

    public final LSDEntity getEntity() {
        if (entity.hasSubEntity(LSDAttribute.REQUEST_ENTITY)) {
            return entity.getSubEntity(LSDAttribute.REQUEST_ENTITY);
        } else {
            return null;
        }
    }


    public final LSDEntity getResponse() {
        if (entity.hasSubEntity(LSDAttribute.REQUEST_RESULT)) {
            return entity.getSubEntity(LSDAttribute.REQUEST_RESULT);
        } else {
            return null;
        }
    }

    @Override
    public String getCacheIdentifier() {
        return getId().toString() + ":" + getState() + ":" + getOrigin();
    }

    @Override
    public boolean isCacheable() {
        return false;
    }

    public final LiquidURI getAlias() {
        return getSessionIdentifier().getAlias();
    }

    public final LiquidRequestDetailLevel getDetail() {
        return LiquidRequestDetailLevel.valueOf(entity.getAttribute(LSDAttribute.QUERY_DETAIL, LiquidRequestDetailLevel.NORMAL.name()));
    }

    @Override
    public final LiquidCachingScope getCachingScope() {
        return LiquidCachingScope.valueOf(entity.getAttribute(LSDAttribute.REQUEST_CACHING_SCOPE, LiquidCachingScope.NONE.name()));
    }

    @Override
    public final void setCachingScope(LiquidCachingScope cachingScope) {
        entity.setAttribute(LSDAttribute.REQUEST_CACHING_SCOPE, cachingScope.name());
    }

    @Override
    public String getDeduplicationIdentifier() {
        return getId().toString() + ":" + getState() + ":" + getOrigin();
    }

    @Override
    public final LSDEntity getResponseOrRequestEntity() {
        if (getResponse() != null) {
            return getResponse();
        } else {
            return getEntity();
        }
    }


    public Collection<LiquidURI> getAffectedEntities() {
        return getStandardAffectedEntitiesInternalPlus();
    }

    protected final Set<LiquidURI> getStandardAffectedEntitiesInternalPlus(LiquidURI... uris) {
        final Set<LiquidURI> result = new HashSet<LiquidURI>();
        if (getEntity() != null) {
            result.addAll(getAffectedEntitiesInternal(getEntity().getURI()));
        }
        if (getResponse() != null) {
            result.addAll(getAffectedEntitiesInternal(getResponse().getURI()));
        }
        if (getUri() != null) {
            result.addAll(getAffectedEntitiesInternal(getUri()));
        }
        result.addAll(getAffectedEntitiesInternal(uris));
        return result;

    }

    protected final Set<LiquidURI> getAffectedEntitiesInternal(LiquidURI... uris) {
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

    public final void setRpc(Boolean rpc) {
        entity.setAttribute(LSDAttribute.REQUEST_EXPLICIT_RPC, rpc);
    }


    @Override
    public void adjustTimeStampForServerTime() {
        if (getEntity() != null) {
            getEntity().timestamp();
        }
    }

    @Override
    public boolean shouldSendProvisional() {
        return false;
    }

    public final void setRequestEntity(LSDEntity requestEntity) {
        entity.addSubEntity(LSDAttribute.REQUEST_ENTITY, requestEntity, false);
    }

    @Override
    public SerializedRequest asSerializedRequest() {
        return new SerializedRequest(getRequestType(), entity);
    }


}
