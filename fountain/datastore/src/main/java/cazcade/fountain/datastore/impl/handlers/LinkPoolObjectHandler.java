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
public class LinkPoolObjectHandler extends AbstractDataStoreHandler<LinkPoolObjectRequest> implements LinkPoolObjectRequestHandler {
    @Nonnull
    public LinkPoolObjectRequest handle(@Nonnull final LinkPoolObjectRequest request) throws InterruptedException {
        final Transaction transaction = fountainNeo.beginTx();
        try {
            final LiquidUUID from = request.getFrom();
            final LiquidUUID to = request.getTo();
            final LiquidUUID target = request.getTarget();
            final Node result;
            final Node targetNode = fountainNeo.findByUUID(target);
            final LiquidURI uri = targetNode.getURI();

            final LiquidURI alias = request.getAlias();
            final Node newOwner = fountainNeo.findByURI(alias);


            if (request.isUnlink()) {
//                if(targetNode.) {
//                    System.err.println("Exists.");
//                    System.err.println(fountainNeo.convertNodeToLSD(fountainNeo.findByURI(new LiquidURI(uri)), true).toString());
//                    System.err.println("Target.");
//                    System.err.println(fountainNeo.convertNodeToLSD(targetNode, true).toString());
//                    System.exit(-1);
//                }
                poolDAO.unlinkPoolObject(targetNode);

            }

            if (from == null) {
                result = poolDAO.linkPoolObject(request.getSessionIdentifier(), newOwner, targetNode, fountainNeo.findByUUID(to));
            } else {
                result = poolDAO.linkPoolObject(request.getSessionIdentifier(), newOwner, targetNode, fountainNeo.findByUUID(from), fountainNeo.findByUUID(to));
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