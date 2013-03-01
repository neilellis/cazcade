/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.admin.commands;

import cazcade.common.Logger;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.FountainRelationship;
import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.fountain.datastore.impl.admin.AdminCommand;
import cazcade.liquid.api.LURI;
import cazcade.liquid.api.PermissionSet;
import cazcade.liquid.api.lsd.Dictionary;
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
        final PersistedEntity peoplePool = fountainNeo.find(new LURI("pool:///people"));
        assert peoplePool != null;
        final Iterable<FountainRelationship> children = peoplePool.relationships(FountainRelationships.CHILD, Direction.OUTGOING);
        for (final FountainRelationship child : children) {
            final PersistedEntity personPool = child.other(peoplePool);
            final String personPoolURI = personPool.$(Dictionary.URI);
            resetPermissions(fountainNeo, new LURI(personPoolURI + "/profile"), PermissionSet.getDefaultPermissionsNoDelete());
            resetPermissions(fountainNeo, new LURI(personPoolURI
                                                        + "/private"), PermissionSet.getPrivateNoDeletePermissionSet());
        }
    }

    private void resetPermissions(@Nonnull final FountainNeo fountainNeo, @Nonnull final LURI uri, @Nonnull final PermissionSet permissionSet) throws InterruptedException {
        log.info("Fixing permissions on {0}", uri);
        final PersistedEntity profilePool = fountainNeo.find(uri);
        if (profilePool != null) {
            final String permissionString = permissionSet.toString();
            final Traverser traverse = profilePool.traverse(Traverser.Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL, FountainRelationships.CHILD, Direction.OUTGOING, FountainRelationships.VIEW, Direction.OUTGOING);
            for (final org.neo4j.graphdb.Node node : traverse) {
                node.setProperty(Dictionary.PERMISSIONS.getKeyName(), permissionString);
            }
        }
    }
}
