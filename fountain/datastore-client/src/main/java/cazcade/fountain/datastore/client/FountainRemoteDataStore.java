package cazcade.fountain.datastore.client;

import cazcade.common.Logger;
import cazcade.fountain.common.service.AbstractServiceStateMachine;
import cazcade.fountain.datastore.api.FountainDataStore;
import cazcade.fountain.datastore.client.validation.SecurityValidator;
import cazcade.fountain.datastore.client.validation.SecurityValidatorImpl;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.messaging.FountainPubSub;
import cazcade.fountain.messaging.InMemoryPubSub;
import cazcade.fountain.messaging.LiquidMessageSender;
import cazcade.fountain.validation.api.FountainRequestValidator;
import cazcade.fountain.validation.api.ValidationLevel;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidMessageState;
import cazcade.liquid.api.LiquidRequest;
import cazcade.liquid.impl.UUIDFactory;

import javax.annotation.Nonnull;
import java.io.IOException;

import static cazcade.common.CommonConstants.RPC_EXCHANGE;
import static cazcade.common.CommonConstants.SERVICE_STORE;

/**
 * @author neilelliz@cazcade.com
 *         TODO: move all the async messaging stuff in here too.
 */
public class FountainRemoteDataStore extends AbstractServiceStateMachine implements FountainDataStore {
    @Nonnull
    private static final Logger log = Logger.getLogger(FountainRemoteDataStore.class);
    @Nonnull

    private FountainRequestValidator requestValidator;
    private SecurityValidator securityValidator;


    private boolean alwaysSynchronous;
    private LiquidMessageSender messageSender;
    private FountainPubSub pubSub;


    public FountainRemoteDataStore() {
        super();
    }

    @Nonnull
    public LiquidRequest process(@Nonnull final LiquidRequest request) throws InterruptedException {
        log.debug("Processing request " + request.getId());
        begin();
        try {
            if (request.getId() == null) {
                request.setId(UUIDFactory.randomUUID());
            }
            if (request.getRequestEntity() != null && request.getRequestEntity().getUpdated() == null) {
                request.getRequestEntity().timestamp();
            }
            requestValidator.validate(request, ValidationLevel.MODERATE);

            //pre-validate provisional messages
            if (securityValidator != null && request.shouldSendProvisional()) {
                final LiquidRequest failureMessage = securityValidator.validate(request);
                if (failureMessage != null) {
                    return LiquidResponseHelper.forFailure(request, failureMessage);
                }
            }

            request.setState(LiquidMessageState.PROVISIONAL);

            if (request.shouldNotify() && request.shouldSendProvisional()) {
                notifyOfRequest(request);
            }

            if (request.isAsyncRequest() && !alwaysSynchronous) {
                return (LiquidRequest) processAsync(request);
            }
            else {
                final Object result;
                result = processSync(request);
                if (result instanceof LiquidRequest) {
                    return (LiquidRequest) result;
                }
                else if (result instanceof Exception) {
                    throw (Exception) result;
                }
                else {
                    throw new Error("Unexpected return type from synchronous call to data store: " + result.getClass());
                }
            }
        } catch (Exception e) {
            log.error(e);
            return LiquidResponseHelper.forException(e, request);
        } finally {
            end();
        }
    }

    private void notifyOfRequest(@Nonnull final LiquidRequest request) {
        log.debug("Sending notifications");
        messageSender.sendNotifications(request);
    }

    @Nonnull
    private LiquidMessage processAsync(@Nonnull final LiquidRequest request) throws IOException {
        log.debug("Sending Asynchronous Request");
        messageSender.dispatch(SERVICE_STORE, request);
        return LiquidResponseHelper.forDeferral(request);
    }

    private LiquidRequest processSync(final LiquidRequest request) throws Exception {
        return pubSub.sendSync(RPC_EXCHANGE, request);
    }

    @Override
    public void start() throws Exception {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }

    public void setAlwaysSynchronous(final boolean alwaysSynchronous) {
        this.alwaysSynchronous = alwaysSynchronous;
    }

    public void setMessageSender(final LiquidMessageSender messageSender) {
        this.messageSender = messageSender;
    }

   
    public void setRequestValidator(@Nonnull final FountainRequestValidator requestValidator) {
        this.requestValidator = requestValidator;
    }

    public void setSecurityValidator(final SecurityValidatorImpl securityValidator) {
        this.securityValidator = securityValidator;
    }

   

    public void setPubSub(InMemoryPubSub pubSub) {this.pubSub = pubSub;}

}
