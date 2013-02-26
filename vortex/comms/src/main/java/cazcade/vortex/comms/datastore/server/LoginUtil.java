/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.comms.datastore.server;

import cazcade.common.CommonConstants;
import cazcade.common.Logger;
import cazcade.fountain.datastore.api.FountainDataStore;
import cazcade.fountain.messaging.FountainPubSub;
import cazcade.fountain.messaging.session.ClientSession;
import cazcade.fountain.messaging.session.ClientSessionManager;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.*;
import cazcade.liquid.api.request.CreateSessionRequest;
import cazcade.liquid.api.request.CreateUserRequest;
import cazcade.liquid.api.request.RetrieveAliasRequest;
import cazcade.liquid.impl.UUIDFactory;
import cazcade.liquid.impl.xstream.LiquidXStreamFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * @author neilellis@cazcade.com
 */
public class LoginUtil {
    @Nonnull
    public static final  String SESSION_KEY       = "sessionId";
    @Nonnull
    public static final  String ALIAS_KEY         = "alias_entity";
    @Nonnull
    public static final  String ALIAS_KEY_FOR_JSP = "alias";
    @Nonnull
    public static final  String APP_KEY           = "123";
    @Nonnull
    private static final Logger log               = Logger.getLogger(LoginUtil.class);
    @Nonnull
    private static final String USERNAME_KEY      = "username";

    @Nonnull
    public static SessionIdentifier login(@Nonnull final ClientSessionManager clientSessionManager, @Nonnull final FountainDataStore dataStore, @Nonnull final LiquidURI alias, @Nonnull final HttpSession session, FountainPubSub pubSub) throws Exception {
        final LiquidMessage response = dataStore.process(new CreateSessionRequest(alias, new ClientApplicationIdentifier("GWT Client", APP_KEY, "UNKNOWN")));
        log.debug(LiquidXStreamFactory.getXstream().toXML(response));

        final Entity responseEntity = response.response();
        if (responseEntity.is(Types.T_SESSION)) {
            final SessionIdentifier serverSession = new SessionIdentifier(alias.sub().sub().asString(), responseEntity.id());
            createClientSession(clientSessionManager, serverSession, true, pubSub);
            if (!serverSession.anon()) {
                placeServerSessionInHttpSession(dataStore, session, serverSession);
            }
            return serverSession;
        } else {
            log.error("{0}", responseEntity.asFreeText());
            throw new RuntimeException("Unexpected result " + responseEntity.type());
        }
    }

    public static void placeServerSessionInHttpSession(@Nonnull final FountainDataStore dataStore, @Nonnull final HttpSession session, @Nonnull final SessionIdentifier serverSession) {
        final TransferEntity aliasEntity;
        try {
            aliasEntity = dataStore.process(new RetrieveAliasRequest(serverSession, serverSession.aliasURI())).response();
        } catch (Exception e) {
            log.error(e);
            return;
        }
        session.setAttribute(SESSION_KEY, serverSession);
        session.setAttribute(USERNAME_KEY, serverSession.name());
        session.setAttribute(ALIAS_KEY, aliasEntity);
        session.setAttribute(ALIAS_KEY_FOR_JSP, aliasEntity.getCamelCaseMap());
    }

    public static ClientSession createClientSession(@Nonnull final ClientSessionManager clientSessionManager, @Nonnull final SessionIdentifier identity, final boolean create, FountainPubSub pubSub) {
        final ClientSession clientSession;
        if (!clientSessionManager.hasSession(identity.session().toString()) && create) {
            //
            //The session manager looks after long lived sessions and expires them.
            clientSession = new ClientSession(new Date(), pubSub.createCollector());
            clientSessionManager.addSession(identity.session().toString(), clientSession);
        } else {
            clientSession = clientSessionManager.getSession(identity.session().toString());
        }
        return clientSession;
    }

    @Nullable
    public static TransferEntity register(@Nonnull final HttpSession session, @Nonnull final FountainDataStore theDataStore, final String fullname, @Nonnull final String username, final String password, final String emailAddress, final boolean restricted) {
        final TransferEntity entity = SimpleEntity.createNewTransferEntity(Types.T_USER, UUIDFactory.randomUUID());
        entity.$(Dictionary.FULL_NAME, fullname)
              .$(Dictionary.NAME, username)
              .$(Dictionary.PLAIN_PASSWORD, password)
              .$(Dictionary.EMAIL_ADDRESS, emailAddress)
              .$(Dictionary.SECURITY_RESTRICTED, restricted)
              .$(Dictionary.IMAGE_URL, CommonConstants.BLANK_PNG_URL);
        try {
            final LiquidMessage response = theDataStore.process(new CreateUserRequest(new SessionIdentifier(username), entity));
            if (response.getState() == LiquidMessageState.SUCCESS) {
                session.setAttribute(CommonConstants.NEW_USER_ATTRIBUTE, response.response());
                session.setAttribute(CommonConstants.NEW_USER_PASSWORD_ATTRIBUTE, password);
                return response.response();
            }
            return null;
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }
}
