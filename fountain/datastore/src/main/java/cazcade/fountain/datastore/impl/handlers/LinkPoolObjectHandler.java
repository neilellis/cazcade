/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.handler.LinkPoolObjectRequestHandler;
import cazcade.liquid.api.request.LinkPoolObjectRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;


/**
 * @author neilelliz@cazcade.com
 */
public class LinkPoolObjectHandler extends AbstractDataStoreHandler<LinkPoolObjectRequest> implements LinkPoolObjectRequestHandler {
    @Nonnull
    public LinkPoolObjectRequest handle(@Nonnull final LinkPoolObjectRequest request) throws Exception {
        final Transaction transaction = neo.beginTx();
        try {
            final LiquidUUID to = request.getTo();
            final LiquidUUID target = request.getTarget();
            final PersistedEntity result;
            final PersistedEntity targetPersistedEntityImpl = neo.find(target);
            final LiquidURI uri = targetPersistedEntityImpl.uri();

            final LiquidURI alias = request.alias();
            final PersistedEntity newOwner = neo.find(alias);


            if (request.isUnlink()) {
                //                if(targetPersistedEntityImpl.) {
                //                    System.err.println("Exists.");
                //                    System.err.println(fountainNeo.toTransfer(fountainNeo.find(new LiquidURI(uri)), true).toString());
                //                    System.err.println("Target.");
                //                    System.err.println(fountainNeo.toTransfer(targetPersistedEntityImpl, true).toString());
                //                    System.exit(-1);
                //                }
                poolDAO.unlinkPoolObject(targetPersistedEntityImpl);
            }

            if (!request.hasFrom()) {
                result = poolDAO.linkPoolObject(request.session(), newOwner, targetPersistedEntityImpl, neo.find(to));
            } else {
                final LiquidUUID from = request.getFrom();

                result = poolDAO.linkPoolObject(request.session(), newOwner, targetPersistedEntityImpl, neo.find(from), neo.find(to));
            }


            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, result.toTransfer(request.detail(), request.internal()));
        } catch (Exception e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}