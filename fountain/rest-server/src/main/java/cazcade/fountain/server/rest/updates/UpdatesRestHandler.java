package cazcade.fountain.server.rest.updates;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.AuthorizationService;
import cazcade.fountain.datastore.api.FountainDataStoreFacade;
import cazcade.fountain.server.rest.AbstractRestHandler;
import cazcade.fountain.server.rest.RestContext;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.request.RetrieveUpdatesRequest;

import javax.annotation.Nonnull;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author Neil Ellis
 */

public class UpdatesRestHandler extends AbstractRestHandler {


    @Nonnull
    private final static Logger log = Logger.getLogger(UpdatesRestHandler.class);

    private FountainDataStoreFacade dataStoreFacade;
    private AuthorizationService authorizationService;

    public UpdatesRestHandler() {
    }

    @Nonnull
    public LiquidMessage get(@Nonnull Map<String, String[]> parameters) throws URISyntaxException {
        LiquidSessionIdentifier username = RestContext.getContext().getCredentials();
        checkForSingleValueParams(parameters, "since");
        final String since = parameters.get("since")[0];

        return dataStoreFacade.process(new RetrieveUpdatesRequest(username, Long.parseLong(since)));
    }

    public void setDataStore(FountainDataStoreFacade dataStore) {
        this.dataStoreFacade = dataStore;
    }

    public void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }
}