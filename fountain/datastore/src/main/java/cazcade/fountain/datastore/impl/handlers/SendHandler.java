/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.handler.SendRequestHandler;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.SendRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class SendHandler extends AbstractDataStoreHandler<SendRequest> implements SendRequestHandler {
    @Nonnull
    public SendRequest handle(@Nonnull final SendRequest request) throws Exception {
        final FountainNeo neo = this.neo;
        final Transaction transaction = neo.beginTx();
        try {
            final PersistedEntity poolPersistedEntity = this.neo.find(request.getInboxURI());
            if (poolPersistedEntity == null) {
                throw new EntityNotFoundException("No such inbox pool " + request.getInboxURI());
            }
            final LiquidURI owner = request.getRecipientAlias();
            //Note we run this as the recipient -- just like in email, the receiver owns the message.
            final TransferEntity entity;
            final SessionIdentifier recipientSessionId = new SessionIdentifier(request.getRecipient(), null);
            if (request.hasRequestEntity()) {
                entity = poolDAO.createPoolObjectTx(poolPersistedEntity, recipientSessionId, owner, request.session()
                                                                                                           .aliasURI(), request.request(), request
                        .detail(), request.internal(), false);
            } else {
                entity = poolDAO.linkPoolObjectTx(recipientSessionId, request.getRecipientAlias(), request.uri(), request.getInboxURI(), request
                        .detail(), request.internal());
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