package cazcade.boardcast.servlet.twitter.auth.signin;

import cazcade.boardcast.servlet.AbstractHashboServlet;
import cazcade.common.Logger;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class AbstractTwitterServlet extends AbstractHashboServlet {
    @Nonnull
    private final static Logger log = Logger.getLogger(AbstractTwitterServlet.class);
    @Nonnull
    public static final String USER_KEY = "twitter_user";
    @Nonnull
    public static final String TWITTER_ALIAS_KEY = "twitter_alias";
    @Nonnull
    public static final String CAZCADE_ALIAS_KEY = "cazcade_alias";
    @Nonnull
    public static final String USERNAME_PARAM = "username";
    @Nonnull
    public static final String EMAIL_PARAM = "email";
    @Nonnull
    public static final String PASSWORD_PARAM = "password";


    @Override
    public void destroy() {
        dataStore.stopIfNotStopped();
        super.destroy();
    }
}
