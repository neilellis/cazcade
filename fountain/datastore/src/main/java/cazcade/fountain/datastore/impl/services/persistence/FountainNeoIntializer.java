/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.services.persistence;

import cazcade.common.CommonConstants;
import cazcade.common.Logger;
import cazcade.fountain.datastore.impl.FountainPoolDAO;
import cazcade.fountain.datastore.impl.FountainUserDAO;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.SimpleEntity;
import cazcade.liquid.api.lsd.Types;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Callable;

/**
 * @author neilellis@cazcade.com
 */
public class FountainNeoIntializer {
    @Nonnull
    public static final  String            DEFAULT_PASSWORD = "npt78eb&AB--gi7";
    @Nonnull
    public static final  SessionIdentifier ADMIN_SESSION    = new SessionIdentifier(CommonConstants.ADMIN, null);
    @Nonnull
    public static final  String            BOARDCAST        = "boardcast";
    @Nonnull
    private static final Logger            log              = Logger.getLogger(FountainNeoIntializer.class);
    @Autowired FountainNeoImpl fountainNeo;
    @Autowired FountainPoolDAO poolDAO;
    @Autowired FountainUserDAO userDAO;

    public FountainNeoIntializer() {
    }

    public void initGraph() throws InterruptedException {
        if (!fountainNeo.isStarted()) {
            throw new IllegalStateException("Fountain Neo not started.");
        }
        try {
            if (fountainNeo.getRootPool() == null) {
                firstInit();
                log.info("First Graph initialization completed successfully.");
            }

            final LURI boardsUri = new LURI("pool:///boards");
            if (fountainNeo.findByURI(boardsUri, false) == null) {
                fountainNeo.doInTransactionAndBeginBlock(new Callable<Object>() {
                    @Nullable @Override
                    public Object call() throws Exception {
                        final PersistedEntity boardsPool = poolDAO.createPoolNoTx(ADMIN_SESSION, FountainNeoImpl.SYSTEM_ALIAS_URI, fountainNeo
                                .getRootPool(), "boards", 0, 0, FountainNeoImpl.privatePermissionValue, false);
                        final PersistedEntity publicBoardsPool = poolDAO.createPoolNoTx(ADMIN_SESSION, FountainNeoImpl.SYSTEM_ALIAS_URI, boardsPool, "public", 0, 0, "Public Boards", false);
                        fountainNeo.changePermissionNoTx(ADMIN_SESSION, publicBoardsPool.uri(), PermissionChangeType.MAKE_PUBLIC, RequestDetailLevel.MINIMAL, true);
                        final PersistedEntity geoBoardsPool = poolDAO.createPoolNoTx(ADMIN_SESSION, FountainNeoImpl.SYSTEM_ALIAS_URI, boardsPool, "geo", 0, 0, "Location Boards", false);
                        fountainNeo.changePermissionNoTx(ADMIN_SESSION, geoBoardsPool.uri(), PermissionChangeType.MAKE_PUBLIC, RequestDetailLevel.MINIMAL, true);
                        log.info("Created core boards pools");
                        return null;
                    }
                });
            } else {
                fountainNeo.doInTransactionAndBeginBlock(new Callable<Object>() {
                    @Nullable @Override
                    public Object call() throws Exception {
                        final PersistedEntity boardsPersistedEntity = fountainNeo.findByURI(boardsUri, false);
                        assert boardsPersistedEntity != null;
                        if (!PermissionSet.createPermissionSet(boardsPersistedEntity.$(Dictionary.PERMISSIONS))
                                          .hasPermission(PermissionScope.WORLD_SCOPE, Permission.P_MODIFY)) {
                            fountainNeo.changePermissionNoTx(ADMIN_SESSION, boardsUri, PermissionChangeType.MAKE_PUBLIC, RequestDetailLevel.MINIMAL, true);
                        }
                        return null;
                    }
                });
            }
        } catch (Exception e) {
            log.error(e);
            //DO NOT CONTINUE!
            throw new Error("Catastrophic failure, could not initialize graph", e);
        }
    }

    private void firstInit() throws Exception {
        fountainNeo.doInTransactionAndBeginBlock(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                fountainNeo.setRootPool(fountainNeo.createSystemPool("pool:///"));

                createSystemUser();
                final SessionIdentifier identity = new SessionIdentifier(FountainNeoImpl.SYSTEM, null);

                poolDAO.createPoolNoTx(identity, FountainNeoImpl.SYSTEM_ALIAS_URI, fountainNeo.getRootPool(), "users", (double) 0, (double) 0, null, false);

                fountainNeo.setPeoplePool(poolDAO.createPoolNoTx(identity, FountainNeoImpl.SYSTEM_ALIAS_URI, fountainNeo.getRootPool(), "people", (double) 0, (double) 0, null, false));

                poolDAO.createPoolNoTx(identity, FountainNeoImpl.SYSTEM_ALIAS_URI, fountainNeo.getRootPool(), "system", (double) 0, (double) 0, null, false);

                final PersistedEntity cazcadePool = poolDAO.createPoolNoTx(identity, FountainNeoImpl.SYSTEM_ALIAS_URI, fountainNeo.getRootPool(), "cazcade", (double) 0, (double) 0, null, false);

                final PersistedEntity cazcadePublicPool = poolDAO.createPoolNoTx(identity, FountainNeoImpl.SYSTEM_ALIAS_URI, cazcadePool, "playground", (double) -210, (double) -210, "Playground", false);
                cazcadePublicPool.$(Dictionary.PERMISSIONS, FountainNeoImpl.publicPermissionValue);

                poolDAO.createPoolNoTx(identity, FountainNeoImpl.SYSTEM_ALIAS_URI, cazcadePool, "welcome", (double) -210, (double) 210, "Welcome", false);

                poolDAO.createPoolNoTx(identity, FountainNeoImpl.SYSTEM_ALIAS_URI, cazcadePool, "help", (double) 210, (double) -210, "Help", false);

                poolDAO.createPoolNoTx(identity, FountainNeoImpl.SYSTEM_ALIAS_URI, cazcadePool, "latest", (double) 210, (double) 210, "Latest", false);

                poolDAO.createPoolNoTx(identity, FountainNeoImpl.SYSTEM_ALIAS_URI, fountainNeo.getRootPool(), "com", (double) 0, (double) 0, null, false);

                poolDAO.createPoolNoTx(identity, FountainNeoImpl.SYSTEM_ALIAS_URI, fountainNeo.getRootPool(), "org", (double) 0, (double) 0, null, false);

                poolDAO.createPoolNoTx(identity, FountainNeoImpl.SYSTEM_ALIAS_URI, fountainNeo.getRootPool(), "geo", (double) 0, (double) 0, null, false);

                poolDAO.createPoolNoTx(identity, FountainNeoImpl.SYSTEM_ALIAS_URI, fountainNeo.getRootPool(), "bluetooth", (double) 0, (double) 0, null, false);

                createBoardcastUser();
                createAnonUser();
                createAdminUser();
                return null; //TODO
            }
        });

    }

    private void createSystemUser() throws InterruptedException, UnsupportedEncodingException {
        userDAO.createUser(SimpleEntity.createEmpty()
                                       .$(Dictionary.NAME, FountainNeoImpl.SYSTEM)
                                       .$(Dictionary.FULL_NAME, "Administrator")
                                       .$(Dictionary.EMAIL_ADDRESS, CommonConstants.INFO_CAZCADE_COM)
                                       .$(Dictionary.TYPE, Types.T_USER.getValue()), true);
        //        poolDAO.createPoolsForUserNoTx(FountainNeoImpl.SYSTEM);
        //        poolDAO.createPoolsForAliasNoTx(new LURI("alias:cazcade:system"), FountainNeoImpl.SYSTEM, "Administrator", true);
    }

    private void createBoardcastUser() throws InterruptedException, UnsupportedEncodingException {

        userDAO.createUser(SimpleEntity.create(Types.T_USER)
                                       .$(Dictionary.PLAIN_PASSWORD, DEFAULT_PASSWORD)
                                       .$(Dictionary.NAME, BOARDCAST)
                                       .$(Dictionary.FULL_NAME, "Boardcast")
                                       .$(Dictionary.EMAIL_ADDRESS, "info@boardcast.it"), false);
        poolDAO.createPoolsForUserNoTx(BOARDCAST);
        poolDAO.createPoolsForAliasNoTx(new LURI("alias:cazcade:boardcast"), "boardcast", "Boardcast", false);
    }

    private void createAnonUser() throws InterruptedException, UnsupportedEncodingException {
        userDAO.createUser(SimpleEntity.create(Types.T_USER)
                                       .$(Dictionary.PLAIN_PASSWORD, DEFAULT_PASSWORD)
                                       .$(Dictionary.EMAIL_ADDRESS, CommonConstants.INFO_CAZCADE_COM)
                                       .$(Dictionary.NAME, CommonConstants.ANON)
                                       .$(Dictionary.FULL_NAME, "Anonymous"), false);
        poolDAO.createPoolsForUserNoTx(CommonConstants.ANON);
        poolDAO.createPoolsForAliasNoTx(new LURI(CommonConstants.ANONYMOUS_ALIAS), CommonConstants.ANON, "Anonymous", false);
    }

    private void createAdminUser() throws InterruptedException, UnsupportedEncodingException {

        userDAO.createUser((SimpleEntity) SimpleEntity.create(Types.T_USER)
                                                      .$(Dictionary.PLAIN_PASSWORD, CommonConstants.ADMIN)
                                                      .$(Dictionary.NAME, CommonConstants.ADMIN)
                                                      .$(Dictionary.FULL_NAME, "Admin")
                                                      .$(Dictionary.EMAIL_ADDRESS, CommonConstants.INFO_CAZCADE_COM), false);
        poolDAO.createPoolsForUserNoTx(CommonConstants.ADMIN);
        poolDAO.createPoolsForAliasNoTx(new LURI("alias:cazcade:admin"), CommonConstants.ADMIN, "Admin", false);
    }

    public void setFountainNeo(final FountainNeoImpl fountainNeo) {
        this.fountainNeo = fountainNeo;
    }

    public void setPoolDAO(final FountainPoolDAO poolDAO) {
        this.poolDAO = poolDAO;
    }

    public void setUserDAO(final FountainUserDAO userDAO) {
        this.userDAO = userDAO;
    }
}
