/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.FountainRelationship;
import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.liquid.api.LURI;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.handler.ClaimAliasRequestHandler;
import cazcade.liquid.api.lsd.*;
import cazcade.liquid.api.request.ClaimAliasRequest;
import cazcade.liquid.impl.UUIDFactory;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author neilelliz@cazcade.com
 */
public class ClaimAliasHandler extends AbstractDataStoreHandler<ClaimAliasRequest> implements ClaimAliasRequestHandler {
    @Nonnull
    public ClaimAliasRequest handle(@Nonnull final ClaimAliasRequest request) throws Exception {
        final TransferEntity entity = SimpleEntity.create(Types.T_ALIAS_LIST);
        entity.timestamp();
        entity.id(UUIDFactory.randomUUID());
        final List<Entity> children = new ArrayList<Entity>();

        final Transaction transaction = neo.beginTx();
        try {
            final PersistedEntity userPersistedEntityImpl = neo.find(request.session().userURL());
            assert userPersistedEntityImpl != null;
            if (userPersistedEntityImpl.has(FountainRelationships.CLAIMED, Direction.OUTGOING)) {
                final Iterable<FountainRelationship> claims = userPersistedEntityImpl.relationships(FountainRelationships.CLAIMED, Direction.OUTGOING);
                for (final FountainRelationship claim : claims) {
                    final PersistedEntity claimedPersistedEntity = claim.other(userPersistedEntityImpl);
                    final Iterable<FountainRelationship> aliases = userPersistedEntityImpl.relationships(FountainRelationships.ALIAS, Direction.INCOMING);
                    //clean up any multiple alias mess!
                    for (final FountainRelationship alias : aliases) {
                        if (alias.other(userPersistedEntityImpl).equals(claimedPersistedEntity)) {
                            alias.delete();
                        }
                    }
                    claimedPersistedEntity.relate(userPersistedEntityImpl, FountainRelationships.ALIAS);
                    final Entity child = claimedPersistedEntity.toTransfer(request.detail(), request.internal());
                    children.add(child);
                    //todo: auto add feeds
                    if (child.attributeIs(Dictionary.NETWORK, "twitter")) {
                        addTwitterFeed(request.session(), request, child);
                    }
                    claim.delete();
                }
            } else {
                transaction.success();
                return LiquidResponseHelper.forEmptyResultResponse(request);
            }
            entity.children(Dictionary.CHILD_A, children);

            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, entity);
        } catch (Exception e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }

    private void addTwitterFeed(final SessionIdentifier identity, @Nonnull final ClaimAliasRequest request, @Nonnull final Entity child) throws Exception {
        final TransferEntity entity = SimpleEntity.createNewTransferEntity(Types.T_TWITTER_FEED, UUIDFactory.randomUUID());
        final String name = child.$(Dictionary.NAME);
        entity.$(Dictionary.EURI, String.format("timeline://%s@twitter/", name));
        entity.$(Dictionary.SOURCE, String.format("http://twitter.com/%s", name));
        entity.$(Dictionary.DESCRIPTION, String.format("%s's Twitter Feed", child.$(Dictionary.FULL_NAME)));
        entity.$(Dictionary.NAME, String.format("twitter_%s_%d", name, System.currentTimeMillis()));
        final PersistedEntity pool = neo.find(new LURI("pool:///people/" + request.session().name() + "/stream"));
        final Entity feed = poolDAO.createPoolObjectTx(pool, identity, request.session()
                                                                              .alias(), child.uri(), entity, request.detail(), request
                .internal(), false);
    }
}