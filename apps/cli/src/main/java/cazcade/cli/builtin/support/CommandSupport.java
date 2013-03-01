/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.cli.builtin.support;

import cazcade.cli.ShellSession;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public class CommandSupport {
    @Nonnull
    public static LURI resolvePoolOrObject(@Nonnull final ShellSession shellSession, @Nonnull final String pool) {
        final LURI poolURI;

        if (".".equals(pool)) {
            poolURI = shellSession.getCurrentPool().uri();
        } else if ("..".equals(pool)) {
            poolURI = shellSession.getCurrentPool().uri().parent();
        } else if (pool.startsWith("pool://")) {
            poolURI = new LURI(pool);
        } else if (pool.startsWith("/")) {
            poolURI = new LURI("pool://" + pool);
        } else {

            poolURI = new LURI(shellSession.getCurrentPool().uri(), pool);
        }
        return poolURI;
    }

    @Nullable
    public static LiquidMessage retrieveObject(final String[] args, @Nonnull final ShellSession shellSession) throws Exception {
        LiquidMessage response = null;
        if ("user".equals(args[0])) {
            final LURI user = resolveUser(shellSession, args[1]);
            response = shellSession.getDataStore().process(new RetrieveUserRequest(shellSession.getIdentity(), user, false));
        } else if ("session".equals(args[0])) {
            final LiquidUUID sessionId;
            final String session = args[1];
            sessionId = resolveSession(shellSession, session);

            response = shellSession.getDataStore().process(new RetrieveSessionRequest(shellSession.getIdentity(), sessionId));
        } else if ("alias".equals(args[0])) {
            final LURI aliasURI;
            final String alias = args[1];
            aliasURI = resolveAlias(shellSession, alias);
            response = shellSession.getDataStore().process(new RetrieveUserRequest(shellSession.getIdentity(), aliasURI, true));
        } else if ("object".equals(args[0])) {
            response = shellSession.getDataStore()
                                   .process(new RetrievePoolObjectRequest(shellSession.getIdentity(), resolvePoolOrObject(shellSession, args[1]), false));
        } else if ("pool".equals(args[0])) {
            response = shellSession.getDataStore()
                                   .process(new RetrievePoolRequest(shellSession.getIdentity(), resolvePoolOrObject(shellSession, args[1]), false, false));
        } else {
            System.err.println("Unrecognized resource type " + args[0]);
        }
        return response;
    }

    public static LURI resolveAlias(@Nonnull final ShellSession shellSession, @Nonnull final String alias) {
        final LURI aliasURI;
        if ("self".equals(alias)) {
            aliasURI = shellSession.getIdentity().aliasURI();
        } else {
            aliasURI = new LURI(LiquidURIScheme.alias, alias);
        }
        return aliasURI;
    }

    public static LiquidUUID resolveSession(@Nonnull final ShellSession shellSession, @Nonnull final String session) {
        final LiquidUUID sessionId;
        if ("self".equals(session)) {
            sessionId = shellSession.getIdentity().session();
        } else {
            sessionId = new LiquidUUID(session);
        }
        return sessionId;
    }

    public static LURI resolveUser(@Nonnull final ShellSession shellSession, @Nonnull final String arg) {
        final LURI user;
        if ("self".equals(arg)) {
            user = shellSession.getIdentity().userURL();
        } else {
            user = new LURI(LiquidURIScheme.user, arg);
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
    public static String alterObject(@Nonnull final ShellSession shellSession, @Nonnull final LURI objectURI, @Nonnull final AlterEntityCallback callback) throws Exception {
        final LURI poolURI = objectURI.withoutFragment();
        final LiquidMessage response1 = shellSession.getDataStore()
                                                    .process(new RetrievePoolObjectRequest(shellSession.getIdentity(), objectURI, false));
        final TransferEntity entity1 = response1.response();
        if (response1.state() != MessageState.SUCCESS) {
            System.err.println(entity1.$(Dictionary.DESCRIPTION));
            return null;
        }

        final TransferEntity newEntity = callback.alter(entity1);

        final LiquidMessage response2 = shellSession.getDataStore()
                                                    .process(new UpdatePoolObjectRequest(shellSession.getIdentity(), objectURI, newEntity));
        final Entity entity2 = response2.response();
        if (response2.state() != MessageState.SUCCESS) {
            System.err.println(entity2.$(Dictionary.DESCRIPTION));
            return null;
        }
        return objectURI.toString();
    }

    @Nullable
    public static String alterPool(@Nonnull final ShellSession shellSession, @Nonnull final LURI poolURI, @Nonnull final AlterEntityCallback callback) throws Exception {
        final LiquidMessage response = shellSession.getDataStore()
                                                   .process(new RetrievePoolRequest(shellSession.getIdentity(), poolURI, false, false));
        final TransferEntity entity = response.response();
        if (response.state() != MessageState.SUCCESS) {
            System.err.println(entity.$(Dictionary.DESCRIPTION));
            return null;
        }

        final TransferEntity newEntity = callback.alter(entity);

        final LiquidMessage response2 = shellSession.getDataStore()
                                                    .process(new UpdatePoolRequest(shellSession.getIdentity(), poolURI, newEntity));
        final Entity entity2 = response2.response();
        if (response2.state() != MessageState.SUCCESS) {
            System.err.println(entity2.$(Dictionary.DESCRIPTION));
            return null;
        }
        return poolURI.toString();
    }

    @Nullable
    public static String alterAlias(@Nonnull final ShellSession shellSession, @Nonnull final LURI aliasURI, @Nonnull final AlterEntityCallback callback) throws Exception {
        final LiquidMessage response = shellSession.getDataStore()
                                                   .process(new RetrieveAliasRequest(shellSession.getIdentity(), aliasURI));
        final TransferEntity entity = response.response();
        if (response.state() != MessageState.SUCCESS) {
            System.err.println(entity.$(Dictionary.DESCRIPTION));
            return null;
        }

        final TransferEntity newEntity = callback.alter(entity);

        final LiquidMessage response2 = shellSession.getDataStore()
                                                    .process(new UpdateAliasRequest(shellSession.getIdentity(), aliasURI, newEntity));
        final Entity entity2 = response2.response();
        if (response2.state() != MessageState.SUCCESS) {
            System.err.println(entity2.$(Dictionary.DESCRIPTION));
            return null;
        }
        return aliasURI.toString();
    }


    @Nullable
    public static String alterUser(@Nonnull final ShellSession shellSession, @Nonnull final LURI userURI, @Nonnull final AlterEntityCallback callback) throws Exception {
        final LiquidMessage response = shellSession.getDataStore()
                                                   .process(new RetrieveUserRequest(shellSession.getIdentity(), userURI, false));
        final TransferEntity entity = response.response();
        if (response.state() != MessageState.SUCCESS) {
            System.err.println(entity.$(Dictionary.DESCRIPTION));
            return null;
        }

        final TransferEntity newEntity = callback.alter(entity);

        final LiquidMessage response2 = shellSession.getDataStore()
                                                    .process(new UpdateUserRequest(shellSession.getIdentity(), entity.id(), newEntity));
        final Entity entity2 = response2.response();
        if (response2.state() != MessageState.SUCCESS) {
            System.err.println(entity2.$(Dictionary.DESCRIPTION));
            return null;
        }
        return userURI.toString();
    }

    public interface AlterEntityCallback {

        TransferEntity alter(TransferEntity entity);
    }
}
