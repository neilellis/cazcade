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
package cazcade.hashbo.servlet.twitter.auth.signin;

import cazcade.common.Logger;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.request.CreateAliasRequest;
import cazcade.liquid.api.request.RetrieveUserRequest;
import cazcade.liquid.api.request.UpdateAliasRequest;
import cazcade.liquid.api.request.util.RequestUtil;
import cazcade.liquid.impl.xstream.LiquidXStreamFactory;
import cazcade.vortex.comms.datastore.server.LoginUtil;
import twitter4j.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.UUID;

public class RegisterServlet extends AbstractTwitterServlet {
    private final static Logger log = Logger.getLogger(RegisterServlet.class);
    private static final long serialVersionUID = 1657390011452788111L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute(USER_KEY);
            LSDSimpleEntity twitterAlias = (LSDSimpleEntity) session.getAttribute(TWITTER_ALIAS_KEY);
            LSDSimpleEntity cazcadeAlias = (LSDSimpleEntity) session.getAttribute(CAZCADE_ALIAS_KEY);

            //from the register.jsp
            String username = request.getParameter(USERNAME_PARAM);
            if (username == null || username.isEmpty()) {
                username = user.getScreenName();
            }
            String email = request.getParameter(EMAIL_PARAM);

            RetrieveUserRequest retrieveUserRequest = dataStore.process(new RetrieveUserRequest(new LiquidSessionIdentifier("admin", null), new LiquidURI(LiquidURIScheme.user, username), true));

            if (RequestUtil.positiveResponse(retrieveUserRequest)) {
                response.sendRedirect(request.getContextPath() + "/_twitter/register.jsp?username=" + URLEncoder.encode(username, "utf8") + "&email=" + URLEncoder.encode(email, "utf8"));
            } else {
                //create user
                LSDEntity userEntity = LoginUtil.register(session, dataStore, user.getName(), username, UUID.randomUUID().toString(), email, false);
//                RetrieveAliasRequest retrieveAliasResponse = dataStore.process(new RetrieveAliasRequest(new LiquidSessionIdentifier("admin"), new LiquidURI("alias:twitter:" + user.getScreenName())));
//                if(RequestUtil.positiveResponse(retrieveAliasResponse)) {
//                    session.setAttribute(TWITTER_ALIAS_KEY, retrieveAliasResponse.getResponse());
//                    LoginUtil.login(clientSessionManager, dataStore, retrieveAliasResponse.getResponse().getURI());
//                    response.sendRedirect(request.getContextPath() + "/_twitter/login.jsp");
//                } else {
                LiquidMessage createAliasResponse = dataStore.process(new CreateAliasRequest(new LiquidSessionIdentifier(username), twitterAlias, true, true, true));
                if (createAliasResponse.getState() == LiquidMessageState.SUCCESS) {
                    LiquidSessionIdentifier sessionIdentifier = LoginUtil.login(clientSessionManager, dataStore, createAliasResponse.getResponse().getURI());
                    dataStore.process(new UpdateAliasRequest(sessionIdentifier, sessionIdentifier.getAlias(), cazcadeAlias));
                    session.setAttribute(SESSION_KEY, sessionIdentifier);
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
