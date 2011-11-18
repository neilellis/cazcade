package cazcade.cli.builtin.support;

import cazcade.cli.ShellSession;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.*;

/**
 * @author neilellis@cazcade.com
 */
public class CommandSupport {
    public static LiquidURI resolvePoolOrObject(ShellSession shellSession, String pool) {
        LiquidURI poolURI;

        if (pool.equals(".")) {
            poolURI = shellSession.getCurrentPool().getURI();
        } else if (pool.equals("..")) {
            poolURI = shellSession.getCurrentPool().getURI().getParentURI();
        } else if (pool.startsWith("pool://")) {
            poolURI = new LiquidURI(pool);
        } else if (pool.startsWith("/")) {
            poolURI = new LiquidURI("pool://" + pool);
        } else {

            poolURI = new LiquidURI(shellSession.getCurrentPool().getURI(), pool);
        }
        return poolURI;
    }

    public static LiquidMessage retrieveObject(String[] args, ShellSession shellSession) throws Exception {
        LiquidMessage response;
        if (args[0].equals("user")) {
            LiquidURI user = resolveUser(shellSession, args[1]);
            response = shellSession.getDataStore().process(new RetrieveUserRequest(shellSession.getIdentity(), user, false));
        } else if (args[0].equals("session")) {
            final LiquidUUID sessionId;
            final String session = args[1];
            sessionId = resolveSession(shellSession, session);

            response = shellSession.getDataStore().process(new RetrieveSessionRequest(shellSession.getIdentity(), sessionId));
        } else if (args[0].equals("alias")) {
            LiquidURI aliasURI;
            final String alias = args[1];
            aliasURI = resolveAlias(shellSession, alias);
            response = shellSession.getDataStore().process(new RetrieveUserRequest(shellSession.getIdentity(), aliasURI, true));
        } else if (args[0].equals("object")) {
            response = shellSession.getDataStore().process(new RetrievePoolObjectRequest(shellSession.getIdentity(), resolvePoolOrObject(shellSession, args[1]), false));
        } else if (args[0].equals("pool")) {
            response = shellSession.getDataStore().process(new RetrievePoolRequest(shellSession.getIdentity(), resolvePoolOrObject(shellSession, args[1]), false, false));
        } else {
            System.err.println("Unrecognized resource type " + args[0]);
            response = null;
        }
        return response;
    }

    public static LiquidURI resolveAlias(ShellSession shellSession, String alias) {
        LiquidURI aliasURI;
        if (alias.equals("self")) {
            aliasURI = shellSession.getIdentity().getAliasURL();
        } else {
            aliasURI = new LiquidURI(LiquidURIScheme.alias, alias);
        }
        return aliasURI;
    }

    public static LiquidUUID resolveSession(ShellSession shellSession, String session) {
        LiquidUUID sessionId;
        if (session.equals("self")) {
            sessionId = shellSession.getIdentity().getSession();
        } else {
            sessionId = new LiquidUUID(session);
        }
        return sessionId;
    }

    public static LiquidURI resolveUser(ShellSession shellSession, String arg) {
        LiquidURI user;
        if (arg.equals("self")) {
            user = shellSession.getIdentity().getUserURL();
        } else {
            user = new LiquidURI(LiquidURIScheme.user, arg);
        }
        return user;
    }

    public static boolean checkEntityOnStack(ShellSession shellSession) {
        if (!shellSession.hasEntityOnStack()) {
            System.err.println("This command must be executed within a 'with' block.");
            return false;
        }
        return true;
    }


    public static String alterObject(ShellSession shellSession, LiquidURI objectURI, AlterEntityCallback callback) throws Exception {
        LiquidURI poolURI = objectURI.getWithoutFragment();
        LiquidMessage response1 = shellSession.getDataStore().process(new RetrievePoolObjectRequest(shellSession.getIdentity(), objectURI, false));
        final LSDEntity entity1 = response1.getResponse();
        if (response1.getState() != LiquidMessageState.SUCCESS) {
            System.err.println(entity1.getAttribute(LSDAttribute.DESCRIPTION));
            return null;
        }

        LSDEntity newEntity = callback.alter(entity1);

        LiquidMessage response2 = shellSession.getDataStore().process(new UpdatePoolObjectRequest(shellSession.getIdentity(), objectURI, newEntity));
        final LSDEntity entity2 = response2.getResponse();
        if (response2.getState() != LiquidMessageState.SUCCESS) {
            System.err.println(entity2.getAttribute(LSDAttribute.DESCRIPTION));
            return null;
        }
        return objectURI.toString();
    }

    public static String alterPool(ShellSession shellSession, LiquidURI poolURI, AlterEntityCallback callback) throws Exception {
        LiquidMessage response = shellSession.getDataStore().process(new RetrievePoolRequest(shellSession.getIdentity(), poolURI, false, false));
        final LSDEntity entity = response.getResponse();
        if (response.getState() != LiquidMessageState.SUCCESS) {
            System.err.println(entity.getAttribute(LSDAttribute.DESCRIPTION));
            return null;
        }

        LSDEntity newEntity = callback.alter(entity);

        LiquidMessage response2 = shellSession.getDataStore().process(new UpdatePoolRequest(shellSession.getIdentity(), poolURI, newEntity));
        final LSDEntity entity2 = response2.getResponse();
        if (response2.getState() != LiquidMessageState.SUCCESS) {
            System.err.println(entity2.getAttribute(LSDAttribute.DESCRIPTION));
            return null;
        }
        return poolURI.toString();
    }

    public static String alterAlias(ShellSession shellSession, LiquidURI aliasURI, AlterEntityCallback callback) throws Exception {
        LiquidMessage response = shellSession.getDataStore().process(new RetrieveAliasRequest(shellSession.getIdentity(), aliasURI));
        final LSDEntity entity = response.getResponse();
        if (response.getState() != LiquidMessageState.SUCCESS) {
            System.err.println(entity.getAttribute(LSDAttribute.DESCRIPTION));
            return null;
        }

        LSDEntity newEntity = callback.alter(entity);

        LiquidMessage response2 = shellSession.getDataStore().process(new UpdateAliasRequest(shellSession.getIdentity(), aliasURI, newEntity));
        final LSDEntity entity2 = response2.getResponse();
        if (response2.getState() != LiquidMessageState.SUCCESS) {
            System.err.println(entity2.getAttribute(LSDAttribute.DESCRIPTION));
            return null;
        }
        return aliasURI.toString();
    }


    public static String alterUser(ShellSession shellSession, LiquidURI userURI, AlterEntityCallback callback) throws Exception {
        LiquidMessage response = shellSession.getDataStore().process(new RetrieveUserRequest(shellSession.getIdentity(), userURI, false));
        final LSDEntity entity = response.getResponse();
        if (response.getState() != LiquidMessageState.SUCCESS) {
            System.err.println(entity.getAttribute(LSDAttribute.DESCRIPTION));
            return null;
        }

        LSDEntity newEntity = callback.alter(entity);

        LiquidMessage response2 = shellSession.getDataStore().process(new UpdateUserRequest(shellSession.getIdentity(), entity.getID(), newEntity));
        final LSDEntity entity2 = response2.getResponse();
        if (response2.getState() != LiquidMessageState.SUCCESS) {
            System.err.println(entity2.getAttribute(LSDAttribute.DESCRIPTION));
            return null;
        }
        return userURI.toString();
    }

    public static interface AlterEntityCallback {

        LSDEntity alter(LSDEntity entity);
    }
}
