/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.server;

import cazcade.common.CommonConstants;
import cazcade.common.Logger;
import cazcade.fountain.datastore.api.FountainDataStore;
import cazcade.fountain.datastore.api.FountainRequestCompensator;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.messaging.InMemoryPubSub;
import cazcade.fountain.messaging.LiquidMessageSender;
import cazcade.liquid.api.*;
import cazcade.liquid.api.request.AbstractRequest;
import cazcade.liquid.api.request.AuthorizationRequest;
import org.perf4j.log4j.Log4JStopWatch;

import javax.annotation.Nonnull;

/**
 * A handler that implements runnable allowing for asynchronous handling of data store requests on another thread.
 */
public class DataStoreServerMessageHandler implements LiquidMessageHandler<LiquidRequest> {
    @Nonnull
    private static final Logger log = Logger.getLogger(DataStoreServerMessageHandler.class);
    private FountainRequestCompensator<LiquidRequest> compensator;
    private FountainDataStore                         store;
    private LiquidMessageSender                       messageSender;
    private InMemoryPubSub                            pubSub;
    private long                                      listenerId;

    public void start() {
        listenerId = pubSub.addListener(CommonConstants.SERVICE_STORE, this);
    }

    public void stop() {
        if (listenerId > 0) {
            pubSub.removeListener(listenerId);
        }
    }


    @Nonnull
    public LiquidRequest handle(final LiquidRequest message) {
        try {
            final LiquidRequest request = (LiquidRequest) message;
            final Log4JStopWatch stopWatch = new Log4JStopWatch("recv.message." + request.requestType().name().toLowerCase());

            log.addContext(request);
            final LiquidUUID session = request.session().session();
            log.setSession(session == null ? null : session.toString(), request.session().name());
            if (request.requestType() == RequestType.AUTHORIZATION_REQUEST) {
                log.debug("Authorization request to {0} on {1}/{2}.", ((AuthorizationRequest) request).getActions(), ((AbstractRequest) request)
                                                                                                                             .hasTarget()
                                                                                                                     ? ((AbstractRequest) request)
                                                                                                                             .getTarget()
                                                                                                                     : "null", ((AbstractRequest) request)
                                                                                                                                       .hasUri()
                                                                                                                               ? ((AbstractRequest) request)
                                                                                                                                       .uri()
                                                                                                                               : "null");
            } else if (request.requestType() == RequestType.RETRIEVE_USER) {
                log.debug("Retrieve user request for {0}/{1}", ((AbstractRequest) request).hasTarget() ? ((AbstractRequest) request)
                        .getTarget() : "null", ((AbstractRequest) request).hasUri() ? ((AbstractRequest) request).uri() : "null");
            } else {
                log.debug("Received request {0}", request);
            }

            final LiquidRequest response;
            try {
                stopWatch.stop("recv." + request.requestType().name().toLowerCase() + ".1.prepro");
                response = store.process(request);
                stopWatch.stop("recv." + request.requestType().name().toLowerCase() + ".2.postpro");
            } catch (InterruptedException e) {
                Thread.interrupted();
                return handleError(request, e);
            } catch (Exception e) {
                return handleError(request, e);
            }
            response.origin(LiquidMessageOrigin.SERVER);

            log.addContext(response);

            if (request.requestType() == RequestType.AUTHORIZATION_REQUEST) {
                log.debug("Authorization request {0}", request.getState());
            } else if (request.requestType() == RequestType.RETRIEVE_USER) {
                log.debug("Retrieve user request {0}", request.getState());
            } else {
                log.debug("Async response: {0} ", response);
            }
            if (request.shouldNotify()) {
                //Notify if async
                //i.e. for pool visits.
                stopWatch.stop("recv." + request.requestType().name().toLowerCase() + ".3.prenot");
                messageSender.sendNotifications(response);
                stopWatch.stop("recv." + request.requestType().name().toLowerCase() + ".4.postnot");
            }
            stopWatch.stop("recv." + request.requestType().name().toLowerCase() + ".5.end");


            return response;
        } catch (Exception e) {
            return handleError((LiquidRequest) message, e);
        } finally {
            log.clearContext();
            log.clearSession();
        }
    }

    @Nonnull
    private LiquidRequest handleError(@Nonnull final LiquidRequest request, @Nonnull final Exception e) {
        final LiquidRequest response = LiquidResponseHelper.forException(e, request);
        response.origin(LiquidMessageOrigin.SERVER);
        messageSender.notifySession(response);
        final LiquidRequest compensation = compensator.compensate(request);
        if (compensation != null) {
            messageSender.sendNotifications(compensation);
        }
        return response;
    }

    public void setCompensator(final FountainRequestCompensator<LiquidRequest> compensator) {
        this.compensator = compensator;
    }

    public void setMessageSender(final LiquidMessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void setStore(final FountainDataStore store) {
        this.store = store;
    }

    public void setPubSub(InMemoryPubSub pubSub) {this.pubSub = pubSub;}

    public InMemoryPubSub getPubSub() { return pubSub; }

}
