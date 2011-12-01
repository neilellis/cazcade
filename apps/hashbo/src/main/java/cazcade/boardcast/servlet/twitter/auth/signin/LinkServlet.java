/*
Copyright (c) 2007-2009, Yusuke Yamamoto
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the Yusuke Yamamoto nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY Yusuke Yamamoto ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL Yusuke Yamamoto BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package cazcade.boardcast.servlet.twitter.auth.signin;

import cazcade.common.Logger;
import cazcade.fountain.security.SecurityProvider;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidMessageState;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
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
    private final static Logger log = Logger.getLogger(LinkServlet.class);
    private static final long serialVersionUID = 1657390011452788111L;
    private SecurityProvider securityProvider;

    @Override
    public void init(@Nonnull ServletConfig config) throws ServletException {
        super.init(config);
        securityProvider = new SecurityProvider(dataStore);

    }


    protected void doPost(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            LSDSimpleEntity twitterAlias = (LSDSimpleEntity) session.getAttribute(TWITTER_ALIAS_KEY);

            //from the register.jsp
            String username = request.getParameter(USERNAME_PARAM);
            String password = request.getParameter(PASSWORD_PARAM);
            Principal principal = securityProvider.doAuthentication(username, password);
            if (principal == null) {
                response.sendRedirect(request.getContextPath() + "/_twitter/link.jsp?username=" + URLEncoder.encode(username, "utf8") + "&message=Login+failed.");
            } else {
                LiquidMessage createAliasResponse = dataStore.process(new CreateAliasRequest(new LiquidSessionIdentifier(username), twitterAlias, true, true, true));
                if (createAliasResponse.getState() == LiquidMessageState.SUCCESS) {
                    LiquidSessionIdentifier sessionIdentifier = LoginUtil.login(clientSessionManager, dataStore, createAliasResponse.getResponse().getURI(), session);
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
