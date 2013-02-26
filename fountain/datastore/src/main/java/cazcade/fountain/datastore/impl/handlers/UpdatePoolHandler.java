/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.liquid.api.ChildSortOrder;
import cazcade.liquid.api.RequestDetailLevel;
import cazcade.liquid.api.SessionIdentifier;
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
    public UpdatePoolRequest handle(@Nonnull final UpdatePoolRequest request) throws Exception {
        final Transaction transaction = neo.beginTx();
        try {
            final TransferEntity entity;
            final PersistedEntity persistedEntityImpl;

            if (!request.hasUri()) {
                throw new UnsupportedOperationException("Only URI based updates of pools supported");
            } else {
            }

            final RequestDetailLevel detail = request.detail();
            final boolean internal = request.internal();
            final boolean historical = false;
            final Integer end = null;
            final int start = 0;
            final ChildSortOrder order = null;
            final boolean contents = true;


            persistedEntityImpl = neo.findForWrite(request.uri());
            final SessionIdentifier sessionIdentifier = request.session();
            final TransferEntity requestEntity = request.request();

            final Runnable onRenameAction = new Runnable() {
                @Override
                public void run() {
                    try {
                        poolDAO.recalculatePoolURIs(persistedEntityImpl);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            };

            entity = poolDAO.updatePool(sessionIdentifier, persistedEntityImpl, detail, internal, historical, end, start, order, contents, requestEntity, onRenameAction);

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