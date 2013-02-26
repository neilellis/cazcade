/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.RetrieveCommentsRequestHandler;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.SimpleEntity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Types;
import cazcade.liquid.api.request.RetrieveCommentsRequest;
import cazcade.liquid.impl.UUIDFactory;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author neilelliz@cazcade.com
 */
public class RetrieveCommentsHandler extends AbstractRetrievalHandler<RetrieveCommentsRequest> implements RetrieveCommentsRequestHandler {
    @Nonnull
    public RetrieveCommentsRequest handle(@Nonnull final RetrieveCommentsRequest request) throws Exception {
        final Collection<TransferEntity> entities;
        final TransferEntity entity = SimpleEntity.createNewTransferEntity(Types.T_COMMENT_LIST, UUIDFactory.randomUUID());

        entities = poolDAO.getCommentsTx(request.session(), request.uri(), request.getMax(), request.internal(), request.detail());
        if (entities == null) {
            return LiquidResponseHelper.forEmptyResultResponse(request);
        }
        entity.children(Dictionary.CHILD_A, entities);
        return LiquidResponseHelper.forServerSuccess(request, entity);
    }
}