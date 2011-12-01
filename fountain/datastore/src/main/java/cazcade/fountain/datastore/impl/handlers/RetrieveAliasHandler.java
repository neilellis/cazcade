package cazcade.fountain.datastore.impl.handlers;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.fountain.datastore.impl.FountainRelationship;
import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.LSDPersistedEntity;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
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
        LSDTransferEntity result;
        if (request.getTarget() != null) {
            throw new UnsupportedOperationException("Retrieval by alias UUID not supported anymore.");
//            log.warn("Retrieving alias using UUID - this behaviour is deprecated, use URIs.");
//            result = fountainNeo.getEntityByUUID(request.getTarget(), request.isInternal(), request.getDetail());
        } else if (request.getUri() != null) {
            log.debug("Retrieving alias using URI {0}", request.getUri());
            result = socialDAO.getAliasAsProfileTx(request.getSessionIdentifier(), request.getUri(), request.isInternal(), request.getDetail());
        } else {
            log.debug("Retrieving aliases for current user {0}", request.getSessionIdentifier().getUserURL());
            //todo: make this part of FountainNeo
            final LSDTransferEntity entity = LSDSimpleEntity.createEmpty();
            entity.timestamp();
            entity.setID(UUIDFactory.randomUUID());
            entity.setType(LSDDictionaryTypes.ALIAS_LIST);
            final List<LSDBaseEntity> children = new ArrayList<LSDBaseEntity>();
            final Transaction transaction = fountainNeo.beginTx();
            try {

                final LSDPersistedEntity userPersistedEntity = fountainNeo.findByURI(request.getSessionIdentifier().getUserURL());
                if (userPersistedEntity == null) {
                    throw new EntityNotFoundException("Could not locate the entity for the logged in user %s.", request.getSessionIdentifier().getName());
                }

                if (userPersistedEntity.hasRelationship(FountainRelationships.ALIAS, Direction.INCOMING)) {
                    boolean found = false;
                    final Iterable<FountainRelationship> relationships = userPersistedEntity.getRelationships(FountainRelationships.ALIAS, Direction.INCOMING);
                    for (final FountainRelationship relationship : relationships) {
                        final LSDPersistedEntity aliasPersistedEntity = relationship.getOtherNode(userPersistedEntity);
                        if (!aliasPersistedEntity.isDeleted()) {
                            final LSDBaseEntity child = aliasPersistedEntity.convertNodeToLSD(request.getDetail(), request.isInternal());
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
                entity.addSubEntities(LSDAttribute.CHILD, children);
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