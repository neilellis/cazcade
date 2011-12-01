package cazcade.fountain.datastore.client;

import cazcade.common.Logger;
import cazcade.fountain.common.service.AbstractServiceStateMachine;
import cazcade.fountain.datastore.api.FountainDataStore;
import cazcade.fountain.datastore.client.validation.SecurityValidator;
import cazcade.fountain.datastore.client.validation.SecurityValidatorImpl;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.messaging.LiquidMessageSender;
import cazcade.fountain.validation.api.FountainRequestValidator;
import cazcade.fountain.validation.api.ValidationLevel;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidMessageState;
import cazcade.liquid.api.LiquidRequest;
import cazcade.liquid.impl.UUIDFactory;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.RpcClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import javax.annotation.Nonnull;
import java.io.IOException;

import static cazcade.common.CommonConstants.RABBITMQ_FOUNTAIN_STORE_REQUEST_KEY;
import static cazcade.common.CommonConstants.RABBITMQ_RPC_EXCHANGE_NAME;

/**
 * @author neilelliz@cazcade.com
 *         TODO: move all the async messaging stuff in here too.
 */
public class FountainRemoteDataStore extends AbstractServiceStateMachine implements FountainDataStore {
    @Nonnull
    private final static Logger log = Logger.getLogger(FountainRemoteDataStore.class);
    @Nonnull
    private static final ThreadLocal<RpcClient> rpcClientThreadLocal = new ThreadLocal<RpcClient>();

    private FountainRequestValidator requestValidator;
    private SecurityValidator securityValidator;


    private boolean alwaysSynchronous;
    private LiquidMessageSender messageSender;
    private RabbitTemplate template;
    private RabbitAdmin rabbitAdmin;
//    private FountainDataStore syncDataStore;


    public FountainRemoteDataStore() {
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
                LiquidRequest failureMessage = securityValidator.validate(request);
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
            } else {
                Object result;
                result = processSync(request);
                if (result instanceof LiquidMessage) {
                    return (LiquidRequest) result;
                } else if (result instanceof Exception) {
                    throw (Exception) result;
                } else {
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

    private Object processSync(final LiquidRequest request) {
        return template.execute(new ChannelCallback<Object>() {
            @Override
            public Object doInRabbit(Channel channel) throws Exception {
                final RpcClient rpcClient = new RpcClient(channel, RABBITMQ_RPC_EXCHANGE_NAME, "");
                return template.getMessageConverter().fromMessage(new Message(rpcClient.primitiveCall(template.getMessageConverter().toMessage(request, null).getBody()), null));
            }
        });

    }


    @Nonnull
    private LiquidMessage processAsync(@Nonnull LiquidRequest request) throws IOException {
        log.debug("Sending Asynchronous Request");
        messageSender.dispatch(RABBITMQ_FOUNTAIN_STORE_REQUEST_KEY, request);
        return LiquidResponseHelper.forDeferral(request);
    }

    private void notifyOfRequest(@Nonnull LiquidRequest request) {
        log.debug("Sending notifications");
        messageSender.sendNotifications(request);
    }

    @Override
    public void start() throws Exception {
        super.start();

    }

    @Override
    public void stop() {
        super.stop();
    }

    public void setRequestValidator(FountainRequestValidator requestValidator) {
        this.requestValidator = requestValidator;
    }

    public void setSecurityValidator(SecurityValidatorImpl securityValidator) {
        this.securityValidator = securityValidator;
    }


    public void setAlwaysSynchronous(boolean alwaysSynchronous) {
        this.alwaysSynchronous = alwaysSynchronous;
    }

    public void setMessageSender(LiquidMessageSender messageSender) {
        this.messageSender = messageSender;
    }


    public void setTemplate(RabbitTemplate template) {
        this.template = template;
    }


    public void setRabbitAdmin(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

}
