package cazcade.fountain.server.rest.servlet;

import cazcade.common.Logger;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.LiquidUUID;


import java.io.IOException;

/**
 * HAChannelConfiguration instance that handles the channel configuration including binding involved in pool movement.
 */
public class LiquidChannelConfiguration /* implements HAChannelConfiguration */{
//    private final String sessionExchange;
//    private String aliasExchange;
//    private final Consumer consumer;
//    private String queueName;
//    private LiquidUUID poolUUID;
//    private LiquidURI poolURI;
//    private static final Logger log = Logger.getLogger(LiquidChannelConfiguration.class);
//
//    public LiquidChannelConfiguration(String sessionExchange, String aliasExchange, Consumer consumer) {
//        this.sessionExchange = sessionExchange;
//        this.aliasExchange = aliasExchange;
//        this.consumer = consumer;
//    }
//
//    public String getQueueName() {
//        return queueName;
//    }
//
//    /**
//     * Configure the channel to listen for session messages and, if configured any pool messages
//     *
//     * @param channel The channel to use.
//     * @throws IOException if there is a problem with the configuration.
//     */
//    public void configureChannel(Channel channel) throws IOException {
//        queueName = channel.queueDeclare().getQueue();
//        channel.exchangeDeclare(sessionExchange, "fanout", false, true, null);
//        channel.queueBind(queueName, sessionExchange, sessionExchange);
//        channel.exchangeDeclare(aliasExchange, "fanout", false, true, null);
//        channel.queueBind(queueName, aliasExchange, aliasExchange);
//        bindPool(poolUUID, poolURI, channel);
//
//        channel.basicConsume(queueName, consumer);
//    }
//
//    //TODO determine behaviour when an error occurs in binding.
//
//    private void bindPool(LiquidUUID poolUUID, LiquidURI poolURI, Channel channel) throws IOException {
//        if (poolUUID != null) {
//            channel.exchangeDeclare(poolUUID.toString(), "fanout", false, true, null);
//            channel.queueBind(queueName, poolUUID.toString(), poolUUID.toString());
//        }
//
//        if (poolURI != null) {
//            channel.exchangeDeclare(poolURI.toString(), "fanout", false, true, null);
//            channel.queueBind(queueName, poolURI.toString(), poolURI.toString());
//        }
//    }
//
//    /**
//     * Move the queue bindings to a new pool, unbinding the old queue if required.
//     *
//     * @param newPoolUUID The UUID of the new pool.
//     * @param newPoolURI  The URI of the new pool.
//     * @param channel     The channel involved in the binding.
//     * @throws IOException if there is a problem in the bindings change.
//     */
//    public void reconfigurePoolBindings(LiquidUUID newPoolUUID, LiquidURI newPoolURI, Channel channel) throws IOException {
//        synchronized (this) {
//            bindPool(newPoolUUID, newPoolURI, channel);
//            unbindPool(poolUUID, poolURI, channel);
//            this.poolUUID = newPoolUUID;
//            this.poolURI = newPoolURI;
//        }
//    }
//
//    private void unbindPool(LiquidUUID poolUUID, LiquidURI poolURI, Channel channel) {
//        try {
//            if (poolUUID != null) {
//                channel.queueUnbind(queueName, poolUUID.toString(), poolUUID.toString());
//            }
//        } catch (IOException e) {
//            log.warn(e, "Error unbinding queue from {0}", queueName, poolUUID);
//        } catch (AlreadyClosedException ace) {
//            log.warn(ace, "Error unbinding queue {0} from {1}", queueName, poolURI);
//        }
//        try {
//            if (poolURI != null) {
//                channel.queueUnbind(queueName, poolURI.toString(), poolURI.toString());
//            }
//        } catch (IOException e) {
//            log.warn(e, "Error unbinding queue {0} from {1}", queueName, poolURI);
//        } catch (AlreadyClosedException ace) {
//            log.warn(ace, "Error unbinding queue {0} from {1}", queueName, poolURI);
//        }
//
//    }

}
