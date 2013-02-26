/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.api.AuthorizationException;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.RequestDetailLevel;
import cazcade.liquid.api.handler.ChangePasswordRequestHandler;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.request.ChangePasswordRequest;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class ChangePasswordHandler extends AbstractDataStoreHandler<ChangePasswordRequest> implements ChangePasswordRequestHandler {
    public static final String HASHED_PASSWORD = Dictionary.HASHED_AND_SALTED_PASSWORD.getKeyName();

    @Nonnull
    public ChangePasswordRequest handle(@Nonnull final ChangePasswordRequest request) throws Exception {
        final Transaction transaction = neo.beginTx();
        try {
            final LiquidURI userURL = request.session().userURL();
            final PersistedEntity persistedEntity = neo.find(userURL);
            if (persistedEntity == null) {
                throw new AuthorizationException("No such user " + userURL);
            }
            if (request.hasChangePasswordSecurityHash()) {
                if (!userDAO.confirmHash(userURL, request.getChangePasswordSecurityHash())) {
                    throw new AuthorizationException("Could not authorize changing of password for %s - hash didn't match.", userURL);
                }
            }
            if (!request.hasPassword()) {
                userDAO.sendPasswordChangeRequest(userURL);
                transaction.success();
                return LiquidResponseHelper.forServerSuccess(request, persistedEntity.toTransfer(RequestDetailLevel.MINIMAL, request
                        .internal()));
            } else {
                final String plainPassword = request.getPassword();
                final StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
                final String encryptedPassword = passwordEncryptor.encryptPassword(plainPassword);
                persistedEntity.$(Dictionary.HASHED_AND_SALTED_PASSWORD, encryptedPassword);
                transaction.success();
                return LiquidResponseHelper.forServerSuccess(request, persistedEntity.toTransfer(request.detail(), request.internal()));
            }
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}