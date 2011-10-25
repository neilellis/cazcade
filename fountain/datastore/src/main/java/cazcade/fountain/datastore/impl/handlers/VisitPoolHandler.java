package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.ChildSortOrder;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.handler.VisitPoolRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.VisitPoolRequest;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * @author neilelliz@cazcade.com
 */
public class VisitPoolHandler extends AbstractDataStoreHandler<VisitPoolRequest> implements VisitPoolRequestHandler {

    public VisitPoolRequest handle(final VisitPoolRequest request) throws Exception {
        Node node;
        final Transaction transaction = fountainNeo.beginTx();
        try {
            LSDEntity entity = null;

            if (request.getUri() != null) {
                node = fountainNeo.findByURI(request.getUri());
            } else {
                node = fountainNeo.findByUUID(request.getTarget());
            }

            if (node == null && request.isOrCreate() && !request.getSessionIdentifier().isAnon()) {
                Node parentNode = fountainNeo.findByURI(request.getUri().getParentURI());
                LiquidURI owner = defaultAndCheckOwner(request, request.getAlias());

                node = poolDAO.createPoolNoTx(request.getSessionIdentifier(), owner, parentNode, request.getType(), request.getUri().getLastPathElement(), 0.0, 0.0, "New Board", request.isListed());
                if (request.getPermission() != null) {
                    node = fountainNeo.changeNodePermissionNoTx(node, request.getSessionIdentifier(), request.getPermission());
                    fountainNeo.assertLatestVersion(node);
                }
            }

            if (node == null) {
                return LiquidResponseHelper.forResourceNotFound("Could not find pool " + request.getUri(), request);
            } else {
                fountainNeo.assertLatestVersion(node);
                poolDAO.visitNodeNoTx(node, request.getSessionIdentifier());
                fountainNeo.assertLatestVersion(node);
                entity = poolDAO.getPoolAndContentsNoTx(node, request.getDetail(), true, ChildSortOrder.AGE, request.isInternal(), request.getSessionIdentifier(), null, null, request.isHistorical());
                final LSDEntity visitor = userDAO.getAliasFromNode(fountainNeo.findByURI(request.getAlias()), request.isInternal(), request.getDetail());
                entity.addSubEntity(LSDAttribute.VISITOR, visitor);
                transaction.success();
            }
            if (entity != null) {
                return LiquidResponseHelper.forServerSuccess(request, entity);
            } else {
                return LiquidResponseHelper.forEmptyResultResponse(request);
            }
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }

    }
}