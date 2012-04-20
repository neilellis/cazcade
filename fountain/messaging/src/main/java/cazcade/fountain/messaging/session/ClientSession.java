package cazcade.fountain.messaging.session;

/**
 * @author neilellis@cazcade.com
 */

import cazcade.common.Logger;
import cazcade.fountain.messaging.FountainPubSub;

import javax.annotation.Nonnull;
import java.util.Date;

/**
 * @author neilellis@cazcade.com
 */
public class ClientSession {
    @Nonnull
    private static final Logger log = Logger.getLogger(ClientSession.class);

    private Date lastUsed;

    private FountainPubSub.Collector collector;

    public ClientSession(final Date lastUsedDate, FountainPubSub.Collector collector) {
        lastUsed = lastUsedDate;
        this.collector = collector;
    }

//    public void addMessage(final LiquidMessage message) {
//        log.debug("Adding message to session.");
//        synchronized (messages) {
//            messages.add(message);
//        }
//        if (continuation != null) {
//            if (continuation.isSuspended()) {
//                continuation.resume();
//            }
//        }
//    }

//    @Deprecated
//    public void addPreviousLocations(final ArrayList<String> locations) {
//        previousLocations.addAll(locations);
//    }

    public void close() {
        log.debug("Closing session.");
        collector.close();
    }

    public void markUsed() {
        log.debug("Marking session used.");
        lastUsed = new Date();
    }

    public Date getLastUsed() {
        return lastUsed;
    }

//    public ArrayList<String> getPreviousLocations() {
//        return previousLocations;
//    }
//
//    public void setPreviousLocations(final ArrayList<String> location) {
//        previousLocations = location;
//    }




    public FountainPubSub.Collector getCollector() {
        return collector;
    }
}
