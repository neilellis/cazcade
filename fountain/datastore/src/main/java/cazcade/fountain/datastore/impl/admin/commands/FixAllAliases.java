package cazcade.fountain.datastore.impl.admin.commands;

import cazcade.common.Logger;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.FountainRelationship;
import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.LSDPersistedEntity;
import cazcade.fountain.datastore.impl.admin.AdminCommand;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
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
        final LSDPersistedEntity peoplePool = fountainNeo.findByURI(new LiquidURI("pool:///people"));
        assert peoplePool != null;
        final Iterable<FountainRelationship> children = peoplePool.getRelationships(FountainRelationships.CHILD, Direction.OUTGOING
                                                                                   );
        for (final FountainRelationship child : children) {
            final LSDPersistedEntity personPool = child.getOtherNode(peoplePool);
            log.info("Repairing " + personPool.getAttribute(LSDAttribute.URI));

            final FountainRelationships relationshipType = FountainRelationships.OWNER;
            final FountainRelationship ownerRel = fixRelationship(fountainNeo, personPool, relationshipType);
            final LSDPersistedEntity ownerAlias = ownerRel.getOtherNode(personPool);
            final FountainRelationship aliasToUserRel = fixRelationship(fountainNeo, ownerAlias, FountainRelationships.ALIAS);
        }
    }

    @Nonnull
    private FountainRelationship fixRelationship(@Nonnull final FountainNeo fountainNeo,
                                                 @Nonnull final LSDPersistedEntity startPersistedEntity,
                                                 final FountainRelationships relationshipType) throws InterruptedException {
        final Iterable<FountainRelationship> currentRels = startPersistedEntity.getRelationships(relationshipType,
                                                                                                 Direction.OUTGOING
                                                                                                );

        String otherNodeURI = null;

        //remove stale relationships
        for (final FountainRelationship ownerRel : currentRels) {
            otherNodeURI = ownerRel.getOtherNode(startPersistedEntity).getAttribute(LSDAttribute.URI);
            removeIfStale(startPersistedEntity, ownerRel);
        }
        FountainRelationship rel = startPersistedEntity.getSingleRelationship(relationshipType, Direction.OUTGOING);
        if (rel == null && otherNodeURI != null) {
            final LSDPersistedEntity otherNodeEntity = fountainNeo.findByURI(new LiquidURI(otherNodeURI), true);
            assert otherNodeEntity != null;
            rel = startPersistedEntity.createRelationshipTo(otherNodeEntity, relationshipType);
            log.info("Created new relationship " +
                     startPersistedEntity.getAttribute(LSDAttribute.URI) +
                     " -> " +
                     relationshipType +
                     " -> " +
                     otherNodeURI
                    );
        }
        else if (rel == null) {
            throw new RuntimeException("Could not fix " +
                                       startPersistedEntity.getAttribute(LSDAttribute.URI) +
                                       " no alias found for" +
                                       relationshipType
            );
        }
        return rel;
    }

    private boolean removeIfStale(@Nonnull final LSDPersistedEntity persistedEntity, @Nonnull final FountainRelationship rel) {
        final LSDPersistedEntity otherPersistedEntity = rel.getOtherNode(persistedEntity);
        if (otherPersistedEntity.hasRelationship(FountainRelationships.VERSION_PARENT, Direction.INCOMING)) {
            rel.delete();
            log.info("Removed stale relationship to " + otherPersistedEntity.getAttribute(LSDAttribute.URI));
            return true;
        }
        else {
            return false;
        }
    }
}
