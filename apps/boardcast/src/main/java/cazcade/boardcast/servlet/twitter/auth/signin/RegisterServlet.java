/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */
package cazcade.boardcast.servlet.twitter.auth.signin;

import cazcade.common.Logger;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.SimpleEntity;
import cazcade.liquid.api.request.CreateAliasRequest;
import cazcade.liquid.api.request.RetrieveUserRequest;
import cazcade.liquid.api.request.UpdateAliasRequest;
import cazcade.liquid.api.request.util.RequestUtil;
import cazcade.liquid.impl.xstream.LiquidXStreamFactory;
import cazcade.vortex.comms.datastore.server.LoginUtil;
import twitter4j.User;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.UUID;

public class RegisterServlet extends AbstractTwitterServlet {
    @Nonnull
    private static final Logger log              = Logger.getLogger(RegisterServlet.class);
    private static final long   serialVersionUID = 1657390011452788111L;

    protected void doGet(@Nonnull final HttpServletRequest request, @Nonnull final HttpServletResponse response) throws ServletException, IOException {
        try {
            final HttpSession session = request.getSession();
            final User user = (User) session.getAttribute(USER_KEY);
            final SimpleEntity twitterAlias = (SimpleEntity) session.getAttribute(TWITTER_ALIAS_KEY);
            final SimpleEntity cazcadeAlias = (SimpleEntity) session.getAttribute(CAZCADE_ALIAS_KEY);

            //from the register.jsp
            String username = request.getParameter(USERNAME_PARAM);
            if (username == null || username.isEmpty()) {
                username = user.getScreenName();
            }
            final String email = request.getParameter(EMAIL_PARAM);

            final RetrieveUserRequest retrieveUserRequest = dataStore.process(new RetrieveUserRequest(new SessionIdentifier("admin", null), new LiquidURI(LiquidURIScheme.user, username), true));

            if (RequestUtil.positiveResponse(retrieveUserRequest)) {
                response.sendRedirect(request.getContextPath()
                                      + "/_twitter/register.jsp?username="
                                      + URLEncoder.encode(username, "utf8")
                                      + "&email="
                                      + URLEncoder.encode(email, "utf8"));
            } else {
                //create user
                final Entity userEntity = LoginUtil.register(session, dataStore, user.getName(), username, UUID.randomUUID()
                                                                                                               .toString(), email, false);
                //                RetrieveAliasRequest retrieveAliasResponse = dataStore.process(new RetrieveAliasRequest(new SessionIdentifier("admin"), new LiquidURI("alias:twitter:" + user.getScreenName())));
                //                if(RequestUtil.positiveResponse(retrieveAliasResponse)) {
                //                    session.$(TWITTER_ALIAS_KEY, retrieveAliasResponse.response());
                //                    LoginUtil.login(clientSessionManager, dataStore, retrieveAliasResponse.response().uri());
                //                    response.sendRedirect(request.getContextPath() + "/_twitter/login.jsp");
                //                } else {
                final LiquidMessage createAliasResponse = dataStore.process(new CreateAliasRequest(new SessionIdentifier(username), twitterAlias, true, true, true));
                if (createAliasResponse.getState() == LiquidMessageState.SUCCESS) {
                    final SessionIdentifier sessionIdentifier = LoginUtil.login(clientSessionManager, dataStore, createAliasResponse
                            .response()
                            .uri(), session, pubSub);
                    dataStore.process(new UpdateAliasRequest(sessionIdentifier, sessionIdentifier.alias(), cazcadeAlias));
                    session.setAttribute(SESSION_KEY, sessionIdentifier);
                    LoginUtil.login(clientSessionManager, dataStore, twitterAlias.uri(), session, pubSub);
                    response.sendRedirect(request.getContextPath() + "/_twitter/login.jsp");
                } else {
                    log.error(LiquidXStreamFactory.getXstream().toXML(createAliasResponse));
                    response.sendRedirect(request.getContextPath() + "/_twitter/fail.jsp");
                }

                //                }
            }

            session.removeAttribute("requestToken");
        } catch (Exception e) {
            log.error(e);
            throw new ServletException(e);
        }
    }
}
