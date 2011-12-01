package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.FountainEntity;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.LiquidURI;
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
    public LinkPoolObjectRequest handle(@Nonnull final LinkPoolObjectRequest request) throws InterruptedException {
        final Transaction transaction = fountainNeo.beginTx();
        try {
            final LiquidUUID from = request.getFrom();
            final LiquidUUID to = request.getTo();
            final LiquidUUID target = request.getTarget();
            final FountainEntity result;
            final FountainEntity targetPool = fountainNeo.findByUUID(target);

            if (request.isUnlink()) {

                result = poolDAO.unlinkPool(targetPool);

            } else {

                final LiquidURI alias = request.getAlias();
                final FountainEntity newOwner = fountainNeo.findByURI(alias);
                if (from == null) {
                    result = poolDAO.linkPool(newOwner, targetPool, fountainNeo.findByUUID(to));
                } else {
                    result = poolDAO.linkPool(newOwner, targetPool, fountainNeo.findByUUID(from), fountainNeo.findByUUID(to));
                }
            }

            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, result.convertNodeToLSD(request.getDetail(), request.isInternal()));
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}