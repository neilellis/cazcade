package cazcade.fountain.datastore.impl.admin.commands;

import cazcade.common.Logger;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.FountainRelationship;
import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.LSDPersistedEntity;
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
    private static final Logger log = Logger.getLogger(FixAllPermissions.class);

    @Override
    public void execute(final String[] args, @Nonnull final FountainNeo fountainNeo) throws InterruptedException {
        final LSDPersistedEntity peoplePool = fountainNeo.findByURI(new LiquidURI("pool:///people"));
        final Iterable<FountainRelationship> children = peoplePool.getRelationships(FountainRelationships.CHILD, Direction.OUTGOING);
        for (final FountainRelationship child : children) {
            final LSDPersistedEntity personPool = child.getOtherNode(peoplePool);
            final String personPoolURI = personPool.getAttribute(LSDAttribute.URI);
            resetPermissions(fountainNeo, new LiquidURI(personPoolURI + "/profile"), LiquidPermissionSet.getDefaultPermissionsNoDelete());
            resetPermissions(fountainNeo, new LiquidURI(personPoolURI + "/private"), LiquidPermissionSet.getPrivateNoDeletePermissionSet());
        }
    }

    private void resetPermissions(@Nonnull final FountainNeo fountainNeo, @Nonnull final LiquidURI uri, @Nonnull final LiquidPermissionSet permissionSet) throws InterruptedException {
        log.info("Fixing permissions on {0}", uri);
        final LSDPersistedEntity profilePool = fountainNeo.findByURI(uri);
        if (profilePool != null) {
            final String permissionString = permissionSet.toString();
            final Traverser traverse = profilePool.traverse(Traverser.Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH,
                    ReturnableEvaluator.ALL, FountainRelationships.CHILD, Direction.OUTGOING, FountainRelationships.VIEW, Direction.OUTGOING);
            for (final org.neo4j.graphdb.Node node : traverse) {
                node.setProperty(LSDAttribute.PERMISSIONS.getKeyName(), permissionString);
            }

        }
    }
}
