/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */
package cazcade.apps.twitter.auth.signin;

import cazcade.common.CommonConstants;
import cazcade.common.Logger;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidMessageState;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.SimpleEntity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Types;
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
    private static final Logger log              = Logger.getLogger(CallbackServlet.class);
    private static final long   serialVersionUID = 1657390011452788111L;

    protected void doGet(@Nonnull final HttpServletRequest request, @Nonnull final HttpServletResponse response) throws ServletException, IOException {
        try {
            final Twitter twitter = (Twitter) request.getSession().getAttribute("twitter");
            final RequestToken requestToken = (RequestToken) request.getSession().getAttribute("requestToken");
            final String verifier = request.getParameter("oauth_verifier");

            final AccessToken authAccessToken = twitter.getOAuthAccessToken(requestToken, verifier);
            final SessionIdentifier identity = (SessionIdentifier) request.getSession()
                                                                          .getAttribute(CommonConstants.IDENTITY_ATTRIBUTE);
            final TransferEntity alias = SimpleEntity.create(Types.T_ALIAS);
            alias.timestamp();
            final User user = twitter.verifyCredentials();
            alias.$(Dictionary.NAME, user.getScreenName());
            alias.$notnull(Dictionary.FULL_NAME, user.getName());
            if (user.getProfileImageURL() != null) {
                alias.$notnull(Dictionary.IMAGE_URL, user.getProfileImageURL().toString().replace("_normal.jpg", ".jpg"));
                alias.$notnull(Dictionary.ICON_URL, user.getProfileImageURL().toString());
            }
            if (user.getURL() != null) {
                alias.$(Dictionary.SOURCE, user.getURL().toString());
            }
            alias.$notnull(Dictionary.DESCRIPTION, user.getDescription());
            alias.$notnull(Dictionary.TEXT, user.getDescription());
            alias.$notnull(Dictionary.LOCALE_LANGUAGE, user.getLang());
            final String location = user.getLocation();
            if (location != null) {
                final String[] strings = location.split(",");

                if (strings.length == 2 && StringUtils.isNumeric(strings[0].trim()) && StringUtils.isNumeric(strings[1].trim())) {
                    alias.$(Dictionary.LOCATION_LAT, strings[0].trim());
                    alias.$(Dictionary.LOCATION_LONG, strings[1].trim());
                } else {
                    alias.$(Dictionary.LOCATION_NAME, location);
                }
            }
            alias.$notnull(Dictionary.LOCALE_TIMEZONE, user.getTimeZone());
            if (user.getCreatedAt() != null) {
                alias.$(Dictionary.PUBLISHED, String.valueOf(user.getCreatedAt().getTime()));
            }
            alias.$(Dictionary.EURI, "twitter:user:" + user.getScreenName());
            alias.$(Dictionary.NETWORK, "twitter");
            alias.$(Dictionary.SECURITY_TOKEN, authAccessToken.getToken());
            alias.$(Dictionary.SECURITY_SECRET, authAccessToken.getTokenSecret());

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
