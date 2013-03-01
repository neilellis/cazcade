/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.api.AuthorizationException;
import cazcade.fountain.datastore.impl.*;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidMessageHandler;
import cazcade.liquid.api.LiquidRequest;
import cazcade.liquid.api.LURI;
import cazcade.liquid.api.lsd.Dictionary;
import org.neo4j.graphdb.Direction;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilelliz@cazcade.com
 */
public abstract class AbstractDataStoreHandler<T extends LiquidMessage> implements LiquidMessageHandler<T> {
    @Autowired
    protected FountainNeo neo;

    @Autowired
    protected FountainPoolDAO poolDAO;

    @Autowired
    protected FountainUserDAO userDAO;

    @Autowired
    protected FountainSocialDAO socialDAO;

    @Nonnull
    protected LURI defaultAndCheckOwner(@Nonnull final LiquidRequest request, @Nullable LURI owner) throws InterruptedException {
        LURI owner1 = owner;
        if (owner1 == null) {
            owner1 = request.session().alias();
        } else {
            final PersistedEntity ownerAlias = neo.find(owner1);
            if (ownerAlias == null) {
                throw new AuthorizationException("Could not locate owner %s", owner1);
            }
            final FountainRelationship ownerRelationship = ownerAlias.relationship(FountainRelationships.ALIAS, Direction.OUTGOING);
            if (ownerRelationship == null) {
                throw new AuthorizationException("Could not locate owner relationship for alias %s", owner1);
            }
            final PersistedEntity ownerPersistedEntity = ownerRelationship.other(ownerAlias);
            if (!ownerPersistedEntity.has(Dictionary.URI)) {
                throw new AuthorizationException("Could not locate owner URI for alias %s", owner1);
            }
            final String ownerURL = ownerPersistedEntity.$(Dictionary.URI);
            if (!request.session().userURL().asString().equals(ownerURL)) {
                throw new AuthorizationException("Attempted to create a pool object when you are not the owner of the alias %s", owner1);
            }
        }
        return owner1;
    }

    public FountainNeo getNeo() {
        return neo;
    }

    public void setNeo(final FountainNeo neo) {
        this.neo = neo;
    }
}
