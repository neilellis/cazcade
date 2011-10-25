package cazcade.fountain.datastore.impl.handlers;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.RetrieveAliasRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.request.RetrieveAliasRequest;
import cazcade.liquid.impl.UUIDFactory;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author neilelliz@cazcade.com
 */
public class RetrieveAliasHandler extends AbstractRetrievalHandler<RetrieveAliasRequest> implements RetrieveAliasRequestHandler {
    private final static Logger log = Logger.getLogger(RetrieveAliasHandler.class);

    public RetrieveAliasRequest handle(RetrieveAliasRequest request) throws Exception {
        LSDEntity result;
        if (request.getTarget() != null) {
            throw new UnsupportedOperationException("Retrieval by alias UUID not supported anymore.");
//            log.warn("Retrieving alias using UUID - this behaviour is deprecated, use URIs.");
//            result = fountainNeo.getEntityByUUID(request.getTarget(), request.isInternal(), request.getDetail());
        } else if (request.getUri() != null) {
            log.debug("Retrieving alias using URI {0}", request.getUri());
            result = socialDAO.getAliasAsProfileTx(request.getSessionIdentifier(), request.getUri(), request.isInternal(), request.getDetail());
        } else {
            log.debug("Retrieving aliases for current user {0}", request.getSessionIdentifier().getUserURL());
            //todo: make this part of FountainNeo
            LSDSimpleEntity entity = LSDSimpleEntity.createEmpty();
            entity.timestamp();
            entity.setID(UUIDFactory.randomUUID());
            entity.setType(LSDDictionaryTypes.ALIAS_LIST);
            List<LSDEntity> children = new ArrayList<LSDEntity>();
            final Transaction transaction = fountainNeo.beginTx();
            try {

                Node userNode = fountainNeo.findByURI(request.getSessionIdentifier().getUserURL());
                if (userNode == null) {
                    throw new EntityNotFoundException("Could not locate the entity for the logged in user %s.", request.getSessionIdentifier().getName());
                }

                if (userNode.hasRelationship(FountainRelationships.ALIAS, Direction.INCOMING)) {
                    boolean found= false;
                    Iterable<Relationship> relationships = userNode.getRelationships(FountainRelationships.ALIAS, Direction.INCOMING);
                    for (Relationship relationship : relationships) {
                        Node aliasNode = relationship.getOtherNode(userNode);
                        if (!fountainNeo.isDeleted(aliasNode)) {
                            LSDEntity child = fountainNeo.convertNodeToLSD(aliasNode, request.getDetail(), request.isInternal());
                            children.add(child);
                            found= true;
                        }
                    }
                    if(!found) {
                        transaction.success();
                        return LiquidResponseHelper.forEmptyResultResponse(request);
                    }
                } else {
                    transaction.success();
                    return LiquidResponseHelper.forEmptyResultResponse(request);
                }
                transaction.success();
                entity.addSubEntities(LSDAttribute.CHILD, children);
                result = entity;
                return LiquidResponseHelper.forServerSuccess(request, result);

            } catch (RuntimeException e) {
                transaction.failure();
                throw e;
            } finally {
                transaction.finish();
            }

        }
        return LiquidResponseHelper.forServerSuccess(request, result);
    }
}