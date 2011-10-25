package cazcade.vortex.comms.datastore.server;

import cazcade.common.Logger;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.impl.UUIDFactory;
import com.rabbitmq.client.Channel;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Note this is not synchronized.
 *
 * @author neilellis@cazcade.com
 */
public class NotificationSession {
    private final static Logger log = Logger.getLogger(NotificationSession.class);

    private Continuation continuation;
    private Channel channel;
    private List<LiquidMessage> messages = new ArrayList<LiquidMessage>();
    private String queueName = UUIDFactory.randomUUID().toString();
    private boolean closed;

    public NotificationSession() {
        log.debug("Opening notification session.");

    }


    public Channel getChannel() {
        return channel;
    }

    public void removeChannel() throws IOException {
        log.debug("Removing channel from notification session.");
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
        channel = null;
    }


    public ArrayList<LiquidMessage> getMessages() {
        ArrayList<LiquidMessage> result = new ArrayList<LiquidMessage>(messages);
        messages.removeAll(result);
        return result;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void close() throws IOException {
        log.debug("Closing notification session.");
        removeChannel();
        endContinuation();
        closed = true;
    }

    public void addMessage(LiquidMessage message) {
        messages.add(message);
    }

    public boolean hasMessages() {
        return messages.size() > 0;
    }

    public void endContinuation() {
        log.debug("Ending continuation.");
        if (continuation != null) {
            continuation.complete();
            continuation = null;
        }
    }

    public void suspendContinuation() {
        if (continuation != null) {
            log.debug("Suspending notification session.");
            continuation.suspend();
        }
    }

    public void resume() {
        if (continuation == null) {
            log.warn("Continuation was null.");
            return;
        }
        if (!continuation.isSuspended()) {
            log.warn("Continuation was not suspended.");
            return;
        }
        if (continuation.isExpired()) {
            log.warn("Continuation was expired.");
            return;
        }
        log.debug("Resuming notification session.");
        continuation.resume();
    }

    public boolean isInitial() {
        return channel == null;
    }

    public boolean isClosed() {
        return closed;
    }

    public void resetContinuation(Continuation continuation) {
        if (!continuation.equals(this.continuation)) {
            this.continuation = continuation;
            continuation.setTimeout(0);
            continuation.addContinuationListener(new ContinuationListener() {

                public void onComplete(Continuation con) {
                    log.debug("Completed continuation.");
                }

                public void onTimeout(Continuation con) {
                    log.debug("Timeout on continuation.");
                }
            });
        }
    }

    public void completeContinuation() {
        if (continuation != null) {
            continuation.complete();
        }
        continuation = null;
    }

    public void removeContinuation() {
        continuation = null;
    }
}
