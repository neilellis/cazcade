package cazcade.fountain.server.rest.test;

import cazcade.common.Logger;
import cazcade.fountain.server.rest.cli.FountainRestServer;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.LinkPoolObjectRequest;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.junit.*;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author neilelliz@cazcade.com
 */
@SuppressWarnings({"CallToThreadStopSuspendOrResumeManager"})
public class FountainIntegrationTest {
    @Nonnull
    public static final String CAZCADE_COPYRIGHT_STATEMENT = "(C) Cazcade Ltd. We'd like to thank the special characters & < > '";
    @Nonnull
    public static final String YOU_HAVE_NO_RIGHTS = "YOU HAVE NO RIGHTS!";
    @Nonnull
    public static final String TITLE = "My Title";
    @Nonnull
    private static final Logger log = Logger.getLogger(FountainIntegrationTest.class);
//    private static FountainDataStoreServer dataStore;
    private static FountainRestServer restServer;

    private String testUsername;
    private String sessionId;
    private HttpClient client;
    private LiquidUUID userId;
    private UsernamePasswordCredentials credentials;
    private String otherUsername;
    private LiquidUUID otherId;
    private ClientSession otherUserSession;
    private ClientSession userSession;
    private String testUserHomePool;
    private String testUserHomePoolId;
    private String testUserPublicPoolId;


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
        final LSDBaseEntity otherEntity = FountainTestClientSupport.callRESTApiWithGet(new ClientSession(client, null, otherUsername
        ), "user/create.xml?.email=other@cazcade.com&.name=" +
           otherUsername +
           "&.security.password.plain=other&.fn=Other+User&.type=" +
           LSDDictionaryTypes.USER.getValue()
                                                                                      );
        otherId = otherEntity.getUUID();
        userId = otherEntity.getUUID();
        credentials = new UsernamePasswordCredentials(otherUsername, "other");
        client.getState().clearCredentials();
        client.getState().setCredentials(FountainTestClientSupport.AUTH_SCOPE, credentials);

        final LSDBaseEntity otherSessionEntity = FountainTestClientSupport.callRESTApiWithGet(new ClientSession(client, null,
                                                                                                                otherUsername
        ), "session/create.xml?client=TestDataBuilder&key=123&hostinfo=macosx"
                                                                                             );
        sessionId = otherSessionEntity.getUUID().toString();

        otherUserSession = new ClientSession(client, sessionId, otherUsername);
        FountainTestClientSupport.initHomePool(otherUserSession);
        FountainTestClientSupport.initStream(otherUserSession);
    }

    private void initaliseTestUser() throws IOException {
        testUsername = randomName("cazcadetest");
        final LSDBaseEntity userEntity = FountainTestClientSupport.callRESTApiWithGet(new ClientSession(client, null, testUsername),
                                                                                      "user/create.xml?.email=neil.ellis@mangala.co.uk&.name=" +
                                                                                      testUsername +
                                                                                      "&.security.password.plain=test&.fn=Neil+Ellis&.type=" +
                                                                                      LSDDictionaryTypes.USER.getValue()
                                                                                     );
        userId = userEntity.getUUID();
        credentials = new UsernamePasswordCredentials(testUsername, "test");
        client.getState().clearCredentials();
        client.getState().setCredentials(FountainTestClientSupport.AUTH_SCOPE, credentials);

        final LSDBaseEntity sessionEntity = FountainTestClientSupport.callRESTApiWithGet(new ClientSession(client, null,
                                                                                                           testUsername
        ), "session/create.xml?client=TestDataBuilder&key=123&hostinfo=macosx"
                                                                                        );
        sessionId = sessionEntity.getUUID().toString();
        userSession = new ClientSession(client, sessionId, testUsername);

        FountainTestClientSupport.callRESTApiWithGet(userSession, "alias/create.xml?me&.network=twitter&.name=" +
                                                                  testUsername +
                                                                  "&.type=Identity.Person.Alias"
                                                    );
        final LSDBaseEntity aliasEntity = FountainTestClientSupport.callRESTApiWithGet(userSession,
                                                                                       "alias.xml?uri=alias:twitter:" + testUsername
                                                                                      );
        assertTrue(aliasEntity.getTypeDef().getPrimaryType().asString().equals(LSDDictionaryTypes.ALIAS.getValue()));

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
        final LSDBaseEntity testEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                                   testUserHomePool +
                                                                                                   "%23TestObject4"
                                                                                     );
        System.err.println(testEntity.toString());
        Assert.assertTrue(testEntity.getTypeDef().getPrimaryType().getClassOnlyType().asString().equals(
                LSDDictionaryTypes.BITMAP_IMAGE_2D.getValue()
                                                                                                       )
                         );

        final LSDBaseEntity testEntity2 = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                                    testUserHomePool +
                                                                                                    "/stream%23rssdfeed01"
                                                                                      );
        System.err.println(testEntity2.toString());
        Assert.assertEquals("http://newsrss.bbc.co.uk/rss/newsonline_uk_edition/front_page/rss.xml", testEntity2.getAttribute(
                LSDAttribute.SOURCE
                                                                                                                             )
                           );
        Assert.assertEquals("pool:///people/" + testUsername + "/stream#rssdfeed01", testEntity2.getAttribute(LSDAttribute.URI));
        //todo: more assertions
    }

    @Test
    public void testCannotDeletePublicPool() throws InterruptedException, IOException {
        final String LiquidURI = testUserHomePool + "/public";
        final Thread listenThread = FountainTestClientSupport.listenToSession(sessionId, LiquidURI, credentials);
        final LSDBaseEntity poolEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" + LiquidURI);
        Thread.sleep(500);
        try {
            final LSDBaseEntity deletionEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool/" +
                                                                                                           poolEntity.getUUID()
                                                                                                                     .toString() +
                                                                                                           "/delete"
                                                                                             );
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
                                                                  "&description=&x=0&y=0"
                                                    );
        Thread.sleep(500);
        final LSDBaseEntity poolEntity = FountainTestClientSupport.callRESTApiWithGet(userSession,
                                                                                      "pool.xml?uri=" + LiquidURI + "/testchild"
                                                                                     );
        Assert.assertEquals(TITLE, poolEntity.getAttribute(LSDAttribute.TITLE));
        listenThread.stop();
    }

    @Test
    public void testDeleteObject() throws IOException, InterruptedException {
        writeThenDeleteObjectTest(testUserHomePool);
        final LSDBaseEntity poolEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                                   testUserHomePool +
                                                                                                   "&contents"
                                                                                     );
    }

    @Test
    public void testDeletePool() throws InterruptedException, IOException {
        final String LiquidURI = testUserHomePool + "/public";
        final Thread listenThread = FountainTestClientSupport.listenToSession(sessionId, LiquidURI, credentials);
        FountainTestClientSupport.callRESTApiWithGet(userSession, "pool/create.xml?parent=" +
                                                                  LiquidURI +
                                                                  "&name=testchildfordelete&title=&description=&x=0&y=0"
                                                    );
        Thread.sleep(500);
        final LSDBaseEntity poolEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?uri=" +
                                                                                                   LiquidURI +
                                                                                                   "/testchildfordelete"
                                                                                     );
        final LSDBaseEntity deletionEntity = FountainTestClientSupport.callRESTApiWithGet(userSession,
                                                                                          "pool/delete?uri=" + poolEntity.getURI()
                                                                                         );
        Thread.sleep(500);
        try {
            final LSDBaseEntity entityToTest = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                                         LiquidURI +
                                                                                                         "/testchildfordelete"
                                                                                           );
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

        final LSDTransferEntity testEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                                       testUserHomePool +
                                                                                                       "%23TestObject4"
                                                                                         );
        testEntity.setAttribute(LSDAttribute.RIGHTS, CAZCADE_COPYRIGHT_STATEMENT);
        testEntity.setAttribute(LSDAttribute.VIEW_X, "999");
        FountainTestClientSupport.putEntityToURL(userSession, "pool/" + testUserHomePoolId + "/" + testEntity.getUUID(), testEntity,
                                                 "alias:cazcade:" + testUsername
                                                );
        Thread.sleep(3000);

        FountainTestClientSupport.callRESTApiWithGet(userSession, "pool/" +
                                                                  testUserHomePoolId +
                                                                  "/" +
                                                                  testEntity.getUUID() +
                                                                  "/link?to=" +
                                                                  testUserPublicPoolId
                                                    );
        final LSDBaseEntity copy = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                             testUserHomePool +
                                                                                             "/public%23TestObject4"
                                                                               );
        Assert.assertEquals(testUserHomePool + "/public#TestObject4", copy.getAttribute(LSDAttribute.URI));
        listenThread.stop();
    }

    @Test
    public void testOtherPool() throws IOException, InterruptedException {
        final String LiquidURI = "pool:///people/" + otherUsername;
        final LSDBaseEntity readTestEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?uri=" +
                                                                                                       LiquidURI +
                                                                                                       "%23TestObject4"
                                                                                         );
        Assert.assertEquals("Failed to read test object.", "TestObject4", readTestEntity.getAttribute(LSDAttribute.NAME));
        final Thread listenThread = FountainTestClientSupport.listenToSession(sessionId, LiquidURI, credentials);
        try {
            final LSDBaseEntity entity = FountainTestClientSupport.writeThenGetTestPoolObject(userSession, LiquidURI);
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
        final LSDBaseEntity entity = FountainTestClientSupport.writeThenGetTestPoolObject(userSession, LiquidURI);
        Thread.sleep(100);
        Assert.assertEquals("Should be able to add to other user's public pool.", "Image.Bitmap.2DBitmap",
                            entity.getTypeDef().getPrimaryType().asString()
                           );
        listenThread.stop();
    }

    @Test
    public void testPublicSubPool() throws IOException, InterruptedException {
        final String LiquidURI = testUserHomePool + "/public";
        writeThenDeleteObjectTest(LiquidURI);
    }

//    public void testOtherDropPool() throws IOException, InterruptedException {
//        String LiquidURI = "pool:///users/" + otherUsername+"/drop";
//        LSDTransferEntity entity = writeTestPool(userSession, LiquidURI);
//        Thread.sleep(100);
//        assertEquals("Should be able to add to other user's drop pool.", "Image.Bitmap.2DBitmap.GIF", entity.getTypeDef().getPrimaryType().toString());
//        LSDTransferEntity readTestEntity = getTestPoolObject(userSession, LiquidURI, "TestObject4");
//        assertEquals("Failed to read test object.", "TestObject4", readTestEntity.getAttribute(LSDDictionary.NAME));
//    }

    private void writeThenDeleteObjectTest(final String poolURI) throws IOException, InterruptedException {
        final Thread listenThread = FountainTestClientSupport.listenToSession(sessionId, poolURI, credentials);
        final LSDBaseEntity createdObject = FountainTestClientSupport.writeThenGetTestPoolObject(userSession, poolURI);
        final LSDBaseEntity deletedEntity = FountainTestClientSupport.writeThenDeleteTestPoolObject(userSession, poolURI);
        Thread.sleep(1000);
        final LSDBaseEntity entityToTest = FountainTestClientSupport.getTestPoolObject(userSession, poolURI,
                                                                                       deletedEntity.getAttribute(LSDAttribute.NAME)
                                                                                      );
        Assert.assertEquals("System.Entity.Empty", entityToTest.getTypeDef().getPrimaryType().asString());
        Thread.sleep(1000);
        listenThread.stop();
    }

    @Test
    public void testRelocateFail() throws IOException, InterruptedException {
        final Thread listenThread = FountainTestClientSupport.listenToSession(sessionId, testUserHomePool, credentials);

        final LSDTransferEntity testEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                                       testUserHomePool +
                                                                                                       "%23TestObject4"
                                                                                         );
        testEntity.setAttribute(LSDAttribute.RIGHTS, CAZCADE_COPYRIGHT_STATEMENT);
        testEntity.setAttribute(LSDAttribute.VIEW_X, "999");
        FountainTestClientSupport.putEntityToURL(userSession, "pool/" + testUserHomePoolId + "/" + testEntity.getUUID(), testEntity,
                                                 "alias:cazcade:" + testUsername
                                                );
        Thread.sleep(1000);

        FountainTestClientSupport.callRESTApiWithGet(userSession, "pool/" +
                                                                  testUserHomePoolId +
                                                                  "/" +
                                                                  testEntity.getUUID() +
                                                                  "/relocate?to=" +
                                                                  testUserPublicPoolId
                                                    );
        Thread.sleep(1000);
        final LSDBaseEntity relocated = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                                  testUserHomePool +
                                                                                                  "/public%23TestObject4"
                                                                                    );
        Assert.assertEquals("System.Entity.Empty", relocated.getTypeDef().getPrimaryType().asString());

        listenThread.stop();
    }

    @Test
    public void testRelocateObject() throws IOException, InterruptedException {
        final Thread listenThread = FountainTestClientSupport.listenToSession(sessionId, testUserHomePool, credentials);

        final LSDBaseEntity testEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                                   testUserHomePool +
                                                                                                   "%23TestObject4"
                                                                                     );
        FountainTestClientSupport.callRESTApiWithGet(userSession, "pool/" +
                                                                  testUserHomePoolId +
                                                                  "/" +
                                                                  testEntity.getUUID() +
                                                                  "/relocate?to=" +
                                                                  testUserPublicPoolId
                                                    );
        Thread.sleep(1000);
        final LSDBaseEntity relocated = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                                  testUserHomePool +
                                                                                                  "/public%23TestObject4"
                                                                                    );
        Assert.assertEquals(testUserHomePool + "/public#TestObject4", relocated.getAttribute(LSDAttribute.URI));
        final LSDBaseEntity original = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                                 testUserHomePool +
                                                                                                 "%23TestObject4"
                                                                                   );
        Assert.assertEquals("System.Entity.Empty", original.getTypeDef().getPrimaryType().asString());

        listenThread.stop();
    }

    @Test
    public void testUnlinkObject() throws IOException, InterruptedException {
        final Thread listenThread = FountainTestClientSupport.listenToSession(sessionId, testUserHomePool, credentials);

        LSDTransferEntity testEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                                 testUserHomePool +
                                                                                                 "%23TestObject4"
                                                                                   );
        testEntity.setAttribute(LSDAttribute.RIGHTS, CAZCADE_COPYRIGHT_STATEMENT);
        testEntity.setAttribute(LSDAttribute.VIEW_X, "999");
        FountainTestClientSupport.putEntityToURL(userSession, "pool/" + testUserHomePoolId + "/" + testEntity.getUUID(), testEntity,
                                                 "alias:cazcade:" + testUsername
                                                );
        Thread.sleep(1000);

        testEntity = FountainTestClientSupport.callRESTApiWithGet(userSession,
                                                                  "pool.xml?url=" + testUserHomePool + "%23TestObject4"
                                                                 );
        FountainTestClientSupport.callRESTApiWithGet(userSession,
                                                     "pool/" + testUserHomePoolId + "/" + testEntity.getUUID() + "/unlink"
                                                    );
        Thread.sleep(1000);
        final LSDBaseEntity deletedEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                                      testUserHomePool +
                                                                                                      "%23TestObject4"
                                                                                        );
        Assert.assertEquals("Image.Bitmap.2DBitmap", deletedEntity.getAttribute(LSDAttribute.TYPE));
        listenThread.stop();
    }

    @Test
    public void testUpdateObject() throws IOException, InterruptedException {
        final Thread listenThread = FountainTestClientSupport.listenToSession(sessionId, testUserHomePool, credentials);
        final String objectURL = testUserHomePool + "%23TestObject4";

        LSDTransferEntity testEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" + objectURL);
        testEntity.setAttribute(LSDAttribute.RIGHTS, CAZCADE_COPYRIGHT_STATEMENT);
        testEntity.setAttribute(LSDAttribute.VIEW_X, "999");
        FountainTestClientSupport.putEntityToURL(userSession, "pool/" + testUserHomePoolId + "/" + testEntity.getUUID(), testEntity,
                                                 "alias:cazcade:" + testUsername
                                                );
        FountainTestClientSupport.callRESTApiWithGet(userSession, "comment/create.xml?uri=" +
                                                                  objectURL +
                                                                  "&text=HelloWorld&image=" +
                                                                  URLEncoder.encode(
                                                                          "http://www.google.co.uk/logos/2011/calder11-sr.png"
                                                                                   )
                                                    );
        Thread.sleep(2000);
        LSDBaseEntity commentList = FountainTestClientSupport.callRESTApiWithGet(userSession, "comment.xml?uri=" + objectURL);
        List<LSDBaseEntity> comments = commentList.getSubEntities(LSDAttribute.CHILD);
        Assert.assertEquals(1, comments.size());
        log.debug("Child");
        log.debug("{0}", comments.get(0));
        log.debug("Child finished.");
        Assert.assertEquals("HelloWorld", comments.get(0).getAttribute(LSDAttribute.TEXT_EXTENDED));


        LSDBaseEntity updatedEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" + objectURL);
        Assert.assertEquals(CAZCADE_COPYRIGHT_STATEMENT, updatedEntity.getAttribute(LSDAttribute.RIGHTS));
        Assert.assertNotSame("999", updatedEntity.getAttribute(LSDAttribute.VIEW_X));

        testEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" + objectURL);
        testEntity.setAttribute(LSDAttribute.RIGHTS, YOU_HAVE_NO_RIGHTS);
        FountainTestClientSupport.putEntityToURL(userSession, "pool/" + testUserHomePoolId + "/" + testEntity.getUUID(), testEntity,
                                                 "alias:cazcade:" + testUsername
                                                );
        Thread.sleep(1000);

        updatedEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" + objectURL + "&history");
        Assert.assertEquals(YOU_HAVE_NO_RIGHTS, updatedEntity.getAttribute(LSDAttribute.RIGHTS));
        Assert.assertEquals("3", updatedEntity.getAttribute(LSDAttribute.VERSION));
        commentList = FountainTestClientSupport.callRESTApiWithGet(userSession, "comment.xml?uri=" + objectURL);
        comments = commentList.getSubEntities(LSDAttribute.CHILD);
        Assert.assertEquals(1, comments.size());
        Assert.assertEquals("HelloWorld", comments.get(0).getAttribute(LSDAttribute.TEXT_EXTENDED));
        listenThread.stop();
        //LSDTransferEntity comments = FountainTestClientSupport.callRESTApiWithGet(userSession, "comment.xml?url=" + objectURL);
    }

    @Test
    public void testUpdatePool() throws IOException, InterruptedException {
        final Thread listenThread = FountainTestClientSupport.listenToSession(sessionId, testUserHomePool, credentials);
        final LSDTransferEntity testEntity = FountainTestClientSupport.callRESTApiWithGet(userSession,
                                                                                          "pool.xml?uri=" + testUserHomePool
                                                                                         );
        testEntity.setAttribute(LSDAttribute.RIGHTS, CAZCADE_COPYRIGHT_STATEMENT);
        FountainTestClientSupport.putEntityToURL(userSession, "pool/update?uri=" + testUserHomePool, testEntity,
                                                 "alias:cazcade:" + testUsername
                                                );
        Thread.sleep(1000);
        final LSDBaseEntity updatedEntity = FountainTestClientSupport.callRESTApiWithGet(userSession,
                                                                                         "pool.xml?uri=" + testUserHomePool
                                                                                        );
        Assert.assertEquals(CAZCADE_COPYRIGHT_STATEMENT, updatedEntity.getAttribute(LSDAttribute.RIGHTS));
        listenThread.stop();
    }

    @Test
    public void testWithContents() throws IOException, InterruptedException {
        Thread.sleep(1000);
        final LSDBaseEntity testEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "pool.xml?url=" +
                                                                                                   testUserHomePool +
                                                                                                   "&contents"
                                                                                     );
        System.err.println(testEntity.toString());
        Assert.assertTrue(testEntity.getTypeDef().asString().equals(LSDDictionaryTypes.POOL2D.getValue()));
    }
}
