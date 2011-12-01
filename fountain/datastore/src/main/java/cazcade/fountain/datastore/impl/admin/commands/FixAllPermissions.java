package cazcade.fountain.datastore.impl.admin.commands;

import cazcade.common.Logger;
import cazcade.fountain.datastore.Node;
import cazcade.fountain.datastore.Relationship;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.admin.AdminCommand;
import cazcade.liquid.api.LiquidPermissionSet;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Traverser;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class FixAllPermissions implements AdminCommand {
    @Nonnull
    private final static Logger log = Logger.getLogger(FixAllPermissions.class);

    @Override
    public void execute(String[] args, @Nonnull FountainNeo fountainNeo) throws InterruptedException {
        Node peoplePool = fountainNeo.findByURI(new LiquidURI("pool:///people"));
        Iterable<Relationship> children = peoplePool.getRelationships(FountainRelationships.CHILD, Direction.OUTGOING);
        for (Relationship child : children) {
            Node personPool = child.getOtherNode(peoplePool);
            String personPoolURI = personPool.getProperty(LSDAttribute.URI);
            resetPermissions(fountainNeo, new LiquidURI(personPoolURI + "/profile"), LiquidPermissionSet.getDefaultPermissionsNoDelete());
            resetPermissions(fountainNeo, new LiquidURI(personPoolURI + "/private"), LiquidPermissionSet.getPrivateNoDeletePermissionSet());
        }
    }

    private void resetPermissions(@Nonnull FountainNeo fountainNeo, @Nonnull LiquidURI uri, @Nonnull LiquidPermissionSet permissionSet) throws InterruptedException {
        log.info("Fixing permissions on {0}", uri);
        Node profilePool = fountainNeo.findByURI(uri);
        if (profilePool != null) {
            String permissionString = permissionSet.toString();
            Traverser traverse = profilePool.traverse(Traverser.Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH,
                    ReturnableEvaluator.ALL, FountainRelationships.CHILD, Direction.OUTGOING, FountainRelationships.VIEW, Direction.OUTGOING);
            for (org.neo4j.graphdb.Node node : traverse) {
                node.setProperty(LSDAttribute.PERMISSIONS.getKeyName(), permissionString);
            }

        }
    }
}
