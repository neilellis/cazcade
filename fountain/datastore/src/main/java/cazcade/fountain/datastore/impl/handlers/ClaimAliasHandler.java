package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.handler.ClaimAliasRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.request.ClaimAliasRequest;
import cazcade.liquid.impl.UUIDFactory;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author neilelliz@cazcade.com
 */
public class ClaimAliasHandler extends AbstractDataStoreHandler<ClaimAliasRequest> implements ClaimAliasRequestHandler {

    public ClaimAliasRequest handle(ClaimAliasRequest request) throws Exception {
        LSDSimpleEntity entity = LSDSimpleEntity.createEmpty();
        entity.timestamp();
        entity.setID(UUIDFactory.randomUUID());
        entity.setType(LSDDictionaryTypes.ALIAS_LIST);
        List<LSDEntity> children = new ArrayList<LSDEntity>();

        final Transaction transaction = fountainNeo.beginTx();
        try {

            Node userNode = fountainNeo.findByURI(request.getSessionIdentifier().getUserURL());
            if (userNode.hasRelationship(FountainRelationships.CLAIMED, Direction.OUTGOING)) {
                Iterable<Relationship> claims = userNode.getRelationships(FountainRelationships.CLAIMED, Direction.OUTGOING);
                for (Relationship claim : claims) {
                    Node claimedNode = claim.getOtherNode(userNode);
                    Iterable<Relationship> aliases = userNode.getRelationships(FountainRelationships.ALIAS, Direction.INCOMING);
                    //clean up any multiple alias mess!
                    for (Relationship alias : aliases) {
                        if (alias.getOtherNode(userNode).equals(claimedNode)) {
                            alias.delete();
                        }
                    }
                    claimedNode.createRelationshipTo(userNode, FountainRelationships.ALIAS);
                    LSDEntity child = fountainNeo.convertNodeToLSD(claimedNode, request.getDetail(), request.isInternal());
                    children.add(child);
                    //todo: auto add feeds
                    if (child.attributeIs(LSDAttribute.NETWORK, "twitter")) {
                        addTwitterFeed(request.getSessionIdentifier(), request, child);
                    }
                    claim.delete();
                }
            } else {
                transaction.success();
                return LiquidResponseHelper.forEmptyResultResponse(request);
            }
            entity.addSubEntities(LSDAttribute.CHILD, children);

            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, entity);
        } catch (Exception e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }

    private void addTwitterFeed(LiquidSessionIdentifier identity, ClaimAliasRequest request, LSDEntity child) throws Exception {
        LSDSimpleEntity entity = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.TWITTER_FEED, UUIDFactory.randomUUID());
        String name = child.getAttribute(LSDAttribute.NAME);
        entity.setAttribute(LSDAttribute.EURI, String.format("timeline://%s@twitter/", name));
        entity.setAttribute(LSDAttribute.SOURCE, String.format("http://twitter.com/%s", name));
        entity.setAttribute(LSDAttribute.DESCRIPTION, String.format("%s's Twitter Feed", child.getAttribute(LSDAttribute.FULL_NAME)));
        entity.setAttribute(LSDAttribute.NAME, String.format("twitter_%s_%d", name, System.currentTimeMillis()));
        Node pool = fountainNeo.findByURI(new LiquidURI("pool:///people/" + request.getSessionIdentifier().getName() + "/stream"));
        LSDEntity feed = poolDAO.createPoolObjectTx(pool, identity, request.getSessionIdentifier().getAlias(), child.getURI(), entity, request.getDetail(), request.isInternal(), false);
    }
}