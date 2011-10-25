package cazcade.fountain.datastore.server;

import cazcade.common.Logger;
import cazcade.fountain.messaging.LiquidMessageSender;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequest;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.RpcServer;
import com.rabbitmq.client.ShutdownSignalException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;

/**
 * @author neilellis@cazcade.com
 */
public class FountainDataStoreRPCServer {
    private final static Logger log = Logger.getLogger(FountainDataStoreRPCServer.class);
    private RabbitTemplate template;
    private DataStoreServerMessageHandler handler;
    private RpcServer rpcServer;
    private String queue;
    private LiquidMessageSender messageSender;

    public void start() throws IOException {
        log.info("Starting RabbitMQ RPC Server");

        rpcServer = new RpcServer(template.getConnectionFactory().createConnection().createChannel(false), queue) {

            @Override
            public byte[] handleCall(byte[] requestBody, AMQP.BasicProperties replyProperties) {
                try {
                    log.debug("Handling RabbitMQ RPC call");
                    final LiquidMessage message = (LiquidMessage) template.getMessageConverter().fromMessage(new Message(requestBody, null));
                    final LiquidRequest response = (LiquidRequest) handler.handle(message);
                    LiquidRequest request = (LiquidRequest) message;
                    if (response.shouldNotify()) {
                        //Notify if async
                        //i.e. for pool visits.
                        messageSender.sendNotifications(response);
                    }
                    return template.getMessageConverter().toMessage(response, null).getBody();
                } catch (Exception e) {
                    log.error(e);
                    return new byte[0];
                }
            }

        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ShutdownSignalException exception = rpcServer.mainloop();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }).start();
        log.info("Started RabbitMQ RPC Server");
    }

    public void stop() throws IOException {
        rpcServer.close();
    }

    public void setTemplate(RabbitTemplate template) {
        this.template = template;
    }

    public void setHandler(DataStoreServerMessageHandler handler) {
        this.handler = handler;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getQueue() {
        return queue;
    }


    public void setMessageSender(LiquidMessageSender messageSender) {
        this.messageSender = messageSender;
    }
}
