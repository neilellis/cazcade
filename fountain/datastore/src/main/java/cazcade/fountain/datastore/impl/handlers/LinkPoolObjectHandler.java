package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.handler.LinkPoolObjectRequestHandler;
import cazcade.liquid.api.request.LinkPoolObjectRequest;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;


/**
 * @author neilelliz@cazcade.com
 */
public class LinkPoolObjectHandler extends AbstractDataStoreHandler<LinkPoolObjectRequest> implements LinkPoolObjectRequestHandler {
    public LinkPoolObjectRequest handle(LinkPoolObjectRequest request) throws InterruptedException {
        Transaction transaction = fountainNeo.beginTx();
        try {
            LiquidUUID from = request.getFrom();
            LiquidUUID to = request.getTo();
            LiquidUUID target = request.getTarget();
            Node result;
            Node targetNode = fountainNeo.findByUUID(target);
            String uri = targetNode.getProperty("uri").toString();

            LiquidURI alias = request.getAlias();
            Node newOwner = fountainNeo.findByURI(alias);


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
            return LiquidResponseHelper.forServerSuccess(request, fountainNeo.convertNodeToLSD(result, request.getDetail(), request.isInternal()));
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}