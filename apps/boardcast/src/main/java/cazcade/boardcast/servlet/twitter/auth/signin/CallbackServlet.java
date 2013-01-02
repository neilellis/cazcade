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
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.LiquidURIScheme;
import cazcade.liquid.api.lsd.*;
import cazcade.liquid.api.request.RetrieveAliasRequest;
import cazcade.liquid.api.request.RetrieveUserRequest;
import cazcade.liquid.api.request.UpdateAliasRequest;
import cazcade.liquid.api.request.util.RequestUtil;
import org.apache.commons.lang.StringUtils;
import twitter4j.Twitter;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;

public class CallbackServlet extends AbstractTwitterServlet {
    @Nonnull
    private static final Logger log = Logger.getLogger(CallbackServlet.class);
    private static final long serialVersionUID = 1657390011452788111L;

    protected void doGet(@Nonnull final HttpServletRequest request, @Nonnull final HttpServletResponse response) throws ServletException, IOException {
        try {
            final HttpSession session = request.getSession();
            final Twitter twitter = (Twitter) session.getAttribute("twitter");
            final RequestToken requestToken = (RequestToken) session.getAttribute("requestToken");
            final String verifier = request.getParameter("oauth_verifier");

            final AccessToken authAccessToken = twitter.getOAuthAccessToken(requestToken, verifier);
//            LiquidSessionIdentifier identity = (LiquidSessionIdentifier) request.getSession().getAttribute(CommonConstants.IDENTITY_ATTRIBUTE);
            final User user = twitter.verifyCredentials();
            session.setAttribute(USER_KEY, user);

            final LSDTransferEntity twitterAlias = buildAlias(authAccessToken, user, true);

            final RetrieveAliasRequest retrieveAliasRequest = dataStore.process(new RetrieveAliasRequest(new LiquidSessionIdentifier("admin"), new LiquidURI("alias:twitter:" + user.getScreenName())));

            if (RequestUtil.positiveResponse(retrieveAliasRequest)) {
                final LSDBaseEntity responseEntity = retrieveAliasRequest.getResponse();
                final LiquidMessage createSessionRequest = createSession(responseEntity.getURI());
                if (createSessionRequest.getResponse().isA(LSDDictionaryTypes.SESSION)) {
                    final LiquidSessionIdentifier serverSession = createClientSession(session, createSessionRequest);
                    dataStore.process(new UpdateAliasRequest(serverSession, twitterAlias));
                    response.sendRedirect(request.getContextPath() + "/_twitter/login.jsp");
                    session.removeAttribute("requestToken");
                    return;
                } else if (createSessionRequest.getResponse().isA(LSDDictionaryTypes.RESOURCE_NOT_FOUND)) {
                    log.warn("Could not locate Cazcade alias for {0}, will try to register as normal.", responseEntity.getURI());
                } else {
                    log.warn("Could not log alias {0} in, reason was {1}", responseEntity.getURI(), createSessionRequest.getResponse().asFreeText());
                    response.sendRedirect(request.getContextPath() + "/_twitter/fail.jsp?message=" + URLEncoder.encode(createSessionRequest.getResponse().asFreeText(), "utf8"));
                    return;
                }
            }
            session.setAttribute(TWITTER_ALIAS_KEY, twitterAlias);
            final LSDTransferEntity cazcadeAlias = buildAlias(authAccessToken, user, false);
            session.setAttribute(CAZCADE_ALIAS_KEY, cazcadeAlias);
            final RetrieveUserRequest retrieveUserRequest = dataStore.process(new RetrieveUserRequest(new LiquidSessionIdentifier("admin", null), new LiquidURI(LiquidURIScheme.user, user.getScreenName()), true));
            if (RequestUtil.positiveResponse(retrieveUserRequest)) {
                response.sendRedirect(request.getContextPath() + "/_twitter/register.jsp?username=" + URLEncoder.encode(user.getScreenName(), "utf8"));
            } else {
                response.sendRedirect(request.getContextPath() + "/_twitter/register.jsp?username=");
            }
        } catch (Exception e) {
            log.error(e);
            throw new ServletException(e);
        }
    }

    @Nonnull
    private LSDTransferEntity buildAlias(@Nonnull final AccessToken authAccessToken, @Nonnull final User user, final boolean twitter) {
        final LSDTransferEntity alias = LSDSimpleEntity.createEmpty();
        alias.setType(LSDDictionaryTypes.ALIAS);
        alias.timestamp();
        if (twitter) {
            alias.setAttribute(LSDAttribute.NAME, user.getScreenName());
        }
        alias.setAttributeConditonally(LSDAttribute.FULL_NAME, user.getName());
        if (user.getProfileImageURL() != null) {
            alias.setAttributeConditonally(LSDAttribute.IMAGE_URL, user.getProfileImageURL().toString().replace("_normal.jpg", ".jpg"));
            alias.setAttributeConditonally(LSDAttribute.ICON_URL, user.getProfileImageURL().toString());
        }
        if (user.getURL() != null) {
            alias.setAttribute(LSDAttribute.SOURCE, user.getURL().toString());
        }
        alias.setAttributeConditonally(LSDAttribute.DESCRIPTION, user.getDescription());
        alias.setAttributeConditonally(LSDAttribute.TEXT, user.getDescription());
        alias.setAttributeConditonally(LSDAttribute.LOCALE_LANGUAGE, user.getLang());
        final String location = user.getLocation();
        if (location != null) {
            final String[] strings = location.split(",");

            if (strings.length == 2 && StringUtils.isNumeric(strings[0].trim()) && StringUtils.isNumeric(strings[1].trim())) {
                alias.setAttribute(LSDAttribute.LOCATION_LAT, strings[0].trim());
                alias.setAttribute(LSDAttribute.LOCATION_LONG, strings[1].trim());
            } else {
                alias.setAttribute(LSDAttribute.LOCATION_NAME, location);
            }
        }
        alias.setAttributeConditonally(LSDAttribute.LOCALE_TIMEZONE, user.getTimeZone());
        if (user.getCreatedAt() != null) {
            alias.setAttribute(LSDAttribute.PUBLISHED, String.valueOf(user.getCreatedAt().getTime()));
        }
        if (twitter) {
            alias.setAttribute(LSDAttribute.URI, "alias:twitter:" + user.getScreenName());
            alias.setAttribute(LSDAttribute.EURI, "twitter:user:" + user.getScreenName());
            alias.setAttribute(LSDAttribute.NETWORK, "twitter");
            alias.setAttribute(LSDAttribute.SECURITY_TOKEN, authAccessToken.getToken());
            alias.setAttribute(LSDAttribute.SECURITY_SECRET, authAccessToken.getTokenSecret());
        }
        return alias;
    }
}