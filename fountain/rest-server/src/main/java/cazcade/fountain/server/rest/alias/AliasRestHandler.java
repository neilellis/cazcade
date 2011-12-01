package cazcade.fountain.server.rest.alias;

import cazcade.fountain.datastore.api.FountainDataStoreFacade;
import cazcade.fountain.server.rest.AbstractRestHandler;
import cazcade.fountain.server.rest.RestContext;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDEntityFactory;
import cazcade.liquid.api.request.*;

import javax.annotation.Nonnull;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author Neil Ellis
 */

public class AliasRestHandler extends AbstractRestHandler {

    private LSDEntityFactory lsdEntityFactory;

    private FountainDataStoreFacade dataStoreFacade;

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


    @Nonnull
    public LiquidMessage create(final LSDEntity lsdEntity, @Nonnull final Map<String, String[]> parameters) throws URISyntaxException {
        final boolean me = parameters.containsKey("me");
        final boolean orupdate = parameters.containsKey("orupdate");

        final LiquidSessionIdentifier username = RestContext.getContext().getCredentials();
        return dataStoreFacade.process(new CreateAliasRequest(username, lsdEntity, me, orupdate, false));
    }

    @Nonnull
    public LiquidMessage update(final LiquidUUID aliasId, final LSDEntity lsdEntity, final Map<String, String[]> parameters) throws URISyntaxException {

        final LiquidSessionIdentifier username = RestContext.getContext().getCredentials();
        return dataStoreFacade.process(new UpdateAliasRequest(username, aliasId, lsdEntity));
    }

    @Nonnull
    public LiquidMessage claim() throws URISyntaxException {
        return dataStoreFacade.process(new ClaimAliasRequest(RestContext.getContext().getCredentials()));
    }


    @Nonnull
    public LiquidMessage delete(final LiquidUUID userId) throws URISyntaxException {
        return dataStoreFacade.process(new UnlinkAliasRequest(RestContext.getContext().getCredentials(), userId));
    }

    public void setLsdFactory(final LSDEntityFactory lsdEntityFactory) {
        this.lsdEntityFactory = lsdEntityFactory;
    }


    public void setDataStore(final FountainDataStoreFacade dataStoreFacade) {
        this.dataStoreFacade = dataStoreFacade;
    }


}