package cazcade.fountain.messaging.session;

/**
 * @author neilellis@cazcade.com
 */

import cazcade.common.Logger;
import cazcade.liquid.api.LiquidMessage;
import org.eclipse.jetty.continuation.Continuation;
import org.springframework.amqp.core.Queue;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class ClientSession {

    private final static Logger log = Logger.getLogger(ClientSession.class);

    private ClassPathXmlApplicationContext springContext;
    private Date lastUsed;
    private final List<LiquidMessage> messages = new ArrayList<LiquidMessage>();
    private Continuation continuation;
    private ArrayList<String> previousLocations= new ArrayList<String>();
    private Queue sessionQueue;

    public ClientSession(ClassPathXmlApplicationContext springContext, Date lastUsedDate) {
        this.springContext = springContext;
        this.lastUsed = lastUsedDate;
    }

    public ClassPathXmlApplicationContext getSpringContext() {
        return springContext;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public void markUsed() {
        log.debug("Marking session used.");
        lastUsed = new Date();
    }

    public void close() {
        log.debug("Closing session.");
        springContext.stop();
    }

    public void addMessage(LiquidMessage message) {
        log.debug("Adding message to session.");
        synchronized (messages) {
            messages.add(message);
        }
        if (continuation != null) {
            if (continuation.isSuspended()) {
                continuation.resume();
            }
        }
    }

    public synchronized ArrayList<LiquidMessage> removeMessages() {
//        log.debug("Removing messages from session.");
        final ArrayList<LiquidMessage> result = new ArrayList<LiquidMessage>(messages);
        synchronized (messages) {
            messages.removeAll(result);
        }
        return result;
    }


    public void setContinuation(Continuation continuation) {
        this.continuation = continuation;
    }

    public void setPreviousLocations(ArrayList<String> location) {
        this.previousLocations = location;
    }

    public ArrayList<String> getPreviousLocations() {
        return previousLocations;
    }

    @Deprecated
    public void addPreviousLocations(ArrayList<String> locations) {
        previousLocations.addAll(locations);
    }

    public void setSessionQueue(Queue sessionQueue) {
        this.sessionQueue = sessionQueue;
    }

    public Queue getSessionQueue() {
        return sessionQueue;
    }
}
