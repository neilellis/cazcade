/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */
package cazcade.boardcast.servlet.twitter.auth.signin;

import cazcade.common.Logger;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.LiquidURIScheme;
import cazcade.liquid.api.SessionIdentifier;
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
    private static final Logger log              = Logger.getLogger(CallbackServlet.class);
    private static final long   serialVersionUID = 1657390011452788111L;

    protected void doGet(@Nonnull final HttpServletRequest request, @Nonnull final HttpServletResponse response) throws ServletException, IOException {
        try {
            final HttpSession session = request.getSession();
            final Twitter twitter = (Twitter) session.getAttribute("twitter");
            final RequestToken requestToken = (RequestToken) session.getAttribute("requestToken");
            final String verifier = request.getParameter("oauth_verifier");

            final AccessToken authAccessToken = twitter.getOAuthAccessToken(requestToken, verifier);
            //            SessionIdentifier identity = (SessionIdentifier) request.session().$(CommonConstants.IDENTITY_ATTRIBUTE);
            final User user = twitter.verifyCredentials();
            session.setAttribute(USER_KEY, user);

            final TransferEntity twitterAlias = buildAlias(authAccessToken, user, true);

            final RetrieveAliasRequest retrieveAliasRequest = dataStore.process(new RetrieveAliasRequest(new SessionIdentifier("admin"), new LiquidURI(
                    "alias:twitter:"
                    + user.getScreenName())));

            if (RequestUtil.positiveResponse(retrieveAliasRequest)) {
                final Entity responseEntity = retrieveAliasRequest.response();
                final LiquidMessage createSessionRequest = createSession(responseEntity.uri());
                if (createSessionRequest.response().is(Types.T_SESSION)) {
                    final SessionIdentifier serverSession = createClientSession(session, createSessionRequest);
                    dataStore.process(new UpdateAliasRequest(serverSession, twitterAlias));
                    response.sendRedirect(request.getContextPath() + "/_twitter/login.jsp");
                    session.removeAttribute("requestToken");
                    return;
                } else if (createSessionRequest.response().is(Types.T_RESOURCE_NOT_FOUND)) {
                    log.warn("Could not locate Cazcade alias for {0}, will try to register as normal.", responseEntity.uri());
                } else {
                    log.warn("Could not log alias {0} in, reason was {1}", responseEntity.uri(), createSessionRequest.response()
                                                                                                                     .asFreeText());
                    response.sendRedirect(request.getContextPath()
                                          + "/_twitter/fail.jsp?message="
                                          + URLEncoder.encode(createSessionRequest.response().asFreeText(), "utf8"));
                    return;
                }
            }
            session.setAttribute(TWITTER_ALIAS_KEY, twitterAlias);
            final TransferEntity cazcadeAlias = buildAlias(authAccessToken, user, false);
            session.setAttribute(CAZCADE_ALIAS_KEY, cazcadeAlias);
            final RetrieveUserRequest retrieveUserRequest = dataStore.process(new RetrieveUserRequest(new SessionIdentifier("admin", null), new LiquidURI(LiquidURIScheme.user, user
                    .getScreenName()), true));
            if (RequestUtil.positiveResponse(retrieveUserRequest)) {
                response.sendRedirect(request.getContextPath()
                                      + "/_twitter/register.jsp?username="
                                      + URLEncoder.encode(user.getScreenName(), "utf8"));
            } else {
                response.sendRedirect(request.getContextPath() + "/_twitter/register.jsp?username=");
            }
        } catch (Exception e) {
            log.error(e);
            throw new ServletException(e);
        }
    }

    @Nonnull
    private TransferEntity buildAlias(@Nonnull final AccessToken authAccessToken, @Nonnull final User user, final boolean twitter) {
        final TransferEntity alias = SimpleEntity.create(Types.T_ALIAS);
        alias.timestamp();
        if (twitter) {
            alias.$(Dictionary.NAME, user.getScreenName());
        }
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
        if (twitter) {
            alias.$(Dictionary.URI, "alias:twitter:" + user.getScreenName());
            alias.$(Dictionary.EURI, "twitter:user:" + user.getScreenName());
            alias.$(Dictionary.NETWORK, "twitter");
            alias.$(Dictionary.SECURITY_TOKEN, authAccessToken.getToken());
            alias.$(Dictionary.SECURITY_SECRET, authAccessToken.getTokenSecret());
        }
        return alias;
    }
}
