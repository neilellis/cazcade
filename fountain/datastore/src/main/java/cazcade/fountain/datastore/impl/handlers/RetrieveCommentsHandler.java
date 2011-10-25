package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.RetrieveCommentsRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.request.RetrieveCommentsRequest;
import cazcade.liquid.impl.UUIDFactory;

import java.util.Collection;

/**
 * @author neilelliz@cazcade.com
 */
public class RetrieveCommentsHandler extends AbstractRetrievalHandler<RetrieveCommentsRequest> implements RetrieveCommentsRequestHandler {

    public RetrieveCommentsRequest handle(RetrieveCommentsRequest request) throws InterruptedException {
        Collection<LSDEntity> entities;
        final LSDSimpleEntity entity = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.COMMENT_LIST, UUIDFactory.randomUUID());

        entities = poolDAO.getCommentsTx(request.getSessionIdentifier(), request.getUri(), request.getMax(), request.isInternal(), request.getDetail());
        if (entities == null) {
            return LiquidResponseHelper.forEmptyResultResponse(request);
        }
        entity.addSubEntities(LSDAttribute.CHILD, entities);
        return LiquidResponseHelper.forServerSuccess(request, entity);
    }
}