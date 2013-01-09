package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LSDPersistedEntity;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.RetrievePoolRequestHandler;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.RetrievePoolRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class RetrievePoolHandler extends AbstractDataStoreHandler<RetrievePoolRequest> implements RetrievePoolRequestHandler {
    @Nonnull
    public RetrievePoolRequest handle(@Nonnull final RetrievePoolRequest request) throws Exception {
        LSDPersistedEntity persistedEntity;
        final Transaction transaction = fountainNeo.beginTx();
        try {
            final LSDTransferEntity entity;
            if (request.hasUri()) {
                entity = poolDAO.getPoolAndContentsNoTx(request.getUri(), request.getDetail(), request.getOrder(),
                                                        request.isContents(), request.isInternal(), request.getSessionIdentifier(),
                                                        0, request.getMax(), request.isHistorical()
                                                       );
            }
            else {
                throw new UnsupportedOperationException("Only URI retrieval supported now.");
//                entity = poolDAO.getPoolAndContentsNoTx(request.getTarget(), request.getDetail(), request.getOrder(), request.isContents(), request.isInternal(), request.getSessionIdentifier(), 0, request.getMax(), request.isHistorical());
            }
            transaction.success();
            if (entity == null) {
                if (request.isOrCreate()) {
                    final LSDPersistedEntity parentPersistedEntity = fountainNeo.findByURI(request.getUri().getParentURI());

                    final LSDPersistedEntity pool = poolDAO.createPoolNoTx(request.getSessionIdentifier(), request.getAlias(),
                                                                           parentPersistedEntity,
                                                                           request.getUri().getLastPathElement(), 0.0, 0.0,
                                                                           request.getUri().getLastPathElement(), request.isListed()
                                                                          );
                    final LSDTransferEntity newPoolEntity = poolDAO.convertNodeToEntityWithRelatedEntitiesNoTX(
                            request.getSessionIdentifier(), pool, parentPersistedEntity, request.getDetail(), request.isInternal(),
                            false
                                                                                                              );
                    transaction.success();
                    return LiquidResponseHelper.forServerSuccess(request, newPoolEntity);
                }
                else {
                    return LiquidResponseHelper.forEmptyResultResponse(request);
                }
            }
            else {
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