package cazcade.fountain.messaging;

import cazcade.common.Logger;
import cazcade.liquid.api.LiquidRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class LiquidMessageSender {

    @Nonnull
    private final static Logger log = Logger.getLogger(LiquidMessageSender.class);

    private RabbitTemplate template;


    public void setTemplate(RabbitTemplate template) {
        this.template = template;
    }


    public void sendNotifications(@Nonnull LiquidRequest request) {
        notifySession(request);
        notifyLocation(request);
    }

    public void notifySession(@Nonnull LiquidRequest request) {
        log.debug("Ready to send notification to session(s).");
        String session = request.getNotificationSession();
        template.convertAndSend("session." + session, request);
        log.debug("Notification(s) sent.");
    }

    public void notifyLocation(@Nonnull LiquidRequest request) {
        log.debug("Ready to send location notification(s).");

        List<String> locations = request.getNotificationLocations();


        if (locations != null) {
            for (String location : locations) {
                log.debug("Notifying location {0}.", location);
                template.convertAndSend("location." + location, request);
                log.debug("Notification(s) sent.");
            }
        }

        log.debug("Notification(s) sent.");
    }


    public void dispatch(String key, LiquidRequest request) {
        template.convertAndSend(key, request);
    }

//    public LiquidRequest sendRPC(LiquidRequest request) {
//        template.convertAndSend("server.rpc", "server.rpc", request);
//        template.receiveAndConvert("rpc")
//    }
}
