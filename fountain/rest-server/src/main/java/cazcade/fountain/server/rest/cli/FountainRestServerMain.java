/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.server.rest.cli;

import cazcade.common.Logger;
import cazcade.fountain.common.app.AppSignalHandler;
import cazcade.fountain.common.app.ApplicationLifecycleListener;
import cazcade.fountain.common.app.ApplicationLifecycleManager;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * @author neilelliz@cazcade.com
 */
public class FountainRestServerMain {
    @Nonnull
    private static final Logger log = Logger.getLogger(FountainRestServerMain.class);

    //private static final String CONTEXT_FILE = "applicationContext.xml";
    //    private static final String SVN_ID = "SVN Identification : $Id$";
    private static FountainRestServer restServer;


    @Nonnull
    private static final ApplicationLifecycleManager lifecycleManager = new ApplicationLifecycleManager();

    public static void main(final String[] args) throws Exception {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                log.info("Fountain REST server shutdown hook called.");
                if (!lifecycleManager.isShutdown()) {
                    lifecycleManager.shutdown();
                }
            }
        });

        final SignalHandler handler = new AppSignalHandler(new Runnable() {
            public void run() {
                log.info("Shutting down Fountain REST server ...");
                lifecycleManager.shutdown();
            }
        }, new Runnable() {
            public void run() {
                log.info("Fountain REST server hardstop initiated!");
                if (restServer != null && !restServer.isStopped()) {
                    restServer.hardstop();
                    System.exit(-2);
                }
            }
        }, new Runnable() {
            public void run() {
                log.info("Forcing shutdown of Fountain REST Server...");
            }
        }
        );

        Signal.handle(new Signal("INT"), handler);
        Signal.handle(new Signal("TERM"), handler);


        //ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONTEXT_FILE);


        final CommandLineParser parser = new PosixParser();
        final Options options = new Options();
        options.addOption("d", "debug", false, "Display debug information.");
        options.addOption("i", "info", false, "Display informational messages.");

        final CommandLine line = parser.parse(options, args);

        restServer = new FountainRestServer();

        final String[] strings = line.getArgs();
        log.info("Arguments " + Arrays.toString(strings));

        lifecycleManager.register(new ApplicationLifecycleListener() {
            public void shutdown() throws Exception {
                restServer.stopIfNotStopped();
                log.info("Lifecycle Manager stopped REST Server.");
            }
        });

        restServer.start();
    }
}