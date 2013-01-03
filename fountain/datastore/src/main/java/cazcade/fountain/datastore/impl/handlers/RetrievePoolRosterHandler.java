package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LSDPersistedEntity;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.RetrievePoolRosterRequestHandler;
import cazcade.liquid.api.lsd.*;
import cazcade.liquid.api.request.RetrievePoolRosterRequest;
import cazcade.liquid.impl.UUIDFactory;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author neilelliz@cazcade.com
 */
public class RetrievePoolRosterHandler extends AbstractDataStoreHandler<RetrievePoolRosterRequest>
        implements RetrievePoolRosterRequestHandler {
    @Nonnull
    public RetrievePoolRosterRequest handle(@Nonnull final RetrievePoolRosterRequest request) throws InterruptedException {
        LSDPersistedEntity persistedEntity;
        final Transaction transaction = fountainNeo.beginTx();
        try {
            final Collection<LSDBaseEntity> entities;
            final LSDTransferEntity entity = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.ALIAS_LIST);
            entity.timestamp();
            entity.setID(UUIDFactory.randomUUID());

            if (request.getUri() != null) {
                entities = socialDAO.getRosterNoTX(request.getUri(), request.isInternal(), request.getSessionIdentifier(),
                                                   request.getDetail()
                                                  );
            }
            else {
                entities = socialDAO.getRosterNoTX(request.getTarget(), request.isInternal(), request.getSessionIdentifier(),
                                                   request.getDetail()
                                                  );
            }
            transaction.success();
            if (entities == null || entities.isEmpty()) {
                return LiquidResponseHelper.forEmptyResultResponse(request);
            }
            else {
                entity.addSubEntities(LSDAttribute.CHILD, entities);
                return LiquidResponseHelper.forServerSuccess(request, entity);
            }
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}