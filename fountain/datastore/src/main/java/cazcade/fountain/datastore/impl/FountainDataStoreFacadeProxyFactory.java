/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

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
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.impl.UUIDFactory;
import cazcade.liquid.impl.xstream.LiquidXStreamFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author neilelliz@cazcade.com
 * @todo this needs to be a dynamic proxy.
 */
public class FountainDataStoreFacadeProxyFactory {
    @Nonnull
    private static final Logger log = Logger.getLogger(FountainDataStoreFacadeProxyFactory.class);

    private FountainDataStore       dataStore;
    private AuthorizationService    authorizationService;
    private FountainEntityValidator entityValidator;

    @Nonnull
    public FountainDataStoreFacade create() {
        final InvocationHandler handler = new MyInvocationHandler();
        final FountainDataStoreFacade proxy = (FountainDataStoreFacade) Proxy.newProxyInstance(FountainDataStoreFacade.class.getClassLoader(), new Class[]{
                FountainDataStoreFacade.class}, handler);
        return proxy;
    }

    public void destroy() {
        dataStore.stopIfNotStopped();
    }

    public void init() throws Exception {
        dataStore.startIfNotStarted();
    }

    public void setAuthorizationService(final AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    public void setDataStore(final FountainDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void setEntityValidator(final FountainEntityValidator entityValidator) {
        this.entityValidator = entityValidator;
    }

    private class MyInvocationHandler implements InvocationHandler {
        @Nullable
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            log.debug("Facade invoked.");
            if (args == null || args.length != 1) {
                return null;
            }
            final LiquidRequest liquidRequest = (LiquidRequest) args[0];
            if (!liquidRequest.hasId()) {
                liquidRequest.id(UUIDFactory.randomUUID());
            }
            liquidRequest.origin(LiquidMessageOrigin.CLIENT);
            LiquidMessage response = null;
            try {
                if (liquidRequest.hasRequestEntity()) {
                    entityValidator.validate(liquidRequest.request(), ValidationLevel.MODERATE);
                }
                response = authorizationService.authorize(liquidRequest);
                if (response == null) {
                    log.debug("SUCCESS Authorized.");
                    response = dataStore.process(liquidRequest);
                    final TransferEntity responseEntity = response.response();
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
