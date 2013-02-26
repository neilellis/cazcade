/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.server.rest.alias;

import cazcade.fountain.datastore.api.FountainDataStoreFacade;
import cazcade.fountain.server.rest.AbstractRestHandler;
import cazcade.fountain.server.rest.RestContext;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.EntityFactory;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.*;

import javax.annotation.Nonnull;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author Neil Ellis
 */

public class AliasRestHandler extends AbstractRestHandler {
    private EntityFactory entityFactory;

    private FountainDataStoreFacade dataStoreFacade;

    @Nonnull
    public LiquidMessage claim() throws URISyntaxException {
        return dataStoreFacade.process(new ClaimAliasRequest(RestContext.getContext().getCredentials()));
    }

    @Nonnull
    public LiquidMessage create(final TransferEntity lsdEntity, @Nonnull final Map<String, String[]> parameters) throws URISyntaxException {
        final boolean me = parameters.containsKey("me");
        final boolean orupdate = parameters.containsKey("orupdate");

        final SessionIdentifier username = RestContext.getContext().getCredentials();
        return dataStoreFacade.process(new CreateAliasRequest(username, lsdEntity, me, orupdate, false));
    }

    @Nonnull
    public LiquidMessage delete(final LiquidUUID userId) throws URISyntaxException {
        return dataStoreFacade.process(new UnlinkAliasRequest(RestContext.getContext().getCredentials(), userId));
    }

    @Nonnull
    public LiquidMessage get(@Nonnull final Map<String, String[]> parameters) throws URISyntaxException {
        if (parameters.get("uri") != null) {
            final String uri = parameters.get("uri")[0];
            return dataStoreFacade.process(new RetrieveAliasRequest(RestContext.getContext().getCredentials(), new LiquidURI(uri)));
        } else {
            return dataStoreFacade.process(new RetrieveAliasRequest(RestContext.getContext().getCredentials()));
        }
    }

    @Nonnull
    public LiquidMessage get(final LiquidUUID id) throws URISyntaxException {
        return dataStoreFacade.process(new RetrieveAliasRequest(RestContext.getContext().getCredentials(), id));
    }

    public void setDataStore(final FountainDataStoreFacade dataStoreFacade) {
        this.dataStoreFacade = dataStoreFacade;
    }

    public void setLsdFactory(final EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    @Nonnull
    public LiquidMessage update(final LiquidUUID aliasId, final TransferEntity lsdEntity, final Map<String, String[]> parameters) throws URISyntaxException {
        final SessionIdentifier username = RestContext.getContext().getCredentials();
        return dataStoreFacade.process(new UpdateAliasRequest(username, aliasId, lsdEntity));
    }
}