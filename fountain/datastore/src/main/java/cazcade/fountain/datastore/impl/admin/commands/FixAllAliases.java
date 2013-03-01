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
import cazcade.liquid.api.lsd.Dictionary;
import org.neo4j.graphdb.Direction;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class FixAllAliases implements AdminCommand {
    @Nonnull
    private static final Logger log = Logger.getLogger(FixAllAliases.class);

    @Override
    public void execute(final String[] args, @Nonnull final FountainNeo fountainNeo) throws InterruptedException {
        final PersistedEntity peoplePool = fountainNeo.find(new LURI("pool:///people"));
        assert peoplePool != null;
        final Iterable<FountainRelationship> children = peoplePool.relationships(FountainRelationships.CHILD, Direction.OUTGOING);
        for (final FountainRelationship child : children) {
            final PersistedEntity personPool = child.other(peoplePool);
            log.info("Repairing " + personPool.$(Dictionary.URI));

            final FountainRelationships relationshipType = FountainRelationships.OWNER;
            final FountainRelationship ownerRel = fixRelationship(fountainNeo, personPool, relationshipType);
            final PersistedEntity ownerAlias = ownerRel.other(personPool);
            final FountainRelationship aliasToUserRel = fixRelationship(fountainNeo, ownerAlias, FountainRelationships.ALIAS);
        }
    }

    @Nonnull
    private FountainRelationship fixRelationship(@Nonnull final FountainNeo fountainNeo, @Nonnull final PersistedEntity startPersistedEntity, final FountainRelationships relationshipType) throws InterruptedException {
        final Iterable<FountainRelationship> currentRels = startPersistedEntity.relationships(relationshipType, Direction.OUTGOING);

        String otherNodeURI = null;

        //remove stale relationships
        for (final FountainRelationship ownerRel : currentRels) {
            otherNodeURI = ownerRel.other(startPersistedEntity).$(Dictionary.URI);
            removeIfStale(startPersistedEntity, ownerRel);
        }
        FountainRelationship rel = startPersistedEntity.relationship(relationshipType, Direction.OUTGOING);
        if (rel == null && otherNodeURI != null) {
            final PersistedEntity otherNodeEntity = fountainNeo.findByURI(new LURI(otherNodeURI), true);
            assert otherNodeEntity != null;
            rel = startPersistedEntity.relate(otherNodeEntity, relationshipType);
            log.info("Created new relationship " +
                     startPersistedEntity.$(Dictionary.URI) +
                     " -> " +
                     relationshipType +
                     " -> " +
                     otherNodeURI);
        } else if (rel == null) {
            throw new RuntimeException("Could not fix " +
                                       startPersistedEntity.$(Dictionary.URI) +
                                       " no alias found for" +
                                       relationshipType);
        }
        return rel;
    }

    private boolean removeIfStale(@Nonnull final PersistedEntity persistedEntity, @Nonnull final FountainRelationship rel) {
        final PersistedEntity otherPersistedEntity = rel.other(persistedEntity);
        if (otherPersistedEntity.has(FountainRelationships.VERSION_PARENT, Direction.INCOMING)) {
            rel.delete();
            log.info("Removed stale relationship to " + otherPersistedEntity.$(Dictionary.URI));
            return true;
        } else {
            return false;
        }
    }
}
