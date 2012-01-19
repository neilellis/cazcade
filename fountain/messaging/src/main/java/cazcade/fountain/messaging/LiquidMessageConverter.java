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
    private static final Logger log = Logger.getLogger(LiquidMessageConverter.class);

    @Override
    public Object fromMessage(@Nonnull final Message message) throws MessageConversionException {
        final String xml = new String(message.getBody());
        log.debug("<<<<< Converting from {0}", xml);
        final Object object = LiquidXStreamFactory.getXstream().fromXML(xml);
        if (object instanceof LiquidMessage) {
            return object;
        }
        else if (object instanceof RuntimeException) {
            throw (RuntimeException) object;
        }
        else if (object instanceof Exception) {
            throw new MessageConversionException("Exception sent as object.", (Throwable) object);
        }
        else {
            return object;
        }
    }

    @Nonnull
    @Override
    public Message toMessage(final Object object, final MessageProperties messageProperties) throws MessageConversionException {
        final String xml = LiquidXStreamFactory.getXstream().toXML(object);
        log.debug(">>>>> Converted to {0}", xml);
        return new Message(xml.getBytes(), new MessageProperties());
    }
}
