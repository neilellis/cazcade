package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.Node;
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
    public LinkPoolObjectRequest handle(@Nonnull LinkPoolObjectRequest request) throws InterruptedException {
        Transaction transaction = fountainNeo.beginTx();
        try {
            LiquidUUID from = request.getFrom();
            LiquidUUID to = request.getTo();
            LiquidUUID target = request.getTarget();
            Node result;
            Node targetPool = fountainNeo.findByUUID(target);

            if (request.isUnlink()) {

                result = poolDAO.unlinkPool(targetPool);

            } else {

                LiquidURI alias = request.getAlias();
                Node newOwner = fountainNeo.findByURI(alias);
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