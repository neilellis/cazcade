package cazcade.fountain.messaging.continuations;

import cazcade.common.Logger;
import cazcade.fountain.messaging.session.ClientSession;
import cazcade.fountain.messaging.session.ClientSessionManager;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidMessageHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public class ClientSessionMessageListener implements LiquidMessageHandler {
    @Nonnull
    private static final Logger log = Logger.getLogger(ClientSessionMessageListener.class);
    private ClientSessionManager sessionManager;
    private String sessionId;

    @Nullable
    @Override
    public LiquidMessage handle(final LiquidMessage message) throws Exception {
        try {
            final ClientSession session = sessionManager.getSession(sessionId);
            if (session != null) {
                session.addMessage(message);
            }
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }

    public void setSessionManager(final ClientSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }
}
