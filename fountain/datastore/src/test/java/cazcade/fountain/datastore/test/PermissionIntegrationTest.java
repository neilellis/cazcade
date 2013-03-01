/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.test;

import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.fountain.datastore.impl.services.persistence.FountainPoolDAOImpl;
import cazcade.fountain.datastore.impl.services.persistence.FountainUserDAOImpl;
import cazcade.liquid.api.LURI;
import cazcade.liquid.api.PermissionChangeType;
import cazcade.liquid.api.RequestDetailLevel;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.*;
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

import static cazcade.liquid.api.Permission.*;
import static cazcade.liquid.api.PermissionScope.OWNER_SCOPE;
import static cazcade.liquid.api.PermissionScope.WORLD_SCOPE;
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
public class PermissionIntegrationTest {
    @Autowired
    private FountainNeo         fountainNeo;
    private String              stickyName;
    private String              username;
    private String              userPublicPoolName;
    @Nullable
    private SessionIdentifier   session;
    private LURI                stickyURI;
    private LURI                publicPoolURI;
    private LURI                subPoolURI;
    private String              sticky2Name;
    private LURI                sticky2URI;
    private PersistedEntity     subPool;
    private String              userProfilePoolName;
    private LURI                sticky3URI;
    private String              sticky3Name;
    private LURI                profilePoolURI;
    @Autowired

    private FountainPoolDAOImpl poolDAO;
    @Autowired
    private FountainUserDAOImpl userDAO;

    @Before
    public void setUp() throws Exception {
        fountainNeo.doInTransaction(new Callable() {
            @Nullable @Override
            public Object call() throws InterruptedException, UnsupportedEncodingException {
                final TransferEntity user = SimpleEntity.create(Types.T_USER);
                user.$(Dictionary.PLAIN_PASSWORD, "123");
                user.$(Dictionary.EMAIL_ADDRESS, cazcade.common.CommonConstants.INFO_CAZCADE_COM);
                username = "test" + System.currentTimeMillis();
                user.$(Dictionary.NAME, username);
                user.$(Dictionary.FULL_NAME, "Anonymous");

                final PersistedEntity userPersistedEntity = userDAO.createUser(user, false);
                poolDAO.createPoolsForUserNoTx(username);
                poolDAO.createPoolsForCazcadeAliasNoTx(username, user.$(Dictionary.FULL_NAME), false);

                session = new SessionIdentifier(username, null);
                userPublicPoolName = "pool:///people/" + username + "/public";
                userProfilePoolName = "pool:///people/" + username + "/profile";
                publicPoolURI = new LURI(userPublicPoolName);
                profilePoolURI = new LURI(userProfilePoolName);
                subPoolURI = new LURI(userPublicPoolName + "/sub");
                stickyName = "sticky" + System.currentTimeMillis();
                sticky2Name = "sticky2" + System.currentTimeMillis();
                sticky3Name = "sticky3" + System.currentTimeMillis();
                stickyURI = new LURI(userPublicPoolName + "/sub#" + stickyName);
                sticky2URI = new LURI(userPublicPoolName + "/sub#" + sticky2Name);
                sticky3URI = new LURI(userProfilePoolName + "#" + sticky3Name);
                final PersistedEntity publicPoolPersistedEntity = fountainNeo.findByURI(publicPoolURI, true);
                final PersistedEntity profilePoolPersistedEntity = fountainNeo.findByURI(profilePoolURI, true);
                assertNotNull(publicPoolPersistedEntity);
                assertNotNull(session);
                subPool = poolDAO.createPoolNoTx(session, session.aliasURI(), publicPoolPersistedEntity, "sub", (double) 0, (double) 0, "sub", false);
                createSticky(subPool, stickyName);
                createSticky(profilePoolPersistedEntity, sticky3Name);
                return null;
            }
        });
    }

    private void createSticky(@Nonnull final PersistedEntity subPool, final String stickyName) throws InterruptedException {
        final TransferEntity sticky = SimpleEntity.create(Types.T_STICKY);
        sticky.$(Dictionary.TEXT_EXTENDED, "TEST");
        sticky.$(Dictionary.NAME, stickyName);
        poolDAO.createPoolObjectNoTx(session, subPool, sticky, session.aliasURI(), session.aliasURI(), false);
    }

    @Test
    public void testPermissionsAfterChange() throws Exception {
        fountainNeo.doInTransaction(new Callable<Object>() {
            @Nullable @Override
            public Object call() throws Exception {
                fountainNeo.changePermissionNoTx(session, publicPoolURI, PermissionChangeType.MAKE_PUBLIC_READONLY, RequestDetailLevel.NORMAL, false);
                final Entity publicPoolEntity = poolDAO.getPoolObjectTx(session, publicPoolURI, false, false, RequestDetailLevel.NORMAL);
                assertTrue(publicPoolEntity.canBe(Types.T_POOL2D));
                assertFalse(publicPoolEntity.allowed(WORLD_SCOPE, P_MODIFY));
                assertFalse(publicPoolEntity.allowed(WORLD_SCOPE, P_EDIT));

                final Entity subPoolEntity = poolDAO.getPoolObjectTx(session, subPoolURI, false, false, RequestDetailLevel.NORMAL);
                assertTrue(subPoolEntity.canBe(Types.T_POOL2D));
                assertFalse(subPoolEntity.allowed(WORLD_SCOPE, P_MODIFY));
                assertFalse(subPoolEntity.allowed(WORLD_SCOPE, P_EDIT));

                Entity stickyEntity = poolDAO.getPoolObjectTx(session, stickyURI, false, false, RequestDetailLevel.NORMAL);
                assertTrue(stickyEntity.canBe(Types.T_STICKY));
                assertTrue(stickyEntity.allowed(OWNER_SCOPE, P_MODIFY));
                assertTrue(stickyEntity.allowed(OWNER_SCOPE, P_EDIT));

                assertTrue(stickyEntity.allowed(WORLD_SCOPE, P_VIEW));
                assertFalse(stickyEntity.allowed(WORLD_SCOPE, P_MODIFY));
                assertFalse(stickyEntity.allowed(WORLD_SCOPE, P_EDIT));

                final PersistedEntity newSubPool = fountainNeo.find(subPoolURI);
                assertEquals("o=vmeds,f=v,m=vm,v=v,w=v,u=v,a=vmeds,t=vmeds,c=,e=", newSubPool.$(Dictionary.PERMISSIONS));
                createSticky(newSubPool, sticky2Name);

                final Entity sticky2Entity = poolDAO.getPoolObjectTx(session, sticky2URI, false, false, RequestDetailLevel.NORMAL);
                assertTrue(sticky2Entity.canBe(Types.T_STICKY));
                assertTrue(sticky2Entity.allowed(OWNER_SCOPE, P_MODIFY));
                assertTrue(sticky2Entity.allowed(OWNER_SCOPE, P_EDIT));

                assertTrue(sticky2Entity.allowed(WORLD_SCOPE, P_VIEW));
                assertFalse(sticky2Entity.allowed(WORLD_SCOPE, P_MODIFY));
                assertFalse(sticky2Entity.allowed(WORLD_SCOPE, P_EDIT));

                fountainNeo.changePermissionNoTx(session, publicPoolURI, PermissionChangeType.MAKE_PUBLIC, RequestDetailLevel.NORMAL, false);
                stickyEntity = poolDAO.getPoolObjectTx(session, stickyURI, false, false, RequestDetailLevel.NORMAL);
                assertTrue(stickyEntity.allowed(WORLD_SCOPE, P_MODIFY));
                assertFalse(stickyEntity.allowed(WORLD_SCOPE, P_EDIT));
                return null;
            }
        });
    }

    @Test
    public void testProfilePoolStickyInitialPermissions() throws Exception {
        final Entity entity = poolDAO.getPoolObjectTx(session, sticky3URI, false, false, RequestDetailLevel.NORMAL);
        assertTrue(entity.allowed(WORLD_SCOPE, P_VIEW));
        assertFalse(entity.allowed(WORLD_SCOPE, P_MODIFY));
        assertFalse(entity.allowed(WORLD_SCOPE, P_EDIT));
    }

    @Test
    public void testPublicPoolStickyInitialPermissions() throws Exception {
        final Entity entity = poolDAO.getPoolObjectTx(session, stickyURI, false, false, RequestDetailLevel.NORMAL);
        assertTrue(entity.allowed(WORLD_SCOPE, P_VIEW));
        assertTrue(entity.allowed(WORLD_SCOPE, P_MODIFY));
        assertFalse(entity.allowed(WORLD_SCOPE, P_EDIT));
    }
}
