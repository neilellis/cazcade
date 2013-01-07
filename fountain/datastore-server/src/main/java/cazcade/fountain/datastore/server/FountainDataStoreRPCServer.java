package cazcade.fountain.datastore.server;

import cazcade.common.Logger;
import cazcade.fountain.messaging.FountainPubSub;
import cazcade.fountain.messaging.LiquidMessageSender;
import cazcade.liquid.api.LiquidMessageHandler;
import cazcade.liquid.api.LiquidRequest;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author neilellis@cazcade.com
 */
public class FountainDataStoreRPCServer {
    @Nonnull
    private static final Logger log = Logger.getLogger(FountainDataStoreRPCServer.class);
    private DataStoreServerMessageHandler handler;
    private String topic;
    private LiquidMessageSender messageSender;
    private FountainPubSub pubSub;
    private long listenerId;

    public void start() throws IOException {
        listenerId = pubSub.addListener(topic, new LiquidMessageHandler<LiquidRequest>() {

            @Nonnull
            @Override
            public LiquidRequest handle(LiquidRequest message) throws Exception {
                final LiquidRequest response = handler.handle(message);
                if (response.shouldNotify()) {
                    messageSender.sendNotifications(response);
                }
                return response;
            }
        }
                                       );
    }

    public void stop() throws IOException {
        pubSub.removeListener(listenerId);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(final String topic) {
        this.topic = topic;
    }

    public void setHandler(final DataStoreServerMessageHandler handler) {
        this.handler = handler;
    }

    public void setMessageSender(final LiquidMessageSender messageSender) {
        this.messageSender = messageSender;
    }


    public void setPubSub(FountainPubSub pubSub) {this.pubSub = pubSub;}

    public FountainPubSub getPubSub() { return pubSub; }
}
