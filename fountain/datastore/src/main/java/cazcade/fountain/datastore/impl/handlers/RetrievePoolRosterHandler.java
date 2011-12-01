package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.FountainEntity;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.RetrievePoolRosterRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.request.RetrievePoolRosterRequest;
import cazcade.liquid.impl.UUIDFactory;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author neilelliz@cazcade.com
 */
public class RetrievePoolRosterHandler extends AbstractDataStoreHandler<RetrievePoolRosterRequest> implements RetrievePoolRosterRequestHandler {

    @Nonnull
    public RetrievePoolRosterRequest handle(@Nonnull final RetrievePoolRosterRequest request) throws InterruptedException {
        FountainEntity fountainEntity;
        final Transaction transaction = fountainNeo.beginTx();
        try {
            final Collection<LSDEntity> entities;
            final LSDSimpleEntity entity = LSDSimpleEntity.createEmpty();
            entity.timestamp();
            entity.setID(UUIDFactory.randomUUID());
            entity.setType(LSDDictionaryTypes.ALIAS_LIST);

            if (request.getUri() != null) {
                entities = socialDAO.getRosterNoTX(request.getUri(), request.isInternal(), request.getSessionIdentifier(), request.getDetail());
            } else {
                entities = socialDAO.getRosterNoTX(request.getTarget(), request.isInternal(), request.getSessionIdentifier(), request.getDetail());
            }
            transaction.success();
            if (entities == null || entities.isEmpty()) {
                return LiquidResponseHelper.forEmptyResultResponse(request);

            } else {
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