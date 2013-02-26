/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */
package cazcade.boardcast.servlet.twitter.auth.signin;

import cazcade.common.CommonConstants;
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
    //
    //    public static final String DEV_CONSUMER_KEY = "yz6AfROC4muyfs8Zd9m54Q";
    //    public static final String DEV_CONSUMER_SECRET = "fo32px1fw2Sw2LgRnEsLfguuKQa3N0Utw4VFXodw";
    //    public static final String LIVE_CONSUMER_KEY = "Vhs7wZCrTa2ynUBg80ShyA";
    //    public static final String LIVE_CONSUMER_SECRET = "K4O7i09RdyIGArpZ7QNw9eGWPd97RUV1W2aTRcTq70";


    @Nonnull
    public static final String DEV_CONSUMER_KEY     = "EfmjgI7O3GmLCK3IxBBZA";
    @Nonnull
    public static final String DEV_CONSUMER_SECRET  = "1AlgHVMJ3jcuigT5560Qgho0KAIVI6tZZy2XAtM";
    @Nonnull
    public static final String LIVE_CONSUMER_KEY    = "ee7d5nMi59j5WsbLXfAjMQ";
    @Nonnull
    public static final String LIVE_CONSUMER_SECRET = "XLO3cqTyXP3h20nl0OzEDC5OOkJsks02yczL0k8vQ0";


    protected void doGet(@Nonnull final HttpServletRequest request, @Nonnull final HttpServletResponse response) throws ServletException, IOException {
        try {
            final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
            configurationBuilder.setDebugEnabled(true);
            final Twitter twitter = new TwitterFactory(configurationBuilder.build()).getInstance();
            if (CommonConstants.IS_PRODUCTION) {
                twitter.setOAuthConsumer(LIVE_CONSUMER_KEY, LIVE_CONSUMER_SECRET);
            } else {
                twitter.setOAuthConsumer(DEV_CONSUMER_KEY, DEV_CONSUMER_SECRET);
            }
            request.getSession().setAttribute("twitter", twitter);


            //            if (identity == null && request.getParameter("name") != null) {
            //            SessionIdentifier identity = (SessionIdentifier) request.session(true).$(CommonConstants.IDENTITY_ATTRIBUTE);
            //                identity = new SessionIdentifier(request.getParameter("name"));
            //                request.session().$(CommonConstants.IDENTITY_ATTRIBUTE, identity);
            //            }


            final StringBuffer callbackURL = request.getRequestURL();
            final int index = callbackURL.lastIndexOf("/");
            callbackURL.replace(index, callbackURL.length(), "").append("/callback");
            System.err.println("Callback URL=" + callbackURL);
            final RequestToken requestToken = twitter.getOAuthRequestToken(callbackURL.toString());
            request.getSession().setAttribute("requestToken", requestToken);
            response.sendRedirect(requestToken.getAuthenticationURL());

        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new ServletException(e);
        }

    }
}
