package cazcade.fountain.server.rest.test;

import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;

import java.io.IOException;

/**
 * @author neilelliz@cazcade.com
 */
public class FountainTestDataBuilderClient {
    private static String username = "dtv";
    private static ClientSession clientSession;

    static {

    }

    public static void main(String[] args) throws IOException {

        HttpClient client = new HttpClient();
        client.getState().setCredentials(new AuthScope(FountainTestClientSupport.host, FountainTestClientSupport.port, AuthScope.ANY_REALM), new UsernamePasswordCredentials("anon", "anon"));
        client.getParams().setAuthenticationPreemptive(true);


        FountainTestClientSupport.callRESTApiWithGet(new ClientSession(client, null, username), "user/create.xml?.email=neil.ellis@mangala.co.uk&.name=" + username + "&.security.password.plain=" + username + "&.fn=Neil+Ellis&.type=" + LSDDictionaryTypes.USER.getValue());
        client.getState().setCredentials(new AuthScope(FountainTestClientSupport.host, FountainTestClientSupport.port, AuthScope.ANY_REALM), new UsernamePasswordCredentials(username, username));
        LSDEntity sessionEntity = FountainTestClientSupport.callRESTApiWithGet(new ClientSession(client, null, username), "session/create.xml?client=TestDataBuilder&key=123&hostinfo=macosx");
        String sessionId = sessionEntity.getID().toString();

        clientSession = new ClientSession(client, sessionId, username);
//        FountainTestClientSupport.callRESTApiWithGet(clientSession, "alias/create.xml?me&.name=neilellis&.network=twitter&.type=Identity.Person.Alias");
//        LSDEntity aliasEntity = FountainTestClientSupport.callRESTApiWithGet(clientSession, "alias.xml?uri=alias:twitter:neilellis");
//        assertTrue(aliasEntity.getTypeDef().getPrimaryType().toString().equals(LSDTypeDictionary.ALIAS.getAttribute()));


        initHomePool();
        initStream();
        initInbox();


    }

    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new Error("Assertion failed.");
        }
    }

    private static void initStream() throws IOException {
        LSDEntity streamEntity = FountainTestClientSupport.callRESTApiWithGet(clientSession, "pool.xml?url=pool:///people/" + username + "/.stream");
        String streamId = streamEntity.getID().toString();
//        FountainTestClientSupport.postEntityToURL(clientSession, "pool/" + streamId + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("TwitterFeed"), "alias:cazcade:"+username);
        FountainTestClientSupport.postEntityToURL(clientSession, "pool/" + streamId + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("RSSFeed1"), "alias:cazcade:" + username);
        FountainTestClientSupport.postEntityToURL(clientSession, "pool/" + streamId + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("RSSFeed2"), "alias:cazcade:" + username);
        FountainTestClientSupport.postEntityToURL(clientSession, "pool/" + streamId + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("RSSFeed3"), "alias:cazcade:" + username);
    }

    private static void initHomePool() throws IOException {
        LSDEntity poolEntity = FountainTestClientSupport.callRESTApiWithGet(clientSession, "pool.xml?url=pool:///people/" + username);
        String entityId = poolEntity.getID().toString();
        FountainTestClientSupport.postEntityToURL(clientSession, "pool/" + entityId + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("TestObject1"), "alias:cazcade:" + username);
        FountainTestClientSupport.postEntityToURL(clientSession, "pool/" + entityId + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("TestObject2"), "alias::cazcade:" + username);
        FountainTestClientSupport.postEntityToURL(clientSession, "pool/" + entityId + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("TestObject3"), "alias:cazcade:" + username);
        FountainTestClientSupport.postEntityToURL(clientSession, "pool/" + entityId + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("TestObject4"), "alias:cazcade:" + username);
        FountainTestClientSupport.postEntityToURL(clientSession, "pool/" + entityId + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("TestObject5"), "alias:youtube:HomeAdditionPlus");
        LSDEntity testEntity = FountainTestClientSupport.callRESTApiWithGet(clientSession, "pool.xml?url=pool:///people/" + username + "%23TestObject4");
        System.err.println(testEntity.toString());
        assertTrue(testEntity.getTypeDef().getPrimaryType().getClassOnlyType().asString().equals(LSDDictionaryTypes.BITMAP_IMAGE_2D.getValue()));
    }

    private static void initInbox() throws IOException {
        LSDEntity poolEntity = FountainTestClientSupport.callRESTApiWithGet(clientSession, "pool.xml?url=pool:///people/" + username + "/.inbox");
        String entityId = poolEntity.getID().toString();
        FountainTestClientSupport.postEntityToURL(clientSession, "pool/" + entityId + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("TestObject1"), "alias:cazcade:" + username);
        FountainTestClientSupport.postEntityToURL(clientSession, "pool/" + entityId + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("TestObject2"), "alias::cazcade:" + username);
        FountainTestClientSupport.postEntityToURL(clientSession, "pool/" + entityId + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("TestObject3"), "alias:cazcade:" + username);
        FountainTestClientSupport.postEntityToURL(clientSession, "pool/" + entityId + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("TestObject4"), "alias:cazcade:" + username);
        FountainTestClientSupport.postEntityToURL(clientSession, "pool/" + entityId + ".xml", FountainTestClientSupport.createEntityFromPropertyfile("TestObject5"), "alias:youtube:HomeAdditionPlus");
        LSDEntity testEntity = FountainTestClientSupport.callRESTApiWithGet(clientSession, "pool.xml?url=pool:///people/" + username + "/.inbox%23TestObject4");
        System.err.println(testEntity.toString());
        assertTrue(testEntity.getTypeDef().getPrimaryType().getClassOnlyType().asString().equals(LSDDictionaryTypes.BITMAP_IMAGE_2D.getValue()));
    }


}