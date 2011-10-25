package cazcade.fountain.server.rest.test;

import org.apache.commons.httpclient.HttpClient;

public class ClientSession {
    private final HttpClient client;
    private final String sessionId;
    private final String testUsername;

    public ClientSession(HttpClient client, String sessionId, String testUsername) {
        this.client = client;
        this.sessionId = sessionId;
        this.testUsername = testUsername;
    }

    public HttpClient getClient() {
        return client;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getTestUsername() {
        return testUsername;
    }
}
