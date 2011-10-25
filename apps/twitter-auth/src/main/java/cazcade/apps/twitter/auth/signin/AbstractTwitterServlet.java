package cazcade.apps.twitter.auth.signin;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.FountainDataStore;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * @author neilellis@cazcade.com
 */
public class AbstractTwitterServlet extends HttpServlet {
    private final static Logger log = Logger.getLogger(AbstractTwitterServlet.class);
    private ClassPathXmlApplicationContext applicationContext;
    protected FountainDataStore dataStore;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        applicationContext = new ClassPathXmlApplicationContext("classpath:datastore-client-spring-config.xml");
        dataStore = (FountainDataStore) applicationContext.getBean("remoteDataStore");
        try {
            dataStore.startIfNotStarted();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    @Override
    public void destroy() {
        dataStore.stopIfNotStopped();
        super.destroy();
    }
}
