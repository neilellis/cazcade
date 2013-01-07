package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.api.AuthorizationException;
import cazcade.fountain.datastore.impl.*;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidMessageHandler;
import cazcade.liquid.api.LiquidRequest;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import org.neo4j.graphdb.Direction;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilelliz@cazcade.com
 */
public abstract class AbstractDataStoreHandler<T extends LiquidMessage> implements LiquidMessageHandler<T> {
    @Autowired
    protected FountainNeo fountainNeo;

    @Autowired
    protected FountainPoolDAO poolDAO;

    @Autowired
    protected FountainUserDAO userDAO;

    @Autowired
    protected FountainSocialDAO socialDAO;

    @Nonnull
    protected LiquidURI defaultAndCheckOwner(@Nonnull final LiquidRequest request, @Nullable LiquidURI owner)
            throws InterruptedException {
        LiquidURI owner1 = owner;
        if (owner1 == null) {
            owner1 = request.getSessionIdentifier().getAlias();
        } else {
            final LSDPersistedEntity ownerAlias = fountainNeo.findByURI(owner1);
            if (ownerAlias == null) {
                throw new AuthorizationException("Could not locate owner %s", owner1);
            }
            final FountainRelationship ownerRelationship = ownerAlias.getSingleRelationship(FountainRelationships.ALIAS,
                    Direction.OUTGOING
            );
            if (ownerRelationship == null) {
                throw new AuthorizationException("Could not locate owner relationship for alias %s", owner1);
            }
            final LSDPersistedEntity ownerPersistedEntity = ownerRelationship.getOtherNode(ownerAlias);
            if (!ownerPersistedEntity.hasAttribute(LSDAttribute.URI)) {
                throw new AuthorizationException("Could not locate owner URI for alias %s", owner1);
            }
            final String ownerURL = ownerPersistedEntity.getAttribute(LSDAttribute.URI);
            if (!request.getSessionIdentifier().getUserURL().asString().equals(ownerURL)) {
                throw new AuthorizationException("Attempted to create a pool object when you are not the owner of the alias %s",
                        owner1
                );
            }
        }
        return owner1;
    }

    public FountainNeo getFountainNeo() {
        return fountainNeo;
    }

    public void setFountainNeo(final FountainNeo fountainNeo) {
        this.fountainNeo = fountainNeo;
    }
}
