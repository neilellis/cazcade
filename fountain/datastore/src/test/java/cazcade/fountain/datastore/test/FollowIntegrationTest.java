/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.test;

import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.FountainSocialDAO;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.fountain.datastore.impl.services.persistence.FountainPoolDAOImpl;
import cazcade.fountain.datastore.impl.services.persistence.FountainUserDAOImpl;
import cazcade.liquid.api.LURI;
import cazcade.liquid.api.RequestDetailLevel;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.SimpleEntity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Types;
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
public class FollowIntegrationTest {
    @Autowired
    private FountainNeo       fountainNeo;
    private String            stickyName;
    private String            userPublicPoolName;
    @Nullable
    private SessionIdentifier session;
    private LURI              stickyURI;
    private LURI              publicPoolURI;
    private LURI              subPoolURI;
    private String            sticky2Name;
    private LURI              sticky2URI;
    private PersistedEntity   subPool;
    private String            userProfilePoolName;
    private LURI              sticky3URI;
    private String            sticky3Name;
    private LURI              profilePoolURI;
    private String            username;
    private String            otherUsername;
    private String            otherUserPublicPoolName;
    private LURI              otherUserPublicPoolURI;
    private LURI              otherUserURI;
    private LURI              userURI;

    @Autowired
    private FountainPoolDAOImpl poolDAO;
    @Autowired
    private FountainUserDAOImpl userDAO;
    @Autowired
    private FountainSocialDAO   socialDAO;

    @Before
    public void setUp() throws Exception {
        fountainNeo.doInTransaction(new Callable() {
            @Nullable @Override
            public Object call() throws InterruptedException, UnsupportedEncodingException {
                final PersistedEntity userPersistedEntity = createUser();
                final PersistedEntity otherUserPersistedEntity = createUser();
                username = userPersistedEntity.$(Dictionary.NAME);
                otherUsername = otherUserPersistedEntity.$(Dictionary.NAME);
                otherUserURI = new LURI("alias:cazcade:" + otherUsername);
                userURI = new LURI("alias:cazcade:" + username);
                session = new SessionIdentifier(username, null);
                System.out.println(userPersistedEntity);
                userPublicPoolName = "pool:///people/" + username + "/public";
                userProfilePoolName = "pool:///people/" + username + "/profile";
                otherUserPublicPoolName = "pool:///people/" + otherUsername + "/public";
                publicPoolURI = new LURI(userPublicPoolName);
                otherUserPublicPoolURI = new LURI(otherUserPublicPoolName);
                profilePoolURI = new LURI(userProfilePoolName);
                subPoolURI = new LURI(userPublicPoolName + "/sub");
                stickyName = "sticky" + System.currentTimeMillis();
                sticky2Name = "sticky2" + System.currentTimeMillis();
                sticky3Name = "sticky3" + System.currentTimeMillis();
                stickyURI = new LURI(userPublicPoolName + "/sub#" + stickyName);
                sticky2URI = new LURI(userPublicPoolName + "/sub#" + sticky2Name);
                sticky3URI = new LURI(userProfilePoolName + "#" + sticky3Name);
                final PersistedEntity publicPoolPersistedEntity = fountainNeo.find(publicPoolURI);
                final PersistedEntity profilePoolPersistedEntity = fountainNeo.find(profilePoolURI);

                subPool = poolDAO.createPoolNoTx(session, session.aliasURI(), publicPoolPersistedEntity, "sub", (double) 0, (double) 0, "sub", false);
                createSticky(subPool, stickyName);
                createSticky(profilePoolPersistedEntity, sticky3Name);
                return null;
            }
        });
    }

    @Nonnull
    private PersistedEntity createUser() throws InterruptedException, UnsupportedEncodingException {
        final TransferEntity user = SimpleEntity.create(Types.T_USER);
        user.$(Dictionary.PLAIN_PASSWORD, "123");
        user.$(Dictionary.EMAIL_ADDRESS, cazcade.common.CommonConstants.INFO_CAZCADE_COM);
        final String username = "test" + System.currentTimeMillis();
        user.$(Dictionary.NAME, username);
        user.$(Dictionary.FULL_NAME, "Anonymous");

        final PersistedEntity newUser = userDAO.createUser(user, false);
        poolDAO.createPoolsForUserNoTx(username);
        poolDAO.createPoolsForCazcadeAliasNoTx(username, user.$(Dictionary.FULL_NAME), false);
        return newUser;
    }

    private void createSticky(@Nonnull final PersistedEntity subPool, final String stickyName) throws InterruptedException {
        final TransferEntity sticky = SimpleEntity.create(Types.T_STICKY);
        sticky.$(Dictionary.TEXT_EXTENDED, "TEST");
        sticky.$(Dictionary.NAME, stickyName);
        poolDAO.createPoolObjectNoTx(session, subPool, sticky, session.aliasURI(), session.aliasURI(), false);
    }

    @Test
    public void testFollow() throws Exception {
        assertFalse("Already following", socialDAO.isFollowing(fountainNeo.find(userURI), fountainNeo.find(otherUserURI)));
        socialDAO.followResourceTX(session, otherUserURI, RequestDetailLevel.NORMAL, false);
        assertTrue("Not following", socialDAO.isFollowing(fountainNeo.find(userURI), fountainNeo.find(otherUserURI)));
    }
}
