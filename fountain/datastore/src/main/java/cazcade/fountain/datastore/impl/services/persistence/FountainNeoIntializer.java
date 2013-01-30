/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.services.persistence;

import cazcade.common.CommonConstants;
import cazcade.common.Logger;
import cazcade.fountain.datastore.impl.FountainPoolDAO;
import cazcade.fountain.datastore.impl.FountainUserDAO;
import cazcade.fountain.datastore.impl.LSDPersistedEntity;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
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
    public static final  String                  DEFAULT_PASSWORD = "npt78eb&AB--gi7";
    @Nonnull
    public static final  LiquidSessionIdentifier ADMIN_SESSION    = new LiquidSessionIdentifier(CommonConstants.ADMIN, null);
    @Nonnull
    public static final  String                  BOARDCAST        = "boardcast";
    @Nonnull
    private static final Logger                  log              = Logger.getLogger(FountainNeoIntializer.class);
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

            final LiquidURI boardsUri = new LiquidURI("pool:///boards");
            if (fountainNeo.findByURI(boardsUri, false) == null) {
                fountainNeo.doInTransactionAndBeginBlock(new Callable<Object>() {
                    @Nullable @Override
                    public Object call() throws Exception {
                        final LSDPersistedEntity boardsPool = poolDAO.createPoolNoTx(ADMIN_SESSION, FountainNeoImpl.SYSTEM_ALIAS_URI, fountainNeo
                                .getRootPool(), "boards", 0, 0, FountainNeoImpl.privatePermissionValue, false);
                        poolDAO.createPoolNoTx(ADMIN_SESSION, FountainNeoImpl.SYSTEM_ALIAS_URI, boardsPool, "public", 0, 0, FountainNeoImpl.publicPermissionNoDeleteValue, false);
                        poolDAO.createPoolNoTx(ADMIN_SESSION, FountainNeoImpl.SYSTEM_ALIAS_URI, boardsPool, "geo", 0, 0, FountainNeoImpl.publicPermissionNoDeleteValue, false);
                        log.info("Created core boards pools");
                        return null;
                    }
                });
            }
            else {
                fountainNeo.doInTransactionAndBeginBlock(new Callable<Object>() {
                    @Nullable @Override
                    public Object call() throws Exception {
                        final LSDPersistedEntity boardsPersistedEntity = fountainNeo.findByURI(boardsUri, false);
                        assert boardsPersistedEntity != null;
                        if (!LiquidPermissionSet.createPermissionSet(boardsPersistedEntity.getAttribute(LSDAttribute.PERMISSIONS))
                                                .hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.MODIFY)) {
                            fountainNeo.changePermissionNoTx(ADMIN_SESSION, boardsUri, LiquidPermissionChangeType.MAKE_PUBLIC, LiquidRequestDetailLevel.MINIMAL, true);
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
                final LiquidSessionIdentifier identity = new LiquidSessionIdentifier(FountainNeoImpl.SYSTEM, null);

                poolDAO.createPoolNoTx(identity, FountainNeoImpl.SYSTEM_ALIAS_URI, fountainNeo.getRootPool(), "users", (double) 0, (double) 0, null, false);

                fountainNeo.setPeoplePool(poolDAO.createPoolNoTx(identity, FountainNeoImpl.SYSTEM_ALIAS_URI, fountainNeo.getRootPool(), "people", (double) 0, (double) 0, null, false));

                poolDAO.createPoolNoTx(identity, FountainNeoImpl.SYSTEM_ALIAS_URI, fountainNeo.getRootPool(), "system", (double) 0, (double) 0, null, false);

                final LSDPersistedEntity cazcadePool = poolDAO.createPoolNoTx(identity, FountainNeoImpl.SYSTEM_ALIAS_URI, fountainNeo
                        .getRootPool(), "cazcade", (double) 0, (double) 0, null, false);
                final double x1 = -210;
                final double y1 = -210;

                final LSDPersistedEntity cazcadePublicPool = poolDAO.createPoolNoTx(identity, FountainNeoImpl.SYSTEM_ALIAS_URI, cazcadePool, "playground", x1, y1, "Playground", false);
                cazcadePublicPool.setAttribute(LSDAttribute.PERMISSIONS, FountainNeoImpl.publicPermissionValue);
                final double x = -210;

                poolDAO.createPoolNoTx(identity, FountainNeoImpl.SYSTEM_ALIAS_URI, cazcadePool, "welcome", x, (double) 210, "Welcome", false);
                final double y = -210;

                poolDAO.createPoolNoTx(identity, FountainNeoImpl.SYSTEM_ALIAS_URI, cazcadePool, "help", (double) 210, y, "Help", false);

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
        final LSDTransferEntity systemUser = LSDSimpleEntity.createEmpty();
        systemUser.setAttribute(LSDAttribute.NAME, FountainNeoImpl.SYSTEM);
        systemUser.setAttribute(LSDAttribute.FULL_NAME, "Administrator");
        systemUser.setAttribute(LSDAttribute.EMAIL_ADDRESS, CommonConstants.INFO_CAZCADE_COM);
        systemUser.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.USER.getValue());
        userDAO.createUser(systemUser, true);
        //        poolDAO.createPoolsForUserNoTx(FountainNeoImpl.SYSTEM);
        //        poolDAO.createPoolsForAliasNoTx(new LiquidURI("alias:cazcade:system"), FountainNeoImpl.SYSTEM, "Administrator", true);
    }

    private void createBoardcastUser() throws InterruptedException, UnsupportedEncodingException {
        final LSDTransferEntity boardcastUser = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.USER);
        boardcastUser.setAttribute(LSDAttribute.PLAIN_PASSWORD, DEFAULT_PASSWORD);
        boardcastUser.setAttribute(LSDAttribute.NAME, BOARDCAST);
        boardcastUser.setAttribute(LSDAttribute.FULL_NAME, "Boardcast");
        boardcastUser.setAttribute(LSDAttribute.EMAIL_ADDRESS, "info@boardcast.it");

        userDAO.createUser(boardcastUser, false);
        poolDAO.createPoolsForUserNoTx(BOARDCAST);
        poolDAO.createPoolsForAliasNoTx(new LiquidURI("alias:cazcade:boardcast"), "boardcast", "Boardcast", false);
    }

    private void createAnonUser() throws InterruptedException, UnsupportedEncodingException {
        final LSDTransferEntity anonUser = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.USER);
        anonUser.setAttribute(LSDAttribute.PLAIN_PASSWORD, DEFAULT_PASSWORD);
        anonUser.setAttribute(LSDAttribute.EMAIL_ADDRESS, CommonConstants.INFO_CAZCADE_COM);
        anonUser.setAttribute(LSDAttribute.NAME, CommonConstants.ANON);
        anonUser.setAttribute(LSDAttribute.FULL_NAME, "Anonymous");
        userDAO.createUser(anonUser, false);
        poolDAO.createPoolsForUserNoTx(CommonConstants.ANON);
        poolDAO.createPoolsForAliasNoTx(new LiquidURI(CommonConstants.ANONYMOUS_ALIAS), CommonConstants.ANON, "Anonymous", false);
    }

    private void createAdminUser() throws InterruptedException, UnsupportedEncodingException {
        final LSDSimpleEntity boardcastUser = (LSDSimpleEntity) LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.USER);
        boardcastUser.setAttribute(LSDAttribute.PLAIN_PASSWORD, CommonConstants.ADMIN);
        boardcastUser.setAttribute(LSDAttribute.NAME, CommonConstants.ADMIN);
        boardcastUser.setAttribute(LSDAttribute.FULL_NAME, "Admin");
        boardcastUser.setAttribute(LSDAttribute.EMAIL_ADDRESS, CommonConstants.INFO_CAZCADE_COM);

        final LSDPersistedEntity user = userDAO.createUser(boardcastUser, false);
        poolDAO.createPoolsForUserNoTx(CommonConstants.ADMIN);
        poolDAO.createPoolsForAliasNoTx(new LiquidURI("alias:cazcade:admin"), CommonConstants.ADMIN, "Admin", false);
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
