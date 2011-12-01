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

    public FountainNeo getFountainNeo() {
        return fountainNeo;
    }

    public void setFountainNeo(final FountainNeo fountainNeo) {
        this.fountainNeo = fountainNeo;
    }

    @Nullable
    protected LiquidURI defaultAndCheckOwner(@Nonnull final LiquidRequest request, @Nullable LiquidURI owner) throws InterruptedException {
        if (request.getSessionIdentifier() == null) {
            throw new IllegalArgumentException("Could not check ownership as request has no identity associated with it.");
        }
        if (owner == null) {
            owner = request.getSessionIdentifier().getAlias();
        } else {
            final FountainEntity ownerAlias = fountainNeo.findByURI(owner);
            if (ownerAlias == null) {
                throw new AuthorizationException("Could not locate owner %s", owner);
            }
            final FountainRelationship ownerRelationship = ownerAlias.getSingleRelationship(FountainRelationships.ALIAS, Direction.OUTGOING);
            if (ownerRelationship == null) {
                throw new AuthorizationException("Could not locate owner relationship for alias %s", owner);
            }
            final FountainEntity ownerFountainEntity = ownerRelationship.getOtherNode(ownerAlias);
            if (ownerFountainEntity == null) {
                throw new AuthorizationException("Could not locate owner node for alias %s", owner);
            }
            if (!ownerFountainEntity.hasAttribute(LSDAttribute.URI)) {
                throw new AuthorizationException("Could not locate owner URI for alias %s", owner);
            }
            final String ownerURL = ownerFountainEntity.getAttribute(LSDAttribute.URI);
            if (!ownerURL.equals(request.getSessionIdentifier().getUserURL().asString())) {
                throw new AuthorizationException("Attempted to create a pool object when you are not the owner of the alias %s", owner);
            }

        }
        return owner;
    }


}
