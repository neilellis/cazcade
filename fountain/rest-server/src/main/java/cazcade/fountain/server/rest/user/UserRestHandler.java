package cazcade.fountain.server.rest.user;

import cazcade.fountain.datastore.api.AuthorizationService;
import cazcade.fountain.datastore.api.FountainDataStoreFacade;
import cazcade.fountain.server.rest.AbstractRestHandler;
import cazcade.fountain.server.rest.RestContext;
import cazcade.fountain.server.rest.RestHandlerException;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDEntityFactory;
import cazcade.liquid.api.request.*;

import javax.annotation.Nonnull;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author Neil Ellis
 */

public class UserRestHandler extends AbstractRestHandler {

    private LSDEntityFactory lsdEntityFactory;

    private FountainDataStoreFacade dataStoreFacade;

    private AuthorizationService authorizationService;

    @Nonnull
    public LiquidMessage get(@Nonnull Map<String, String[]> parameters) throws Exception {
        checkForSingleValueParams(parameters, "name");
        final String username = parameters.get("name")[0];
        LiquidMessage message = dataStoreFacade.process(new RetrieveUserRequest(RestContext.getContext().getCredentials(), new LiquidURI(LiquidURIScheme.user, username), false));

        return authorizationService.postAuthorize(RestContext.getContext().getCredentials(), (AbstractRetrievalRequest) message, LiquidPermission.VIEW);
    }

    @Nonnull
    public LiquidMessage get(LiquidUUID userId) throws URISyntaxException {
        return dataStoreFacade.process(new RetrieveUserRequest(RestContext.getContext().getCredentials(), userId));
    }

    @Nonnull
    public LiquidMessage create(LSDEntity lsdEntity, Map<String, String[]> parameters) throws URISyntaxException {
        LiquidSessionIdentifier identity = RestContext.getContext().getCredentials();
        return dataStoreFacade.process(new CreateUserRequest(identity, lsdEntity));
    }

//    public LiquidMessage follow(Map<String, String[]> parameters) throws URISyntaxException {
//        checkForSingleValueParams(parameters, "name");
//        final String name = parameters.get("name")[0];
//        LiquidSessionIdentifier identity = RestContext.getContext().getCredentials();
//        return dataStoreFacade.process(new FollowRequest(identity, name, true));
//    }
//
//    public LiquidMessage following(Map<String, String[]> parameters) throws URISyntaxException {
//        LiquidSessionIdentifier identity = RestContext.getContext().getCredentials();
//        return dataStoreFacade.process(new RetrieveFollowingRequest(identity,  true));
//    }
//
//    public LiquidMessage followers(Map<String, String[]> parameters) throws URISyntaxException {
//        LiquidSessionIdentifier identity = RestContext.getContext().getCredentials();
//        return dataStoreFacade.process(new RetrieveFollowersRequest(identity,  true));
//    }

    @Nonnull
    public LiquidMessage update(LiquidUUID userId, @Nonnull LSDEntity lsdEntity, Map<String, String[]> parameters) throws URISyntaxException {

        LiquidSessionIdentifier username = RestContext.getContext().getCredentials();
        if ((username != null) && !lsdEntity.getAttribute(LSDAttribute.NAME).equalsIgnoreCase(username.getName())) {
            throw new RestHandlerException("You tried to update a different username than yourself. Naughty! make sure the "
                    + LSDAttribute.NAME.getKeyName() + " property is equal to you (in this case " + username + ")");
        }
        return dataStoreFacade.process(new UpdateUserRequest(username, userId, lsdEntity));
    }

    @Nonnull
    public LiquidMessage password(@Nonnull Map<String, String[]> parameters) throws URISyntaxException {
        checkForSingleValueParams(parameters, "password");
        final String password = parameters.get("password")[0];
        return dataStoreFacade.process(new ChangePasswordRequest(RestContext.getContext().getCredentials(), password));
    }

    @Nonnull
    public LiquidMessage delete(LiquidUUID userId) throws URISyntaxException {
        return dataStoreFacade.process(new DeleteUserRequest(RestContext.getContext().getCredentials(), userId));
    }

    public void setLsdFactory(LSDEntityFactory lsdEntityFactory) {
        this.lsdEntityFactory = lsdEntityFactory;
    }

    public LSDEntityFactory getLsdFactory() {
        return lsdEntityFactory;
    }

    public void setDataStore(FountainDataStoreFacade dataStoreFacade) {
        this.dataStoreFacade = dataStoreFacade;
    }

    public FountainDataStoreFacade getDataStore() {
        return dataStoreFacade;
    }


    public void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }
}