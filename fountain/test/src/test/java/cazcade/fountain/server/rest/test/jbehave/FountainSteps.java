package cazcade.fountain.server.rest.test.jbehave;

import cazcade.common.Logger;
import cazcade.fountain.datastore.server.FountainDataStoreServer;
import cazcade.fountain.server.rest.cli.FountainRestServer;
import cazcade.fountain.server.rest.test.ClientSession;
import cazcade.fountain.server.rest.test.FountainTestClientSupport;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.jbehave.core.annotations.*;
import org.jbehave.core.model.ExamplesTable;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Set of steps used in the JBehave testing of Fountain.
 */
public class FountainSteps {

    private static final Logger log = Logger.getLogger(FountainSteps.class);
    private Map<String, UserDetails> userMap = new HashMap<String, UserDetails>();
    private static FountainDataStoreServer dataStore;
    private static FountainRestServer restServer;
    private Exception serverStartupException;
    private HttpClient client;
    private String currentUser;
    private LSDEntity currentEntity;
    private String currentPool;
    private String currentPoolId;

    @BeforeScenario
    public void setupForScenario() {
        client = new HttpClient();
        client.getState().setCredentials(FountainTestClientSupport.AUTH_SCOPE, new UsernamePasswordCredentials("anon", "anon"));
        client.getParams().setAuthenticationPreemptive(true);
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

    @Given("the servers are shutdown")
    public void shutdownServers() {
        dataStore.stop();
        restServer.stop();
    }


    @Given("a user called $user")
    public void theUserIsAvailable(String user) throws IOException {
        this.currentUser = user;
        if (!userMap.containsKey(user)) {
            String username = randomName("cazcadetest");
            LSDEntity userEntity = FountainTestClientSupport.callRESTApiWithGet(
                    new ClientSession(client, null, username),
                    "user/create.xml?.email=neil.ellis@mangala.co.uk&.name=" + username +
                            "&.security.password.plain=test&.fn=Neil+Ellis&.type=" + LSDDictionaryTypes.USER.getValue());
            LiquidUUID userId = userEntity.getID();
            Credentials credentials = new UsernamePasswordCredentials(username, "test");
            client.getState().clearCredentials();
            client.getState().setCredentials(FountainTestClientSupport.AUTH_SCOPE, credentials);

            LSDEntity sessionEntity = FountainTestClientSupport.callRESTApiWithGet(
                    new ClientSession(client, null, username),
                    "session/create.xml?client=TestDataBuilder&key=123&hostinfo=macosx");
            String sessionId = sessionEntity.getID().toString();
            ClientSession userSession = new ClientSession(client, sessionId, username);

            FountainTestClientSupport.callRESTApiWithGet(userSession,
                    "alias/create.xml?me&.network=twitter&.name=" + username + "&.type=Identity.Person.Alias");
            LSDEntity aliasEntity = FountainTestClientSupport.callRESTApiWithGet(
                    userSession, "alias.xml?uri=alias:twitter:" + username);
            assertTrue(aliasEntity.getTypeDef().getPrimaryType().asString().equals(LSDDictionaryTypes.ALIAS.getValue()));

            String homePool = "pool:///people/" + username;
            String homePoolId = FountainTestClientSupport.convertLiquidURIToId(userSession, homePool);
            String publicPoolId = FountainTestClientSupport.convertLiquidURIToId(userSession, homePool + "/public");

            Thread listenThread = FountainTestClientSupport.listenToSession(sessionId, homePool, credentials);
            FountainTestClientSupport.initHomePool(userSession);
            FountainTestClientSupport.initStream(userSession);
            listenThread.stop(); //TODO come up with a cleaner way of finishing this thread off...
            userMap.put(user, new UserDetails(
                    username, userId, credentials, sessionId, userSession, homePool, homePoolId, publicPoolId));
        }
        client.getState().clearCredentials();
        client.getState().setCredentials(FountainTestClientSupport.AUTH_SCOPE, userMap.get(user).getCredentials());
    }

    @When("we wait $seconds seconds")
    @Alias("we wait $seconds second")
    public static void waitForSomeSeconds(int seconds) throws InterruptedException {
        Thread.sleep(1000l * seconds);
    }

    @Then("the user can retrieve an object called $objectName from a pool called $poolName of type $typeString")
    public void theObjectOfTypeExistsInHomePool(String objectName, String poolName, String typeString) throws IOException {
        UserDetails details = userMap.get(currentUser);
        String actualPoolName = substitutePoolVariables(poolName);
        currentEntity = FountainTestClientSupport.callRESTApiWithGet(details.getUserSession(),
                "pool.xml?url=" + actualPoolName + "%23" + objectName);
        currentPool = actualPoolName;
        currentPoolId = FountainTestClientSupport.convertLiquidURIToId(details.getUserSession(), currentPool);
        assertNotNull(currentEntity);
        assertEquals(typeString, currentEntity.getTypeDef().getPrimaryType().asString());

    }

    @Then("the user can retrieve an RSS feed called $objectName from their stream pool pointing to $url")
    public void theObjectOfTypeExistsInStreamPool(String objectName, String url) throws IOException {
        UserDetails details = userMap.get(currentUser);
        currentEntity = FountainTestClientSupport.callRESTApiWithGet(details.getUserSession(),
                "pool.xml?url=" + details.getHomePool() + "/stream%23" + objectName);
        currentPool = details.getHomePool() + "/stream";
        currentPoolId = FountainTestClientSupport.convertLiquidURIToId(details.getUserSession(), currentPool);
        assertNotNull(currentEntity);
        assertEquals(url, currentEntity.getAttribute(LSDAttribute.SOURCE));

    }

    @When("the user updates the object's rights with \"$text\"")
    public void updateObjectRights(String text) throws IOException, InterruptedException {
        UserDetails details = userMap.get(currentUser);
        currentEntity.setAttribute(LSDAttribute.RIGHTS, text);
        updateCurrentEntity();
    }

    @When("the user moves the object to $x, $y")
    public void moveObject(int x, int y) throws IOException, InterruptedException {
        //TODO implement with the correct move request...
        currentEntity.setAttribute(LSDAttribute.VIEW_X, Integer.toString(x));
        currentEntity.setAttribute(LSDAttribute.VIEW_Y, Integer.toString(y));
        updateCurrentEntity();
    }

    @When("the user adds a comment to the object saying \"$text\"")
    public void addCommentToObject(String text) throws IOException {
        UserDetails details = userMap.get(currentUser);
        String entityURL = "comment/create.xml?uri=" + currentPool + "%23" + currentEntity.getAttribute(LSDAttribute.NAME) + "&text=HelloWorld&image=";
        log.debug("Current Entity URL: " + entityURL);
        FountainTestClientSupport.callRESTApiWithGet(details.getUserSession(),
                entityURL);
    }

    @Then("the object's rights are \"$text\"")
    public void objectRightsAre(String text) {
        assertEquals(text, currentEntity.getAttribute(LSDAttribute.RIGHTS));
    }

    @Then("the object has $count comments")
    @Alias("the object has $count comment")
    public void objectHasComments(int count) throws IOException {
        assertEquals(count, getCurrentEntityComments().size());
    }

    @When("the user relocates the object to the pool called $poolName")
    public void relocateAnObject(String poolName) throws IOException {
        UserDetails details = userMap.get(currentUser);
        String toPoolId = FountainTestClientSupport.convertLiquidURIToId(details.getUserSession(),
                substitutePoolVariables(poolName));
        FountainTestClientSupport.callRESTApiWithGet(details.getUserSession(),
                "pool/" + currentPoolId + "/" + currentEntity.getID() + "/relocate?to=" + toPoolId);
    }

    @Then("there is no object called $objectName in the pool called $poolName")
    public void noObjectWithIdInPool(String objectName, String poolName) throws IOException {
        UserDetails details = userMap.get(currentUser);
        String actualPoolName = substitutePoolVariables(poolName);
        LSDEntity entity = FountainTestClientSupport.callRESTApiWithGet(details.getUserSession(),
                "pool.xml?url=" + actualPoolName + "/stream%23" + objectName);
        assertTrue(entity.getTypeDef().getPrimaryType().isA(LSDDictionaryTypes.EMPTY_RESULT));
    }

    @When("the user links to the object from the pool called $poolName")
    public void objectIsLinkedTo(String poolName) throws IOException {
        UserDetails details = userMap.get(currentUser);
        String pool = substitutePoolVariables(poolName);
        String poolId = FountainTestClientSupport.convertLiquidURIToId(details.getUserSession(), pool);
        FountainTestClientSupport.callRESTApiWithGet(details.getUserSession(),
                "pool/" + currentPoolId + "/" + currentEntity.getID() + "/link?to=" + poolId);
    }

    @When("the user creates a pool called $childPoolName with a title of \"$childPoolTitle\" in the pool called $parentPoolName")
    public void createAChildPool(String childPoolName, String childPoolTitle, String parentPoolName)
            throws IOException, InterruptedException {
        UserDetails details = userMap.get(currentUser);
        String parentPool = substitutePoolVariables(parentPoolName);
        FountainTestClientSupport.callRESTApiWithGet(details.getUserSession(),
                "pool/create.xml?parent=" + parentPool + "&name=" + childPoolName + "&title=" +
                        URLEncoder.encode(childPoolTitle) + "&description=&x=0&y=0");
        waitForSomeSeconds(2);
        FountainTestClientSupport.callRESTApiWithGet(details.getUserSession(),
                "pool.xml?uri=" + parentPool + "/" + childPoolName);

    }

    @Then("a pool called $poolName exists with a title of \"$title\"")
    public void aPoolExists(String poolName, String title) throws IOException {
        UserDetails details = userMap.get(currentUser);
        String pool = substitutePoolVariables(poolName);
        LSDEntity poolEntity = FountainTestClientSupport.callRESTApiWithGet(details.getUserSession(),
                "pool.xml?uri=" + pool);
        assertTrue(poolEntity.getTypeDef().getPrimaryType().isA(LSDDictionaryTypes.POOL2D));
        assertEquals(title, poolEntity.getAttribute(LSDAttribute.TITLE));
    }


    @When("the user creates objects of type $objectType in pool $poolName as $objectTable")
    public void createObjectsInPool(String objectType, String poolName, ExamplesTable objectTable) throws IOException {
        UserDetails details = userMap.get(currentUser);
        String pool = substitutePoolVariables(poolName);
        String poolId = FountainTestClientSupport.convertLiquidURIToId(details.getUserSession(), pool);
        for (int i = 0; i < objectTable.getRowCount(); i++) {
            Map<String, String> objectRow = objectTable.getRow(i);
            objectRow.put(LSDAttribute.TYPE.getKeyName(), objectType);
            LSDSimpleEntity object = LSDSimpleEntity.createFromProperties(objectRow);
            FountainTestClientSupport.putEntityToURL(details.getUserSession(), "pool/" + poolId + ".xml", object,
                    "alias:cazcade:" + details.getUsername());
        }
    }


    private String substitutePoolVariables(String poolString) {
        UserDetails details = userMap.get(currentUser);
        if (poolString.indexOf("%home%") == 0) {
            poolString = details.getHomePool() + poolString.substring(6);
        }
        return poolString;
    }

    private List<LSDEntity> getCurrentEntityComments() throws IOException {
        UserDetails details = userMap.get(currentUser);
        LSDEntity commentList = FountainTestClientSupport.callRESTApiWithGet(details.getUserSession(),
                "comment.xml?uri=" + URLEncoder.encode(currentEntity.getAttribute(LSDAttribute.URI)));
        List<LSDEntity> comments = commentList.getSubEntities(LSDAttribute.CHILD);
        return comments;
    }

    private void updateCurrentEntity() throws IOException, InterruptedException {
        waitForSomeSeconds(4);
        UserDetails details = userMap.get(currentUser);
        FountainTestClientSupport.putEntityToURL(details.getUserSession(),
                "pool/" + currentPoolId + "/" + currentEntity.getID(),
                currentEntity, "alias:cazcade:" + details.getUsername());
        currentEntity = FountainTestClientSupport.callRESTApiWithGet(details.getUserSession(),
                "pool.xml?url=" + URLEncoder.encode(currentEntity.getAttribute(LSDAttribute.URI)));
    }


    private static String randomName(String prefix) {
        return (prefix + Math.random()).replace('.', '_');
    }


}
