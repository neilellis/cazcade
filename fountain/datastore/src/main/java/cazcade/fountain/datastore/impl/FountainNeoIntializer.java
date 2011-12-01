package cazcade.fountain.datastore.impl;

import cazcade.common.Logger;
import cazcade.fountain.datastore.Node;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
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
    private final static Logger log = Logger.getLogger(FountainNeoIntializer.class);

    @Nonnull
    public static final String DEFAULT_PASSWORD = "npt78eb&AB--gi7";
    @Nonnull
    public static final String ADMIN = "admin";
    @Nonnull
    public static final LiquidSessionIdentifier ADMIN_SESSION = new LiquidSessionIdentifier(ADMIN, null);
    @Nonnull
    public static final String ANON = "anon";
    @Nonnull
    public static final String HASHBO = "hashbo";


    @Autowired
    FountainNeo fountainNeo;
    @Autowired
    FountainPoolDAO poolDAO;
    @Autowired
    FountainUserDAO userDAO;

    public FountainNeoIntializer() {
    }

    public void initGraph() throws InterruptedException {
        try {
            if (fountainNeo.getRootPool() == null) {
                firstInit();
                log.info("First Graph initialization completed successfully.");
            }

            final LiquidURI boardsUri = new LiquidURI("pool:///boards");
            if (fountainNeo.findByURI(boardsUri, false) == null) {
                fountainNeo.doInTransactionAndBeginBlock(new Callable<Object>() {
                    @Nullable
                    @Override
                    public Object call() throws Exception {
                        Node boardsPool = poolDAO.createPoolNoTx(ADMIN_SESSION, FountainNeo.ADMIN_ALIAS_URI, fountainNeo.getRootPool(), "boards", 0, 0, FountainNeo.privatePermissionValue, false);
                        poolDAO.createPoolNoTx(ADMIN_SESSION, FountainNeo.ADMIN_ALIAS_URI, boardsPool, "public", 0, 0, FountainNeo.publicPermissionNoDeleteValue, false);
                        poolDAO.createPoolNoTx(ADMIN_SESSION, FountainNeo.ADMIN_ALIAS_URI, boardsPool, "geo", 0, 0, FountainNeo.publicPermissionNoDeleteValue, false);
                        log.info("Created core boards pools");
                        return null;
                    }
                });
            } else {
                fountainNeo.doInTransactionAndBeginBlock(new Callable<Object>() {
                    @Nullable
                    @Override
                    public Object call() throws Exception {
                        final Node boardsNode = fountainNeo.findByURI(boardsUri, false);
                        if (!LiquidPermissionSet.createPermissionSet(boardsNode.getProperty(LSDAttribute.PERMISSIONS)).hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.MODIFY)) {
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

    private void firstInit() throws InterruptedException, UnsupportedEncodingException {
        fountainNeo.setRootPool(fountainNeo.createSystemPool("pool:///"));

        createSystemUser();
        LiquidSessionIdentifier identity = new LiquidSessionIdentifier(FountainNeo.SYSTEM, null);

        poolDAO.createPoolNoTx(identity, FountainNeo.ADMIN_ALIAS_URI, fountainNeo.getRootPool(), "users", (double) 0, (double) 0, null, false);

        fountainNeo.setPeoplePool(poolDAO.createPoolNoTx(identity, FountainNeo.ADMIN_ALIAS_URI, fountainNeo.getRootPool(), "people", (double) 0, (double) 0, null, false));

        poolDAO.createPoolNoTx(identity, FountainNeo.ADMIN_ALIAS_URI, fountainNeo.getRootPool(), "system", (double) 0, (double) 0, null, false);

        Node cazcadePool = poolDAO.createPoolNoTx(identity, FountainNeo.ADMIN_ALIAS_URI, fountainNeo.getRootPool(), "cazcade", (double) 0, (double) 0, null, false);
        double x1 = -210;
        double y1 = -210;

        Node cazcadePublicPool = poolDAO.createPoolNoTx(identity, FountainNeo.ADMIN_ALIAS_URI, cazcadePool, "playground", x1, y1, "Playground", false);
        cazcadePublicPool.setProperty(LSDAttribute.PERMISSIONS, FountainNeo.publicPermissionValue);
        double x = -210;

        poolDAO.createPoolNoTx(identity, FountainNeo.ADMIN_ALIAS_URI, cazcadePool, "welcome", x, (double) 210, "Welcome", false);
        double y = -210;

        poolDAO.createPoolNoTx(identity, FountainNeo.ADMIN_ALIAS_URI, cazcadePool, "help", (double) 210, y, "Help", false);

        poolDAO.createPoolNoTx(identity, FountainNeo.ADMIN_ALIAS_URI, cazcadePool, "latest", (double) 210, (double) 210, "Latest", false);

        poolDAO.createPoolNoTx(identity, FountainNeo.ADMIN_ALIAS_URI, fountainNeo.getRootPool(), "com", (double) 0, (double) 0, null, false);

        poolDAO.createPoolNoTx(identity, FountainNeo.ADMIN_ALIAS_URI, fountainNeo.getRootPool(), "org", (double) 0, (double) 0, null, false);

        poolDAO.createPoolNoTx(identity, FountainNeo.ADMIN_ALIAS_URI, fountainNeo.getRootPool(), "geo", (double) 0, (double) 0, null, false);

        poolDAO.createPoolNoTx(identity, FountainNeo.ADMIN_ALIAS_URI, fountainNeo.getRootPool(), "bluetooth", (double) 0, (double) 0, null, false);

        createHashboUser();
        createAnonUser();
        createAdminUser();
    }

    private void createHashboUser() throws InterruptedException, UnsupportedEncodingException {
        final LSDSimpleEntity hashboUser = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.USER);
        hashboUser.setAttribute(LSDAttribute.PLAIN_PASSWORD, DEFAULT_PASSWORD);
        hashboUser.setAttribute(LSDAttribute.NAME, HASHBO);
        hashboUser.setAttribute(LSDAttribute.FULL_NAME, "Hashbo");
        hashboUser.setAttribute(LSDAttribute.EMAIL_ADDRESS, "info@hashbo.com");

        userDAO.createUser(hashboUser, false);
        poolDAO.createPoolsForUserNoTx(HASHBO);
        poolDAO.createPoolsForAliasNoTx(new LiquidURI("alias:cazcade:hashbo"), "hashbo", "Hashbo", false);
    }

    private void createAdminUser() throws InterruptedException, UnsupportedEncodingException {
        final LSDSimpleEntity hashboUser = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.USER);
        hashboUser.setAttribute(LSDAttribute.PLAIN_PASSWORD, ADMIN);
        hashboUser.setAttribute(LSDAttribute.NAME, ADMIN);
        hashboUser.setAttribute(LSDAttribute.FULL_NAME, "Admin");
        hashboUser.setAttribute(LSDAttribute.EMAIL_ADDRESS, "info@cazcade.com");

        Node user = userDAO.createUser(hashboUser, false);
        poolDAO.createPoolsForUserNoTx(ADMIN);
        poolDAO.createPoolsForAliasNoTx(new LiquidURI("alias:cazcade:admin"), ADMIN, "Admin", false);

    }

    private void createAnonUser() throws InterruptedException, UnsupportedEncodingException {
        final LSDSimpleEntity anonUser = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.USER);
        anonUser.setAttribute(LSDAttribute.PLAIN_PASSWORD, DEFAULT_PASSWORD);
        anonUser.setAttribute(LSDAttribute.EMAIL_ADDRESS, "info@cazcade.com");
        anonUser.setAttribute(LSDAttribute.NAME, ANON);
        anonUser.setAttribute(LSDAttribute.FULL_NAME, "Anonymous");
        userDAO.createUser(anonUser, false);
        poolDAO.createPoolsForUserNoTx(ANON);
        poolDAO.createPoolsForAliasNoTx(new LiquidURI("alias:cazcade:anon"), ANON, "Anonymous", false);
    }


    private void createSystemUser() throws InterruptedException, UnsupportedEncodingException {
        LSDEntity systemUser = LSDSimpleEntity.createEmpty();
        systemUser.setAttribute(LSDAttribute.NAME, FountainNeo.SYSTEM);
        systemUser.setAttribute(LSDAttribute.FULL_NAME, "Administrator");
        systemUser.setAttribute(LSDAttribute.EMAIL_ADDRESS, "info@cazcade.com");
        systemUser.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.USER.getValue());
        userDAO.createUser(systemUser, true);
        poolDAO.createPoolsForUserNoTx(FountainNeo.SYSTEM);
        poolDAO.createPoolsForAliasNoTx(new LiquidURI("alias:cazcade:system"), FountainNeo.SYSTEM, "Administrator", true);

    }

    public void setFountainNeo(FountainNeo fountainNeo) {
        this.fountainNeo = fountainNeo;
    }

    public void setPoolDAO(FountainPoolDAO poolDAO) {
        this.poolDAO = poolDAO;
    }

    public void setUserDAO(FountainUserDAO userDAO) {
        this.userDAO = userDAO;
    }
}
