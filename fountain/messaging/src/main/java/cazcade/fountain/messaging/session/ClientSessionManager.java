/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

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
    private static final Logger log = Logger.getLogger(ClientSessionManager.class);

    private final ScheduledExecutorService                 reaper     = Executors.newSingleThreadScheduledExecutor();
    @Nonnull
    private final ConcurrentHashMap<String, ClientSession> sessionMap = new ConcurrentHashMap<String, ClientSession>();

    public void addSession(final String sessionId, final ClientSession session) {
        log.debug("Adding session for {0}", sessionId);
        sessionMap.put(sessionId, session);
    }

    public ClientSession getSession(final String sessionId) {
        log.debug("Retrieving session for {0}", sessionId);
        final ClientSession clientSession = sessionMap.get(sessionId);
        if (clientSession != null) {
            clientSession.markUsed();
        }
        return clientSession;
    }

    public boolean hasSession(final String sessionId) {
        final boolean result = sessionMap.containsKey(sessionId);
        if (!result) {
            log.debug("Session not found for {0}", sessionId);
        }
        return result;
    }

    public void start() {
        log.info("Starting up Client Session Manager");
        reaper.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                final Set<String> expiredSessions = new HashSet<String>();
                for (final Map.Entry<String, ClientSession> entry : sessionMap.entrySet()) {
                    if (entry.getValue().getLastUsed().getTime() < System.currentTimeMillis() - SESSION_TIMEOUT) {
                        expiredSessions.add(entry.getKey());
                    }
                }
                closeSesions(expiredSessions);
            }
        }, SESSION_TIMEOUT, SESSION_TIMEOUT / 4, TimeUnit.MILLISECONDS);
    }

    private void closeSesions(@Nonnull final Set<String> sessions) {
        for (final String session : sessions) {
            expireSession(session);
        }
    }

    public void expireSession(final String sessionIdentifier) {
        log.info("Expiring session for {0}", sessionIdentifier);
        final ClientSession clientSession = sessionMap.get(sessionIdentifier);
        clientSession.close();
        sessionMap.remove(sessionIdentifier);
    }

    public void stop() {
        log.warn("Shutting down Client Session Manager");
        reaper.shutdownNow();
        closeSesions(sessionMap.keySet());
    }
}
