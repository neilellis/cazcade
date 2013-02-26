/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.server.rest.test;

import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.Types;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author neilelliz@cazcade.com
 */
public class FountainTestDataBuilderClient {
    @Nonnull
    private static final String username = "dtv";
    private static ClientSession clientSession;

    static {
    }

    public static void main(final String[] args) throws IOException {
        final HttpClient client = new HttpClient();
        client.getState()
              .setCredentials(new AuthScope(FountainTestClientSupport.host, FountainTestClientSupport.port, AuthScope.ANY_REALM), new UsernamePasswordCredentials("anon", "anon"));
        client.getParams().setAuthenticationPreemptive(true);


        FountainTestClientSupport.callRESTApiWithGet(new ClientSession(client, null, username),
                "user/create.xml?.email=neil.ellis@mangala.co.uk&.name="
                +
                username
                +
                "&.security.password.plain="
                +
                username
                +
                "&.fn=Neil+Ellis&.type="
                +
                Types.T_USER.getValue());
        client.getState()
              .setCredentials(new AuthScope(FountainTestClientSupport.host, FountainTestClientSupport.port, AuthScope.ANY_REALM), new UsernamePasswordCredentials(username, username));
        final Entity sessionEntity = FountainTestClientSupport.callRESTApiWithGet(new ClientSession(client, null, username), "session/create.xml?client=TestDataBuilder&key=123&hostinfo=macosx");
        final String sessionId = sessionEntity.id().toString();

        clientSession = new ClientSession(client, sessionId, username);
        //        FountainTestClientSupport.callRESTApiWithGet(clientSession, "alias/create.xml?me&.name=neilellis&.network=twitter&.type=Identity.Person.Alias");
        //        TransferEntity aliasEntity = FountainTestClientSupport.callRESTApiWithGet(clientSession, "alias.xml?uri=alias:twitter:neilellis");
        //        assertTrue(aliasEntity.type().getPrimaryType().toString().equals(LSDTypeDictionary.ALIAS.$()));


        initHomePool();
        initStream();
        initInbox();
    }

    private static void initHomePool() throws IOException {
        final Entity poolEntity = FountainTestClientSupport.callRESTApiWithGet(clientSession, "pool.xml?url=pool:///people/"
                                                                                              + username);
        final String entityId = poolEntity.id().toString();
        FountainTestClientSupport.putEntityToURL(clientSession, "pool/"
                                                                + entityId
                                                                + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("TestObject1"),
                "alias:cazcade:"
                + username);
        FountainTestClientSupport.putEntityToURL(clientSession, "pool/"
                                                                + entityId
                                                                + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("TestObject2"),
                "alias::cazcade:"
                + username);
        FountainTestClientSupport.putEntityToURL(clientSession, "pool/"
                                                                + entityId
                                                                + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("TestObject3"),
                "alias:cazcade:"
                + username);
        FountainTestClientSupport.putEntityToURL(clientSession, "pool/"
                                                                + entityId
                                                                + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("TestObject4"),
                "alias:cazcade:"
                + username);
        FountainTestClientSupport.putEntityToURL(clientSession, "pool/"
                                                                + entityId
                                                                + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("TestObject5"), "alias:youtube:HomeAdditionPlus");
        final Entity testEntity = FountainTestClientSupport.callRESTApiWithGet(clientSession, "pool.xml?url=pool:///people/" +
                                                                                              username +
                                                                                              "%23TestObject4");
        System.err.println(testEntity.toString());
        assertTrue(testEntity.type().getPrimaryType().getClassOnlyType().asString().equals(Types.T_BITMAP_IMAGE_2D.getValue()));
    }

    private static void assertTrue(final boolean condition) {
        if (!condition) {
            throw new Error("Assertion failed.");
        }
    }

    private static void initStream() throws IOException {
        final Entity streamEntity = FountainTestClientSupport.callRESTApiWithGet(clientSession, "pool.xml?url=pool:///people/" +
                                                                                                username +
                                                                                                "/.stream");
        final String streamId = streamEntity.id().toString();
        //        FountainTestClientSupport.postEntityToURL(clientSession, "pool/" + streamId + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("TwitterFeed"), "alias:cazcade:"+username);
        FountainTestClientSupport.putEntityToURL(clientSession, "pool/"
                                                                + streamId
                                                                + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("RSSFeed1"),
                "alias:cazcade:"
                + username);
        FountainTestClientSupport.putEntityToURL(clientSession, "pool/"
                                                                + streamId
                                                                + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("RSSFeed2"),
                "alias:cazcade:"
                + username);
        FountainTestClientSupport.putEntityToURL(clientSession, "pool/"
                                                                + streamId
                                                                + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("RSSFeed3"),
                "alias:cazcade:"
                + username);
    }

    private static void initInbox() throws IOException {
        final Entity poolEntity = FountainTestClientSupport.callRESTApiWithGet(clientSession, "pool.xml?url=pool:///people/" +
                                                                                              username +
                                                                                              "/.inbox");
        final String entityId = poolEntity.id().toString();
        FountainTestClientSupport.putEntityToURL(clientSession, "pool/"
                                                                + entityId
                                                                + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("TestObject1"),
                "alias:cazcade:"
                + username);
        FountainTestClientSupport.putEntityToURL(clientSession, "pool/"
                                                                + entityId
                                                                + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("TestObject2"),
                "alias::cazcade:"
                + username);
        FountainTestClientSupport.putEntityToURL(clientSession, "pool/"
                                                                + entityId
                                                                + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("TestObject3"),
                "alias:cazcade:"
                + username);
        FountainTestClientSupport.putEntityToURL(clientSession, "pool/"
                                                                + entityId
                                                                + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("TestObject4"),
                "alias:cazcade:"
                + username);
        FountainTestClientSupport.putEntityToURL(clientSession, "pool/"
                                                                + entityId
                                                                + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("TestObject5"), "alias:youtube:HomeAdditionPlus");
        final Entity testEntity = FountainTestClientSupport.callRESTApiWithGet(clientSession, "pool.xml?url=pool:///people/" +
                                                                                              username +
                                                                                              "/.inbox%23TestObject4");
        System.err.println(testEntity.toString());
        assertTrue(testEntity.type().getPrimaryType().getClassOnlyType().asString().equals(Types.T_BITMAP_IMAGE_2D.getValue()));
    }
}