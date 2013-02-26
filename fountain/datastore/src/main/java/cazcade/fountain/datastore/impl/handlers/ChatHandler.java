/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.LiquidURIScheme;
import cazcade.liquid.api.RequestDetailLevel;
import cazcade.liquid.api.handler.ChatRequestHandler;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.ChatRequest;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author neilelliz@cazcade.com
 */
public class ChatHandler extends AbstractUpdateHandler<ChatRequest> implements ChatRequestHandler {
    @Nonnull @Override
    public ChatRequest handle(@Nonnull final ChatRequest request) throws Exception {
        socialDAO.recordChat(request.session(), request.uri(), request.request());
        final TransferEntity response = request.request().$();
        //fill in the author details for the recipient
        response.removeChild(Dictionary.AUTHOR_A);
        final String id = UUID.randomUUID().toString();
        response.id(id);
        response.uri(new LiquidURI(LiquidURIScheme.chat, id));
        final PersistedEntity authorEntity = neo.findByURI(request.session().aliasURI(), true);
        assert authorEntity != null;
        response.child(Dictionary.AUTHOR_A, userDAO.getAliasFromNode(authorEntity, request.getInternal(), RequestDetailLevel.PERSON_MINIMAL), true);
        return LiquidResponseHelper.forServerSuccess(request, response);

        //        final Transaction transaction = fountainNeo.beginTx();
        //        try {
        //            final FountainEntity commentTargetNode = request.getTarget() != null ? fountainNeo.find(request.getTarget()) : fountainNeo.find(request.uri());
        //            final TransferEntity response = poolDAO.convertNodeToEntityWithRelatedEntitiesNoTX(request.session(), poolDAO.addCommentNoTX(commentTargetNode, request.getEntity(), request.alias()), null, request.detail(), request.internal(), false);
        //
        //            //This is an iPad app hack//
        //            // removed by Neil, we'll need to go back and fix a lot in the iPad application
        //            //request.getEntity().$child(Attribute.AUTHOR, fountainNeo.toTransfer(fountainNeo.find(request.alias()), request.detail(), request.internal()));
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