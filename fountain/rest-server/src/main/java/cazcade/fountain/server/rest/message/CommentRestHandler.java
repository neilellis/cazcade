/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.server.rest.message;

import cazcade.fountain.datastore.api.FountainDataStoreFacade;
import cazcade.fountain.server.rest.AbstractRestHandler;
import cazcade.fountain.server.rest.RestContext;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.SimpleEntity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Types;
import cazcade.liquid.api.request.AddCommentRequest;
import cazcade.liquid.api.request.RetrieveCommentsRequest;
import cazcade.liquid.impl.UUIDFactory;

import javax.annotation.Nonnull;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author neilellis@cazcade.com
 */
public class CommentRestHandler extends AbstractRestHandler {
    private FountainDataStoreFacade dataStore;

    @Nonnull
    public LiquidMessage create(@Nonnull final Map<String, String[]> parameters) throws URISyntaxException {
        final SessionIdentifier username = RestContext.getContext().getCredentials();
        checkForSingleValueParams(parameters, "text", "image", "uri");
        final String text = parameters.get("text")[0];
        final String image = parameters.get("image")[0];
        final String uri = parameters.get("uri")[0];
        final TransferEntity message = SimpleEntity.createNewTransferEntity(Types.T_COMMENT, UUIDFactory.randomUUID());
        message.$(Dictionary.TEXT_EXTENDED, text);
        message.$(Dictionary.IMAGE_URL, image);
        message.$(Dictionary.ICON_URL, image);
        return dataStore.process(new AddCommentRequest(username, new LiquidURI(uri), message));
    }

    @Nonnull
    public LiquidMessage get(@Nonnull final Map<String, String[]> parameters) throws URISyntaxException {
        final SessionIdentifier username = RestContext.getContext().getCredentials();
        checkForSingleValueParams(parameters, "uri");
        final String uri = parameters.get("uri")[0];

        return dataStore.process(new RetrieveCommentsRequest(username, new LiquidURI(uri), 20, false));
    }

    public void setDataStore(final FountainDataStoreFacade dataStore) {
        this.dataStore = dataStore;
    }
}
