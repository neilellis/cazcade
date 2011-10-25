package cazcade.fountain.server.rest.cli;

import cazcade.common.Logger;
import cazcade.fountain.common.service.AbstractServiceStateMachine;
import cazcade.fountain.server.rest.servlet.DictionaryServlet;
import cazcade.fountain.server.rest.servlet.LiquidNotificationServlet;
import cazcade.fountain.server.rest.servlet.LiquidRestServlet;
import cazcade.fountain.server.rest.servlet.ObjectiveCDictionaryServlet;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.concurrent.CountDownLatch;

/**
 * @author neilelliz@cazcade.com
 */
public class FountainRestServer extends AbstractServiceStateMachine {
    private final static Logger log = Logger.getLogger(FountainRestServer.class);

    private CountDownLatch initialisationLatch = new CountDownLatch(1);
    private Server server;


    @Override
    public void start() throws Exception {

        super.start();
        server = new Server(8088);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SECURITY);
        context.setContextPath("/");
        SecurityHandler securityHandler = new CazcadeSecurityHandler();
        securityHandler.setLoginService(new CazcadeLoginService());
        securityHandler.setAuthMethod("BASIC");
        securityHandler.setRealmName("Rest API");
        context.setSecurityHandler(securityHandler);

        context.addServlet(LiquidRestServlet.class, "/liquid/rest/1.0/*");
        context.addServlet(LiquidNotificationServlet.class, "/liquid/notification/1.0/*");
        context.addServlet(DictionaryServlet.class, "/liquid/dev/1.0/dictionary.html");
        context.addServlet(ObjectiveCDictionaryServlet.class, "/liquid/dev/1.0/dictionary.h");

        server.setHandler(context);
        server.start();
        initialisationLatch.countDown();
        log.info("");
        log.info("*************************************************************");
        log.info("********* Fountain Rest Server Started Successfully *********");
        log.info("*************************************************************");
        log.info("");
        server.join();

    }

    public void waitForInitialisation() throws InterruptedException {
        initialisationLatch.await();
    }

    @Override
    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.stop();
    }
}
