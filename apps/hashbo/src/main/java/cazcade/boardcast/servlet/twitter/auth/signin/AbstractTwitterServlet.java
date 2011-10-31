package cazcade.boardcast.servlet.twitter.auth.signin;

import cazcade.boardcast.servlet.AbstractHashboServlet;
import cazcade.common.Logger;

/**
 * @author neilellis@cazcade.com
 */
public class AbstractTwitterServlet extends AbstractHashboServlet {
    private final static Logger log = Logger.getLogger(AbstractTwitterServlet.class);
    public static final String USER_KEY = "twitter_user";
    public static final String TWITTER_ALIAS_KEY = "twitter_alias";
    public static final String CAZCADE_ALIAS_KEY = "cazcade_alias";
    public static final String USERNAME_PARAM = "username";
    public static final String EMAIL_PARAM = "email";
    public static final String PASSWORD_PARAM = "password";


    @Override
    public void destroy() {
        dataStore.stopIfNotStopped();
        super.destroy();
    }
}
