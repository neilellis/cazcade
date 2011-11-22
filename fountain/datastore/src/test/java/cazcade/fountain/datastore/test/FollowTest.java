package cazcade.fountain.datastore.test;

import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.FountainPoolDAOImpl;
import cazcade.fountain.datastore.impl.FountainSocialDAO;
import cazcade.fountain.datastore.impl.FountainUserDAOImpl;
import cazcade.liquid.api.LiquidRequestDetailLevel;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The first in hopefully many unit tests agains the fountain server.
 *
 * @author neilellis@cazcade.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
// ApplicationContext will be loaded from "/applicationContext.xml" and "/applicationContext-test.xml"
// in the root of the classpath
@ContextConfiguration({"classpath:datastore-spring-config.xml"})
public class FollowTest {

    @Autowired
    private FountainNeo fountainNeo;
    private String stickyName;
    private String userPublicPoolName;
    private LiquidSessionIdentifier session;
    private LiquidURI stickyURI;
    private LiquidURI publicPoolURI;
    private LiquidURI subPoolURI;
    private String sticky2Name;
    private LiquidURI sticky2URI;
    private Node subPool;
    private String userProfilePoolName;
    private LiquidURI sticky3URI;
    private String sticky3Name;
    private LiquidURI profilePoolURI;
    private String username;
    private String otherUsername;
    private String otherUserPublicPoolName;
    private LiquidURI otherUserPublicPoolURI;
    private LiquidURI otherUserURI;
    private LiquidURI userURI;

    @Autowired
    private FountainPoolDAOImpl poolDAO;
    @Autowired
    private FountainUserDAOImpl userDAO;
    @Autowired
    private FountainSocialDAO socialDAO;

    @Before
    public void setUp() throws Exception {
        fountainNeo.doInTransaction(new Callable() {

            @Override
            public Object call() throws InterruptedException, UnsupportedEncodingException {
                Node userNode = createUser();
                Node otherUserNode = createUser();
                username = (String) userNode.getProperty(LSDAttribute.NAME.getKeyName());
                otherUsername = (String) otherUserNode.getProperty(LSDAttribute.NAME.getKeyName());
                otherUserURI = new LiquidURI("alias:cazcade:" + otherUsername);
                userURI = new LiquidURI("alias:cazcade:" + username);
                session = new LiquidSessionIdentifier(username, null);
                System.out.println(userNode);
                userPublicPoolName = "pool:///people/" + username + "/public";
                userProfilePoolName = "pool:///people/" + username + "/profile";
                otherUserPublicPoolName = "pool:///people/" + otherUsername + "/public";
                publicPoolURI = new LiquidURI(userPublicPoolName);
                otherUserPublicPoolURI = new LiquidURI(otherUserPublicPoolName);
                profilePoolURI = new LiquidURI(userProfilePoolName);
                subPoolURI = new LiquidURI(userPublicPoolName + "/sub");
                stickyName = "sticky" + System.currentTimeMillis();
                sticky2Name = "sticky2" + System.currentTimeMillis();
                sticky3Name = "sticky3" + System.currentTimeMillis();
                stickyURI = new LiquidURI(userPublicPoolName + "/sub#" + stickyName);
                sticky2URI = new LiquidURI(userPublicPoolName + "/sub#" + sticky2Name);
                sticky3URI = new LiquidURI(userProfilePoolName + "#" + sticky3Name);
                Node publicPoolNode = fountainNeo.findByURI(publicPoolURI);
                Node profilePoolNode = fountainNeo.findByURI(profilePoolURI);

                subPool = poolDAO.createPoolNoTx(session, session.getAliasURL(), publicPoolNode, "sub", (double) 0, (double) 0, "sub", false);
                createSticky(subPool, stickyName);
                createSticky(profilePoolNode, sticky3Name);
                return null;
            }
        });
    }

    private Node createUser() throws InterruptedException, UnsupportedEncodingException {
        final LSDSimpleEntity user = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.USER);
        user.setAttribute(LSDAttribute.PLAIN_PASSWORD, "123");
        user.setAttribute(LSDAttribute.EMAIL_ADDRESS, "info@cazcade.com");
        String username = "test" + System.currentTimeMillis();
        user.setAttribute(LSDAttribute.NAME, username);
        user.setAttribute(LSDAttribute.FULL_NAME, "Anonymous");

        Node newUser = userDAO.createUser(user, false);
        poolDAO.createPoolsForUserNoTx(username);
        return newUser;
    }

    private void createSticky(Node subPool, String stickyName) throws InterruptedException {
        final LSDSimpleEntity sticky = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.STICKY);
        sticky.setAttribute(LSDAttribute.TEXT_EXTENDED, "TEST");
        sticky.setAttribute(LSDAttribute.NAME, stickyName);
        poolDAO.createPoolObjectNoTx(session, subPool, sticky, session.getAliasURL(), session.getAliasURL(), false);
    }


    @Test
    public void testFollow() throws Exception {
        assertFalse("Already following", socialDAO.isFollowing(fountainNeo.findByURI(userURI), fountainNeo.findByURI(otherUserURI)));
        socialDAO.followResourceTX(session, otherUserURI, LiquidRequestDetailLevel.NORMAL, false);
        assertTrue("Not following", socialDAO.isFollowing(fountainNeo.findByURI(userURI), fountainNeo.findByURI(otherUserURI)));
    }


}
