/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.server.rest.user;

import cazcade.fountain.datastore.api.AuthorizationService;
import cazcade.fountain.datastore.api.FountainDataStoreFacade;
import cazcade.fountain.server.rest.AbstractRestHandler;
import cazcade.fountain.server.rest.RestContext;
import cazcade.fountain.server.rest.RestHandlerException;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.EntityFactory;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.*;

import javax.annotation.Nonnull;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author Neil Ellis
 */

public class UserRestHandler extends AbstractRestHandler {
    private EntityFactory entityFactory;

    private FountainDataStoreFacade dataStoreFacade;

    private AuthorizationService authorizationService;

    @Nonnull
    public LiquidMessage create(final TransferEntity lsdEntity, final Map<String, String[]> parameters) throws URISyntaxException {
        final SessionIdentifier identity = RestContext.getContext().getCredentials();
        return dataStoreFacade.process(new CreateUserRequest(identity, lsdEntity));
    }

    @Nonnull
    public LiquidMessage delete(final LiquidUUID userId) throws URISyntaxException {
        return dataStoreFacade.process(new DeleteUserRequest(RestContext.getContext().getCredentials(), userId));
    }

    @Nonnull
    public LiquidMessage get(@Nonnull final Map<String, String[]> parameters) throws Exception {
        checkForSingleValueParams(parameters, "name");
        final String username = parameters.get("name")[0];
        final LiquidMessage message = dataStoreFacade.process(new RetrieveUserRequest(RestContext.getContext()
                                                                                                 .getCredentials(), new LiquidURI(LiquidURIScheme.user, username), false));

        return authorizationService.postAuthorize(RestContext.getContext()
                                                             .getCredentials(), (AbstractRetrievalRequest) message, Permission.VIEW_PERM);
    }

    @Nonnull
    public LiquidMessage get(final LiquidUUID userId) throws URISyntaxException {
        return dataStoreFacade.process(new RetrieveUserRequest(RestContext.getContext().getCredentials(), userId));
    }

    public FountainDataStoreFacade getDataStore() {
        return dataStoreFacade;
    }

    public EntityFactory getLsdFactory() {
        return entityFactory;
    }

    @Nonnull
    public LiquidMessage password(@Nonnull final Map<String, String[]> parameters) throws URISyntaxException {
        checkForSingleValueParams(parameters, "password");
        final String password = parameters.get("password")[0];
        return dataStoreFacade.process(new ChangePasswordRequest(RestContext.getContext().getCredentials(), password));
    }

    public void setDataStore(final FountainDataStoreFacade dataStoreFacade) {
        this.dataStoreFacade = dataStoreFacade;
    }

    public void setLsdFactory(final EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    //    public LiquidMessage follow(Map<String, String[]> parameters) throws URISyntaxException {
    //        checkForSingleValueParams(parameters, "name");
    //        final String name = parameters.get("name")[0];
    //        SessionIdentifier identity = RestContext.getContext().getCredentials();
    //        return dataStoreFacade.process(new FollowRequest(identity, name, true));
    //    }
    //
    //    public LiquidMessage following(Map<String, String[]> parameters) throws URISyntaxException {
    //        SessionIdentifier identity = RestContext.getContext().getCredentials();
    //        return dataStoreFacade.process(new RetrieveFollowingRequest(identity,  true));
    //    }
    //
    //    public LiquidMessage followers(Map<String, String[]> parameters) throws URISyntaxException {
    //        SessionIdentifier identity = RestContext.getContext().getCredentials();
    //        return dataStoreFacade.process(new RetrieveFollowersRequest(identity,  true));
    //    }

    @Nonnull
    public LiquidMessage update(final LiquidUUID userId, @Nonnull final TransferEntity lsdEntity, final Map<String, String[]> parameters) throws URISyntaxException {
        final SessionIdentifier username = RestContext.getContext().getCredentials();
        if (username != null && !lsdEntity.$(Dictionary.NAME).equalsIgnoreCase(username.name())) {
            throw new RestHandlerException("You tried to update a different username than yourself. Naughty! make sure the " +
                                           Dictionary.NAME.getKeyName() +
                                           " property is equal to you (in this case " +
                                           username +
                                           ")");
        }
        return dataStoreFacade.process(new UpdateUserRequest(username, userId, lsdEntity));
    }

    public void setAuthorizationService(final AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }
}