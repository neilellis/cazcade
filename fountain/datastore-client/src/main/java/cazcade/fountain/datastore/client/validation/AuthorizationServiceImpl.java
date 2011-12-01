package cazcade.fountain.datastore.client.validation;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.AuthorizationService;
import cazcade.fountain.datastore.api.AuthorizationStatus;
import cazcade.fountain.datastore.api.FountainDataStore;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDType;
import cazcade.liquid.api.request.AbstractRetrievalRequest;
import cazcade.liquid.api.request.AuthorizationRequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author neilelliz@cazcade.com
 */
public class AuthorizationServiceImpl implements AuthorizationService {
    @Nonnull
    private final static Logger log = Logger.getLogger(AuthorizationServiceImpl.class);

    private FountainDataStore dataStore;

    @Nonnull
    public AuthorizationStatus authorize(LiquidSessionIdentifier identity, LiquidUUID resource, LiquidPermission permission) throws Exception {
        LiquidMessage message = dataStore.process(new AuthorizationRequest(identity, resource, permission));
        final LSDEntity response = message.getResponse();
        LSDType lsdType = response.getTypeDef().getPrimaryType();
        if (LSDDictionaryTypes.AUTHORIZATION_ACCEPTANCE.equals(lsdType)) {
            return AuthorizationStatus.ACCEPTED;
        } else if (LSDDictionaryTypes.AUTHORIZATION_DENIAL.equals(lsdType)) {
            return AuthorizationStatus.DENIED;
        } else if (LSDDictionaryTypes.AUTHORIZATION_NOT_REQUIRED.equals(lsdType)) {
            return AuthorizationStatus.NOT_REQUIRED;
        } else if (LSDDictionaryTypes.AUTHORIZATION_INVALID.equals(lsdType)) {
            return AuthorizationStatus.INVALID;
        } else if (LSDDictionaryTypes.EXCEPTION.equals(lsdType.getClassOnlyType())) {
            log.error(message.getResponse().getAttribute(LSDAttribute.TITLE));
            log.error(message.getResponse().getAttribute(LSDAttribute.TEXT));
            return AuthorizationStatus.INVALID;
        } else {
            log.warn("Authorization denied due to the following reason:");
            log.warn(message.getResponse().getAttribute(LSDAttribute.TITLE));
            log.warn(message.getResponse().getAttribute(LSDAttribute.TEXT));
            return AuthorizationStatus.INVALID;
        }
    }

    @Nonnull
    public LiquidMessage postAuthorize(LiquidSessionIdentifier identity, @Nonnull AbstractRetrievalRequest message, LiquidPermission permission) throws Exception {
        log.debug("Post authorizing: {0}", message.getClass().getSimpleName());
        final LSDEntity response = message.getResponse();
        if (response.getTypeDef().getPrimaryType().isSystemType()) {
            return message;
        }
        LiquidUUID resource = response.getUUID();
        LiquidMessage authMessage = dataStore.process(new AuthorizationRequest(identity, resource, permission));
        LSDType lsdType = authMessage.getResponse().getTypeDef().getPrimaryType();
        if (LSDDictionaryTypes.AUTHORIZATION_ACCEPTANCE.equals(lsdType)) {
            log.debug("SUCCESS authorizing: {0}", message.getClass().getSimpleName());
            return message;
        } else {
            log.debug("FAILED authorizing: {0}", message.getClass().getSimpleName());
            return authMessage;
        }
    }


    @Nullable
    public LiquidRequest authorize(@Nonnull LiquidRequest liquidRequest) throws Exception {
        List<AuthorizationRequest> authRequests = liquidRequest.getAuthorizationRequests();
        for (AuthorizationRequest authRequest : authRequests) {
            log.debug("Authorization request {0} for {1} being processed.", authRequest.getActions(), authRequest.getTarget());
            authRequest.setSessionId(liquidRequest.getSessionIdentifier());
            LiquidMessage message = dataStore.process(authRequest);
            if (message == null) {
                throw new NullPointerException("Received a null message back from the data store during authorization.");
            }
            if (message.getResponse() == null) {
                throw new NullPointerException("Received a null response back from the data store during authorization.");
            }
            LSDType lsdType = message.getResponse().getTypeDef().getPrimaryType();
            if (LSDDictionaryTypes.AUTHORIZATION_ACCEPTANCE.equals(lsdType)) {
                log.debug("SUCCESS for authorization.");
            } else {
                log.debug("FAILED authorization for actions {0}", authRequest.getActions());
                message.setState(LiquidMessageState.FAIL);
                return (LiquidRequest) message;
            }
        }
        return null;
    }

    public void setDataStore(FountainDataStore dataStore) {
        this.dataStore = dataStore;
    }
}
