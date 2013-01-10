/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LSDPersistedEntity;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.ChildSortOrder;
import cazcade.liquid.api.LiquidRequestDetailLevel;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.handler.UpdatePoolRequestHandler;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.UpdatePoolRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class UpdatePoolHandler extends AbstractUpdateHandler<UpdatePoolRequest> implements UpdatePoolRequestHandler {
    @Nonnull
    public UpdatePoolRequest handle(@Nonnull final UpdatePoolRequest request) throws Exception {
        final Transaction transaction = fountainNeo.beginTx();
        try {
            final LSDTransferEntity entity;
            final LSDPersistedEntity persistedEntityImpl;

            if (!request.hasUri()) {
                throw new UnsupportedOperationException("Only URI based updates of pools supported");
            }
            else {
            }

            final LiquidRequestDetailLevel detail = request.getDetail();
            final boolean internal = request.isInternal();
            final boolean historical = false;
            final Integer end = null;
            final int start = 0;
            final ChildSortOrder order = null;
            final boolean contents = true;


            persistedEntityImpl = fountainNeo.findByURI(request.getUri());
            final LiquidSessionIdentifier sessionIdentifier = request.getSessionIdentifier();
            final LSDTransferEntity requestEntity = request.getRequestEntity();

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