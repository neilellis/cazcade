/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.server.rest.test;

import cazcade.common.Logger;
import cazcade.fountain.server.rest.cli.FountainRestServer;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.*;
import cazcade.liquid.api.request.LinkPoolObjectRequest;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.junit.*;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URLEncoder;

import static org.junit.Assert.assertTrue;

/**
 * @author neilelliz@cazcade.com
 */
@SuppressWarnings({"CallToThreadStopSuspendOrResumeManager"})
public class FountainIntegrationTest {
    @Nonnull
    public static final  String CAZCADE_COPYRIGHT_STATEMENT = "(C) Cazcade Ltd. We'd like to thank the special characters & < > '";
    @Nonnull
    public static final  String YOU_HAVE_NO_RIGHTS          = "YOU HAVE NO RIGHTS!";
    @Nonnull
    public static final  String TITLE                       = "My Title";
    @Nonnull
    private static final Logger log                         = Logger.getLogger(FountainIntegrationTest.class);
    //    private static FountainDataStoreServer dataStore;
    private static FountainRestServer restServer;

    private String                      testUsername;
    private String                      sessionId;
    private HttpClient                  client;
    private LiquidUUID                  userId;
    private UsernamePasswordCredentials credentials;
    private String                      otherUsername;
    private LiquidUUID                  otherId;
    private ClientSession               otherUserSession;
    private ClientSession               userSession;
    private String                      testUserHomePool;
    private String                      testUserHomePoolId;
    private String                      testUserPublicPoolId;


    static {
    }

    @BeforeClass
    public static void beforeClass() throws InterruptedException {
        //        dataStore = new FountainDataStoreServer();
        //        new Thread() {
        //            @Override
        //            public void run() {
        //                try {
        //                    dataStore.start();
        //                } catch (Exception e) {
        //                    log.error(e);
        //                }
        //            }
        //        }.start();
        restServer = new FountainRestServer();

        //        dataStore.waitForInitialisation();
        new Thread() {
            @Override
            public void run() {
                try {
                    restServer.start();
                } catch (Exception e) {
                    log.error(e);
                }
            }
        }.start();
        restServer.waitForInitialisation();
    }

    @AfterClass
    public static void destroyServers() throws InterruptedException {
        Thread.sleep(5000);
        try {
            restServer.stop();
            //            dataStore.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() throws Exception {
        client = new HttpClient();
        client.getState().setCredentials(FountainTestClientSupport.AUTH_SCOPE, new UsernamePasswordCredentials("anon", "anon"));
        client.getParams().setAuthenticationPreemptive(true);

        initialiseOtherUser();
        initaliseTestUser();


        //todo: register listener and listen for errors!
    }

    private void initialiseOtherUser() throws IOException {
        otherUsername = randomName("other");
        final Entity otherEntity = FountainTestClientSupport.callRESTApiWithGet(new ClientSession(client, null, otherUsername),
                "user/create.xml?.email=other@cazcade.com&.name="
                +
                otherUsername
                +
                "&.security.password.plain=other&.fn=Other+User&.type="
                +
                Types.T_USER.getValue());
        otherId = otherEntity.id();
        userId = otherEntity.id();
        credentials = new UsernamePasswordCredentials(otherUsername, "other");
        client.getState().clearCredentials();
        client.getState().setCredentials(FountainTestClientSupport.AUTH_SCOPE, credentials);

        final Entity otherSessionEntity = FountainTestClientSupport.callRESTApiWithGet(new ClientSession(client, null, otherUsername), "session/create.xml?client=TestDataBuilder&key=123&hostinfo=macosx");
        sessionId = otherSessionEntity.id().toString();

        otherUserSession = new ClientSession(client, sessionId, otherUsername);
        FountainTestClientSupport.initHomePool(otherUserSession);
        FountainTestClientSupport.initStream(otherUserSession);
    }

    private void initaliseTestUser() throws IOException {
        testUsername = randomName("cazcadetest");
        final Entity userEntity = FountainTestClientSupport.callRESTApiWithGet(new ClientSession(client, null, testUsername),
                "user/create.xml?.email=neil.ellis@mangala.co.uk&.name="
                +
                testUsername
                +
                "&.security.password.plain=test&.fn=Neil+Ellis&.type="
                +
                Types.T_USER.getValue());
        userId = userEntity.id();
        credentials = new UsernamePasswordCredentials(testUsername, "test");
        client.getState().clearCredentials();
        client.getState().setCredentials(FountainTestClientSupport.AUTH_SCOPE, credentials);

        final Entity sessionEntity = FountainTestClientSupport.callRESTApiWithGet(new ClientSession(client, null, testUsername), "session/create.xml?client=TestDataBuilder&key=123&hostinfo=macosx");
        sessionId = sessionEntity.id().toString();
        userSession = new ClientSession(client, sessionId, testUsername);

        FountainTestClientSupport.callRESTApiWithGet(userSession, "alias/create.xml?me&.network=twitter&.name=" +
                                                                  testUsername +
                                                                  "&.type=Identity.Person.Alias");
        final Entity aliasEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "alias.xml?uri=alias:twitter:"
                                                                                             + testUsername);
        assertTrue(aliasEntity.type().getPrimaryType().asString().equals(Types.T_ALIAS.getValue()));

        testUserHomePool = "pool:///people/" + testUsername;
        testUserHomePoolId = FountainTestClientSupport.convertLiquidURIToId(userSession, testUserHomePool);
        testUserPublicPoolId = FountainTestClientSupport.convertLiquidURIToId(userSession, testUserHomePool + "/public");
        final Thread listenThread = FountainTestClientSupport.listenToSession(sessionId, testUserHomePool, credentials);
        FountainTestClientSupport.initHomePool(userSession);
        FountainTestClientSupport.initStream(userSession);
        listenThread.stop();
    }

    private static String randomName(final String prefix) {
        return (prefix + Math.random()).replace('.', '_');
    }

    @After
    public void tearDown() throws Exception {
        //        callRESTApiWithGet(client, sessionId, "user/" + userId.toString() + "/delete.xml");
        //        callRESTApiWithGet(client, sessionId, "user/" + otherId.toString() + "/delete.xml");
    }

    @Test
    public void test() throws IOException, InterruptedException {
        Thread.sleep(1000);
        final Entity testEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                            testUserHomePool +
                                                                                            "%23TestObject4");
        System.err.println(testEntity.toString());
        Assert.assertTrue(testEntity.type()
                                    .getPrimaryType()
                                    .getClassOnlyType()
                                    .asString()
                                    .equals(Types.T_BITMAP_IMAGE_2D.getValue()));

        final Entity testEntity2 = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                             testUserHomePool +
                                                                                             "/stream%23rssdfeed01");
        System.err.println(testEntity2.toString());
        Assert.assertEquals("http://newsrss.bbc.co.uk/rss/newsonline_uk_edition/front_page/rss.xml", testEntity2.$(Dictionary.SOURCE));
        Assert.assertEquals("pool:///people/" + testUsername + "/stream#rssdfeed01", testEntity2.$(Dictionary.URI));
        //todo: more assertions
    }

    @Test
    public void testCannotDeletePublicPool() throws InterruptedException, IOException {
        final String LiquidURI = testUserHomePool + "/public";
        final Thread listenThread = FountainTestClientSupport.listenToSession(sessionId, LiquidURI, credentials);
        final Entity poolEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" + LiquidURI);
        Thread.sleep(500);
        try {
            final Entity deletionEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool/" +
                                                                                                    poolEntity.id().toString() +
                                                                                                    "/delete");
            Assert.fail("Was able to delete public pool, fool!");
        } catch (ClientTestStatusCodeException e) {
        } finally {
            listenThread.stop();
        }
    }

    @Test
    public void testCreatePool() throws InterruptedException, IOException {
        final String LiquidURI = testUserHomePool + "/public";
        final Thread listenThread = FountainTestClientSupport.listenToSession(sessionId, LiquidURI, credentials);
        FountainTestClientSupport.callRESTApiWithGet(userSession, "pool/create.xml?parent=" +
                                                                  LiquidURI +
                                                                  "&name=testchild&title=" +
                                                                  URLEncoder.encode(TITLE) +
                                                                  "&description=&x=0&y=0");
        Thread.sleep(500);
        final Entity poolEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?uri="
                                                                                            + LiquidURI
                                                                                            + "/testchild");
        Assert.assertEquals(TITLE, poolEntity.$(Dictionary.TITLE));
        listenThread.stop();
    }

    @Test
    public void testDeleteObject() throws IOException, InterruptedException {
        writeThenDeleteObjectTest(testUserHomePool);
        final Entity poolEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                            testUserHomePool +
                                                                                            "&contents");
    }

    @Test
    public void testDeletePool() throws InterruptedException, IOException {
        final String LiquidURI = testUserHomePool + "/public";
        final Thread listenThread = FountainTestClientSupport.listenToSession(sessionId, LiquidURI, credentials);
        FountainTestClientSupport.callRESTApiWithGet(userSession, "pool/create.xml?parent=" +
                                                                  LiquidURI +
                                                                  "&name=testchildfordelete&title=&description=&x=0&y=0");
        Thread.sleep(500);
        final Entity poolEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?uri=" +
                                                                                            LiquidURI +
                                                                                            "/testchildfordelete");
        final Entity deletionEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool/delete?uri="
                                                                                                + poolEntity.uri());
        Thread.sleep(500);
        try {
            final Entity entityToTest = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                                  LiquidURI +
                                                                                                  "/testchildfordelete");
            Assert.fail("Was able to retrieve deleted pool, fool!");
        } catch (ClientTestStatusCodeException e) {
        } finally {
            listenThread.stop();
        }
    }

    @Test
    public void testLinkObject() throws IOException, InterruptedException {
        if (!LinkPoolObjectRequest.SUPPORTS_URI) {
            return;
        }
        final Thread listenThread = FountainTestClientSupport.listenToSession(sessionId, testUserHomePool, credentials);

        final TransferEntity testEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                                    testUserHomePool +
                                                                                                    "%23TestObject4");
        testEntity.$(Dictionary.RIGHTS, CAZCADE_COPYRIGHT_STATEMENT);
        testEntity.$(Dictionary.VIEW_X, "999");
        FountainTestClientSupport.putEntityToURL(userSession, "pool/" + testUserHomePoolId + "/" + testEntity.id(), testEntity,
                "alias:cazcade:"
                + testUsername);
        Thread.sleep(3000);

        FountainTestClientSupport.callRESTApiWithGet(userSession, "pool/" +
                                                                  testUserHomePoolId +
                                                                  "/" +
                                                                  testEntity.id() +
                                                                  "/link?to=" +
                                                                  testUserPublicPoolId);
        final Entity copy = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                      testUserHomePool +
                                                                                      "/public%23TestObject4");
        Assert.assertEquals(testUserHomePool + "/public#TestObject4", copy.$(Dictionary.URI));
        listenThread.stop();
    }

    @Test
    public void testOtherPool() throws IOException, InterruptedException {
        final String LiquidURI = "pool:///people/" + otherUsername;
        final Entity readTestEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?uri=" +
                                                                                                LiquidURI +
                                                                                                "%23TestObject4");
        Assert.assertEquals("Failed to read test object.", "TestObject4", readTestEntity.$(Dictionary.NAME));
        final Thread listenThread = FountainTestClientSupport.listenToSession(sessionId, LiquidURI, credentials);
        try {
            final Entity entity = FountainTestClientSupport.writeThenGetTestPoolObject(userSession, LiquidURI);
            Assert.fail("Was able to add to non-public pool, fool!");
        } catch (ClientTestStatusCodeException e) {
        } finally {
            listenThread.stop();
        }
    }

    @Test
    public void testOtherPublicPool() throws IOException, InterruptedException {
        final String LiquidURI = "pool:///people/" + otherUsername + "/public";
        final Thread listenThread = FountainTestClientSupport.listenToSession(sessionId, LiquidURI, credentials);
        final Entity entity = FountainTestClientSupport.writeThenGetTestPoolObject(userSession, LiquidURI);
        Thread.sleep(100);
        Assert.assertEquals("Should be able to add to other user's public pool.", "Image.Bitmap.2DBitmap", entity.type()
                                                                                                                 .getPrimaryType()
                                                                                                                 .asString());
        listenThread.stop();
    }

    @Test
    public void testPublicSubPool() throws IOException, InterruptedException {
        final String LiquidURI = testUserHomePool + "/public";
        writeThenDeleteObjectTest(LiquidURI);
    }

    //    public void testOtherDropPool() throws IOException, InterruptedException {
    //        String LURI = "pool:///users/" + otherUsername+"/drop";
    //        TransferEntity entity = writeTestPool(userSession, LURI);
    //        Thread.sleep(100);
    //        assertEquals("Should be able to add to other user's drop pool.", "Image.Bitmap.2DBitmap.GIF", entity.type().getPrimaryType().toString());
    //        TransferEntity readTestEntity = getTestPoolObject(userSession, LURI, "TestObject4");
    //        assertEquals("Failed to read test object.", "TestObject4", readTestEntity.$(LSDDictionary.NAME));
    //    }

    private void writeThenDeleteObjectTest(final String poolURI) throws IOException, InterruptedException {
        final Thread listenThread = FountainTestClientSupport.listenToSession(sessionId, poolURI, credentials);
        final Entity createdObject = FountainTestClientSupport.writeThenGetTestPoolObject(userSession, poolURI);
        final Entity deletedEntity = FountainTestClientSupport.writeThenDeleteTestPoolObject(userSession, poolURI);
        Thread.sleep(1000);
        final Entity entityToTest = FountainTestClientSupport.getTestPoolObject(userSession, poolURI, deletedEntity.$(Dictionary.NAME));
        Assert.assertEquals("System.Entity.Empty", entityToTest.type().getPrimaryType().asString());
        Thread.sleep(1000);
        listenThread.stop();
    }

    @Test
    public void testRelocateFail() throws IOException, InterruptedException {
        final Thread listenThread = FountainTestClientSupport.listenToSession(sessionId, testUserHomePool, credentials);

        final TransferEntity testEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                                    testUserHomePool +
                                                                                                    "%23TestObject4");
        testEntity.$(Dictionary.RIGHTS, CAZCADE_COPYRIGHT_STATEMENT);
        testEntity.$(Dictionary.VIEW_X, "999");
        FountainTestClientSupport.putEntityToURL(userSession, "pool/" + testUserHomePoolId + "/" + testEntity.id(), testEntity,
                "alias:cazcade:"
                + testUsername);
        Thread.sleep(1000);

        FountainTestClientSupport.callRESTApiWithGet(userSession, "pool/" +
                                                                  testUserHomePoolId +
                                                                  "/" +
                                                                  testEntity.id() +
                                                                  "/relocate?to=" +
                                                                  testUserPublicPoolId);
        Thread.sleep(1000);
        final Entity relocated = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                           testUserHomePool +
                                                                                           "/public%23TestObject4");
        Assert.assertEquals("System.Entity.Empty", relocated.type().getPrimaryType().asString());

        listenThread.stop();
    }

    @Test
    public void testRelocateObject() throws IOException, InterruptedException {
        final Thread listenThread = FountainTestClientSupport.listenToSession(sessionId, testUserHomePool, credentials);

        final Entity testEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                            testUserHomePool +
                                                                                            "%23TestObject4");
        FountainTestClientSupport.callRESTApiWithGet(userSession, "pool/" +
                                                                  testUserHomePoolId +
                                                                  "/" +
                                                                  testEntity.id() +
                                                                  "/relocate?to=" +
                                                                  testUserPublicPoolId);
        Thread.sleep(1000);
        final Entity relocated = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                           testUserHomePool +
                                                                                           "/public%23TestObject4");
        Assert.assertEquals(testUserHomePool + "/public#TestObject4", relocated.$(Dictionary.URI));
        final Entity original = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                          testUserHomePool +
                                                                                          "%23TestObject4");
        Assert.assertEquals("System.Entity.Empty", original.type().getPrimaryType().asString());

        listenThread.stop();
    }

    @Test
    public void testUnlinkObject() throws IOException, InterruptedException {
        final Thread listenThread = FountainTestClientSupport.listenToSession(sessionId, testUserHomePool, credentials);

        TransferEntity testEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                              testUserHomePool +
                                                                                              "%23TestObject4");
        testEntity.$(Dictionary.RIGHTS, CAZCADE_COPYRIGHT_STATEMENT);
        testEntity.$(Dictionary.VIEW_X, "999");
        FountainTestClientSupport.putEntityToURL(userSession, "pool/" + testUserHomePoolId + "/" + testEntity.id(), testEntity,
                "alias:cazcade:"
                + testUsername);
        Thread.sleep(1000);

        testEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url="
                                                                               + testUserHomePool
                                                                               + "%23TestObject4");
        FountainTestClientSupport.callRESTApiWithGet(userSession, "pool/" + testUserHomePoolId + "/" + testEntity.id() + "/unlink");
        Thread.sleep(1000);
        final Entity deletedEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                               testUserHomePool +
                                                                                               "%23TestObject4");
        Assert.assertEquals("Image.Bitmap.2DBitmap", deletedEntity.$(Dictionary.TYPE));
        listenThread.stop();
    }

    @Test
    public void testUpdateObject() throws IOException, InterruptedException {
        final Thread listenThread = FountainTestClientSupport.listenToSession(sessionId, testUserHomePool, credentials);
        final String objectURL = testUserHomePool + "%23TestObject4";

        TransferEntity testEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" + objectURL);
        testEntity.$(Dictionary.RIGHTS, CAZCADE_COPYRIGHT_STATEMENT);
        testEntity.$(Dictionary.VIEW_X, "999");
        FountainTestClientSupport.putEntityToURL(userSession, "pool/" + testUserHomePoolId + "/" + testEntity.id(), testEntity,
                "alias:cazcade:"
                + testUsername);
        FountainTestClientSupport.callRESTApiWithGet(userSession, "comment/create.xml?uri=" +
                                                                  objectURL +
                                                                  "&text=HelloWorld&image=" +
                                                                  URLEncoder.encode("http://www.google.co.uk/logos/2011/calder11-sr.png"));
        Thread.sleep(2000);
        TransferEntity commentList = FountainTestClientSupport.callRESTApiWithGet(userSession, "comment.xml?uri=" + objectURL);
        TransferEntityCollection<? extends TransferEntity> comments = commentList.children(Dictionary.CHILD_A);
        Assert.assertEquals(1, comments.size());
        log.debug("Child");
        log.debug("{0}", comments.get(0));
        log.debug("Child finished.");
        Assert.assertEquals("HelloWorld", comments.get(0).$(Dictionary.TEXT_EXTENDED));


        Entity updatedEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" + objectURL);
        Assert.assertEquals(CAZCADE_COPYRIGHT_STATEMENT, updatedEntity.$(Dictionary.RIGHTS));
        Assert.assertNotSame("999", updatedEntity.child(Dictionary.VIEW_ENTITY, false).$(Dictionary.VIEW_X));

        testEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" + objectURL);
        testEntity.$(Dictionary.RIGHTS, YOU_HAVE_NO_RIGHTS);
        FountainTestClientSupport.putEntityToURL(userSession, "pool/" + testUserHomePoolId + "/" + testEntity.id(), testEntity,
                "alias:cazcade:"
                + testUsername);
        Thread.sleep(1000);

        updatedEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" + objectURL + "&history");
        Assert.assertEquals(YOU_HAVE_NO_RIGHTS, updatedEntity.$(Dictionary.RIGHTS));
        Assert.assertEquals("3", updatedEntity.$(Dictionary.VERSION));
        commentList = FountainTestClientSupport.callRESTApiWithGet(userSession, "comment.xml?uri=" + objectURL);
        comments = commentList.children();
        Assert.assertEquals(1, comments.size());
        Assert.assertEquals("HelloWorld", comments.get(0).$(Dictionary.TEXT_EXTENDED));
        listenThread.stop();
        //TransferEntity comments = FountainTestClientSupport.callRESTApiWithGet(userSession, "comment.xml?url=" + objectURL);
    }

    @Test
    public void testUpdatePool() throws IOException, InterruptedException {
        final Thread listenThread = FountainTestClientSupport.listenToSession(sessionId, testUserHomePool, credentials);
        final TransferEntity testEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?uri="
                                                                                                    + testUserHomePool);
        testEntity.$(Dictionary.RIGHTS, CAZCADE_COPYRIGHT_STATEMENT);
        FountainTestClientSupport.putEntityToURL(userSession, "pool/update?uri=" + testUserHomePool, testEntity, "alias:cazcade:"
                                                                                                                 + testUsername);
        Thread.sleep(1000);
        final Entity updatedEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?uri=" + testUserHomePool);
        Assert.assertEquals(CAZCADE_COPYRIGHT_STATEMENT, updatedEntity.$(Dictionary.RIGHTS));
        listenThread.stop();
    }

    @Test
    public void testWithContents() throws IOException, InterruptedException {
        Thread.sleep(1000);
        final Entity testEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                            testUserHomePool +
                                                                                            "&contents");
        System.err.println(testEntity.toString());
        Assert.assertTrue(testEntity.type().asString().equals(Types.T_POOL2D.getValue()));
    }
}
