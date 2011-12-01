package cazcade.cli.builtin.support;

import cazcade.cli.ShellSession;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public class CommandSupport {
    @Nullable
    public static LiquidURI resolvePoolOrObject(@Nonnull final ShellSession shellSession, @Nonnull final String pool) {
        final LiquidURI poolURI;

        if (".".equals(pool)) {
            poolURI = shellSession.getCurrentPool().getURI();
        } else if ("..".equals(pool)) {
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

    @Nullable
    public static LiquidMessage retrieveObject(final String[] args, @Nonnull final ShellSession shellSession) throws Exception {
        LiquidMessage response = null;
        if ("user".equals(args[0])) {
            final LiquidURI user = resolveUser(shellSession, args[1]);
            response = shellSession.getDataStore().process(new RetrieveUserRequest(shellSession.getIdentity(), user, false));
        } else if ("session".equals(args[0])) {
            final LiquidUUID sessionId;
            final String session = args[1];
            sessionId = resolveSession(shellSession, session);

            response = shellSession.getDataStore().process(new RetrieveSessionRequest(shellSession.getIdentity(), sessionId));
        } else if ("alias".equals(args[0])) {
            final LiquidURI aliasURI;
            final String alias = args[1];
            aliasURI = resolveAlias(shellSession, alias);
            response = shellSession.getDataStore().process(new RetrieveUserRequest(shellSession.getIdentity(), aliasURI, true));
        } else if ("object".equals(args[0])) {
            response = shellSession.getDataStore().process(new RetrievePoolObjectRequest(shellSession.getIdentity(), resolvePoolOrObject(shellSession, args[1]), false));
        } else if ("pool".equals(args[0])) {
            response = shellSession.getDataStore().process(new RetrievePoolRequest(shellSession.getIdentity(), resolvePoolOrObject(shellSession, args[1]), false, false));
        } else {
            System.err.println("Unrecognized resource type " + args[0]);
        }
        return response;
    }

    public static LiquidURI resolveAlias(@Nonnull final ShellSession shellSession, @Nonnull final String alias) {
        final LiquidURI aliasURI;
        if ("self".equals(alias)) {
            aliasURI = shellSession.getIdentity().getAliasURL();
        } else {
            aliasURI = new LiquidURI(LiquidURIScheme.alias, alias);
        }
        return aliasURI;
    }

    public static LiquidUUID resolveSession(@Nonnull final ShellSession shellSession, @Nonnull final String session) {
        final LiquidUUID sessionId;
        if ("self".equals(session)) {
            sessionId = shellSession.getIdentity().getSession();
        } else {
            sessionId = new LiquidUUID(session);
        }
        return sessionId;
    }

    public static LiquidURI resolveUser(@Nonnull final ShellSession shellSession, @Nonnull final String arg) {
        final LiquidURI user;
        if ("self".equals(arg)) {
            user = shellSession.getIdentity().getUserURL();
        } else {
            user = new LiquidURI(LiquidURIScheme.user, arg);
        }
        return user;
    }

    public static boolean checkEntityOnStack(@Nonnull final ShellSession shellSession) {
        if (!shellSession.hasEntityOnStack()) {
            System.err.println("This command must be executed within a 'with' block.");
            return false;
        }
        return true;
    }


    @Nullable
    public static String alterObject(@Nonnull final ShellSession shellSession, @Nonnull final LiquidURI objectURI, @Nonnull final AlterEntityCallback callback) throws Exception {
        final LiquidURI poolURI = objectURI.getWithoutFragment();
        final LiquidMessage response1 = shellSession.getDataStore().process(new RetrievePoolObjectRequest(shellSession.getIdentity(), objectURI, false));
        final LSDEntity entity1 = response1.getResponse();
        if (response1.getState() != LiquidMessageState.SUCCESS) {
            System.err.println(entity1.getAttribute(LSDAttribute.DESCRIPTION));
            return null;
        }

        final LSDEntity newEntity = callback.alter(entity1);

        final LiquidMessage response2 = shellSession.getDataStore().process(new UpdatePoolObjectRequest(shellSession.getIdentity(), objectURI, newEntity));
        final LSDEntity entity2 = response2.getResponse();
        if (response2.getState() != LiquidMessageState.SUCCESS) {
            System.err.println(entity2.getAttribute(LSDAttribute.DESCRIPTION));
            return null;
        }
        return objectURI.toString();
    }

    @Nullable
    public static String alterPool(@Nonnull final ShellSession shellSession, @Nonnull final LiquidURI poolURI, @Nonnull final AlterEntityCallback callback) throws Exception {
        final LiquidMessage response = shellSession.getDataStore().process(new RetrievePoolRequest(shellSession.getIdentity(), poolURI, false, false));
        final LSDEntity entity = response.getResponse();
        if (response.getState() != LiquidMessageState.SUCCESS) {
            System.err.println(entity.getAttribute(LSDAttribute.DESCRIPTION));
            return null;
        }

        final LSDEntity newEntity = callback.alter(entity);

        final LiquidMessage response2 = shellSession.getDataStore().process(new UpdatePoolRequest(shellSession.getIdentity(), poolURI, newEntity));
        final LSDEntity entity2 = response2.getResponse();
        if (response2.getState() != LiquidMessageState.SUCCESS) {
            System.err.println(entity2.getAttribute(LSDAttribute.DESCRIPTION));
            return null;
        }
        return poolURI.toString();
    }

    @Nullable
    public static String alterAlias(@Nonnull final ShellSession shellSession, @Nonnull final LiquidURI aliasURI, @Nonnull final AlterEntityCallback callback) throws Exception {
        final LiquidMessage response = shellSession.getDataStore().process(new RetrieveAliasRequest(shellSession.getIdentity(), aliasURI));
        final LSDEntity entity = response.getResponse();
        if (response.getState() != LiquidMessageState.SUCCESS) {
            System.err.println(entity.getAttribute(LSDAttribute.DESCRIPTION));
            return null;
        }

        final LSDEntity newEntity = callback.alter(entity);

        final LiquidMessage response2 = shellSession.getDataStore().process(new UpdateAliasRequest(shellSession.getIdentity(), aliasURI, newEntity));
        final LSDEntity entity2 = response2.getResponse();
        if (response2.getState() != LiquidMessageState.SUCCESS) {
            System.err.println(entity2.getAttribute(LSDAttribute.DESCRIPTION));
            return null;
        }
        return aliasURI.toString();
    }


    @Nullable
    public static String alterUser(@Nonnull final ShellSession shellSession, @Nonnull final LiquidURI userURI, @Nonnull final AlterEntityCallback callback) throws Exception {
        final LiquidMessage response = shellSession.getDataStore().process(new RetrieveUserRequest(shellSession.getIdentity(), userURI, false));
        final LSDEntity entity = response.getResponse();
        if (response.getState() != LiquidMessageState.SUCCESS) {
            System.err.println(entity.getAttribute(LSDAttribute.DESCRIPTION));
            return null;
        }

        final LSDEntity newEntity = callback.alter(entity);

        final LiquidMessage response2 = shellSession.getDataStore().process(new UpdateUserRequest(shellSession.getIdentity(), entity.getUUID(), newEntity));
        final LSDEntity entity2 = response2.getResponse();
        if (response2.getState() != LiquidMessageState.SUCCESS) {
            System.err.println(entity2.getAttribute(LSDAttribute.DESCRIPTION));
            return null;
        }
        return userURI.toString();
    }

    public interface AlterEntityCallback {

        LSDEntity alter(LSDEntity entity);
    }
}
