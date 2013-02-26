/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.server.rest.test.jbehave;

import cazcade.common.Logger;
import cazcade.fountain.datastore.server.FountainDataStoreServer;
import cazcade.fountain.server.rest.cli.FountainRestServer;
import cazcade.fountain.server.rest.test.ClientSession;
import cazcade.fountain.server.rest.test.FountainTestClientSupport;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.*;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.jbehave.core.annotations.*;
import org.jbehave.core.model.ExamplesTable;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Set of steps used in the JBehave testing of Fountain.
 */
public class FountainSteps {
    @Nonnull
    private static final Logger log = Logger.getLogger(FountainSteps.class);
    private static FountainDataStoreServer dataStore;
    private static FountainRestServer      restServer;
    @Nonnull
    private final Map<String, UserDetails> userMap = new HashMap<String, UserDetails>();
    private Exception      serverStartupException;
    private HttpClient     client;
    private String         currentUser;
    private TransferEntity currentEntity;
    private String         currentPool;
    private String         currentPoolId;

    @Then("a pool called $poolName exists with a title of \"$title\"")
    public void aPoolExists(@Nonnull final String poolName, final String title) throws IOException {
        final UserDetails details = userMap.get(currentUser);
        final String pool = substitutePoolVariables(poolName);
        final Entity poolEntity = FountainTestClientSupport.callRESTApiWithGet(details.getUserSession(), "pool.xml?uri=" + pool);
        assertTrue(poolEntity.type().getPrimaryType().isA(Types.T_POOL2D));
        assertEquals(title, poolEntity.$(Dictionary.TITLE));
    }

    @When("the user adds a comment to the object saying \"$text\"")
    public void addCommentToObject(final String text) throws IOException {
        final UserDetails details = userMap.get(currentUser);
        final String entityURL = "comment/create.xml?uri=" +
                                 currentPool +
                                 "%23" +
                                 currentEntity.$(Dictionary.NAME) +
                                 "&text=HelloWorld&image=";
        log.debug("Current Entity URL: " + entityURL);
        FountainTestClientSupport.callRESTApiWithGet(details.getUserSession(), entityURL);
    }

    @When("the user creates a pool called $childPoolName with a title of \"$childPoolTitle\" in the pool called $parentPoolName")
    public void createAChildPool(final String childPoolName, final String childPoolTitle, @Nonnull final String parentPoolName) throws IOException, InterruptedException {
        final UserDetails details = userMap.get(currentUser);
        final String parentPool = substitutePoolVariables(parentPoolName);
        FountainTestClientSupport.callRESTApiWithGet(details.getUserSession(), "pool/create.xml?parent="
                                                                               + parentPool
                                                                               + "&name="
                                                                               + childPoolName
                                                                               + "&title="
                                                                               +
                                                                               URLEncoder.encode(childPoolTitle)
                                                                               + "&description=&x=0&y=0");
        waitForSomeSeconds(2);
        FountainTestClientSupport.callRESTApiWithGet(details.getUserSession(), "pool.xml?uri=" + parentPool + "/" + childPoolName);
    }

    @When("we wait $seconds seconds") @Alias("we wait $seconds second")
    public static void waitForSomeSeconds(final int seconds) throws InterruptedException {
        Thread.sleep(1000l * seconds);
    }

    @When("the user creates objects of type $objectType in pool $poolName as $objectTable")
    public void createObjectsInPool(final String objectType, @Nonnull final String poolName, @Nonnull final ExamplesTable objectTable) throws IOException {
        final UserDetails details = userMap.get(currentUser);
        final String pool = substitutePoolVariables(poolName);
        final String poolId = FountainTestClientSupport.convertLiquidURIToId(details.getUserSession(), pool);
        for (int i = 0; i < objectTable.getRowCount(); i++) {
            final Map<String, String> objectRow = objectTable.getRow(i);
            objectRow.put(Dictionary.TYPE.getKeyName(), objectType);
            final SimpleEntity object = SimpleEntity.createFromProperties(objectRow);
            FountainTestClientSupport.putEntityToURL(details.getUserSession(), "pool/" + poolId + ".xml", object, "alias:cazcade:"
                                                                                                                  + details.getUsername());
        }
    }

    @When("the user moves the object to $x, $y")
    public void moveObject(final int x, final int y) throws IOException, InterruptedException {
        //TODO implement with the correct move request...
        currentEntity.$(Dictionary.VIEW_X, Integer.toString(x));
        currentEntity.$(Dictionary.VIEW_Y, Integer.toString(y));
        updateCurrentEntity();
    }

    @Then("there is no object called $objectName in the pool called $poolName")
    public void noObjectWithIdInPool(final String objectName, @Nonnull final String poolName) throws IOException {
        final UserDetails details = userMap.get(currentUser);
        final String actualPoolName = substitutePoolVariables(poolName);
        final Entity entity = FountainTestClientSupport.callRESTApiWithGet(details.getUserSession(), "pool.xml?url=" +
                                                                                                     actualPoolName +
                                                                                                     "/stream%23" +
                                                                                                     objectName);
        assertTrue(entity.type().getPrimaryType().isA(Types.T_EMPTY_RESULT));
    }

    @Then("the object has $count comments") @Alias("the object has $count comment")
    public void objectHasComments(final int count) throws IOException {
        assertEquals(count, getCurrentEntityComments().size());
    }

    @When("the user links to the object from the pool called $poolName")
    public void objectIsLinkedTo(@Nonnull final String poolName) throws IOException {
        final UserDetails details = userMap.get(currentUser);
        final String pool = substitutePoolVariables(poolName);
        final String poolId = FountainTestClientSupport.convertLiquidURIToId(details.getUserSession(), pool);
        FountainTestClientSupport.callRESTApiWithGet(details.getUserSession(), "pool/"
                                                                               + currentPoolId
                                                                               + "/"
                                                                               + currentEntity.id()
                                                                               + "/link?to="
                                                                               + poolId);
    }

    @Then("the object's rights are \"$text\"")
    public void objectRightsAre(final String text) {
        assertEquals(text, currentEntity.$(Dictionary.RIGHTS));
    }

    @When("the user relocates the object to the pool called $poolName")
    public void relocateAnObject(@Nonnull final String poolName) throws IOException {
        final UserDetails details = userMap.get(currentUser);
        final String toPoolId = FountainTestClientSupport.convertLiquidURIToId(details.getUserSession(), substitutePoolVariables(poolName));
        FountainTestClientSupport.callRESTApiWithGet(details.getUserSession(), "pool/" +
                                                                               currentPoolId +
                                                                               "/" +
                                                                               currentEntity.id() +
                                                                               "/relocate?to=" +
                                                                               toPoolId);
    }

    @BeforeScenario
    public void setupForScenario() {
        client = new HttpClient();
        client.getState().setCredentials(FountainTestClientSupport.AUTH_SCOPE, new UsernamePasswordCredentials("anon", "anon"));
        client.getParams().setAuthenticationPreemptive(true);
    }

    @Given("the servers are shutdown")
    public void shutdownServers() {
        dataStore.stop();
        restServer.stop();
    }

    @Then("the user can retrieve an object called $objectName from a pool called $poolName of type $typeString")
    public void theObjectOfTypeExistsInHomePool(final String objectName, @Nonnull final String poolName, final String typeString) throws IOException {
        final UserDetails details = userMap.get(currentUser);
        final String actualPoolName = substitutePoolVariables(poolName);
        currentEntity = FountainTestClientSupport.callRESTApiWithGet(details.getUserSession(), "pool.xml?url="
                                                                                               + actualPoolName
                                                                                               + "%23"
                                                                                               + objectName);
        currentPool = actualPoolName;
        currentPoolId = FountainTestClientSupport.convertLiquidURIToId(details.getUserSession(), currentPool);
        assertNotNull(currentEntity);
        assertEquals(typeString, currentEntity.type().getPrimaryType().asString());
    }

    @Nonnull
    private String substitutePoolVariables(@Nonnull String poolString) {
        final UserDetails details = userMap.get(currentUser);
        if (poolString.indexOf("%home%") == 0) {
            poolString = details.getHomePool() + poolString.substring(6);
        }
        return poolString;
    }

    @Then("the user can retrieve an RSS feed called $objectName from their stream pool pointing to $url")
    public void theObjectOfTypeExistsInStreamPool(final String objectName, final String url) throws IOException {
        final UserDetails details = userMap.get(currentUser);
        currentEntity = FountainTestClientSupport.callRESTApiWithGet(details.getUserSession(), "pool.xml?url=" +
                                                                                               details.getHomePool() +
                                                                                               "/stream%23" +
                                                                                               objectName);
        currentPool = details.getHomePool() + "/stream";
        currentPoolId = FountainTestClientSupport.convertLiquidURIToId(details.getUserSession(), currentPool);
        assertNotNull(currentEntity);
        assertEquals(url, currentEntity.$(Dictionary.SOURCE));
    }

    @Given("the servers are running")
    public void theServersAreRunning() throws Exception {
        if (dataStore == null && restServer == null) {
            dataStore = new FountainDataStoreServer();
            restServer = new FountainRestServer();
            new Thread() {
                @Override
                public void run() {
                    try {
                        dataStore.start();
                    } catch (Exception e) {
                        serverStartupException = e;
                    }
                }
            }.start();
            new Thread() {
                @Override
                public void run() {
                    try {
                        restServer.start();
                    } catch (Exception e) {
                        serverStartupException = e;
                    }
                }
            }.start();

            dataStore.waitForInitialisation();
            restServer.waitForInitialisation();
        }

        if (serverStartupException != null) {
            throw serverStartupException;
        }
    }

    @Given("a user called $user")
    public void theUserIsAvailable(final String user) throws IOException {
        currentUser = user;
        if (!userMap.containsKey(user)) {
            final String username = randomName("cazcadetest");
            final Entity userEntity = FountainTestClientSupport.callRESTApiWithGet(new ClientSession(client, null, username),
                    "user/create.xml?.email=neil.ellis@mangala.co.uk&.name="
                    + username
                    +
                    "&.security.password.plain=test&.fn=Neil+Ellis&.type="
                    + Types.T_USER.getValue());
            final LiquidUUID userId = userEntity.id();
            final Credentials credentials = new UsernamePasswordCredentials(username, "test");
            client.getState().clearCredentials();
            client.getState().setCredentials(FountainTestClientSupport.AUTH_SCOPE, credentials);

            final Entity sessionEntity = FountainTestClientSupport.callRESTApiWithGet(new ClientSession(client, null, username), "session/create.xml?client=TestDataBuilder&key=123&hostinfo=macosx");
            final String sessionId = sessionEntity.id().toString();
            final ClientSession userSession = new ClientSession(client, sessionId, username);

            FountainTestClientSupport.callRESTApiWithGet(userSession, "alias/create.xml?me&.network=twitter&.name=" +
                                                                      username +
                                                                      "&.type=Identity.Person.Alias");
            final Entity aliasEntity = FountainTestClientSupport.callRESTApiWithGet(userSession, "alias.xml?uri=alias:twitter:"
                                                                                                 + username);
            assertTrue(aliasEntity.type().getPrimaryType().asString().equals(Types.T_ALIAS.getValue()));

            final String homePool = "pool:///people/" + username;
            final String homePoolId = FountainTestClientSupport.convertLiquidURIToId(userSession, homePool);
            final String publicPoolId = FountainTestClientSupport.convertLiquidURIToId(userSession, homePool + "/public");

            final Thread listenThread = FountainTestClientSupport.listenToSession(sessionId, homePool, credentials);
            FountainTestClientSupport.initHomePool(userSession);
            FountainTestClientSupport.initStream(userSession);
            listenThread.stop(); //TODO come up with a cleaner way of finishing this thread off...
            userMap.put(user, new UserDetails(username, userId, credentials, sessionId, userSession, homePool, homePoolId, publicPoolId));
        }
        client.getState().clearCredentials();
        client.getState().setCredentials(FountainTestClientSupport.AUTH_SCOPE, userMap.get(user).getCredentials());
    }

    private static String randomName(final String prefix) {
        return (prefix + Math.random()).replace('.', '_');
    }

    @When("the user updates the object's rights with \"$text\"")
    public void updateObjectRights(final String text) throws IOException, InterruptedException {
        final UserDetails details = userMap.get(currentUser);
        currentEntity.$(Dictionary.RIGHTS, text);
        updateCurrentEntity();
    }

    private void updateCurrentEntity() throws IOException, InterruptedException {
        waitForSomeSeconds(4);
        final UserDetails details = userMap.get(currentUser);
        FountainTestClientSupport.putEntityToURL(details.getUserSession(), "pool/"
                                                                           + currentPoolId
                                                                           + "/"
                                                                           + currentEntity.id(), currentEntity, "alias:cazcade:"
                                                                                                                + details.getUsername());
        currentEntity = FountainTestClientSupport.callRESTApiWithGet(details.getUserSession(), "pool.xml?url="
                                                                                               + URLEncoder.encode(currentEntity.$(Dictionary.URI)));
    }

    @Nonnull
    private TransferEntityCollection getCurrentEntityComments() throws IOException {
        final UserDetails details = userMap.get(currentUser);
        final Entity commentList = FountainTestClientSupport.callRESTApiWithGet(details.getUserSession(), "comment.xml?uri="
                                                                                                          + URLEncoder.encode(currentEntity
                .$(Dictionary.URI)));
        final TransferEntityCollection comments = commentList.children(Dictionary.CHILD_A);
        return comments;
    }
}
