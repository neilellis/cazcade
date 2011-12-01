package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.FountainEntity;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.UpdatePoolObjectRequestHandler;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.UpdatePoolObjectRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class UpdatePoolObjectHandler extends AbstractUpdateHandler<UpdatePoolObjectRequest> implements UpdatePoolObjectRequestHandler {
    @Nonnull
    @Override
    public UpdatePoolObjectRequest handle(@Nonnull final UpdatePoolObjectRequest request) throws InterruptedException {
        final FountainEntity fountainEntityImpl;
        final Transaction transaction = fountainNeo.beginTx();
        try {
            final LSDEntity entity;
            FountainEntity pool = null;
            if (request.getUri() != null) {
                fountainEntityImpl = fountainNeo.findByURI(request.getUri());
                pool = fountainNeo.findByURI(request.getUri().getWithoutFragment());
            } else {
                fountainEntityImpl = fountainNeo.findByUUID(request.getTarget());
            }

            entity = poolDAO.updatePoolObjectNoTx(request.getSessionIdentifier(), request.getSessionIdentifier(), request.getRequestEntity(), pool, fountainEntityImpl, request.isInternal(), request.getDetail());
            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, entity);
        } catch (RuntimeException e) {
            transaction.failure();
            return LiquidResponseHelper.forException(e, request);
        } finally {
            transaction.finish();
        }


    }

}