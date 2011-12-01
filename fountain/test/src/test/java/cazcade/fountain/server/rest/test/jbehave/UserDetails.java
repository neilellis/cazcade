package cazcade.fountain.server.rest.test.jbehave;

import cazcade.fountain.server.rest.test.ClientSession;
import cazcade.liquid.api.LiquidUUID;
import org.apache.commons.httpclient.Credentials;

/**
 * Representation of user details as defined in the tests...
 */
public class UserDetails {
    private final String username;
    private final LiquidUUID userId;
    private final Credentials credentials;
    private final String sessionId;
    private final ClientSession userSession;
    private final String homePool;
    private final String homePoolId;
    private final String publicPoolId;

    public UserDetails(final String username, final LiquidUUID userId, final Credentials credentials, final String sessionId,
                       final ClientSession userSession, final String homePool, final String homePoolId, final String publicPoolId) {
        this.username = username;
        this.userId = userId;
        this.credentials = credentials;
        this.sessionId = sessionId;
        this.userSession = userSession;
        this.homePool = homePool;
        this.homePoolId = homePoolId;
        this.publicPoolId = publicPoolId;
    }

    public String getUsername() {
        return username;
    }

    public LiquidUUID getUserId() {
        return userId;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public String getSessionId() {
        return sessionId;
    }

    public ClientSession getUserSession() {
        return userSession;
    }

    public String getHomePool() {
        return homePool;
    }

    public String getHomePoolId() {
        return homePoolId;
    }

    public String getPublicPoolId() {
        return publicPoolId;
    }
}
