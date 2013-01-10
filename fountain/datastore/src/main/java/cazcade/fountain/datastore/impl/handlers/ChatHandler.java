/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LSDPersistedEntity;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.LiquidRequestDetailLevel;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.LiquidURIScheme;
import cazcade.liquid.api.handler.ChatRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.ChatRequest;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author neilelliz@cazcade.com
 */
public class ChatHandler extends AbstractUpdateHandler<ChatRequest> implements ChatRequestHandler {
    @Nonnull @Override
    public ChatRequest handle(@Nonnull final ChatRequest request) throws InterruptedException {
        socialDAO.recordChat(request.getSessionIdentifier(), request.getUri(), request.getRequestEntity());
        final LSDTransferEntity response = request.getRequestEntity().copy();
        //fill in the author details for the recipient
        response.removeSubEntity(LSDAttribute.AUTHOR);
        final String id = UUID.randomUUID().toString();
        response.setId(id);
        response.setURI(new LiquidURI(LiquidURIScheme.chat, id));
        final LSDPersistedEntity authorEntity = fountainNeo.findByURI(request.getSessionIdentifier().getAliasURL(), true);
        assert authorEntity != null;
        response.addSubEntity(LSDAttribute.AUTHOR, userDAO.getAliasFromNode(authorEntity, request.getInternal(), LiquidRequestDetailLevel.PERSON_MINIMAL), true);
        return LiquidResponseHelper.forServerSuccess(request, response);

        //        final Transaction transaction = fountainNeo.beginTx();
        //        try {
        //            final FountainEntityImpl commentTargetNode = request.getTarget() != null ? fountainNeo.findByUUID(request.getTarget()) : fountainNeo.findByURI(request.getUri());
        //            final LSDTransferEntity response = poolDAO.convertNodeToEntityWithRelatedEntitiesNoTX(request.getSessionIdentifier(), poolDAO.addCommentNoTX(commentTargetNode, request.getEntity(), request.getAlias()), null, request.getDetail(), request.isInternal(), false);
        //
        //            //This is an iPad app hack//
        //            // removed by Neil, we'll need to go back and fix a lot in the iPad application
        //            //request.getEntity().addSubEntity(LSDAttribute.AUTHOR, fountainNeo.toLSD(fountainNeo.findByURI(request.getAlias()), request.getDetail(), request.isInternal()));
        //            transaction.success();
        //            return LiquidResponseHelper.forServerSuccess(request, response);
        //        } catch (RuntimeException e) {
        //            transaction.failure();
        //            throw e;
        //        } finally {
        //            transaction.finish();
        //        }
    }
}