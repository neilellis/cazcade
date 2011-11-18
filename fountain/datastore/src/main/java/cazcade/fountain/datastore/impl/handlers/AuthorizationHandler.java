package cazcade.fountain.datastore.impl.handlers;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.AuthorizationException;
import cazcade.fountain.datastore.api.DeletedEntityException;
import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.AuthorizationRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.request.AuthorizationRequest;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import java.util.List;

/**
 * @author neilelliz@cazcade.com
 */
public class AuthorizationHandler extends AbstractDataStoreHandler<AuthorizationRequest> implements AuthorizationRequestHandler {
    private final static Logger log = Logger.getLogger(AuthorizationHandler.class);

    /**
     * Authorizes a user to
     *
     * @param request
     * @return
     */
    public AuthorizationRequest handle(AuthorizationRequest request) throws InterruptedException {
        final LSDEntity entity = LSDSimpleEntity.createEmpty();
        final Transaction transaction = fountainNeo.beginTx();
        try {
            if (request.getSessionIdentifier() == null) {
                throw new AuthorizationException("No identity supplied.");
            }
            final Node node;
            if (request.getTarget() != null) {
                node = fountainNeo.findByUUID(request.getTarget());
            } else {
                node = fountainNeo.findByURI(request.getUri());
                if (node == null) {
                    log.warn("Client asked for authorization on  " + request.getUri() + " which could not be found.");
                    entity.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.AUTHORIZATION_DENIAL.getValue());
                    return LiquidResponseHelper.forServerSuccess(request, entity);
                }
            }
            if (node.hasProperty(LSDAttribute.PERMISSIONS.getKeyName())) {
                boolean auth = isAuthorized(request, node);
                if (auth) {
                    entity.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.AUTHORIZATION_ACCEPTANCE.getValue());
                } else {
                    entity.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.AUTHORIZATION_DENIAL.getValue());
                }
            } else {
                entity.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.AUTHORIZATION_NOT_REQUIRED.getValue());
            }
            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, entity);
        } catch (DeletedEntityException dee) {
            log.warn("Client asked for authorization on  " + request.getTarget() + " which has been deleted.");
            entity.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.AUTHORIZATION_INVALID.getValue());
            entity.setAttribute(LSDAttribute.DESCRIPTION, request.getTarget() + " has been deleted.");
            return LiquidResponseHelper.forServerSuccess(request, entity);
        } catch (EntityNotFoundException enfe) {
            log.warn("Client asked for authorization on  " + request.getTarget() + " which could not be found.");
//            entity.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.AUTHORIZATION_INVALID.getAttribute());
            return LiquidResponseHelper.forResourceNotFound(enfe.getMessage(), request);
        } catch (RuntimeException exception) {
            transaction.failure();
            throw exception;
        } finally {
            transaction.finish();
        }
    }

    private boolean isAuthorized(AuthorizationRequest request, Node node) throws InterruptedException {
        boolean auth;
        auth = fountainNeo.isAuthorized(node, request.getSessionIdentifier(), request.getActions());
        List<AuthorizationRequest> and = request.getAnd();
        for (AuthorizationRequest andRequest : and) {
            if (isAuthorized(andRequest, node)) {
                auth = false;
            }
        }
        if (!auth) {
            final List<AuthorizationRequest> alternates = request.getOr();
            for (AuthorizationRequest orRequest : alternates) {
                if (isAuthorized(orRequest, node)) {
                    auth = true;
                    break;
                }
            }
        }
        return auth;
    }


}