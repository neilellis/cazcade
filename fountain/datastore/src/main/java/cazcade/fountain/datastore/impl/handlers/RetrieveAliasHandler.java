/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.fountain.datastore.impl.FountainRelationship;
import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.liquid.api.handler.RetrieveAliasRequestHandler;
import cazcade.liquid.api.lsd.*;
import cazcade.liquid.api.request.RetrieveAliasRequest;
import cazcade.liquid.impl.UUIDFactory;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author neilelliz@cazcade.com
 */
public class RetrieveAliasHandler extends AbstractRetrievalHandler<RetrieveAliasRequest> implements RetrieveAliasRequestHandler {
    @Nonnull
    private static final Logger log = Logger.getLogger(RetrieveAliasHandler.class);

    @Nonnull
    public RetrieveAliasRequest handle(@Nonnull final RetrieveAliasRequest request) throws Exception {
        TransferEntity result;
        if (request.hasTarget()) {
            throw new UnsupportedOperationException("Retrieval by alias UUID not supported anymore.");
            //            log.warn("Retrieving alias using UUID - this behaviour is deprecated, use URIs.");
            //            result = fountainNeo.getEntityByUUID(request.getTarget(), request.internal(), request.detail());
        } else if (request.hasUri()) {
            log.debug("Retrieving alias using URI {0}", request.uri());
            result = socialDAO.getAliasAsProfileTx(request.session(), request.uri(), request.internal(), request.detail());
        } else {
            log.debug("Retrieving aliases for current user {0}", request.session().userURL());
            //todo: make this part of FountainNeo
            final TransferEntity entity = SimpleEntity.create(Types.T_ALIAS_LIST);
            entity.timestamp();
            entity.id(UUIDFactory.randomUUID());
            final List<Entity> children = new ArrayList<Entity>();
            final Transaction transaction = neo.beginTx();
            try {
                final PersistedEntity userPersistedEntity = neo.find(request.session().userURL());
                if (userPersistedEntity == null) {
                    throw new EntityNotFoundException("Could not locate the entity for the logged in user %s.", request.session()
                                                                                                                       .name());
                }

                if (userPersistedEntity.has(FountainRelationships.ALIAS, Direction.INCOMING)) {
                    boolean found = false;
                    final Iterable<FountainRelationship> relationships = userPersistedEntity.relationships(FountainRelationships.ALIAS, Direction.INCOMING);
                    for (final FountainRelationship relationship : relationships) {
                        final PersistedEntity aliasPersistedEntity = relationship.other(userPersistedEntity);
                        if (!aliasPersistedEntity.deleted()) {
                            final Entity child = aliasPersistedEntity.toTransfer(request.detail(), request.internal());
                            children.add(child);
                            found = true;
                        }
                    }
                    if (!found) {
                        transaction.success();
                        return LiquidResponseHelper.forEmptyResultResponse(request);
                    }
                } else {
                    transaction.success();
                    return LiquidResponseHelper.forEmptyResultResponse(request);
                }
                transaction.success();
                entity.children(Dictionary.CHILD_A, children);
                result = entity;
                return LiquidResponseHelper.forServerSuccess(request, result);
            } catch (RuntimeException e) {
                transaction.failure();
                throw e;
            } finally {
                transaction.finish();
            }
        }
        return LiquidResponseHelper.forServerSuccess(request, result);
    }
}