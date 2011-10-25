package cazcade.fountain.server.rest.pool;

import cazcade.fountain.datastore.api.AuthorizationService;
import cazcade.fountain.datastore.api.FountainDataStoreFacade;
import cazcade.fountain.server.rest.AbstractRestHandler;
import cazcade.fountain.server.rest.RestContext;
import cazcade.fountain.server.rest.RestHandlerException;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.*;
import cazcade.liquid.api.request.*;
import cazcade.liquid.impl.UUIDFactory;

import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author Neil Ellis
 */

public class PoolRestHandler extends AbstractRestHandler {

    private LSDEntityFactory lsdEntityFactory;

    private FountainDataStoreFacade dataStoreFacade;
    private AuthorizationService authorizationService;


    public LiquidMessage update(LiquidUUID poolId, LSDEntity lsdEntity) {
//        poolValidator.validate(lsdEntity);
        return dataStoreFacade.process(new UpdatePoolRequest(RestContext.getContext().getCredentials(), poolId, lsdEntity));
    }

    public LiquidMessage update(LiquidUUID poolId, LiquidUUID objectId, LSDEntity lsdEntity) {
//        poolObjectValidator.validate(lsdEntity);
        return dataStoreFacade.process(new UpdatePoolObjectRequest(RestContext.getContext().getCredentials(), poolId, objectId, lsdEntity));
    }

    public LiquidMessage move(LiquidUUID poolId, LiquidUUID objectId, Map<String, String[]> parameters) {
        checkForSingleValueParams(parameters, "x", "y", "z");
        final String x = parameters.get("x")[0];
        final String y = parameters.get("y")[0];
        final String z = parameters.get("z")[0];
        return dataStoreFacade.process(new MovePoolObjectRequest(RestContext.getContext().getCredentials(), null, poolId, objectId, Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z)));
    }

    public LiquidMessage resize(LiquidUUID poolId, LiquidUUID objectId, Map<String, String[]> parameters) {
        checkForSingleValueParams(parameters, "width", "height");
        final String width = parameters.get("width")[0];
        final String height = parameters.get("height")[0];
        return dataStoreFacade.process(new ResizePoolObjectRequest(RestContext.getContext().getCredentials(), poolId, objectId, Integer.parseInt(width), Integer.parseInt(height), null /*todo: use URIs*/));
    }

    public LiquidMessage rotateXY(LiquidUUID poolId, LiquidUUID objectId, Map<String, String[]> parameters) {
        checkForSingleValueParams(parameters, "angle");
        final String angle = parameters.get("angle")[0];
        return dataStoreFacade.process(new RotateXYPoolObjectRequest(RestContext.getContext().getCredentials(), poolId, objectId, Double.parseDouble(angle), null /*todo: use URIs*/));
    }


    public LiquidMessage select(LiquidUUID poolId, LiquidUUID objectId) {
        return dataStoreFacade.process(new SelectPoolObjectRequest(RestContext.getContext().getCredentials(), objectId, true));
    }

    public LiquidMessage unselect(LiquidUUID poolId, LiquidUUID objectId) {
        return dataStoreFacade.process(new SelectPoolObjectRequest(RestContext.getContext().getCredentials(), objectId, false));
    }

    public LiquidMessage get(Map<String, String[]> parameters) throws Exception {
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
        LiquidSessionIdentifier username = RestContext.getContext().getCredentials();

        LiquidURI liquidURI = new LiquidURI(url);
        String fragment = liquidURI.getFragment();
        LiquidMessage message;
        if (fragment != null && !fragment.isEmpty()) {
            message = dataStoreFacade.process(new RetrievePoolObjectRequest(username, liquidURI, history));
            return message;
        } else {
            message = dataStoreFacade.process(new RetrievePoolRequest(username, liquidURI, parameters.containsKey("contents"), false));
            return message;
        }
    }

    public LiquidMessage get(LiquidUUID pool, Map<String, String[]> parameters) throws URISyntaxException {
        LiquidSessionIdentifier username = RestContext.getContext().getCredentials();
        return dataStoreFacade.process(new RetrievePoolRequest(username, pool, parameters.containsKey("contents"), false));
    }

    public LiquidMessage visit(LiquidUUID pool, Map<String, String[]> parameters) throws URISyntaxException {
        throw new UnsupportedOperationException("Please use a URI not a UUID to visit a pool.");
//        LiquidSessionIdentifier username = RestContext.getContext().getCredentials();
//        return dataStoreFacade.process(new VisitPoolRequest(username, pool, false));
    }

    public LiquidMessage visit(Map<String, String[]> parameters) throws URISyntaxException {
        LiquidSessionIdentifier username = RestContext.getContext().getCredentials();
        checkForSingleValueParams(parameters, "url");
        final String url = parameters.get("url")[0];
        return dataStoreFacade.process(new VisitPoolRequest(username, new LiquidURI(url)));
    }

    public LiquidMessage roster(LiquidUUID pool, Map<String, String[]> parameters) throws URISyntaxException {
        LiquidSessionIdentifier username = RestContext.getContext().getCredentials();
        return dataStoreFacade.process(new RetrievePoolRosterRequest(username, pool));
    }

    /**
     * Deprecated, see {@link cazcade.fountain.server.rest.message.CommentRestHandler}
     */
    public LiquidMessage chat(LiquidUUID pool, Map<String, String[]> parameters) throws URISyntaxException {
        LiquidSessionIdentifier username = RestContext.getContext().getCredentials();
        checkForSingleValueParams(parameters, "text", "image");
        final String text = parameters.get("text")[0];
        final String image = parameters.get("image")[0];
        LSDSimpleEntity message = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.CHAT, UUIDFactory.randomUUID());
        message.setAttribute(LSDAttribute.TEXT_EXTENDED, text);
        message.setAttribute(LSDAttribute.IMAGE_URL, image);
        message.setAttribute(LSDAttribute.ICON_URL, image);
        return dataStoreFacade.process(new AddCommentRequest(username, pool, message));
    }

    public LiquidMessage get(LiquidUUID pool, LiquidUUID object, Map<String, String[]> parameters) throws URISyntaxException {
        final boolean history = parameters.get("history") != null;
        return dataStoreFacade.process(new RetrievePoolObjectRequest(RestContext.getContext().getCredentials(), pool, object, history));
    }

    /**
     * Copying an object or pool creates an independent version in the new location.
     *
     * @param pool
     * @param object
     * @param parameters
     * @return
     * @throws URISyntaxException
     *
     *         //todo: support URIS

     */
    public LiquidMessage copy(LiquidUUID pool, LiquidUUID object, Map<String, String[]> parameters) throws URISyntaxException {
        checkForSingleValueParams(parameters, "to");
        if (parameters.containsKey("from")) {
            LiquidUUID from = LiquidUUID.fromString(parameters.get("from")[0]);
            LiquidUUID to = LiquidUUID.fromString(parameters.get("to")[0]);
            return dataStoreFacade.process(new LinkPoolObjectRequest(RestContext.getContext().getCredentials(), object, from, to));
        } else {
            LiquidUUID to = LiquidUUID.fromString(parameters.get("to")[0]);
            return dataStoreFacade.process(new LinkPoolObjectRequest(RestContext.getContext().getCredentials(), object, to));
        }
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
    public LiquidMessage relocate(LiquidUUID pool, LiquidUUID object, Map<String, String[]> parameters) throws URISyntaxException {
        checkForSingleValueParams(parameters, "to");
        if (parameters.containsKey("from")) {
            LiquidUUID from = LiquidUUID.fromString(parameters.get("from")[0]);
            LiquidUUID to = LiquidUUID.fromString(parameters.get("to")[0]);
            return dataStoreFacade.process(new LinkPoolObjectRequest(RestContext.getContext().getCredentials(), object, from, to, true));
        } else {
            LiquidUUID to = LiquidUUID.fromString(parameters.get("to")[0]);
            return dataStoreFacade.process(new LinkPoolObjectRequest(RestContext.getContext().getCredentials(), object, pool, to, true));
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
    public LiquidMessage remove(LiquidUUID pool, LiquidUUID object) throws URISyntaxException {
        return dataStoreFacade.process(new LinkPoolObjectRequest(RestContext.getContext().getCredentials(), object, pool, true));
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
    public LiquidMessage link(LiquidUUID parent, LiquidUUID poolObject, Map<String, String[]> parameters) throws URISyntaxException {
        checkForSingleValueParams(parameters, "to");
        LiquidUUID to = LiquidUUID.fromString(parameters.get("to")[0]);
        return dataStoreFacade.process(new LinkPoolObjectRequest(RestContext.getContext().getCredentials(), poolObject, parent, to));
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
    public LiquidMessage unlink(LiquidUUID parent, LiquidUUID poolObject) throws URISyntaxException {
        return dataStoreFacade.process(new LinkPoolObjectRequest(RestContext.getContext().getCredentials(), poolObject, parent, true));
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
    public LiquidMessage reference(Map<String, String[]> parameters) {
        //TODO implements
        return null;
    }

    public LiquidMessage create(Map<String, String[]> parameters) throws URISyntaxException {
        checkForSingleValueParams(parameters, "parent", "name", "title", "description", "x", "y");
        final String parent = parameters.get("parent")[0];
        final String name = parameters.get("name")[0];
        final String title = parameters.get("title")[0];
        final String description = parameters.get("description")[0];
        final double x = Double.parseDouble(parameters.get("x")[0]);
        final double y = Double.parseDouble(parameters.get("y")[0]);
        LiquidSessionIdentifier identity = RestContext.getContext().getCredentials();
        return dataStoreFacade.process(new CreatePoolRequest(identity, new LiquidURI(parent), name, title, description, x, y));
    }

    public LiquidMessage create(LiquidUUID poolId, LSDEntity entity, Map<String, String[]> parameters) throws URISyntaxException {
        checkForSingleValueParams(parameters, "author");
        final String author = parameters.get("author")[0];
        if (entity.getID() != null && entity.getID().equals(poolId)) {
            return update(poolId, entity);
        } else {
            return dataStoreFacade.process(new CreatePoolObjectRequest(RestContext.getContext().getCredentials(), poolId, entity, new LiquidURI(author)));
        }
    }

    public LiquidMessage create(LiquidUUID poolId, LiquidUUID objectId, LSDEntity entity) throws URISyntaxException {
        return update(poolId, objectId, entity);
    }


    public LiquidMessage delete(Map<String, String[]> parameters) throws URISyntaxException {
        checkForSingleValueParams(parameters, "uri");
        final String uri = parameters.get("uri")[0];
        LiquidSessionIdentifier identity = RestContext.getContext().getCredentials();
        return dataStoreFacade.process(new DeletePoolRequest(RestContext.getContext().getCredentials(), new LiquidURI(uri)));
    }

    public LiquidMessage delete(LiquidUUID pool) throws URISyntaxException {
        throw new UnsupportedOperationException("Use /delete?uri=...");
    }

    public LiquidMessage delete(LiquidUUID pool, LiquidUUID object) throws URISyntaxException {
        throw new UnsupportedOperationException("Use /delete?uri=...");
    }

    public void setLsdFactory(LSDEntityFactory lsdEntityFactory) {
        this.lsdEntityFactory = lsdEntityFactory;
    }

    public LSDEntityFactory getLsdFactory() {
        return lsdEntityFactory;
    }

    public void setDataStore(FountainDataStoreFacade dataStoreFacade) {
        this.dataStoreFacade = dataStoreFacade;
    }

    public FountainDataStoreFacade getDataStore() {
        return dataStoreFacade;
    }


    public void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }
}
