/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.server.rest.session;

import cazcade.fountain.datastore.api.FountainDataStoreFacade;
import cazcade.fountain.server.rest.AbstractRestHandler;
import cazcade.fountain.server.rest.RestContext;
import cazcade.liquid.api.ClientApplicationIdentifier;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.LSDEntityFactory;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.CreateSessionRequest;
import cazcade.liquid.api.request.DeleteSessionRequest;
import cazcade.liquid.api.request.RetrieveSessionRequest;
import cazcade.liquid.api.request.UpdateSessionRequest;

import javax.annotation.Nonnull;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author Neil Ellis
 */

public class SessionRestHandler extends AbstractRestHandler {
    private LSDEntityFactory lsdEntityFactory;

    private FountainDataStoreFacade dataStoreFacade;

    @Nonnull
    public LiquidMessage create(@Nonnull final Map<String, String[]> parameters) throws URISyntaxException {
        checkForSingleValueParams(parameters, "client", "key", "hostinfo");
        final String name = parameters.get("client")[0];
        final String key = parameters.get("key")[0];
        final String hostinfo = parameters.get("hostinfo")[0];
        return dataStoreFacade.process(new CreateSessionRequest(RestContext.getContext()
                                                                           .getCredentials()
                                                                           .getAlias(), new ClientApplicationIdentifier(name, key, hostinfo)));
    }

    @Nonnull
    public LiquidMessage delete(final LiquidUUID sessionId) throws URISyntaxException {
        return dataStoreFacade.process(new DeleteSessionRequest(RestContext.getContext().getCredentials(), sessionId));
    }

    @Nonnull
    public LiquidMessage get(final LiquidUUID userId) throws URISyntaxException {
        return dataStoreFacade.process(new RetrieveSessionRequest(RestContext.getContext().getCredentials(), userId));
    }

    public FountainDataStoreFacade getDataStore() {
        return dataStoreFacade;
    }

    public LSDEntityFactory getLsdFactory() {
        return lsdEntityFactory;
    }

    public void setDataStore(final FountainDataStoreFacade dataStoreFacade) {
        this.dataStoreFacade = dataStoreFacade;
    }

    public void setLsdFactory(final LSDEntityFactory lsdEntityFactory) {
        this.lsdEntityFactory = lsdEntityFactory;
    }

    @Nonnull
    public LiquidMessage update(final LiquidUUID sessionId, final LSDTransferEntity lsdEntity) throws URISyntaxException {
        final LiquidSessionIdentifier username = RestContext.getContext().getCredentials();
        return dataStoreFacade.process(new UpdateSessionRequest(username, sessionId, lsdEntity, false));
    }
}