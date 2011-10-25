package cazcade.hashbo.servlet.twitter.auth.signin;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.FountainDataStore;
import cazcade.fountain.messaging.session.ClientSessionManager;
import cazcade.hashbo.servlet.AbstractHashboServlet;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

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
