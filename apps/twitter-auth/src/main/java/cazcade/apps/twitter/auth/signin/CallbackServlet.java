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
package cazcade.apps.twitter.auth.signin;

import cazcade.common.CommonConstants;
import cazcade.common.Logger;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidMessageState;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.CreateAliasRequest;
import cazcade.liquid.impl.xstream.LiquidXStreamFactory;
import org.apache.commons.lang.StringUtils;
import twitter4j.Twitter;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CallbackServlet extends AbstractTwitterServlet {
    @Nonnull
    private static final Logger log = Logger.getLogger(CallbackServlet.class);
    private static final long serialVersionUID = 1657390011452788111L;

    protected void doGet(@Nonnull final HttpServletRequest request, @Nonnull final HttpServletResponse response) throws ServletException, IOException {
        try {
            final Twitter twitter = (Twitter) request.getSession().getAttribute("twitter");
            final RequestToken requestToken = (RequestToken) request.getSession().getAttribute("requestToken");
            final String verifier = request.getParameter("oauth_verifier");

            final AccessToken authAccessToken = twitter.getOAuthAccessToken(requestToken, verifier);
            final LiquidSessionIdentifier identity = (LiquidSessionIdentifier) request.getSession().getAttribute(CommonConstants.IDENTITY_ATTRIBUTE);
            final LSDTransferEntity alias = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.ALIAS);
            alias.timestamp();
            final User user = twitter.verifyCredentials();
            alias.setAttribute(LSDAttribute.NAME, user.getScreenName());
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
            alias.setAttribute(LSDAttribute.EURI, "twitter:user:" + user.getScreenName());
            alias.setAttribute(LSDAttribute.NETWORK, "twitter");
            alias.setAttribute(LSDAttribute.SECURITY_TOKEN, authAccessToken.getToken());
            alias.setAttribute(LSDAttribute.SECURITY_SECRET, authAccessToken.getTokenSecret());

            final LiquidMessage message = dataStore.process(new CreateAliasRequest(identity, alias, false, true, true));
            if (message.getState() == LiquidMessageState.SUCCESS) {
                response.sendRedirect(request.getContextPath() + "/complete.jsp");
            } else {
                log.error(LiquidXStreamFactory.getXstream().toXML(message));
                response.sendRedirect(request.getContextPath() + "/fail.jsp");
            }
            request.getSession().removeAttribute("requestToken");
        } catch (Exception e) {
            log.error(e);
            throw new ServletException(e);
        }
    }
}
