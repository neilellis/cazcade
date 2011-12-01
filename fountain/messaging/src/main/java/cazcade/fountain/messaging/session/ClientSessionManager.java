package cazcade.fountain.messaging.session;

import cazcade.common.Logger;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author neilellis@cazcade.com
 */
public class ClientSessionManager {

    public static final long SESSION_TIMEOUT = 1200 * 1000;
//    public static final long SESSION_TIMEOUT = 400;

    @Nonnull
    private final static Logger log = Logger.getLogger(ClientSessionManager.class);

    private final ScheduledExecutorService reaper = Executors.newSingleThreadScheduledExecutor();
    @Nonnull
    private final ConcurrentHashMap<String, ClientSession> sessionMap = new ConcurrentHashMap<String, ClientSession>();

    public void addSession(String sessionId, ClientSession session) {
        log.debug("Adding session for {0}", sessionId);
        sessionMap.put(sessionId, session);
    }

    public boolean hasSession(String sessionId) {
        final boolean result = sessionMap.containsKey(sessionId);
        if (!result) {
            log.debug("Session not found for {0}", sessionId);
        }
        return result;
    }

    public ClientSession getSession(String sessionId) {
        log.debug("Retrieving session for {0}", sessionId);
        final ClientSession clientSession = sessionMap.get(sessionId);
        if (clientSession != null) {
            clientSession.markUsed();
        }
        return clientSession;
    }

    public void start() {
        log.info("Starting up Client Session Manager");
        reaper.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                Set<String> expiredSessions = new HashSet<String>();
                for (Map.Entry<String, ClientSession> entry : sessionMap.entrySet()) {
                    if (entry.getValue().getLastUsed().getTime() < (System.currentTimeMillis() - SESSION_TIMEOUT)) {
                        expiredSessions.add(entry.getKey());
                    }
                }
                closeSesions(expiredSessions);
            }
        }, SESSION_TIMEOUT, SESSION_TIMEOUT / 4, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        log.warn("Shutting down Client Session Manager");
        reaper.shutdownNow();
        closeSesions(sessionMap.keySet());
    }

    private void closeSesions(@Nonnull Set<String> sessions) {
        for (String session : sessions) {
            expireSession(session);
        }
    }

    public void expireSession(String sessionIdentifier) {
        log.info("Expiring session for {0}", sessionIdentifier);
        final ClientSession clientSession = sessionMap.get(sessionIdentifier);
        clientSession.close();
        sessionMap.remove(sessionIdentifier);
    }


}
