package cazcade.fountain.datastore.server;

import cazcade.fountain.common.app.AppSignalHandler;
import cazcade.fountain.common.app.ApplicationLifecycleListener;
import cazcade.fountain.common.app.ApplicationLifecycleManager;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import cazcade.common.Logger;
import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * @author neilelliz@cazcade.com
 */
public class FountainDataStoreMain {
    private final static Logger log = Logger.getLogger(FountainDataStoreMain.class);

    //private static final String CONTEXT_FILE = "applicationContext.xml";
//    private static final String SVN_ID = "SVN Identification : $Id$";
    private static FountainDataStoreServer dataStore;


    private static ApplicationLifecycleManager lifecycleManager = new ApplicationLifecycleManager();

    public static void main(String[] args) throws Exception {

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                log.info("Fountain Data Store shutdown hook called.");
                if (!lifecycleManager.isShutdown()) {
                    lifecycleManager.shutdown();
                }
            }
        });

        SignalHandler handler = new AppSignalHandler(new Runnable() {
            public void run() {
                log.info("Shutting down Fountain DataStore...");
                lifecycleManager.shutdown();
            }
        }, new Runnable() {
            public void run() {
                log.info("Fountain DataStore hardstop initiated!");
                if (dataStore != null && !dataStore.isStopped()) {
                    dataStore.hardstop();
                    System.exit(-2);
                }

            }
        }, new Runnable() {
            public void run() {
                log.info("Forcing shutdown of Fountain DataStore...");

            }
        });

        Signal.handle(new Signal("INT"), handler);
        Signal.handle(new Signal("TERM"), handler);


        //ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONTEXT_FILE);


        CommandLineParser parser = new PosixParser();
        Options options = new Options();
        options.addOption("d", "debug", false, "Display debug information.");
        options.addOption("i", "info", false, "Display informational messages.");

        final CommandLine line = parser.parse(options, args);

        dataStore = new FountainDataStoreServer();

        final String[] strings = line.getArgs();

        lifecycleManager.register(new ApplicationLifecycleListener() {
            public void shutdown() throws Exception {
                dataStore.stopIfNotStopped();
                log.info("Lifecycle Manager stopped Data Store Server.");
            }
        });

        dataStore.start();


    }


}
