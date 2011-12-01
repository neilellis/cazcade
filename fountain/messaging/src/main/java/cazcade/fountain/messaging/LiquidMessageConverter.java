package cazcade.fountain.messaging;

import cazcade.common.Logger;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.impl.xstream.LiquidXStreamFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class LiquidMessageConverter implements MessageConverter {

    @Nonnull
    private final static Logger log = Logger.getLogger(LiquidMessageConverter.class);

    @Nonnull
    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        final String xml = LiquidXStreamFactory.getXstream().toXML(object);
        log.debug(">>>>> Converted to {0}", xml);
        return new Message(xml.getBytes(), new MessageProperties());
    }

    @Override
    public Object fromMessage(@Nonnull Message message) throws MessageConversionException {
        final String xml = new String(message.getBody());
        log.debug("<<<<< Converting from {0}", xml);
        Object object = LiquidXStreamFactory.getXstream().fromXML(xml);
        if (object instanceof LiquidMessage) {
            return object;
        } else if (object instanceof RuntimeException) {
            throw (RuntimeException) object;
        } else if (object instanceof Exception) {
            throw new MessageConversionException("Exception sent as object.", (Throwable) object);
        } else {
            return object;
        }
    }
}
