package cazcade.fountain.datastore.server; /**
 * @author neilellis@cazcade.com
 */

import cazcade.common.Logger;

import javax.annotation.Nonnull;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class FountainDataStoreContextListener implements ServletContextListener,
        HttpSessionListener, HttpSessionAttributeListener {
    @Nonnull
    private final static Logger log = Logger.getLogger(FountainDataStoreContextListener.class);

    @Nonnull
    private final FountainDataStoreServer dataStore;

    // Public constructor is required by servlet spec
    public FountainDataStoreContextListener() {

        dataStore = new FountainDataStoreServer();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                dataStore.stopIfNotStopped();
            }
        });
    }

    // -------------------------------------------------------
    // ServletContextListener implementation
    // -------------------------------------------------------
    public void contextInitialized(ServletContextEvent sce) {
        try {
            dataStore.start();
        } catch (Exception e) {
            log.error(e);
        }

    }

    public void contextDestroyed(ServletContextEvent sce) {
        dataStore.stopIfNotStopped();
    }

    // -------------------------------------------------------
    // HttpSessionListener implementation
    // -------------------------------------------------------
    public void sessionCreated(HttpSessionEvent se) {
        /* Session is created. */
    }

    public void sessionDestroyed(HttpSessionEvent se) {
        /* Session is destroyed. */
    }

    // -------------------------------------------------------
    // HttpSessionAttributeListener implementation
    // -------------------------------------------------------

    public void attributeAdded(HttpSessionBindingEvent sbe) {
        /* This method is called when an attribute 
           is added to a session.
        */
    }

    public void attributeRemoved(HttpSessionBindingEvent sbe) {
        /* This method is called when an attribute
           is removed from a session.
        */
    }

    public void attributeReplaced(HttpSessionBindingEvent sbe) {
        /* This method is invoked when an attibute
           is replaced in a session.
        */
    }
}
