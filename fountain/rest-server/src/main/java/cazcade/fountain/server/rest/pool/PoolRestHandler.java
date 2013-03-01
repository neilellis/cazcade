/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.server.rest.pool;

import cazcade.fountain.datastore.api.AuthorizationService;
import cazcade.fountain.datastore.api.FountainDataStoreFacade;
import cazcade.fountain.server.rest.AbstractRestHandler;
import cazcade.fountain.server.rest.RestContext;
import cazcade.fountain.server.rest.RestHandlerException;
import cazcade.fountain.server.rest.message.CommentRestHandler;
import cazcade.liquid.api.LURI;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.*;
import cazcade.liquid.api.request.*;
import cazcade.liquid.impl.UUIDFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author Neil Ellis
 */

public class PoolRestHandler extends AbstractRestHandler {
    private EntityFactory entityFactory;

    private FountainDataStoreFacade dataStoreFacade;
    private AuthorizationService    authorizationService;

    /**
     * Deprecated, see {@link CommentRestHandler}
     */
    @Nonnull
    public LiquidMessage chat(final LiquidUUID pool, @Nonnull final Map<String, String[]> parameters) throws URISyntaxException {
        final SessionIdentifier username = RestContext.getContext().getCredentials();
        checkForSingleValueParams(parameters, "text", "image");
        final String text = parameters.get("text")[0];
        final String image = parameters.get("image")[0];
        final TransferEntity message = SimpleEntity.createNewTransferEntity(Types.T_CHAT, UUIDFactory.randomUUID());
        message.$(Dictionary.TEXT_EXTENDED, text);
        message.$(Dictionary.IMAGE_URL, image);
        message.$(Dictionary.ICON_URL, image);
        return dataStoreFacade.process(new AddCommentRequest(username, pool, message));
    }

    /**
     * Copying an object or pool creates an independent version in the new location.
     *
     * @param pool
     * @param object
     * @param parameters
     * @return
     * @throws URISyntaxException //todo: support URIS
     */
    @Nonnull
    public LiquidMessage copy(final LiquidUUID pool, final LiquidUUID object, @Nonnull final Map<String, String[]> parameters) throws URISyntaxException {
        checkForSingleValueParams(parameters, "to");
        if (parameters.containsKey("from")) {
            final LiquidUUID from = LiquidUUID.fromString(parameters.get("from")[0]);
            final LiquidUUID to = LiquidUUID.fromString(parameters.get("to")[0]);
            return dataStoreFacade.process(new LinkPoolObjectRequest(RestContext.getContext().getCredentials(), object, from, to));
        } else {
            final LiquidUUID to = LiquidUUID.fromString(parameters.get("to")[0]);
            return dataStoreFacade.process(new LinkPoolObjectRequest(RestContext.getContext().getCredentials(), object, to));
        }
    }

    @Nonnull
    public LiquidMessage create(final LiquidUUID poolId, @Nonnull final TransferEntity entity, @Nonnull final Map<String, String[]> parameters) throws URISyntaxException {
        checkForSingleValueParams(parameters, "author");
        final String author = parameters.get("author")[0];
        if (entity.hasId() && entity.id().equals(poolId)) {
            return update(poolId, entity);
        } else {
            return dataStoreFacade.process(new CreatePoolObjectRequest(RestContext.getContext()
                                                                                  .getCredentials(), poolId, entity, new LURI(author)));
        }
    }

    @Nonnull
    public LiquidMessage update(final LiquidUUID poolId, final TransferEntity lsdEntity) {
        return dataStoreFacade.process(new UpdatePoolRequest(RestContext.getContext().getCredentials(), poolId, lsdEntity));
    }

    @Nonnull
    public LiquidMessage create(final LiquidUUID poolId, final LiquidUUID objectId, final TransferEntity entity) throws URISyntaxException {
        return update(poolId, objectId, entity);
    }

    @Nonnull
    public LiquidMessage update(final LiquidUUID poolId, final LiquidUUID objectId, final TransferEntity lsdEntity) {
        return dataStoreFacade.process(new UpdatePoolObjectRequest(RestContext.getContext()
                                                                              .getCredentials(), poolId, objectId, lsdEntity));
    }

    @Nonnull
    public LiquidMessage create(@Nonnull final Map<String, String[]> parameters) throws URISyntaxException {
        checkForSingleValueParams(parameters, "parent", "name", "title", "description", "x", "y");
        final String parent = parameters.get("parent")[0];
        final String name = parameters.get("name")[0];
        final String title = parameters.get("title")[0];
        final String description = parameters.get("description")[0];
        final double x = Double.parseDouble(parameters.get("x")[0]);
        final double y = Double.parseDouble(parameters.get("y")[0]);
        final SessionIdentifier identity = RestContext.getContext().getCredentials();
        return dataStoreFacade.process(new CreatePoolRequest(identity, new LURI(parent), name, title, description, x, y));
    }

    @Nonnull
    public LiquidMessage createPUT(final TransferEntity entity, @Nonnull final Map<String, String[]> parameters) throws URISyntaxException {
        checkForSingleValueParams(parameters, "uri");
        final String uri = parameters.get("uri")[0];
        return dataStoreFacade.process(new CreatePoolObjectRequest(RestContext.getContext()
                                                                              .getCredentials(), new LURI(uri), entity));
    }

    @Nonnull
    public LiquidMessage delete(final LiquidUUID pool, final LiquidUUID object) throws URISyntaxException {
        throw new UnsupportedOperationException("Use /delete?uri=...");
    }

    @Nonnull
    public LiquidMessage delete(@Nonnull final Map<String, String[]> parameters) throws URISyntaxException {
        checkForSingleValueParams(parameters, "uri");
        final String uri = parameters.get("uri")[0];
        final SessionIdentifier identity = RestContext.getContext().getCredentials();
        return dataStoreFacade.process(new DeletePoolRequest(RestContext.getContext().getCredentials(), new LURI(uri)));
    }

    @Nonnull
    public LiquidMessage delete(final LiquidUUID pool) throws URISyntaxException {
        throw new UnsupportedOperationException("Use /delete?uri=...");
    }

    @Nonnull
    public LiquidMessage get(final LiquidUUID pool, final LiquidUUID object, @Nonnull final Map<String, String[]> parameters) throws URISyntaxException {
        final boolean history = parameters.get("history") != null;
        return dataStoreFacade.process(new RetrievePoolObjectRequest(RestContext.getContext()
                                                                                .getCredentials(), pool, object, history));
    }

    @Nonnull
    public LiquidMessage get(final LiquidUUID pool, @Nonnull final Map<String, String[]> parameters) throws URISyntaxException {
        final SessionIdentifier username = RestContext.getContext().getCredentials();
        return dataStoreFacade.process(new RetrievePoolRequest(username, pool, parameters.containsKey("contents"), false));
    }

    public LiquidMessage get(@Nonnull final Map<String, String[]> parameters) throws Exception {
        final String url;
        //migrating to using uri from url
        if (parameters.get("url") != null) {
            url = parameters.get("url")[0];
        } else if (parameters.get("uri") != null) {
            url = parameters.get("uri")[0];
        } else {
            throw new RestHandlerException("Expected parameter uri was missing for this call.");
        }
        final boolean history = parameters.get("history") != null;
        final SessionIdentifier username = RestContext.getContext().getCredentials();

        final LURI liquidURI = new LURI(url);
        final String fragment = liquidURI.getFragment();
        final LiquidMessage message;
        if (fragment != null && !fragment.isEmpty()) {
            message = dataStoreFacade.process(new RetrievePoolObjectRequest(username, liquidURI, history));
            return message;
        } else {
            message = dataStoreFacade.process(new RetrievePoolRequest(username, liquidURI, parameters.containsKey("contents"), false));
            return message;
        }
    }

    public FountainDataStoreFacade getDataStore() {
        return dataStoreFacade;
    }

    public EntityFactory getLsdFactory() {
        return entityFactory;
    }

    /**
     * Linking an object should (but doesn't yet) creates a reference to the original object and its version. The object
     * itself will not exist in the new pool only the reference. The reference should be to the version that was linked.
     *
     * @param parent
     * @param poolObject
     * @param parameters
     * @return
     * @throws URISyntaxException
     * @deprecated relocate or copy
     */
    @Nonnull
    public LiquidMessage link(final LiquidUUID parent, final LiquidUUID poolObject, @Nonnull final Map<String, String[]> parameters) throws URISyntaxException {
        checkForSingleValueParams(parameters, "to");
        final LiquidUUID to = LiquidUUID.fromString(parameters.get("to")[0]);
        return dataStoreFacade.process(new LinkPoolObjectRequest(RestContext.getContext()
                                                                            .getCredentials(), poolObject, parent, to));
    }

    @Nonnull
    public LiquidMessage move(final LiquidUUID poolId, final LiquidUUID objectId, @Nonnull final Map<String, String[]> parameters) {
        checkForSingleValueParams(parameters, "x", "y", "z");
        final String x = parameters.get("x")[0];
        final String y = parameters.get("y")[0];
        final String z = parameters.get("z")[0];
        return dataStoreFacade.process(new MovePoolObjectRequest(RestContext.getContext()
                                                                            .getCredentials(), null, poolId, objectId, Double.parseDouble(x), Double
                .parseDouble(y), Double.parseDouble(z)));
    }

    /**
     * Referencing an object should (but doesn't yet) creates a reference to the original object. The object itself will not
     * exist in the new pool only the reference. The reference should always be to the latest version.
     * <p/>
     * This may better implemented by introducing a new entity type rather than by a method.
     *
     * @param parameters
     * @return
     */
    @Nullable
    public LiquidMessage reference(final Map<String, String[]> parameters) {
        //TODO implements
        return null;
    }

    /**
     * Relocating an object creates a new copy in the new location while unlinking the old copy from its original location.
     *
     * @param pool
     * @param object
     * @param parameters
     * @return
     * @throws URISyntaxException
     */
    @Nonnull
    public LiquidMessage relocate(final LiquidUUID pool, final LiquidUUID object, @Nonnull final Map<String, String[]> parameters) throws URISyntaxException {
        checkForSingleValueParams(parameters, "to");
        if (parameters.containsKey("from")) {
            final LiquidUUID from = LiquidUUID.fromString(parameters.get("from")[0]);
            final LiquidUUID to = LiquidUUID.fromString(parameters.get("to")[0]);
            return dataStoreFacade.process(new LinkPoolObjectRequest(RestContext.getContext()
                                                                                .getCredentials(), object, from, to, true));
        } else {
            final LiquidUUID to = LiquidUUID.fromString(parameters.get("to")[0]);
            return dataStoreFacade.process(new LinkPoolObjectRequest(RestContext.getContext()
                                                                                .getCredentials(), object, pool, to, true));
        }
    }

    /**
     * TBD - is this like delete to trash.
     * but no longer available to any ordinary user.
     *
     * @param pool
     * @param object
     * @return
     * @throws URISyntaxException
     */
    @Nonnull
    public LiquidMessage remove(final LiquidUUID pool, final LiquidUUID object) throws URISyntaxException {
        return dataStoreFacade.process(new LinkPoolObjectRequest(RestContext.getContext().getCredentials(), object, pool, true));
    }

    @Nonnull
    public LiquidMessage resize(final LiquidUUID poolId, final LiquidUUID objectId, @Nonnull final Map<String, String[]> parameters) {
        checkForSingleValueParams(parameters, "width", "height");
        final String width = parameters.get("width")[0];
        final String height = parameters.get("height")[0];
        return dataStoreFacade.process(new ResizePoolObjectRequest(RestContext.getContext()
                                                                              .getCredentials(), poolId, objectId, Integer.parseInt(width), Integer
                .parseInt(height), null
                                                                   /*todo: use URIs*/));
    }

    @Nonnull
    public LiquidMessage roster(final LiquidUUID pool, final Map<String, String[]> parameters) throws URISyntaxException {
        final SessionIdentifier username = RestContext.getContext().getCredentials();
        return dataStoreFacade.process(new RetrievePoolRosterRequest(username, pool));
    }

    @Nonnull
    public LiquidMessage rotateXY(final LiquidUUID poolId, final LiquidUUID objectId, @Nonnull final Map<String, String[]> parameters) {
        checkForSingleValueParams(parameters, "angle");
        final String angle = parameters.get("angle")[0];
        return dataStoreFacade.process(new RotateXYPoolObjectRequest(RestContext.getContext()
                                                                                .getCredentials(), poolId, objectId, Double.parseDouble(angle), null /*todo: use URIs*/));
    }

    @Nonnull
    public LiquidMessage select(final LiquidUUID poolId, final LiquidUUID objectId) {
        return dataStoreFacade.process(new SelectPoolObjectRequest(RestContext.getContext().getCredentials(), objectId, true));
    }

    public void setDataStore(final FountainDataStoreFacade dataStoreFacade) {
        this.dataStoreFacade = dataStoreFacade;
    }

    public void setLsdFactory(final EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    /**
     * TBD - is this like delete to trash.
     * but no longer available to any ordinary user.
     *
     * @param parent
     * @param poolObject
     * @return
     * @throws URISyntaxException
     * @deprecated duplicate of remove
     */
    @Nonnull
    public LiquidMessage unlink(final LiquidUUID parent, final LiquidUUID poolObject) throws URISyntaxException {
        return dataStoreFacade.process(new LinkPoolObjectRequest(RestContext.getContext()
                                                                            .getCredentials(), poolObject, parent, true));
    }

    @Nonnull
    public LiquidMessage unselect(final LiquidUUID poolId, final LiquidUUID objectId) {
        return dataStoreFacade.process(new SelectPoolObjectRequest(RestContext.getContext().getCredentials(), objectId, false));
    }

    @Nonnull
    public LiquidMessage update(final TransferEntity lsdEntity, @Nonnull final Map<String, String[]> parameters) {
        final LURI uri = new LURI(parameters.get("uri")[0]);
        if (uri.hasFragment()) {
            return dataStoreFacade.process(new UpdatePoolRequest(RestContext.getContext().getCredentials(), uri, lsdEntity));
        } else {
            return dataStoreFacade.process(new UpdatePoolObjectRequest(RestContext.getContext().getCredentials(), uri, lsdEntity));
        }
    }

    @Nonnull
    public LiquidMessage visit(final LiquidUUID pool, final Map<String, String[]> parameters) throws URISyntaxException {
        throw new UnsupportedOperationException("Please use a URI not a UUID to visit a pool.");
        //        SessionIdentifier username = RestContext.getContext().getCredentials();
        //        return dataStoreFacade.process(new VisitPoolRequest(username, pool, false));
    }

    @Nonnull
    public LiquidMessage visit(@Nonnull final Map<String, String[]> parameters) throws URISyntaxException {
        final SessionIdentifier username = RestContext.getContext().getCredentials();
        checkForSingleValueParams(parameters, "url");
        final String url = parameters.get("url")[0];
        return dataStoreFacade.process(new VisitPoolRequest(username, new LURI(url)));
    }

    public void setAuthorizationService(final AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }
}
