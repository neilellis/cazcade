/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author neilelliz@cazcade.com
 */
public abstract class AbstractRequest implements LiquidRequest {


    public enum QueryType {
        MY, PROFILE, RECENT, HISTORY, POPULAR
    }

    public enum Category {
        KEYS, TYPES
    }

    @Nonnull
    private SimpleEntity<? extends TransferEntity> entity = SimpleEntity.createNewEntity(new TypeDefImpl(Types.T_REQUEST, getClass()
            .getName()
            .substring(getClass().getName().lastIndexOf('.') + 1)));

    protected AbstractRequest() {
    }

    protected AbstractRequest(@Nonnull final TransferEntity<? extends TransferEntity> entity) {
        this.entity = (SimpleEntity<? extends TransferEntity>) entity.$();
    }

    @Override
    public void adjustTimeStampForServerTime() {
        if (hasRequestEntity()) {
            entity.$(Dictionary.REQUEST_ENTITY, Dictionary.UPDATED, String.valueOf(System.currentTimeMillis()));
        }
    }

    @Override
    public long cacheExpiry() {
        return -1;
    }

    @Override @Nullable
    public String notificationSession() {
        final SessionIdentifier sessionIdentifier = session();
        if (sessionIdentifier.session() == null) {
            return null;
        } else {
            return sessionIdentifier.session().toString();
        }
    }

    @Override @Nonnull
    public final SessionIdentifier session() {
        final SessionIdentifier identifier = getLiquidSessionIdentifierInternal();
        if (identifier == null) {
            return SessionIdentifier.ANON;
        }
        return identifier;
    }

    public boolean isAsyncRequest() {
        final Boolean rpc = getRpc();
        if (rpc != null) {
            return !rpc;
        } else {
            return isMutationRequest();
        }
    }

    @Override
    public boolean isSecureOperation() {
        return false;
    }

    @Nullable
    public final Boolean getRpc() {
        if (entity.has(Dictionary.REQUEST_EXPLICIT_RPC)) {
            return entity.$bool(Dictionary.REQUEST_EXPLICIT_RPC);
        } else {
            return null;
        }
    }

    public final void rpc(final Boolean rpc) {
        entity.$(Dictionary.REQUEST_EXPLICIT_RPC, rpc);
    }

    public final void session(@Nonnull final SessionIdentifier sessionId) {
        //noinspection ConstantConditions
        if (sessionId == null) {
            throw new IllegalArgumentException("Cannot set a null SessionIdentifier.");
        }
        if (sessionId.equals(getLiquidSessionIdentifierInternal())) {
            return;
        }
        if (getLiquidSessionIdentifierInternal() != null
            && !session().alias().equals(sessionId.alias())
            && !getLiquidSessionIdentifierInternal().anon()) {
            throw new IllegalStateException("Cannot change the alias of the session on a request, attempt was from " +
                                            getLiquidSessionIdentifierInternal() +
                                            " to " +
                                            sessionId);
        }
        entity.$(Dictionary.REQUEST_SESSION_ID, sessionId.toString());
    }

    public boolean shouldNotify() {
        return isAsyncRequest();
    }

    @Override
    public boolean shouldSendProvisional() {
        return false;
    }

    public boolean hasRequestEntity() {
        return entity.hasChild(Dictionary.REQUEST_ENTITY);
    }

    @Override public boolean hasId() {
        return entity.has(Dictionary.ID);
    }

    @Nullable
    private SessionIdentifier getLiquidSessionIdentifierInternal() {
        return SessionIdentifier.fromString(entity.$raw(Dictionary.REQUEST_SESSION_ID));
    }

    @Nonnull @Override
    public SerializedRequest asSerializedRequest() {
        return new SerializedRequest(requestType(), entity);
    }

    @Nonnull
    public abstract LiquidMessage copy();

    public Collection<LURI> affectedEntities() {
        return getStandardAffectedEntitiesInternalPlus();
    }

    @Nonnull @Override
    public String cacheIdentifier() {
        return id().toString() + ":" + state() + ":" + origin();
    }

    @Override @Nonnull
    public final CachingScope cachingScope() {
        return CachingScope.valueOf(entity.default$(Dictionary.REQUEST_CACHING_SCOPE, CachingScope.NONE.name()));
    }

    @Nonnull @Override
    public String deduplicationIdentifier() {
        return id().toString() + ":" + state() + ":" + origin();
    }

    @Nonnull
    public final LiquidUUID id() {
        return entity.id();
    }

    @Nonnull
    public LiquidMessageType messageType() {
        return LiquidMessageType.REQUEST;
    }

    @Nonnull
    public final Origin origin() {
        return Origin.valueOf(entity.default$(Dictionary.REQUEST_ORIGIN, Origin.UNASSIGNED.name()));
    }

    @Nonnull
    public final TransferEntity request() {
        return (TransferEntity) entity.child(Dictionary.REQUEST_ENTITY, true);
    }

    @Nonnull
    public final TransferEntity response() {
        if (entity.hasChild(Dictionary.REQUEST_RESULT)) {
            return (TransferEntity) entity.child(Dictionary.REQUEST_RESULT, true);
        } else {
            throw new IllegalStateException("Response expected but none set.");
        }
    }

    @Nullable @Override
    public final Entity getResponseOrRequestEntity() {
        if (hasResponse()) {
            return response();
        } else {
            return request();
        }
    }

    @Nonnull
    public final MessageState state() {
        return MessageState.valueOf(entity.default$(Dictionary.REQUEST_STATE, MessageState.INITIAL.name()));
    }

    @Override
    public boolean isCacheable() {
        return false;
    }

    public final void state(@Nonnull final MessageState state) {
        entity.$(Dictionary.REQUEST_STATE, state.name());
    }

    public boolean hasResponse() {
        return entity.hasChild(Dictionary.REQUEST_RESULT);
    }

    public final void response(final TransferEntity response) {
        if (!response.serializable()) {
            throw new IllegalArgumentException("Oops, you've tried to pass in a raw persistent entity!");
        }
        entity.child(Dictionary.REQUEST_RESULT, response, false);
    }

    public final <T extends TransferEntity> void setRequestEntity(final T requestEntity) {
        entity.child(Dictionary.REQUEST_ENTITY, requestEntity, false);
    }

    public final void origin(@Nonnull final Origin origin) {
        entity.$(Dictionary.REQUEST_ORIGIN, origin.name());
    }

    public final void id(@Nullable final LiquidUUID id) {
        if (id != null && id.toString() != null) {
            entity.id(id.toString());
        } else {
            entity.removeCompletely(Dictionary.ID);
        }
    }

    @Override
    public final void setCachingScope(@Nonnull final CachingScope cachingScope) {
        entity.$(Dictionary.REQUEST_CACHING_SCOPE, cachingScope.name());
    }

    @Nonnull
    protected final Set<LURI> getStandardAffectedEntitiesInternalPlus(final LURI... uris) {
        final Set<LURI> result = new HashSet<LURI>();
        if (hasRequestEntity() && request().hasURI()) {
            final TransferEntity requestEntity = request();
            result.addAll(getAffectedEntitiesInternal(requestEntity.uri()));
        }
        if (hasResponse()) {
            final TransferEntity response = response();
            if (response.hasURI()) {
                result.addAll(getAffectedEntitiesInternal(response.uri()));
            }
        }
        if (hasUri()) {
            result.addAll(getAffectedEntitiesInternal(uri()));
        }
        result.addAll(getAffectedEntitiesInternal(uris));
        return result;
    }

    @Nonnull
    protected final Set<LURI> getAffectedEntitiesInternal(@Nonnull final LURI... uris) {
        final Set<LURI> result = new HashSet<LURI>();
        for (final LURI uri : uris) {
            if (uri != null) {
                result.addAll(uriSplitToParent(uri));
            }
        }
        return result;
    }

    @Nonnull
    private List<LURI> uriSplitToParent(@Nonnull final LURI theURI) {
        if (theURI.hasFragment()) {
            return Arrays.asList(theURI, theURI.withoutFragment());
        } else {
            return Arrays.asList(theURI);
        }
    }

    @Nonnull
    public final LURI uri() {
        return entity.getURIAttribute(Dictionary.REQUEST_URI);
    }

    public final void setUri(@Nullable final LURI uri) {
        if (uri != null) {
            entity.$(Dictionary.REQUEST_URI, uri);
        }
    }

    public final boolean hasUri() {
        return entity.has(Dictionary.REQUEST_URI);
    }

    @Nonnull
    public final LURI alias() {
        return session().alias();
    }

    public final void setAlias(@Nullable final LURI alias) {
        if (alias != null) {
            entity.$(Dictionary.REQUEST_ALIAS, alias.asString());
        }
    }

    @Nonnull
    public final Double angle() {
        return entity.$d(Dictionary.VIEW_ROTATE_XY);
    }

    public final void setAngle(final Double angle) {
        entity.$(Dictionary.VIEW_ROTATE_XY, angle);
    }

    public boolean hasAngle() {
        return entity.has(Dictionary.VIEW_ROTATE_XY);
    }

    @Nonnull
    public final LURI getAuthor() {
        return entity.getURIAttribute(Dictionary.REQUEST_AUTHOR);
    }

    public final void setAuthor(@Nullable final LURI author) {
        if (author != null) {
            entity.$(Dictionary.REQUEST_AUTHOR, author);
        }
    }

    public final boolean hasAuthor() {
        return entity.has(Dictionary.REQUEST_AUTHOR);
    }

    @Nonnull
    public final Category getCategory() {
        return Category.valueOf(entity.$(Dictionary.DICTIONARY_CATEGORY));
    }

    public final void setCategory(@Nonnull final Category category) {
        entity.$(Dictionary.DICTIONARY_CATEGORY, category.name());
    }

    @Nonnull
    public String getChangePasswordSecurityHash() {
        return entity.$(Dictionary.SECURITY_CONFIRMATION_HASH);
    }

    public void setChangePasswordSecurityHash(final String hash) {
        entity.$(Dictionary.SECURITY_CONFIRMATION_HASH, hash);
    }

    public boolean hasChangePasswordSecurityHash() {
        return entity.has(Dictionary.SECURITY_CONFIRMATION_HASH);
    }

    @Nonnull
    public final ClientApplicationIdentifier getClient() {
        return ClientApplicationIdentifier.valueOf(entity.$(Dictionary.CLIENT_APPLICATION_IDENTIFIER));
    }

    public final void setClient(@Nonnull final ClientApplicationIdentifier client) {
        entity.$(Dictionary.CLIENT_APPLICATION_IDENTIFIER, client.toString());
    }

    @Nonnull
    public final String description() {
        return entity.$(Dictionary.DESCRIPTION);
    }

    public final void setDescription(final String description) {
        entity.$(Dictionary.DESCRIPTION, description);
    }

    public final RequestDetailLevel detail() {
        return RequestDetailLevel.valueOf(entity.default$(Dictionary.QUERY_DETAIL, RequestDetailLevel.NORMAL.name()));
    }

    public final void setDetail(@Nonnull final RequestDetailLevel detail) {
        entity.$(Dictionary.QUERY_DETAIL, detail.name());
    }

    @Nonnull
    public final LiquidUUID getFrom() {
        return entity.$uuid(Dictionary.FROM);
    }

    public final void setFrom(@Nullable final LiquidUUID from) {
        if (from != null) {
            entity.$(Dictionary.FROM, from);
        }
    }

    public final boolean hasFrom() {
        return entity.has(Dictionary.FROM);
    }

    public final boolean hasTo() {
        return entity.has(Dictionary.TO);
    }

    @Nonnull
    public final Integer height() {
        return entity.$i(Dictionary.VIEW_WIDTH);
    }

    public final void setHeight(final Integer height) {
        entity.$(Dictionary.VIEW_HEIGHT, height);
    }

    public boolean getInternal() {
        return internal();
    }

    public boolean internal() {
        return entity.default$bool(Dictionary.INTERNAL_REQUEST, false);
    }

    public final void setInternal(final boolean internal) {
        entity.$(Dictionary.INTERNAL_REQUEST, internal);
    }

    public final int getMax() {
        return entity.default$i(Dictionary.QUERY_MAX, 60);
    }

    public final void setMax(final int max) {
        entity.$(Dictionary.QUERY_MAX, max);
    }

    @Nonnull
    public final String getName() {
        return entity.$(Dictionary.NAME);
    }

    public final void setName(final String name) {
        entity.$(Dictionary.NAME, name);
    }

    @Nonnull
    public final LiquidUUID getObjectUUID() {
        return entity.$uuid(Dictionary.REQUEST_OBJECT_UUID);
    }

    public final void setObjectUUID(@Nullable final LiquidUUID objectUUID) {
        if (objectUUID != null) {
            entity.$(Dictionary.REQUEST_OBJECT_UUID, objectUUID);
        }
    }

    @Nonnull
    public final ChildSortOrder getOrder() {
        final String attribute = entity.$(Dictionary.SORT_BY);
        return ChildSortOrder.valueOf(attribute);
    }

    public final void setOrder(@Nullable final ChildSortOrder order) {
        entity.$(Dictionary.SORT_BY, order != null ? order.name() : ChildSortOrder.NONE.name());
    }

    @Nonnull
    public final LURI getParent() {
        return entity.getURIAttribute(Dictionary.REQUEST_PARENT_URI);
    }

    public final void setParent(@Nullable final LURI parent) {
        if (parent != null) {
            entity.$(Dictionary.REQUEST_PARENT_URI, parent);
        }
    }

    @Nonnull
    public final String getPassword() {
        return entity.$(Dictionary.PLAIN_PASSWORD);
    }

    public final void setPassword(final String password) {
        entity.$(Dictionary.PLAIN_PASSWORD, password);
    }

    @Nullable
    public final PermissionChangeType permission() {
        if (entity.has(Dictionary.PERMISSION_CHANGE)) {
            //noinspection ConstantConditions
            return PermissionChangeType.valueOf(entity.$(Dictionary.PERMISSION_CHANGE));
        } else {
            return null;
        }
    }

    public final void setPermission(@Nullable final PermissionChangeType permission) {
        if (permission != null) {
            entity.$(Dictionary.PERMISSION_CHANGE, permission.name());
        }
    }

    @Nonnull
    public final LURI getPoolURI() {
        final LURI uri = uri();
        return uri.withoutFragment();
    }

    @Nonnull
    public final LiquidUUID getPoolUUID() {
        return entity.$uuid(Dictionary.REQUEST_POOL_UUID);
    }

    public final void setPoolUUID(@Nullable final LiquidUUID poolUUID) {
        if (poolUUID != null) {
            entity.$(Dictionary.REQUEST_POOL_UUID, poolUUID);
        }
    }

    public final boolean hasPoolUUID() {
        return entity.has(Dictionary.REQUEST_POOL_UUID);
    }

    @Nonnull
    public final LURI getPreviousPool() {
        return entity.getURIAttribute(Dictionary.REQUEST_PREVIOUS_POOL_URI);
    }

    public final void setPreviousPool(@Nullable final LURI previousPool) {
        if (previousPool != null) {
            entity.$(Dictionary.REQUEST_PREVIOUS_POOL_URI, previousPool);
        }
    }

    public final boolean hasPreviousPool() {
        return entity.has(Dictionary.REQUEST_PREVIOUS_POOL_URI);
    }

    @Nonnull
    public final QueryType getQueryType() {
        return QueryType.valueOf(entity.$(Dictionary.BOARD_QUERY_TYPE));
    }

    public final void setQueryType(@Nonnull final QueryType queryType) {
        entity.$(Dictionary.BOARD_QUERY_TYPE, queryType.name());
    }

    @Nonnull
    public final String getRecipient() {
        return entity.$(Dictionary.RECIPIENT);
    }

    public final void setRecipient(final String recipient) {
        entity.$(Dictionary.RECIPIENT, recipient);
    }

    @Nonnull
    public final String getSearchText() {
        return entity.$(Dictionary.TEXT_BRIEF);
    }

    public final void setSearchText(final String searchText) {
        entity.$(Dictionary.TEXT_BRIEF, searchText);
    }

    public final long getSince() {
        return entity.$l(Dictionary.SINCE);
    }

    public final void setSince(final long since) {
        entity.$(Dictionary.SINCE, since);
    }

    @Nonnull
    public final Integer getStart() {
        return entity.default$i(Dictionary.QUERY_START_OFFSET, 0);
    }

    public final void setStart(final int start) {
        entity.$(Dictionary.QUERY_START_OFFSET, start);
    }

    @Nonnull
    public final LiquidUUID getTarget() {
        return entity.$uuid(Dictionary.REQUEST_UUID);
    }

    public final void setTarget(@Nullable final LiquidUUID target) {
        if (target != null && target.toString() != null) {
            entity.$(Dictionary.REQUEST_UUID, target);
        }
    }

    @Nonnull
    public final String getTitle() {
        return entity.$(Dictionary.TITLE);
    }

    public final void setTitle(final String title) {
        entity.$(Dictionary.TITLE, title);
    }

    @Nonnull
    public final LiquidUUID getTo() {
        return entity.$uuid(Dictionary.TO);
    }

    public final void setTo(@Nullable final LiquidUUID to) {
        if (to != null) {
            entity.$(Dictionary.TO, to);
        }
    }

    @Nonnull
    public final Types type() {
        return Types.valueOf(Types.getNameForValue(entity.$(Dictionary.REQUEST_RESOURCE_TYPE)));
    }

    public final void setType(@Nonnull final Types type) {
        entity.$(Dictionary.REQUEST_RESOURCE_TYPE, type.asString());
    }

    @Nonnull
    public final Integer width() {
        return entity.$i(Dictionary.VIEW_WIDTH);
    }

    public final void setWidth(final Integer width) {
        entity.$(Dictionary.VIEW_WIDTH, width);
    }

    public boolean hasWidth() {
        return entity.has(Dictionary.VIEW_WIDTH);
    }

    public boolean hasHeight() {
        return entity.has(Dictionary.VIEW_HEIGHT);
    }

    @Nonnull
    public final Double x() {
        return entity.$d(Dictionary.VIEW_X);
    }

    public final void setX(final Double x) {
        entity.$(Dictionary.VIEW_X, x);
    }

    @Nonnull
    public final Double y() {
        return entity.$d(Dictionary.VIEW_Y);
    }

    public final void setY(final Double y) {
        entity.$(Dictionary.VIEW_Y, y);
    }

    @Nonnull
    public final Double getZ() {
        return entity.$d(Dictionary.VIEW_Z);
    }

    public final void setZ(final Double z) {
        entity.$(Dictionary.VIEW_Z, z);
    }

    public boolean hasPassword() {
        return entity.has(Dictionary.PLAIN_PASSWORD);
    }

    public boolean hasTarget() {
        return entity.has(Dictionary.REQUEST_UUID);
    }

    public final boolean isClaim() {
        return entity.default$bool(Dictionary.IS_CLAIM, false);
    }

    public final void setClaim(final boolean claim) {
        entity.$(Dictionary.IS_CLAIM, claim);
    }

    public final boolean isContents() {
        return entity.default$bool(Dictionary.CONTENTS, true);
    }

    public final void setContents(final boolean contents) {
        entity.$(Dictionary.CONTENTS, contents);
    }

    public final boolean isFollow() {
        return entity.default$bool(Dictionary.FOLLOW, false);
    }

    public final void setFollow(final boolean follow) {
        entity.$(Dictionary.FOLLOW, follow);
    }

    public final boolean historical() {
        return entity.default$bool(Dictionary.HISTORICAL_REQUEST, false);
    }

    public final void setHistorical(final boolean historical) {
        entity.$(Dictionary.HISTORICAL_REQUEST, historical);
    }

    public final boolean listed() {
        return entity.default$bool(Dictionary.LISTED, false);
    }

    public final void setListed(final boolean listed) {
        entity.$(Dictionary.LISTED, listed);
    }

    public final boolean isMe() {
        return entity.default$bool(Dictionary.IS_ME, false);
    }

    public final void setMe(final boolean me) {
        entity.$(Dictionary.IS_ME, me);
    }

    public final boolean isOrCreate() {
        return entity.default$bool(Dictionary.CREATE_OR_UPDATE, false);
    }

    public final void setOrCreate(final boolean orCreate) {
        entity.$(Dictionary.CREATE_OR_UPDATE, orCreate);
    }

    public final boolean isSelected() {
        return entity.$bool(Dictionary.SELECTED);
    }

    public final void setSelected(final boolean selected) {
        entity.$(Dictionary.SELECTED, selected);
    }

    public final boolean isUnlink() {
        return entity.default$bool(Dictionary.UNLINK, false);
    }

    public final void setUnlink(final boolean unlink) {
        entity.$(Dictionary.UNLINK, unlink);
    }

    public final void setPoolType(@Nonnull final Type poolType) {
        entity.$(Dictionary.REQUEST_RESOURCE_TYPE, poolType.asString());
    }

    @Nonnull
    public TransferEntity getEntity() {
        return entity;
    }

    public void setEntity(@Nonnull final TransferEntity<? extends TransferEntity> entity) {
        if (!entity.has(Dictionary.TYPE)) {
            throw new IllegalArgumentException("Entities must always have a type. Attempted to set an entity on a request of type "
                                               + getClass().getName()
                                               + ", the entity was: "
                                               + entity.asFreeText());
        }
        this.entity = (SimpleEntity<? extends TransferEntity>) entity;
    }

    @Override @Nonnull
    public String toString() {
        return getClass() + " " + entity.asDebugText();
    }

    public String imageUrl() {
        return entity.$(Dictionary.IMAGE_URL);
    }

    public boolean hasImageUrl() {
        return entity.has(Dictionary.IMAGE_URL);
    }

    public void setImageUrl(final String url) {
        entity.$(Dictionary.IMAGE_URL, url);
    }

    public boolean hasDescription() {
        return entity.has(Dictionary.DESCRIPTION);
    }
}
