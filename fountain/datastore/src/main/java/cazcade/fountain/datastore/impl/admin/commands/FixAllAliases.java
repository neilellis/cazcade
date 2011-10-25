package cazcade.fountain.datastore.impl.admin.commands;

import cazcade.common.Logger;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.admin.AdminCommand;
import cazcade.liquid.api.LiquidURI;
import org.neo4j.graphdb.*;

/**
 * @author neilellis@cazcade.com
 */
public class FixAllAliases implements AdminCommand {
    private final static Logger log = Logger.getLogger(FixAllAliases.class);

    @Override
    public void execute(String[] args, FountainNeo fountainNeo) throws InterruptedException {
        Node peoplePool = fountainNeo.findByURI(new LiquidURI("pool:///people"));
        Iterable<Relationship> children = peoplePool.getRelationships(FountainRelationships.CHILD, Direction.OUTGOING);
        for (Relationship child : children) {
            Node personPool = child.getOtherNode(peoplePool);
            log.info("Repairing " + personPool.getProperty(FountainNeo.URI));

            FountainRelationships relationshipType = FountainRelationships.OWNER;
            Relationship ownerRel = fixRelationship(fountainNeo, personPool, relationshipType);
            Node ownerAlias = ownerRel.getOtherNode(personPool);
            Relationship aliasToUserRel = fixRelationship(fountainNeo, ownerAlias, FountainRelationships.ALIAS);
        }
    }

    private Relationship fixRelationship(FountainNeo fountainNeo, Node startNode, FountainRelationships relationshipType) throws InterruptedException {
        Iterable<Relationship> currentRels = startNode.getRelationships(relationshipType, Direction.OUTGOING);

        String otherNodeURI = null;

        //remove stale relationships
        for (Relationship ownerRel : currentRels) {
            otherNodeURI = (String) ownerRel.getOtherNode(startNode).getProperty(FountainNeo.URI);
            removeIfStale(startNode, ownerRel);
        }
        Relationship rel = startNode.getSingleRelationship(relationshipType, Direction.OUTGOING);
        if (rel == null && otherNodeURI != null) {
            rel = startNode.createRelationshipTo(fountainNeo.findByURI(new LiquidURI(otherNodeURI), true), relationshipType);
            log.info("Created new relationship " + startNode.getProperty(FountainNeo.URI) + " -> "+relationshipType+" -> " + otherNodeURI);
        } else if (rel == null) {
            throw new RuntimeException("Could not fix "+ startNode.getProperty(FountainNeo.URI) + " no alias found for" + relationshipType);
        }
        return rel;
    }

    private boolean removeIfStale(Node node, Relationship rel) {
        Node otherNode = rel.getOtherNode(node);
        if (otherNode.hasRelationship(FountainRelationships.VERSION_PARENT, Direction.INCOMING)) {
            rel.delete();
            log.info("Removed stale relationship to " + otherNode.getProperty(FountainNeo.URI));
            return true;
        } else {
            return false;
        }
    }

}
