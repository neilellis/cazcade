/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.liquid.api.ChildSortOrder;
import cazcade.liquid.api.RequestDetailLevel;
import cazcade.liquid.api.handler.UpdatePoolRequestHandler;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.UpdatePoolRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class UpdatePoolHandler extends AbstractUpdateHandler<UpdatePoolRequest> implements UpdatePoolRequestHandler {
    @Nonnull
    public UpdatePoolRequest handle(@Nonnull final UpdatePoolRequest message) throws Exception {
        final Transaction transaction = neo.beginTx();
        try {
            final TransferEntity entity;
            final PersistedEntity persistedEntity;

            final RequestDetailLevel detail = message.detail();
            final Integer end = null;
            final int start = 0;
            final ChildSortOrder order = null;
            final boolean contents = true;


            TransferEntity request = message.request();
            persistedEntity = neo.findForWrite(request.uri());

            final Runnable onRenameAction = new Runnable() {
                @Override
                public void run() {
                    try {
                        poolDAO.recalculatePoolURIs(persistedEntity);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            };

            entity = poolDAO.updatePool(message.session(), persistedEntity, detail, message.internal(), false, end, start, order, contents, request, onRenameAction);

            transaction.success();
            return LiquidResponseHelper.forServerSuccess(message, entity);
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}