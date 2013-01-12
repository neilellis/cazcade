/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author neilelliz@cazcade.com
 */
public abstract class AbstractRequest implements LiquidRequest {


    public enum QueryType {
        MY, USERS_BOARDS, RECENT, HISTORY, POPULAR
    }

    public enum Category {
        KEYS, TYPES
    }

    @Nonnull
    private LSDTransferEntity entity = LSDSimpleEntity.createNewEntity(new LSDTypeDefImpl(LSDDictionaryTypes.REQUEST, getClass().getName()
            .substring(getClass().getName().lastIndexOf('.') + 1)));

    protected AbstractRequest() {
    }

    protected AbstractRequest(@Nonnull final LSDTransferEntity entity) {
        this.entity = entity.copy();
    }

    @Override
    public void adjustTimeStampForServerTime() {
        if (hasRequestEntity()) {
            entity.setAttribute(LSDAttribute.REQUEST_ENTITY, LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        }
    }

    @Override
    public long getCacheExpiry() {
        return -1;
    }

    @Override @Nullable
    public String getNotificationSession() {
        final LiquidSessionIdentifier sessionIdentifier = getSessionIdentifier();
        if (sessionIdentifier.getSession() == null) {
            return null;
        }
        else {
            return sessionIdentifier.getSession().toString();
        }
    }

    @Override @Nonnull
    public final LiquidSessionIdentifier getSessionIdentifier() {
        final LiquidSessionIdentifier identifier = getLiquidSessionIdentifierInternal();
        if (identifier == null) {
            return LiquidSessionIdentifier.ANON;
        }
        return identifier;
    }

    public boolean isAsyncRequest() {
        final Boolean rpc = getRpc();
        if (rpc != null) {
            return !rpc;
        }
        else {
            return isMutationRequest();
        }
    }

    @Override
    public boolean isSecureOperation() {
        return false;
    }

    @Nullable
    public final Boolean getRpc() {
        if (entity.hasAttribute(LSDAttribute.REQUEST_EXPLICIT_RPC)) {
            return entity.getBooleanAttribute(LSDAttribute.REQUEST_EXPLICIT_RPC);
        }
        else {
            return null;
        }
    }

    public final void setRpc(final Boolean rpc) {
        entity.setAttribute(LSDAttribute.REQUEST_EXPLICIT_RPC, rpc);
    }

    public final void setSessionId(@Nonnull final LiquidSessionIdentifier sessionId) {
        //noinspection ConstantConditions
        if (sessionId == null) {
            throw new IllegalArgumentException("Cannot set a null LiquidSessionIdentifier.");
        }
        if (sessionId.equals(getLiquidSessionIdentifierInternal())) {
            return;
        }
        if (getLiquidSessionIdentifierInternal() != null
            && !getSessionIdentifier().getAlias().equals(sessionId.getAlias())
            && !getLiquidSessionIdentifierInternal().isAnon()) {
            throw new IllegalStateException("Cannot change the alias of the session on a request, attempt was from " +
                                            getLiquidSessionIdentifierInternal() +
                                            " to " +
                                            sessionId);
        }
        entity.setAttribute(LSDAttribute.REQUEST_SESSION_ID, sessionId.toString());
    }

    public boolean shouldNotify() {
        return isAsyncRequest();
    }

    @Override
    public boolean shouldSendProvisional() {
        return false;
    }

    public boolean hasRequestEntity() {
        return entity.hasSubEntity(LSDAttribute.REQUEST_ENTITY);
    }

    @Override public boolean hasId() {
        return entity.hasAttribute(LSDAttribute.ID);
    }

    @Nullable
    private LiquidSessionIdentifier getLiquidSessionIdentifierInternal() {
        return LiquidSessionIdentifier.fromString(entity.getRawValue(LSDAttribute.REQUEST_SESSION_ID));
    }

    @Nonnull @Override
    public SerializedRequest asSerializedRequest() {
        return new SerializedRequest(getRequestType(), entity);
    }

    @Nonnull
    public abstract LiquidMessage copy();

    public Collection<LiquidURI> getAffectedEntities() {
        return getStandardAffectedEntitiesInternalPlus();
    }

    @Nonnull @Override
    public String getCacheIdentifier() {
        return getId().toString() + ":" + getState() + ":" + getOrigin();
    }

    @Override @Nonnull
    public final LiquidCachingScope getCachingScope() {
        return LiquidCachingScope.valueOf(entity.getAttribute(LSDAttribute.REQUEST_CACHING_SCOPE, LiquidCachingScope.NONE.name()));
    }

    @Nonnull @Override
    public String getDeduplicationIdentifier() {
        return getId().toString() + ":" + getState() + ":" + getOrigin();
    }

    @Nonnull
    public final LiquidUUID getId() {
        return entity.getUUID();
    }

    @Nonnull
    public LiquidMessageType getMessageType() {
        return LiquidMessageType.REQUEST;
    }

    @Nonnull
    public final LiquidMessageOrigin getOrigin() {
        return LiquidMessageOrigin.valueOf(entity.getAttribute(LSDAttribute.REQUEST_ORIGIN, LiquidMessageOrigin.UNASSIGNED.name()));
    }

    @Nonnull
    public final LSDTransferEntity getRequestEntity() {
        return entity.getSubEntity(LSDAttribute.REQUEST_ENTITY, true);
    }

    @Nonnull
    public final LSDTransferEntity getResponse() {
        if (entity.hasSubEntity(LSDAttribute.REQUEST_RESULT)) {
            return entity.getSubEntity(LSDAttribute.REQUEST_RESULT, true);
        }
        else {
            throw new IllegalStateException("Response expected but none set.");
        }
    }

    @Nullable @Override
    public final LSDBaseEntity getResponseOrRequestEntity() {
        if (hasResponseEntity()) {
            return getResponse();
        }
        else {
            return getRequestEntity();
        }
    }

    @Nonnull
    public final LiquidMessageState getState() {
        return LiquidMessageState.valueOf(entity.getAttribute(LSDAttribute.REQUEST_STATE, LiquidMessageState.INITIAL.name()));
    }

    @Override
    public boolean isCacheable() {
        return false;
    }

    public final void setState(@Nonnull final LiquidMessageState state) {
        entity.setAttribute(LSDAttribute.REQUEST_STATE, state.name());
    }

    public boolean hasResponseEntity() {
        return entity.hasSubEntity(LSDAttribute.REQUEST_RESULT);
    }

    public final void setResponse(final LSDTransferEntity response) {
        if (!response.isSerializable()) {
            throw new IllegalArgumentException("Oops, you've tried to pass in a raw persistent entity!");
        }
        entity.addSubEntity(LSDAttribute.REQUEST_RESULT, response, false);
    }

    public final void setRequestEntity(final LSDTransferEntity requestEntity) {
        entity.addSubEntity(LSDAttribute.REQUEST_ENTITY, requestEntity, false);
    }

    public final void setOrigin(@Nonnull final LiquidMessageOrigin origin) {
        entity.setAttribute(LSDAttribute.REQUEST_ORIGIN, origin.name());
    }

    public final void setId(@Nullable final LiquidUUID id) {
        if (id != null && id.toString() != null) {
            entity.setId(id.toString());
        }
        else {
            entity.removeCompletely(LSDAttribute.ID);
        }
    }

    @Override
    public final void setCachingScope(@Nonnull final LiquidCachingScope cachingScope) {
        entity.setAttribute(LSDAttribute.REQUEST_CACHING_SCOPE, cachingScope.name());
    }

    @Nonnull
    protected final Set<LiquidURI> getStandardAffectedEntitiesInternalPlus(final LiquidURI... uris) {
        final Set<LiquidURI> result = new HashSet<LiquidURI>();
        if (hasRequestEntity() && getRequestEntity().hasURI()) {
            final LSDTransferEntity requestEntity = getRequestEntity();
            result.addAll(getAffectedEntitiesInternal(requestEntity.getURI()));
        }
        if (hasResponseEntity()) {
            final LSDTransferEntity response = getResponse();
            if (response.hasURI()) {
                result.addAll(getAffectedEntitiesInternal(response.getURI()));
            }
        }
        if (hasUri()) {
            result.addAll(getAffectedEntitiesInternal(getUri()));
        }
        result.addAll(getAffectedEntitiesInternal(uris));
        return result;
    }

    @Nonnull
    protected final Set<LiquidURI> getAffectedEntitiesInternal(@Nonnull final LiquidURI... uris) {
        final Set<LiquidURI> result = new HashSet<LiquidURI>();
        for (final LiquidURI uri : uris) {
            if (uri != null) {
                result.addAll(uriSplitToParent(uri));
            }
        }
        return result;
    }

    @Nonnull
    private List<LiquidURI> uriSplitToParent(@Nonnull final LiquidURI theURI) {
        if (theURI.hasFragment()) {
            return Arrays.asList(theURI, theURI.getWithoutFragment());
        }
        else {
            return Arrays.asList(theURI);
        }
    }

    @Nonnull
    public final LiquidURI getUri() {
        return entity.getURIAttribute(LSDAttribute.REQUEST_URI);
    }

    public final void setUri(@Nullable final LiquidURI uri) {
        if (uri != null) {
            entity.setAttribute(LSDAttribute.REQUEST_URI, uri);
        }
    }

    public final boolean hasUri() {
        return entity.hasAttribute(LSDAttribute.REQUEST_URI);
    }

    @Nonnull
    public final LiquidURI getAlias() {
        return getSessionIdentifier().getAlias();
    }

    public final void setAlias(@Nullable final LiquidURI alias) {
        if (alias != null) {
            entity.setAttribute(LSDAttribute.REQUEST_ALIAS, alias.asString());
        }
    }

    @Nonnull
    public final Double getAngle() {
        return entity.getDoubleAttribute(LSDAttribute.VIEW_ROTATE_XY);
    }

    public final void setAngle(final Double angle) {
        entity.setAttribute(LSDAttribute.VIEW_ROTATE_XY, angle);
    }

    public boolean hasAngle() {
        return entity.hasAttribute(LSDAttribute.VIEW_ROTATE_XY);
    }

    @Nonnull
    public final LiquidURI getAuthor() {
        return entity.getURIAttribute(LSDAttribute.REQUEST_AUTHOR);
    }

    public final void setAuthor(@Nullable final LiquidURI author) {
        if (author != null) {
            entity.setAttribute(LSDAttribute.REQUEST_AUTHOR, author);
        }
    }

    public final boolean hasAuthor() {
        return entity.hasAttribute(LSDAttribute.REQUEST_AUTHOR);
    }

    @Nonnull
    public final Category getCategory() {
        return Category.valueOf(entity.getAttribute(LSDAttribute.DICTIONARY_CATEGORY));
    }

    public final void setCategory(@Nonnull final Category category) {
        entity.setAttribute(LSDAttribute.DICTIONARY_CATEGORY, category.name());
    }

    @Nonnull
    public String getChangePasswordSecurityHash() {
        return entity.getAttribute(LSDAttribute.SECURITY_CONFIRMATION_HASH);
    }

    public void setChangePasswordSecurityHash(final String hash) {
        entity.setAttribute(LSDAttribute.SECURITY_CONFIRMATION_HASH, hash);
    }

    public boolean hasChangePasswordSecurityHash() {
        return entity.hasAttribute(LSDAttribute.SECURITY_CONFIRMATION_HASH);
    }

    @Nonnull
    public final ClientApplicationIdentifier getClient() {
        return ClientApplicationIdentifier.valueOf(entity.getAttribute(LSDAttribute.CLIENT_APPLICATION_IDENTIFIER));
    }

    public final void setClient(@Nonnull final ClientApplicationIdentifier client) {
        entity.setAttribute(LSDAttribute.CLIENT_APPLICATION_IDENTIFIER, client.toString());
    }

    @Nonnull
    public final String getDescription() {
        return entity.getAttribute(LSDAttribute.DESCRIPTION);
    }

    public final void setDescription(final String description) {
        entity.setAttribute(LSDAttribute.DESCRIPTION, description);
    }

    public final LiquidRequestDetailLevel getDetail() {
        return LiquidRequestDetailLevel.valueOf(entity.getAttribute(LSDAttribute.QUERY_DETAIL, LiquidRequestDetailLevel.NORMAL
                                                                                                                       .name()));
    }

    public final void setDetail(@Nonnull final LiquidRequestDetailLevel detail) {
        entity.setAttribute(LSDAttribute.QUERY_DETAIL, detail.name());
    }

    @Nonnull
    public final LiquidUUID getFrom() {
        return entity.getUUIDAttribute(LSDAttribute.FROM);
    }

    public final void setFrom(@Nullable final LiquidUUID from) {
        if (from != null) {
            entity.setAttribute(LSDAttribute.FROM, from);
        }
    }

    public final boolean hasFrom() {
        return entity.hasAttribute(LSDAttribute.FROM);
    }

    public final boolean hasTo() {
        return entity.hasAttribute(LSDAttribute.TO);
    }

    @Nonnull
    public final Integer getHeight() {
        return entity.getIntegerAttribute(LSDAttribute.VIEW_WIDTH);
    }

    public final void setHeight(final Integer height) {
        entity.setAttribute(LSDAttribute.VIEW_HEIGHT, height);
    }

    public boolean getInternal() {
        return isInternal();
    }

    public boolean isInternal() {
        return entity.getBooleanAttribute(LSDAttribute.INTERNAL_REQUEST, false);
    }

    public final void setInternal(final boolean internal) {
        entity.setAttribute(LSDAttribute.INTERNAL_REQUEST, internal);
    }

    public final int getMax() {
        return entity.getIntegerAttribute(LSDAttribute.QUERY_MAX, 60);
    }

    public final void setMax(final int max) {
        entity.setAttribute(LSDAttribute.QUERY_MAX, max);
    }

    @Nonnull
    public final String getName() {
        return entity.getAttribute(LSDAttribute.NAME);
    }

    public final void setName(final String name) {
        entity.setAttribute(LSDAttribute.NAME, name);
    }

    @Nonnull
    public final LiquidUUID getObjectUUID() {
        return entity.getUUIDAttribute(LSDAttribute.REQUEST_OBJECT_UUID);
    }

    public final void setObjectUUID(@Nullable final LiquidUUID objectUUID) {
        if (objectUUID != null) {
            entity.setAttribute(LSDAttribute.REQUEST_OBJECT_UUID, objectUUID);
        }
    }

    @Nonnull
    public final ChildSortOrder getOrder() {
        final String attribute = entity.getAttribute(LSDAttribute.SORT_BY);
        return ChildSortOrder.valueOf(attribute);
    }

    public final void setOrder(@Nullable final ChildSortOrder order) {
        entity.setAttribute(LSDAttribute.SORT_BY, order != null ? order.name() : ChildSortOrder.NONE.name());
    }

    @Nonnull
    public final LiquidURI getParent() {
        return entity.getURIAttribute(LSDAttribute.REQUEST_PARENT_URI);
    }

    public final void setParent(@Nullable final LiquidURI parent) {
        if (parent != null) {
            entity.setAttribute(LSDAttribute.REQUEST_PARENT_URI, parent);
        }
    }

    @Nonnull
    public final String getPassword() {
        return entity.getAttribute(LSDAttribute.PLAIN_PASSWORD);
    }

    public final void setPassword(final String password) {
        entity.setAttribute(LSDAttribute.PLAIN_PASSWORD, password);
    }

    @Nullable
    public final LiquidPermissionChangeType getPermission() {
        if (entity.hasAttribute(LSDAttribute.PERMISSION_CHANGE)) {
            //noinspection ConstantConditions
            return LiquidPermissionChangeType.valueOf(entity.getAttribute(LSDAttribute.PERMISSION_CHANGE));
        }
        else {
            return null;
        }
    }

    public final void setPermission(@Nullable final LiquidPermissionChangeType permission) {
        if (permission != null) {
            entity.setAttribute(LSDAttribute.PERMISSION_CHANGE, permission.name());
        }
    }

    @Nonnull
    public final LiquidURI getPoolURI() {
        final LiquidURI uri = getUri();
        return uri.getWithoutFragment();
    }

    @Nonnull
    public final LiquidUUID getPoolUUID() {
        return entity.getUUIDAttribute(LSDAttribute.REQUEST_POOL_UUID);
    }

    public final void setPoolUUID(@Nullable final LiquidUUID poolUUID) {
        if (poolUUID != null) {
            entity.setAttribute(LSDAttribute.REQUEST_POOL_UUID, poolUUID);
        }
    }

    public final boolean hasPoolUUID() {
        return entity.hasAttribute(LSDAttribute.REQUEST_POOL_UUID);
    }

    @Nonnull
    public final LiquidURI getPreviousPool() {
        return entity.getURIAttribute(LSDAttribute.REQUEST_PREVIOUS_POOL_URI);
    }

    public final void setPreviousPool(@Nullable final LiquidURI previousPool) {
        if (previousPool != null) {
            entity.setAttribute(LSDAttribute.REQUEST_PREVIOUS_POOL_URI, previousPool);
        }
    }

    public final boolean hasPreviousPool() {
        return entity.hasAttribute(LSDAttribute.REQUEST_PREVIOUS_POOL_URI);
    }

    @Nonnull
    public final QueryType getQueryType() {
        return QueryType.valueOf(entity.getAttribute(LSDAttribute.BOARD_QUERY_TYPE));
    }

    public final void setQueryType(@Nonnull final QueryType queryType) {
        entity.setAttribute(LSDAttribute.BOARD_QUERY_TYPE, queryType.name());
    }

    @Nonnull
    public final String getRecipient() {
        return entity.getAttribute(LSDAttribute.RECIPIENT);
    }

    public final void setRecipient(final String recipient) {
        entity.setAttribute(LSDAttribute.RECIPIENT, recipient);
    }

    @Nonnull
    public final String getSearchText() {
        return entity.getAttribute(LSDAttribute.TEXT_BRIEF);
    }

    public final void setSearchText(final String searchText) {
        entity.setAttribute(LSDAttribute.TEXT_BRIEF, searchText);
    }

    public final long getSince() {
        return entity.getLongAttribute(LSDAttribute.SINCE);
    }

    public final void setSince(final long since) {
        entity.setAttribute(LSDAttribute.SINCE, since);
    }

    @Nonnull
    public final Integer getStart() {
        return entity.getIntegerAttribute(LSDAttribute.QUERY_START_OFFSET, 0);
    }

    public final void setStart(final int start) {
        entity.setAttribute(LSDAttribute.QUERY_START_OFFSET, start);
    }

    @Nonnull
    public final LiquidUUID getTarget() {
        return entity.getUUIDAttribute(LSDAttribute.REQUEST_UUID);
    }

    public final void setTarget(@Nullable final LiquidUUID target) {
        if (target != null && target.toString() != null) {
            entity.setAttribute(LSDAttribute.REQUEST_UUID, target);
        }
    }

    @Nonnull
    public final String getTitle() {
        return entity.getAttribute(LSDAttribute.TITLE);
    }

    public final void setTitle(final String title) {
        entity.setAttribute(LSDAttribute.TITLE, title);
    }

    @Nonnull
    public final LiquidUUID getTo() {
        return entity.getUUIDAttribute(LSDAttribute.TO);
    }

    public final void setTo(@Nullable final LiquidUUID to) {
        if (to != null) {
            entity.setAttribute(LSDAttribute.TO, to);
        }
    }

    @Nonnull
    public final LSDDictionaryTypes getType() {
        return LSDDictionaryTypes.valueOf(LSDDictionaryTypes.getNameForValue(entity.getAttribute(LSDAttribute.REQUEST_RESOURCE_TYPE)));
    }

    public final void setType(@Nonnull final LSDDictionaryTypes type) {
        entity.setAttribute(LSDAttribute.REQUEST_RESOURCE_TYPE, type.asString());
    }

    @Nonnull
    public final Integer getWidth() {
        return entity.getIntegerAttribute(LSDAttribute.VIEW_WIDTH);
    }

    public final void setWidth(final Integer width) {
        entity.setAttribute(LSDAttribute.VIEW_WIDTH, width);
    }

    public boolean hasWidth() {
        return entity.hasAttribute(LSDAttribute.VIEW_WIDTH);
    }

    public boolean hasHeight() {
        return entity.hasAttribute(LSDAttribute.VIEW_HEIGHT);
    }

    @Nonnull
    public final Double getX() {
        return entity.getDoubleAttribute(LSDAttribute.VIEW_X);
    }

    public final void setX(final Double x) {
        entity.setAttribute(LSDAttribute.VIEW_X, x);
    }

    @Nonnull
    public final Double getY() {
        return entity.getDoubleAttribute(LSDAttribute.VIEW_Y);
    }

    public final void setY(final Double y) {
        entity.setAttribute(LSDAttribute.VIEW_Y, y);
    }

    @Nonnull
    public final Double getZ() {
        return entity.getDoubleAttribute(LSDAttribute.VIEW_Z);
    }

    public final void setZ(final Double z) {
        entity.setAttribute(LSDAttribute.VIEW_Z, z);
    }

    public boolean hasPassword() {
        return entity.hasAttribute(LSDAttribute.PLAIN_PASSWORD);
    }

    public boolean hasTarget() {
        return entity.hasAttribute(LSDAttribute.REQUEST_UUID);
    }

    public final boolean isClaim() {
        return entity.getBooleanAttribute(LSDAttribute.IS_CLAIM, false);
    }

    public final void setClaim(final boolean claim) {
        entity.setAttribute(LSDAttribute.IS_CLAIM, claim);
    }

    public final boolean isContents() {
        return entity.getBooleanAttribute(LSDAttribute.CONTENTS, true);
    }

    public final void setContents(final boolean contents) {
        entity.setAttribute(LSDAttribute.CONTENTS, contents);
    }

    public final boolean isFollow() {
        return entity.getBooleanAttribute(LSDAttribute.FOLLOW, false);
    }

    public final void setFollow(final boolean follow) {
        entity.setAttribute(LSDAttribute.FOLLOW, follow);
    }

    public final boolean isHistorical() {
        return entity.getBooleanAttribute(LSDAttribute.HISTORICAL_REQUEST, false);
    }

    public final void setHistorical(final boolean historical) {
        entity.setAttribute(LSDAttribute.HISTORICAL_REQUEST, historical);
    }

    public final boolean isListed() {
        return entity.getBooleanAttribute(LSDAttribute.LISTED, false);
    }

    public final void setListed(final boolean listed) {
        entity.setAttribute(LSDAttribute.LISTED, listed);
    }

    public final boolean isMe() {
        return entity.getBooleanAttribute(LSDAttribute.IS_ME, false);
    }

    public final void setMe(final boolean me) {
        entity.setAttribute(LSDAttribute.IS_ME, me);
    }

    public final boolean isOrCreate() {
        return entity.getBooleanAttribute(LSDAttribute.CREATE_OR_UPDATE, false);
    }

    public final void setOrCreate(final boolean orCreate) {
        entity.setAttribute(LSDAttribute.CREATE_OR_UPDATE, orCreate);
    }

    public final boolean isSelected() {
        return entity.getBooleanAttribute(LSDAttribute.SELECTED);
    }

    public final void setSelected(final boolean selected) {
        entity.setAttribute(LSDAttribute.SELECTED, selected);
    }

    public final boolean isUnlink() {
        return entity.getBooleanAttribute(LSDAttribute.UNLINK, false);
    }

    public final void setUnlink(final boolean unlink) {
        entity.setAttribute(LSDAttribute.UNLINK, unlink);
    }

    public final void setPoolType(@Nonnull final LSDType poolType) {
        entity.setAttribute(LSDAttribute.REQUEST_RESOURCE_TYPE, poolType.asString());
    }

    @Nonnull
    public LSDTransferEntity getEntity() {
        return entity;
    }

    public void setEntity(@Nonnull final LSDTransferEntity entity) {
        if (!entity.hasAttribute(LSDAttribute.TYPE)) {
            throw new IllegalArgumentException("Entities must always have a type. Attempted to set an entity on a request of type "
                                               + getClass().getName()
                                               + ", the entity was: "
                                               + entity.asFreeText());
        }
        this.entity = entity;
    }

    @Override @Nonnull
    public String toString() {
        return getClass() + " " + entity.toString();
    }
}
