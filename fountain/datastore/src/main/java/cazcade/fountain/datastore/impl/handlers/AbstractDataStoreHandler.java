package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.api.AuthorizationException;
import cazcade.fountain.datastore.impl.*;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidMessageHandler;
import cazcade.liquid.api.LiquidRequest;
import cazcade.liquid.api.LiquidURI;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.springframework.beans.factory.annotation.Autowired;

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

    public void setFountainNeo(FountainNeo fountainNeo) {
        this.fountainNeo = fountainNeo;
    }

    protected LiquidURI defaultAndCheckOwner(LiquidRequest request, LiquidURI owner) throws InterruptedException {
        if (request.getSessionIdentifier() == null) {
            throw new IllegalArgumentException("Could not check ownership as request has no identity associated with it.");
        }
        if (owner == null) {
            owner = request.getSessionIdentifier().getAlias();
        } else {
            Node ownerAlias = fountainNeo.findByURI(owner);
            if (ownerAlias == null) {
                throw new AuthorizationException("Could not locate owner %s", owner);
            }
            final Relationship ownerRelationship = ownerAlias.getSingleRelationship(FountainRelationships.ALIAS, Direction.OUTGOING);
            if (ownerRelationship == null) {
                throw new AuthorizationException("Could not locate owner relationship for alias %s", owner);
            }
            final Node ownerNode = ownerRelationship.getOtherNode(ownerAlias);
            if (ownerNode == null) {
                throw new AuthorizationException("Could not locate owner node for alias %s", owner);
            }
            if (!ownerNode.hasProperty(FountainNeo.URI)) {
                throw new AuthorizationException("Could not locate owner URI for alias %s", owner);
            }
            final String ownerURL = (String) ownerNode.getProperty(FountainNeo.URI);
            if (!ownerURL.equals(request.getSessionIdentifier().getUserURL().asString())) {
                throw new AuthorizationException("Attempted to create a pool object when you are not the owner of the alias %s", owner);
            }

        }
        return owner;
    }



}
