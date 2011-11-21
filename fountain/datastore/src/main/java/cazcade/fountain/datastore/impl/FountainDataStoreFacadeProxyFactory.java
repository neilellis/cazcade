package cazcade.fountain.datastore.impl;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.AuthorizationService;
import cazcade.fountain.datastore.api.FountainDataStore;
import cazcade.fountain.datastore.api.FountainDataStoreFacade;
import cazcade.fountain.validation.FountainEntityValidator;
import cazcade.fountain.validation.api.ValidationLevel;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidMessageOrigin;
import cazcade.liquid.api.LiquidRequest;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.impl.UUIDFactory;
import cazcade.liquid.impl.xstream.LiquidXStreamFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author neilelliz@cazcade.com
 * @todo this needs to be a dynamic proxy.
 */
public class FountainDataStoreFacadeProxyFactory {
    private final static Logger log = Logger.getLogger(FountainDataStoreFacadeProxyFactory.class);

    private FountainDataStore dataStore;
    private AuthorizationService authorizationService;
    private FountainEntityValidator entityValidator;


    public void init() throws Exception {
        dataStore.startIfNotStarted();
    }

    public void destroy() {
        dataStore.stopIfNotStopped();
    }

    public void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    public FountainDataStoreFacade create() {
        InvocationHandler handler = new MyInvocationHandler();
        FountainDataStoreFacade proxy = (FountainDataStoreFacade) Proxy.newProxyInstance(
                FountainDataStoreFacade.class.getClassLoader(),
                new Class[]{FountainDataStoreFacade.class},
                handler);
        return proxy;
    }

    public void setDataStore(FountainDataStore dataStore) {
        this.dataStore = dataStore;
    }


    public void setEntityValidator(FountainEntityValidator entityValidator) {
        this.entityValidator = entityValidator;
    }


    private class MyInvocationHandler implements InvocationHandler {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            log.debug("Facade invoked.");
            LiquidRequest liquidRequest = (LiquidRequest) args[0];
            if (liquidRequest.getId() == null) {
                liquidRequest.setId(UUIDFactory.randomUUID());
            }
            liquidRequest.setOrigin(LiquidMessageOrigin.CLIENT);
            LiquidMessage response = null;
            try {
                if (liquidRequest.getRequestEntity() != null) {
                    entityValidator.validate(liquidRequest.getRequestEntity(), ValidationLevel.MODERATE);
                }
                response = authorizationService.authorize(liquidRequest);
                if (response == null) {
                    log.debug("SUCCESS Authorized.");
                    response = dataStore.process(liquidRequest);
                    final LSDEntity responseEntity = response.getResponse();
                    if (responseEntity == null) {
                        log.warn("Null response.");
                    }
                    entityValidator.validate(responseEntity, ValidationLevel.MODERATE);
                    log.debug("SUCCESS Post validation okay returning response.");
                    return response;
                } else {
                    log.debug("FAIL Authorization failed.");
                    return response;
                }
            } catch (Exception e) {
                log.error("The request was {0}", LiquidXStreamFactory.getXstream().toXML(liquidRequest));
                if (response != null) {
                    log.error("The response was {0}", LiquidXStreamFactory.getXstream().toXML(response));
                }
                return LiquidResponseHelper.forException(e, liquidRequest);
            }
        }
    }
}
