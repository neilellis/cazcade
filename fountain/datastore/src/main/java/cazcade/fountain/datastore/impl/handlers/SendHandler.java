/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.LSDPersistedEntity;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.handler.SendRequestHandler;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.SendRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class SendHandler extends AbstractDataStoreHandler<SendRequest> implements SendRequestHandler {
    @Nonnull
    public SendRequest handle(@Nonnull final SendRequest request) throws Exception {
        final FountainNeo neo = fountainNeo;
        final Transaction transaction = neo.beginTx();
        try {
            final LSDPersistedEntity poolPersistedEntity = fountainNeo.findByURI(request.getInboxURI());
            if (poolPersistedEntity == null) {
                throw new EntityNotFoundException("No such inbox pool " + request.getInboxURI());
            }
            final LiquidURI owner = request.getRecipientAlias();
            //Note we run this as the recipient -- just like in email, the receiver owns the message.
            final LSDTransferEntity entity;
            final LiquidSessionIdentifier recipientSessionId = new LiquidSessionIdentifier(request.getRecipient(), null);
            if (request.hasRequestEntity()) {
                entity = poolDAO.createPoolObjectTx(poolPersistedEntity, recipientSessionId, owner, request.getSessionIdentifier()
                                                                                                           .getAliasURL(), request.getRequestEntity(), request
                        .getDetail(), request.isInternal(), false);
            }
            else {
                entity = poolDAO.linkPoolObjectTx(recipientSessionId, request.getRecipientAlias(), request.getUri(), request.getInboxURI(), request
                        .getDetail(), request.isInternal());
            }
            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, entity);
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}