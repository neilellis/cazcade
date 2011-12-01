package cazcade.vortex.comms.datastore.messaging;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;

/**
 * @author neilellis@cazcade.com
 */
public class GwtClientMessageListener implements ChannelAwareMessageListener {
    @Override
    public void onMessage(final Message message, final Channel channel) throws Exception {
        //TODO
    }
}
