/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.server.rest.servlet;

/**
 * @author neilellis@cazcade.com
 */
public class LiquidNotificationHelper {
    //    private final static Logger log = Logger.getLogger(LiquidNotificationHelper.class);
    //
    //    public static final byte[] KEEP_ALIVE_MESSAGE;
    //    public static final byte[] END_STREAM_TAG_MESSAGE;
    //    public static final byte[] BEGIN_STREAM_TAG_MESSAGE;
    //
    //    static {
    //        try {
    //            END_STREAM_TAG_MESSAGE = "</notifications>".getBytes(STRING_ENCODING);
    //            KEEP_ALIVE_MESSAGE = "<heartbeat>SERVER STATUS HERE</heartbeat>".getBytes(STRING_ENCODING);
    //            BEGIN_STREAM_TAG_MESSAGE = "<notifications xmlns='http://schema.cazcade.com/liquid/1.0'>".getBytes(STRING_ENCODING);
    //        } catch (UnsupportedEncodingException e) {
    //            log.error(e);
    //            throw new Error(e);
    //        }
    //
    //    }
    //
    //    public static final synchronized void initialiseContinuation(HAConnection connection, HttpServletResponse resp, String sessionId, String alias, final Continuation continuation, final Logger log) throws IOException {
    //        //direct messages to the client's session
    //        final String sessionExchange = sessionId;
    //
    //        //Establish the channel and its relationship with the continuation.
    //        HAChannel asyncChannel = connection.createChannel();
    //        final ContinuationConsumer consumer = new ContinuationConsumer(asyncChannel, continuation);
    //        LiquidChannelConfiguration channelConfiguration = new LiquidChannelConfiguration(sessionExchange, alias, consumer);
    //
    //        //Finish configuring the continuation before suspending it.
    //        //continuation.setTimeout(CommonConstants.NOTIFICATION_TIMEOUT);
    //        continuation.$(CommonConstants.CHANNEL_ATTRIBUTE, asyncChannel);
    //        continuation.$(CommonConstants.OUTPUT_STREAM_ATTRIBUTE, resp.getOutputStream());
    //        continuation.$(CommonConstants.MESSAGES_ATTRIBUTE, Collections.synchronizedList(new ArrayList<String>()));
    //        continuation.$(CommonConstants.LIQUID_CHANNEL_CONFIGURATION, channelConfiguration);
    //        continuation.suspend();
    //
    //        outputXML(BEGIN_STREAM_TAG_MESSAGE, continuation);
    //
    //        continuation.addContinuationListener(new ContinuationListener() {
    //
    //            public void onComplete(Continuation con) {
    //                log.info(con.isExpired() ? "Expired " : "Non-expired " + "Continuation finishing: " + con.toString());
    //                endContinuation(continuation);
    //            }
    //
    //            public void onTimeout(Continuation con) {
    //                log.debug("Timeout on continuation.");
    //            }
    //        });
    //
    //        //Start the channel to listening for messages.
    //        asyncChannel.configureChannel(channelConfiguration);
    //        continuation.$(CommonConstants.QUEUE_ATTRIBUTE, channelConfiguration.getQueueName());
    //
    //    }
    //
    //    @SuppressWarnings("unchecked")
    //    public static synchronized void performContinuation(String sessionId, final Continuation continuation) throws IOException {
    //        continuation.suspend();
    //        //this order of add and remove to avoid threading issues.
    //        List<byte[]> messageList = (List<byte[]>) continuation.$(CommonConstants.MESSAGES_ATTRIBUTE);
    //        List<byte[]> messages = new ArrayList<byte[]>(messageList);
    //        messageList.removeAll(messages);
    //        try {
    //            if (messages.size() == 0) {
    //                outputXML(("<heartbeat poolURI='" + continuation.$(CommonConstants.POOL_URI_ATTRIBUTE) + "'>OK</heartbeat>").getBytes(CommonConstants.STRING_ENCODING), continuation);
    //            } else {
    //                for (byte[] message : messages) {
    //                    Object messageObject = LiquidXStreamFactory.getXstream().fromXML(new String(message, CommonConstants.STRING_ENCODING));
    //                    if (messageObject instanceof VisitPoolRequest && ((VisitPoolRequest) messageObject).session().session().toString().equals(sessionId)) {
    //                        log.debug("**** Pool visit, so now switching pools. ****");
    //                        handlePoolVisit(continuation, messageObject);
    //
    //                    }
    //                    outputXML(message, continuation);
    //                }
    //            }
    //        } catch (org.eclipse.jetty.io.EofException eof) {
    //            log.debug("Client gone away.");
    //            complete(continuation);
    //        } catch (Exception e) {
    //            log.error(e);
    //            complete(continuation);
    //        }
    //    }
    //
    //    public static synchronized void endContinuation(Continuation continuation) {
    //        try {
    //            String queueName = (String) continuation.$(CommonConstants.QUEUE_ATTRIBUTE);
    //            Channel channel = (Channel) continuation.$(CommonConstants.CHANNEL_ATTRIBUTE);
    //            try {
    //                channel.queueDelete(queueName);
    //            } finally {
    //                channel.close();
    //            }
    //            outputXML(END_STREAM_TAG_MESSAGE, continuation);
    //            ((OutputStream) continuation.$(CommonConstants.OUTPUT_STREAM_ATTRIBUTE)).close();
    //        } catch (IOException ioe) {
    //            //To be expected we've got a disconnected sender
    //        } catch (RuntimeIOException e) {
    //            //To be expected we've got a disconnected sender
    //        }
    //    }
    //
    //    private static void handlePoolVisit(Continuation continuation, Object messageObject) throws IOException {
    //        //We have visited a pool so we now need to listen to events there.
    //        Channel channel = (Channel) continuation.$(CommonConstants.CHANNEL_ATTRIBUTE);
    //        LiquidChannelConfiguration channelConfiguration = (LiquidChannelConfiguration)
    //                continuation.$(CommonConstants.LIQUID_CHANNEL_CONFIGURATION);
    //        VisitPoolRequest request = (VisitPoolRequest) messageObject;
    //        if (request.getState() == LiquidMessageState.SUCCESS) {
    //            log.debug("Switching pools....");
    //            channelConfiguration.reconfigurePoolBindings(request.response().getID(),
    //                    request.response().uri(), channel);
    //        }
    //    }
    //
    //    static void outputXML(byte[] output, Continuation continuation) throws IOException {
    //        log.session(output);
    //        try {
    //            log.debug("Writing out {0} ", new String(output, STRING_ENCODING));
    //        } catch (UnsupportedEncodingException e) {
    //            log.error(e);
    //        }
    //
    //        OutputStream out = (OutputStream) continuation.$(CommonConstants.OUTPUT_STREAM_ATTRIBUTE);
    //        out.write(output);
    //        out.write('\n');
    //        out.flush();
    //        log.debug("Wrote out response.");
    //
    //    }
    //
    //    private static void complete(Continuation continuation) {
    //        try {
    //            if (continuation.isSuspended()) {
    //                continuation.complete();
    //            }
    //        } catch (Exception e) {
    //            log.warn("{0}", e.getMessage());
    //        }
    //    }
}
