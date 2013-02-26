/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */
package cazcade.boardcast.servlet.twitter.auth.signin;

import cazcade.common.Logger;
import cazcade.fountain.security.SecurityProvider;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidMessageState;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.SimpleEntity;
import cazcade.liquid.api.request.CreateAliasRequest;
import cazcade.liquid.impl.xstream.LiquidXStreamFactory;
import cazcade.vortex.comms.datastore.server.LoginUtil;

import javax.annotation.Nonnull;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.Principal;

public class LinkServlet extends AbstractTwitterServlet {
    @Nonnull
    private static final Logger log              = Logger.getLogger(LinkServlet.class);
    private static final long   serialVersionUID = 1657390011452788111L;
    private SecurityProvider securityProvider;

    @Override
    public void init(@Nonnull final ServletConfig config) throws ServletException {
        super.init(config);
        securityProvider = new SecurityProvider(dataStore);

    }


    protected void doPost(@Nonnull final HttpServletRequest request, @Nonnull final HttpServletResponse response) throws ServletException, IOException {
        try {
            final HttpSession session = request.getSession();
            final SimpleEntity twitterAlias = (SimpleEntity) session.getAttribute(TWITTER_ALIAS_KEY);

            //from the register.jsp
            final String username = request.getParameter(USERNAME_PARAM);
            final String password = request.getParameter(PASSWORD_PARAM);
            final Principal principal = securityProvider.doAuthentication(username, password);
            if (principal == null) {
                response.sendRedirect(request.getContextPath()
                                      + "/_twitter/link.jsp?username="
                                      + URLEncoder.encode(username, "utf8")
                                      + "&message=Login+failed.");
            } else {
                final LiquidMessage createAliasResponse = dataStore.process(new CreateAliasRequest(new SessionIdentifier(username), twitterAlias, true, true, true));
                if (createAliasResponse.getState() == LiquidMessageState.SUCCESS) {
                    final SessionIdentifier sessionIdentifier = LoginUtil.login(clientSessionManager, dataStore, createAliasResponse
                            .response()
                            .uri(), session, pubSub);
                    session.setAttribute(SESSION_KEY, sessionIdentifier);
                    response.sendRedirect(request.getContextPath() + "/_twitter/login.jsp");
                } else {
                    log.error(LiquidXStreamFactory.getXstream().toXML(createAliasResponse));
                    response.sendRedirect(request.getContextPath() + "/_twitter/fail.jsp");
                }
            }
            session.removeAttribute("requestToken");
        } catch (Exception e) {
            log.error(e);
            throw new ServletException(e);
        }
    }
}
