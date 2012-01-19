package cazcade.fountain.server.rest.servlet;

/**
 * A RabbitMQ consumer that passes all deliveries to a Jetty continuation.
 */
public class ContinuationConsumer /*extends DefaultConsumer*/ {
//    private final Continuation continuation;
//
//    private static final Logger LOG = Logger.getLogger(ContinuationConsumer.class);
//
//    /**
//     * Constructs a new instance and records its association to the passed-in channel and continuation.
//     *
//     * @param channel the channel to which this consumer is attached
//     * @param continuation the continuation to notify of delivery.
//     */
//    public ContinuationConsumer(Channel channel, Continuation continuation) {
//        super(channel);
//        this.continuation = continuation;
//    }
//
//    @Override
//    @SuppressWarnings("unchecked")
//    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
//        synchronized (continuation) {
//            List<byte[]> messages = (List<byte[]>) continuation.getAttribute(CommonConstants.MESSAGES_ATTRIBUTE);
//            messages.add(body);
//            try {
//                if (continuation.isSuspended()) {
//                    continuation.resume();
//                }
//                getChannel().basicAck(envelope.getDeliveryTag(), false);
//            } catch (IllegalStateException ise) {
//                LOG.debug(ise.getMessage(), ise);
//            }
//        }
//    }
}
