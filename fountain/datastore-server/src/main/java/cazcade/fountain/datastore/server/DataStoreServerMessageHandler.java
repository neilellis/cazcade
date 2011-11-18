package cazcade.fountain.datastore.server;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.FountainDataStore;
import cazcade.fountain.datastore.api.FountainRequestCompensator;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.messaging.LiquidMessageSender;
import cazcade.liquid.api.*;
import cazcade.liquid.api.request.AuthorizationRequest;
import cazcade.liquid.api.request.RetrieveUserRequest;
import org.perf4j.log4j.Log4JStopWatch;

/**
 * A handler that implements runnable allowing for asynchronous handling of data store requests on another thread.
 */
public class DataStoreServerMessageHandler {

    private final static Logger log = Logger.getLogger(DataStoreServerMessageHandler.class);
    private FountainRequestCompensator<LiquidRequest> compensator;
    private FountainDataStore store;
    private LiquidMessageSender messageSender;

    public DataStoreServerMessageHandler() {
    }

    public void onMessage(LiquidMessage message) {
        handle(message);
    }

    public LiquidMessage handle(LiquidMessage message) {
        try {
            LiquidRequest request = (LiquidRequest) message;
            Log4JStopWatch stopWatch = new Log4JStopWatch("recv.message." + request.getRequestType().name().toLowerCase());

            log.addContext(request);
            if (request.getSessionIdentifier() != null) {
                LiquidUUID session = request.getSessionIdentifier().getSession();
                log.setSession(session == null ? null : session.toString(), request.getSessionIdentifier() == null ? null : request.getSessionIdentifier().getName());
            }
            if (request.getRequestType() == LiquidRequestType.AUTHORIZATION_REQUEST) {
                log.debug("Authorization request to {0} on {1}/{2}.", ((AuthorizationRequest) request).getActions(), ((AuthorizationRequest) request).getTarget(), ((AuthorizationRequest) request).getUri());
            } else if (request.getRequestType() == LiquidRequestType.RETRIEVE_USER) {
                log.debug("Retrieve user request for {0}/{1}", ((RetrieveUserRequest) request).getTarget(), ((RetrieveUserRequest) request).getUri());

            } else {
                log.debug("Received request {0}", request);
            }

            LiquidRequest response;
            try {
                stopWatch.stop("recv." + request.getRequestType().name().toLowerCase() + ".1.prepro");
                response = (LiquidRequest) store.process(request);
                stopWatch.stop("recv." + request.getRequestType().name().toLowerCase() + ".2.postpro");
            } catch (InterruptedException e) {
                Thread.interrupted();
                return null;
            } catch (Exception e) {
                return handleError(request, e);
            }
            response.setOrigin(LiquidMessageOrigin.SERVER);

            log.addContext(response);

            if (request.getRequestType() == LiquidRequestType.AUTHORIZATION_REQUEST) {
                log.debug("Authorization request {0}", request.getState());
            } else if (request.getRequestType() == LiquidRequestType.RETRIEVE_USER) {
                log.debug("Retrieve user request {0}", request.getState());
            } else {
                log.debug("Async response: {0} ", response);
            }
            if (request.shouldNotify()) {
                //Notify if async
                //i.e. for pool visits.
                stopWatch.stop("recv." + request.getRequestType().name().toLowerCase() + ".3.prenot");
                messageSender.sendNotifications(response);
                stopWatch.stop("recv." + request.getRequestType().name().toLowerCase() + ".4.postnot");
            }
            stopWatch.stop("recv." + request.getRequestType().name().toLowerCase() + ".5.end");


            return response;
        } catch (Exception e) {
            return handleError((LiquidRequest) message, e);
        } finally {
            log.clearContext();
            log.clearSession();

        }
    }


    private LiquidRequest handleError(LiquidRequest request, Exception e) {
        final LiquidRequest response = LiquidResponseHelper.forException(e, request);
        response.setOrigin(LiquidMessageOrigin.SERVER);
        messageSender.notifySession(response);
        LiquidRequest compensation = compensator.compensate(request);
        if (compensation != null) {
            messageSender.sendNotifications(compensation);
        }
        return response;
    }


    public void setCompensator(FountainRequestCompensator<LiquidRequest> compensator) {
        this.compensator = compensator;
    }

    public void setStore(FountainDataStore store) {
        this.store = store;
    }

    public void setMessageSender(LiquidMessageSender messageSender) {
        this.messageSender = messageSender;
    }
}
