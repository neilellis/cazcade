package cazcade.fountain.datastore.impl.admin.commands;

import cazcade.common.Logger;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.admin.AdminCommand;
import cazcade.liquid.api.LiquidPermissionSet;
import cazcade.liquid.api.LiquidURI;
import org.neo4j.graphdb.*;

/**
 * @author neilellis@cazcade.com
 */
public class FixAllPermissions implements AdminCommand {
    private final static Logger log = Logger.getLogger(FixAllPermissions.class);

    @Override
    public void execute(String[] args, FountainNeo fountainNeo) throws InterruptedException {
        Node peoplePool = fountainNeo.findByURI(new LiquidURI("pool:///people"));
        Iterable<Relationship> children = peoplePool.getRelationships(FountainRelationships.CHILD, Direction.OUTGOING);
        for (Relationship child : children) {
            Node personPool = child.getOtherNode(peoplePool);
            String personPoolURI = personPool.getProperty(FountainNeo.URI).toString();
            resetPermissions(fountainNeo, new LiquidURI(personPoolURI + "/profile"), LiquidPermissionSet.getDefaultPermissionsNoDelete());
            resetPermissions(fountainNeo, new LiquidURI(personPoolURI + "/private"), LiquidPermissionSet.getPrivateNoDeletePermissionSet());
        }
    }

    private void resetPermissions(FountainNeo fountainNeo, LiquidURI uri, LiquidPermissionSet permissionSet) throws InterruptedException {
        log.info("Fixing permissions on {0}", uri);
        Node profilePool = fountainNeo.findByURI(uri);
        if (profilePool != null) {
            String permissionString = permissionSet.toString();
            Traverser traverse = profilePool.traverse(Traverser.Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH,
                    ReturnableEvaluator.ALL, FountainRelationships.CHILD, Direction.OUTGOING, FountainRelationships.VIEW, Direction.OUTGOING);
            for (Node node : traverse) {
                node.setProperty(FountainNeo.PERMISSIONS, permissionString);
            }

        }
    }
}
