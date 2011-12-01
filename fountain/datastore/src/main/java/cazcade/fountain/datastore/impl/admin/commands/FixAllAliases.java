package cazcade.fountain.datastore.impl.admin.commands;

import cazcade.common.Logger;
import cazcade.fountain.datastore.impl.FountainEntity;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.FountainRelationship;
import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.admin.AdminCommand;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import org.neo4j.graphdb.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public class FixAllAliases implements AdminCommand {
    @Nonnull
    private static final Logger log = Logger.getLogger(FixAllAliases.class);

    @Override
    public void execute(final String[] args, @Nonnull final FountainNeo fountainNeo) throws InterruptedException {
        final FountainEntity peoplePool = fountainNeo.findByURI(new LiquidURI("pool:///people"));
        final Iterable<FountainRelationship> children = peoplePool.getRelationships(FountainRelationships.CHILD, Direction.OUTGOING);
        for (final FountainRelationship child : children) {
            final FountainEntity personPool = child.getOtherNode(peoplePool);
            log.info("Repairing " + personPool.getAttribute(LSDAttribute.URI));

            final FountainRelationships relationshipType = FountainRelationships.OWNER;
            final FountainRelationship ownerRel = fixRelationship(fountainNeo, personPool, relationshipType);
            final FountainEntity ownerAlias = ownerRel.getOtherNode(personPool);
            final FountainRelationship aliasToUserRel = fixRelationship(fountainNeo, ownerAlias, FountainRelationships.ALIAS);
        }
    }

    @Nullable
    private FountainRelationship fixRelationship(@Nonnull final FountainNeo fountainNeo, @Nonnull final FountainEntity startFountainEntity, final FountainRelationships relationshipType) throws InterruptedException {
        final Iterable<FountainRelationship> currentRels = startFountainEntity.getRelationships(relationshipType, Direction.OUTGOING);

        String otherNodeURI = null;

        //remove stale relationships
        for (final FountainRelationship ownerRel : currentRels) {
            otherNodeURI = ownerRel.getOtherNode(startFountainEntity).getAttribute(LSDAttribute.URI);
            removeIfStale(startFountainEntity, ownerRel);
        }
        FountainRelationship rel = startFountainEntity.getSingleRelationship(relationshipType, Direction.OUTGOING);
        if (rel == null && otherNodeURI != null) {
            rel = startFountainEntity.createRelationshipTo(fountainNeo.findByURI(new LiquidURI(otherNodeURI), true), relationshipType);
            log.info("Created new relationship " + startFountainEntity.getAttribute(LSDAttribute.URI) + " -> " + relationshipType + " -> " + otherNodeURI);
        } else if (rel == null) {
            throw new RuntimeException("Could not fix " + startFountainEntity.getAttribute(LSDAttribute.URI) + " no alias found for" + relationshipType);
        }
        return rel;
    }

    private boolean removeIfStale(@Nonnull final FountainEntity fountainEntity, @Nonnull final FountainRelationship rel) {
        final FountainEntity otherFountainEntity = rel.getOtherNode(fountainEntity);
        if (otherFountainEntity.hasRelationship(FountainRelationships.VERSION_PARENT, Direction.INCOMING)) {
            rel.delete();
            log.info("Removed stale relationship to " + otherFountainEntity.getAttribute(LSDAttribute.URI));
            return true;
        } else {
            return false;
        }
    }

}
