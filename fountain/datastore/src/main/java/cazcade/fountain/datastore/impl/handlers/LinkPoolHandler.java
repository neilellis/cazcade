/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.liquid.api.LURI;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.handler.LinkPoolObjectRequestHandler;
import cazcade.liquid.api.request.LinkPoolObjectRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;


/**
 * @author neilelliz@cazcade.com
 */
public class LinkPoolHandler extends AbstractDataStoreHandler<LinkPoolObjectRequest> implements LinkPoolObjectRequestHandler {
    @Nonnull
    public LinkPoolObjectRequest handle(@Nonnull final LinkPoolObjectRequest request) throws Exception {
        final Transaction transaction = neo.beginTx();
        try {
            final LiquidUUID to = request.getTo();
            final LiquidUUID target = request.getTarget();
            final PersistedEntity result;
            final PersistedEntity targetPool = neo.find(target);

            if (request.isUnlink()) {
                result = poolDAO.unlinkPool(targetPool);
            } else {
                final LURI alias = request.alias();
                final PersistedEntity newOwner = neo.findOrFail(alias);
                if (!request.hasFrom()) {
                    result = poolDAO.linkPool(newOwner, targetPool, neo.find(to));
                } else {
                    final LiquidUUID from = request.getFrom();
                    result = poolDAO.linkPool(newOwner, targetPool, neo.find(from), neo.find(to));
                }
            }

            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, result.toTransfer(request.detail(), request.internal()));
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}