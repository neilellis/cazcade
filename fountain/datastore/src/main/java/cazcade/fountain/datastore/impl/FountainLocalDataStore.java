package cazcade.fountain.datastore.impl;

import cazcade.common.Logger;
import cazcade.fountain.common.service.AbstractServiceStateMachine;
import cazcade.fountain.datastore.api.DataStoreException;
import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.fountain.datastore.api.FountainDataStore;
import cazcade.liquid.api.LiquidRequest;
import cazcade.liquid.api.handler.AuthorizationRequestHandler;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.AuthorizationRequest;

import java.util.List;

/**
 * @author Neil Ellis
 */

public class FountainLocalDataStore extends AbstractServiceStateMachine implements FountainDataStore {

    private final static Logger log = Logger.getLogger(FountainLocalDataStore.class);

    private FountainNeo fountainNeo;
    private FountainRequestMap requestMap;
    private AuthorizationRequestHandler authHandler;

    @Override
    public <T extends LiquidRequest> T process(T request) throws Exception {
        try {
            final List<AuthorizationRequest> authorizationRequests = request.getAuthorizationRequests();
            for (AuthorizationRequest authorizationRequest : authorizationRequests) {
                authorizationRequest.setSessionId(request.getSessionIdentifier());
                final AuthorizationRequest result = authHandler.handle(authorizationRequest);
                final LSDEntity responseEntity = result.getResponse();
                if (!LSDDictionaryTypes.AUTHORIZATION_ACCEPTANCE.equals(responseEntity.getTypeDef())) {
                    LiquidResponseHelper.forFailure(request, result);
                }
            }

            FountainRequestConfiguration config = requestMap.getConfiguration(request.getRequestType().getRequestClass());
            if (config == null) {
                throw new Error("No configuration for " + request.getClass());
            }
            if (request.getSessionIdentifier() != null && request.getSessionIdentifier().getSession() != null) {
                fountainNeo.updateSessionTx(request.getSessionIdentifier().getSession());
            }
            return (T) config.getHandler().handle(request);
        } catch (EntityNotFoundException enfe) {
            log.debug(enfe.getMessage(), enfe);
            return LiquidResponseHelper.forEmptyResultResponse(request);
        } catch (DataStoreException de) {
            log.error(de);
            if (de.isClientException()) {
                return LiquidResponseHelper.forException(de, request);
            } else {
                throw de;
            }
        }

    }


    public void setFountainNeo(FountainNeo fountainNeo) {
        this.fountainNeo = fountainNeo;
    }

    public void start() throws Exception {
        super.start();
        requestMap.injectNeo(fountainNeo);

    }

    public void setRequestMap(FountainRequestMap requestMap) {
        this.requestMap = requestMap;
    }

    public FountainRequestMap getRequestMap() {
        return requestMap;
    }

    public void setAuthHandler(AuthorizationRequestHandler authHandler) {
        this.authHandler = authHandler;
    }
}
