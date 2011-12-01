package cazcade.fountain.datastore.test;

import cazcade.fountain.datastore.impl.FountainEntity;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.services.persistence.FountainPoolDAOImpl;
import cazcade.fountain.datastore.impl.services.persistence.FountainUserDAOImpl;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Callable;

import static org.junit.Assert.*;

/**
 * The first in hopefully many unit tests agains the fountain server.
 *
 * @author neilellis@cazcade.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
// ApplicationContext will be loaded from "/applicationContext.xml" and "/applicationContext-test.xml"
// in the root of the classpath
@ContextConfiguration({"classpath:datastore-spring-config.xml"})
public class PermissionTest {

    @Autowired
    private FountainNeo fountainNeo;
    private String stickyName;
    private String username;
    private String userPublicPoolName;
    @Nullable
    private LiquidSessionIdentifier session;
    private LiquidURI stickyURI;
    private LiquidURI publicPoolURI;
    private LiquidURI subPoolURI;
    private String sticky2Name;
    private LiquidURI sticky2URI;
    private FountainEntity subPool;
    private String userProfilePoolName;
    private LiquidURI sticky3URI;
    private String sticky3Name;
    private LiquidURI profilePoolURI;
    @Autowired

    private FountainPoolDAOImpl poolDAO;
    @Autowired
    private FountainUserDAOImpl userDAO;

    @Before
    public void setUp() throws Exception {
        fountainNeo.doInTransaction(new Callable() {


            @Nullable
            @Override
            public Object call() throws InterruptedException, UnsupportedEncodingException {
                final LSDSimpleEntity user = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.USER);
                user.setAttribute(LSDAttribute.PLAIN_PASSWORD, "123");
                user.setAttribute(LSDAttribute.EMAIL_ADDRESS, "info@cazcade.com");
                username = "test" + System.currentTimeMillis();
                user.setAttribute(LSDAttribute.NAME, username);
                user.setAttribute(LSDAttribute.FULL_NAME, "Anonymous");

                final FountainEntity userFountainEntity = userDAO.createUser(user, false);
                poolDAO.createPoolsForUserNoTx(username);
                poolDAO.createPoolsForCazcadeAliasNoTx(username, user.getAttribute(LSDAttribute.FULL_NAME), false);

                session = new LiquidSessionIdentifier(username, null);
                userPublicPoolName = "pool:///people/" + username + "/public";
                userProfilePoolName = "pool:///people/" + username + "/profile";
                publicPoolURI = new LiquidURI(userPublicPoolName);
                profilePoolURI = new LiquidURI(userProfilePoolName);
                subPoolURI = new LiquidURI(userPublicPoolName + "/sub");
                stickyName = "sticky" + System.currentTimeMillis();
                sticky2Name = "sticky2" + System.currentTimeMillis();
                sticky3Name = "sticky3" + System.currentTimeMillis();
                stickyURI = new LiquidURI(userPublicPoolName + "/sub#" + stickyName);
                sticky2URI = new LiquidURI(userPublicPoolName + "/sub#" + sticky2Name);
                sticky3URI = new LiquidURI(userProfilePoolName + "#" + sticky3Name);
                final FountainEntity publicPoolFountainEntity = fountainNeo.findByURI(publicPoolURI, true);
                final FountainEntity profilePoolFountainEntity = fountainNeo.findByURI(profilePoolURI, true);
                assertNotNull(publicPoolFountainEntity);
                assertNotNull(session);
                subPool = poolDAO.createPoolNoTx(session, session.getAliasURL(), publicPoolFountainEntity, "sub", (double) 0, (double) 0, "sub", false);
                createSticky(subPool, stickyName);
                createSticky(profilePoolFountainEntity, sticky3Name);
                return null;
            }
        });
    }

    private void createSticky(@Nonnull final FountainEntity subPool, final String stickyName) throws InterruptedException {
        final LSDSimpleEntity sticky = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.STICKY);
        sticky.setAttribute(LSDAttribute.TEXT_EXTENDED, "TEST");
        sticky.setAttribute(LSDAttribute.NAME, stickyName);
        poolDAO.createPoolObjectNoTx(session, subPool, sticky, session.getAliasURL(), session.getAliasURL(), false);
    }

    @Test
    public void testPublicPoolStickyInitialPermissions() throws InterruptedException {
        final LSDEntity entity = poolDAO.getPoolObjectTx(session, stickyURI, false, false, LiquidRequestDetailLevel.NORMAL);
        assertTrue(entity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.VIEW));
        assertTrue(entity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.MODIFY));
        assertFalse(entity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.EDIT));
    }

    @Test
    public void testProfilePoolStickyInitialPermissions() throws InterruptedException {
        final LSDEntity entity = poolDAO.getPoolObjectTx(session, sticky3URI, false, false, LiquidRequestDetailLevel.NORMAL);
        assertTrue(entity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.VIEW));
        assertFalse(entity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.MODIFY));
        assertFalse(entity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.EDIT));
    }

    @Test
    public void testPermissionsAfterChange() throws Exception {
        fountainNeo.doInTransaction(new Callable<Object>() {
            @Nullable
            @Override
            public Object call() throws Exception {
                fountainNeo.changePermissionNoTx(session, publicPoolURI, LiquidPermissionChangeType.MAKE_PUBLIC_READONLY, LiquidRequestDetailLevel.NORMAL, false);
                final LSDEntity publicPoolEntity = poolDAO.getPoolObjectTx(session, publicPoolURI, false, false, LiquidRequestDetailLevel.NORMAL);
                assertTrue(publicPoolEntity.canBe(LSDDictionaryTypes.POOL2D));
                assertFalse(publicPoolEntity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.MODIFY));
                assertFalse(publicPoolEntity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.EDIT));

                final LSDEntity subPoolEntity = poolDAO.getPoolObjectTx(session, subPoolURI, false, false, LiquidRequestDetailLevel.NORMAL);
                assertTrue(subPoolEntity.canBe(LSDDictionaryTypes.POOL2D));
                assertFalse(subPoolEntity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.MODIFY));
                assertFalse(subPoolEntity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.EDIT));

                LSDEntity stickyEntity = poolDAO.getPoolObjectTx(session, stickyURI, false, false, LiquidRequestDetailLevel.NORMAL);
                assertTrue(stickyEntity.canBe(LSDDictionaryTypes.STICKY));
                assertTrue(stickyEntity.hasPermission(LiquidPermissionScope.OWNER, LiquidPermission.MODIFY));
                assertTrue(stickyEntity.hasPermission(LiquidPermissionScope.OWNER, LiquidPermission.EDIT));

                assertTrue(stickyEntity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.VIEW));
                assertFalse(stickyEntity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.MODIFY));
                assertFalse(stickyEntity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.EDIT));

                final FountainEntity newSubPool = fountainNeo.findByURI(subPoolURI);
                assertEquals("o=vmeds,f=v,m=vm,v=v,w=v,u=v,a=vmeds,t=vmeds,c=,e=", newSubPool.getAttribute(LSDAttribute.PERMISSIONS));
                createSticky(newSubPool, sticky2Name);

                final LSDEntity sticky2Entity = poolDAO.getPoolObjectTx(session, sticky2URI, false, false, LiquidRequestDetailLevel.NORMAL);
                assertTrue(sticky2Entity.canBe(LSDDictionaryTypes.STICKY));
                assertTrue(sticky2Entity.hasPermission(LiquidPermissionScope.OWNER, LiquidPermission.MODIFY));
                assertTrue(sticky2Entity.hasPermission(LiquidPermissionScope.OWNER, LiquidPermission.EDIT));

                assertTrue(sticky2Entity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.VIEW));
                assertFalse(sticky2Entity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.MODIFY));
                assertFalse(sticky2Entity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.EDIT));

                fountainNeo.changePermissionNoTx(session, publicPoolURI, LiquidPermissionChangeType.MAKE_PUBLIC, LiquidRequestDetailLevel.NORMAL, false);
                stickyEntity = poolDAO.getPoolObjectTx(session, stickyURI, false, false, LiquidRequestDetailLevel.NORMAL);
                assertTrue(stickyEntity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.MODIFY));
                assertFalse(stickyEntity.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.EDIT));
                return null;
            }
        });
    }


}
