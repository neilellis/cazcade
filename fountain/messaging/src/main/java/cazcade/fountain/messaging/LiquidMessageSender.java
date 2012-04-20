package cazcade.fountain.messaging;

import cazcade.common.Logger;
import cazcade.liquid.api.LiquidRequest;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class LiquidMessageSender {
    @Nonnull
    private static final Logger log = Logger.getLogger(LiquidMessageSender.class);
    private FountainPubSub pubSub;


    public void dispatch(final String key, final LiquidRequest request) {
        pubSub.dispatch(key, request);
    }

    public void sendNotifications(@Nonnull final LiquidRequest request) {
        notifySession(request);
        notifyLocation(request);
    }

    public void notifySession(@Nonnull final LiquidRequest request) {
        log.debug("Ready to send notification to session(s).");
        final String session = request.getNotificationSession();
        pubSub.dispatch("session." + session, request);
        log.debug("Notification(s) sent.");
    }

    public void notifyLocation(@Nonnull final LiquidRequest request) {
        log.debug("Ready to send location notification(s).");

        final List<String> locations = request.getNotificationLocations();


        if (locations != null) {
            for (final String location : locations) {
                log.debug("Notifying location {0}.", location);
                pubSub.dispatch("location." + location, request);
                log.debug("Notification(s) sent.");
            }
        }

        log.debug("Notification(s) sent.");
    }


//    public LiquidRequest sendRPC(LiquidRequest request) {
//        template.convertAndSend("server.rpc", "server.rpc", request);
//        template.receiveAndConvert("rpc")
//    }

    public void setPubSub(FountainPubSub pubSub) {this.pubSub = pubSub;}

    public FountainPubSub getPubSub() { return pubSub; }
}
