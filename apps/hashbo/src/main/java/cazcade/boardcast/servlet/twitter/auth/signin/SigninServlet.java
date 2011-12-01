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
    public static final String DEV_CONSUMER_KEY = "EfmjgI7O3GmLCK3IxBBZA";
    @Nonnull
    public static final String DEV_CONSUMER_SECRET = "1AlgHVMJ3jcuigT5560Qgho0KAIVI6tZZy2XAtM";
    @Nonnull
    public static final String LIVE_CONSUMER_KEY = "ee7d5nMi59j5WsbLXfAjMQ";
    @Nonnull
    public static final String LIVE_CONSUMER_SECRET = "XLO3cqTyXP3h20nl0OzEDC5OOkJsks02yczL0k8vQ0";


    protected void doGet(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response) throws ServletException, IOException {
        try {
            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
            configurationBuilder.setDebugEnabled(true);
            Twitter twitter = (new TwitterFactory(configurationBuilder.build())).getInstance();
            if (CommonConstants.IS_PRODUCTION) {
                twitter.setOAuthConsumer(LIVE_CONSUMER_KEY, LIVE_CONSUMER_SECRET);
            } else {
                twitter.setOAuthConsumer(DEV_CONSUMER_KEY, DEV_CONSUMER_SECRET);
            }
            request.getSession().setAttribute("twitter", twitter);


//            if (identity == null && request.getParameter("name") != null) {
//            LiquidSessionIdentifier identity = (LiquidSessionIdentifier) request.getSession(true).getAttribute(CommonConstants.IDENTITY_ATTRIBUTE);
//                identity = new LiquidSessionIdentifier(request.getParameter("name"));
//                request.getSession().setAttribute(CommonConstants.IDENTITY_ATTRIBUTE, identity);
//            }


            StringBuffer callbackURL = request.getRequestURL();
            int index = callbackURL.lastIndexOf("/");
            callbackURL.replace(index, callbackURL.length(), "").append("/callback");
            System.err.println("Callback URL=" + callbackURL);
            RequestToken requestToken = twitter.getOAuthRequestToken(callbackURL.toString());
            request.getSession().setAttribute("requestToken", requestToken);
            response.sendRedirect(requestToken.getAuthenticationURL());

        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new ServletException(e);
        }

    }
}
