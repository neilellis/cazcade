/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */
package cazcade.apps.twitter.auth.signin;

import cazcade.common.CommonConstants;
import cazcade.liquid.api.SessionIdentifier;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SigninServlet extends HttpServlet {

    protected void doGet(@Nonnull final HttpServletRequest request, @Nonnull final HttpServletResponse response) throws ServletException, IOException {
        try {
            final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
            configurationBuilder.setDebugEnabled(true);
            final Twitter twitter = new TwitterFactory(configurationBuilder.build()).getInstance();
            twitter.setOAuthConsumer("GSBXlGEdtTqauDu63KdzNg", "26po36F1W9T9iI9916TMRJxf4KV57RDSatghirUVew");
            SessionIdentifier identity = (SessionIdentifier) request.getSession(true)
                                                                    .getAttribute(CommonConstants.IDENTITY_ATTRIBUTE);
            request.getSession().setAttribute("twitter", twitter);
            if (identity == null && request.getParameter("name") != null) {
                identity = new SessionIdentifier(request.getParameter("name"));
                request.getSession().setAttribute(CommonConstants.IDENTITY_ATTRIBUTE, identity);
            }


            final StringBuffer callbackURL = request.getRequestURL();
            final int index = callbackURL.lastIndexOf("/");
            callbackURL.replace(index, callbackURL.length(), "").append("/callback");
            System.err.println("Callback URL=" + callbackURL);
            final RequestToken requestToken = twitter.getOAuthRequestToken(callbackURL.toString());
            request.getSession().setAttribute("requestToken", requestToken);
            response.sendRedirect(requestToken.getAuthorizationURL());

        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new ServletException(e);
        }

    }
}
