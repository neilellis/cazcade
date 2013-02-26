/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.liquid.api.handler.AddCommentRequestHandler;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.AddCommentRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class AddCommentHandler extends AbstractUpdateHandler<AddCommentRequest> implements AddCommentRequestHandler {
    @Nonnull @Override
    public AddCommentRequest handle(@Nonnull final AddCommentRequest request) throws Exception {
        final Transaction transaction = neo.beginTx();
        try {
            final PersistedEntity commentTargetPersistedEntity = request.hasTarget()
                                                                 ? neo.find(request.getTarget())
                                                                 : neo.find(request.uri());
            final TransferEntity response = poolDAO.convertNodeToEntityWithRelatedEntitiesNoTX(request.session(), poolDAO.addCommentNoTX(commentTargetPersistedEntity, request
                    .request(), request.alias()), null, request.detail(), request.internal(), false);

            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, response);
        } catch (Exception e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}