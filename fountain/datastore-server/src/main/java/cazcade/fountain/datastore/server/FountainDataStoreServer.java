package cazcade.fountain.datastore.server;

import cazcade.common.Logger;
import cazcade.fountain.common.service.AbstractServiceStateMachine;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Nonnull;
import java.util.concurrent.CountDownLatch;

/**
 * This is the server implementation that acts on requests from the FountainRemoteDataStore passing requests on to an
 * instance of FountainLocalDataStore.
 *
 * @author neilelliz@cazcade.com
 */
public class FountainDataStoreServer extends AbstractServiceStateMachine {

    @Nonnull
    private final static Logger log = Logger.getLogger(FountainDataStoreServer.class);

    private ClassPathXmlApplicationContext applicationContext;
    @Nonnull
    private final CountDownLatch initialisationLatch = new CountDownLatch(1);

    @Override
    public void start() throws Exception {
        super.start();
        initialiseSpringContext();
        initialisationLatch.countDown();

        log.debug("Latch cleared...");

        log.info("");
        log.info("*************************************************************");
        log.info("****** Fountain Data Store Server Started Successfully ******");
        log.info("*************************************************************");
        log.info("");
    }

    public void waitForInitialisation() throws InterruptedException {
        log.debug("Process waiting on latch...");
        initialisationLatch.await();
        log.debug("Process finished waiting on latch.");
    }

    private void initialiseSpringContext() {
        log.info("Loading Spring Context.");
        applicationContext = new ClassPathXmlApplicationContext("classpath:datastore-server-spring-config.xml");
        applicationContext.start();
        log.info("Spring Context loaded.");
    }


    @Override
    public void stop() {
        super.stop();

        if (applicationContext != null && applicationContext.isActive()) {
            try {
                //The state machine of the store is managed by Spring.
                applicationContext.destroy();
                log.info("Spring Application Context destroyed.");
            } catch (Exception e) {
                log.error(e);
            }
        }
        log.info("Stopped DataStore server.");
    }


}
