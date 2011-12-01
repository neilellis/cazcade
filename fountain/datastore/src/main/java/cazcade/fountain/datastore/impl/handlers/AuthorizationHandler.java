package cazcade.fountain.datastore.impl.handlers;

import cazcade.common.Logger;
import cazcade.fountain.datastore.FountainEntity;
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
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author neilelliz@cazcade.com
 */
public class AuthorizationHandler extends AbstractDataStoreHandler<AuthorizationRequest> implements AuthorizationRequestHandler {
    @Nonnull
    private static final Logger log = Logger.getLogger(AuthorizationHandler.class);

    /**
     * Authorizes a user to
     *
     * @param request
     * @return
     */
    @Nonnull
    public AuthorizationRequest handle(@Nonnull final AuthorizationRequest request) throws InterruptedException {
        final LSDEntity entity = LSDSimpleEntity.createEmpty();
        final Transaction transaction = fountainNeo.beginTx();
        try {
            if (request.getSessionIdentifier() == null) {
                throw new AuthorizationException("No identity supplied.");
            }
            final FountainEntity fountainEntity;
            if (request.getTarget() != null) {
                fountainEntity = fountainNeo.findByUUID(request.getTarget());
            } else {
                fountainEntity = fountainNeo.findByURI(request.getUri());
                if (fountainEntity == null) {
                    log.warn("Client asked for authorization on  " + request.getUri() + " which could not be found.");
                    entity.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.AUTHORIZATION_DENIAL.getValue());
                    return LiquidResponseHelper.forServerSuccess(request, entity);
                }
            }
            if (fountainEntity.hasAttribute(LSDAttribute.PERMISSIONS)) {
                final boolean auth = isAuthorized(request, fountainEntity);
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

    private boolean isAuthorized(@Nonnull final AuthorizationRequest request, @Nonnull final FountainEntity fountainEntity) throws InterruptedException {
        boolean auth;
        auth = fountainEntity.isAuthorized(request.getSessionIdentifier(), request.getActions());
        final List<AuthorizationRequest> and = request.getAnd();
        for (final AuthorizationRequest andRequest : and) {
            if (isAuthorized(andRequest, fountainEntity)) {
                auth = false;
            }
        }
        if (!auth) {
            final List<AuthorizationRequest> alternates = request.getOr();
            for (final AuthorizationRequest orRequest : alternates) {
                if (isAuthorized(orRequest, fountainEntity)) {
                    auth = true;
                    break;
                }
            }
        }
        return auth;
    }


}