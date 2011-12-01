/*
 * Copyright (c) 2009 Mangala Solutions Ltd, all rights reserved.
 */

package cazcade.cli;

import cazcade.cli.commands.*;
import cazcade.cli.script.LiquidScriptParser;
import cazcade.cli.script.ParseException;
import cazcade.common.Logger;
import cazcade.fountain.common.app.ApplicationLifecycleListener;
import cazcade.fountain.common.app.ApplicationLifecycleManager;
import cazcade.fountain.datastore.api.FountainDataStore;
import jline.Completor;
import jline.ConsoleReader;
import org.apache.commons.cli.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Neil Ellis
 */

public class CLI {
    @Nonnull
    private static final Logger log = Logger.getLogger(CLI.class);

    //private static final String CONTEXT_FILE = "applicationContext.xml";
//    private static final String SVN_ID = "SVN Identification : $Id$";
    private static boolean running = true;
    private static int shutdownCount;

    @Nonnull
    private static ApplicationLifecycleManager lifecycleManager = new ApplicationLifecycleManager();

    public static void main(final String[] args) throws Exception {
        final ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("shell-spring-config.xml");
        final CommandFactory commandFactory = (CommandFactory) applicationContext.getBean("commandFactory");
        final FountainDataStore dataStore = (FountainDataStore) applicationContext.getBean("syncRemoteDataStore");
        dataStore.startIfNotStarted();
        lifecycleManager = (ApplicationLifecycleManager) applicationContext.getBean("shellLifecycleManager");
        addShutdownAndTermHandlers();
        try {

            //ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONTEXT_FILE);


            final CommandLineParser parser = new PosixParser();
            final Options options = new Options();
            options.addOption("d", "debug", false, "Display debug information.");
            options.addOption("i", "info", false, "Display informational messages.");
            options.addOption("j", "job-threads", true, "specify the number of threads used for the job list.");
            options.addOption("l", "list-commands", false, "List the commands.");
            options.addOption("f", "file", true, "Execute the file as a script.");

            final CommandLine line = parser.parse(options, args);

            int jobThreads = 8;
            if (line.hasOption("list-commands")) {
                listCommands(commandFactory);
            }
            if (line.hasOption("job-threads")) {
                final String jobThreadValue = line.getOptionValue("job-threads");
                jobThreads = Integer.valueOf(jobThreadValue);
            }
            final CommandExecutor commandExecutor = new CommandExecutorImpl(jobThreads);
            lifecycleManager.register(new ApplicationLifecycleListener() {
                public void shutdown() throws Exception {
                    commandExecutor.shutdown();
                }
            });
            final String[] strings = line.getArgs();
            final ShellSession shellSession = new ShellSession();
            shellSession.setDataStore(dataStore);

            if (line.hasOption("file")) {
                final String filename = line.getOptionValue("file");
                final File file = new File(filename);
                final FileInputStream stream = new FileInputStream(file);
                executeScript(stream, commandFactory, commandExecutor, shellSession, file);

            } else {

                executeCommands(options, commandFactory, commandExecutor, strings, shellSession);
            }


        } finally {
            if (!lifecycleManager.isShutdown()) {
                lifecycleManager.shutdown();
            }
        }


    }

    private static void executeScript(final InputStream stream, final CommandFactory commandFactory, final CommandExecutor commandExecutor, final ShellSession shellSession, @Nullable final File file) {
        final LiquidScriptParser LiquidScriptParser = new LiquidScriptParser(stream);
        LiquidScriptParser.commandFactory = commandFactory;
        LiquidScriptParser.executor = commandExecutor;
        LiquidScriptParser.shellSession = shellSession;
        try {
            LiquidScriptParser.Script();
        } catch (ParseException pe) {
            if (file != null) {
                System.err.println(file.getName() + ": " + pe.getMessage());
            } else {
                System.err.println(pe.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private static void executeCommands(final Options options, @Nonnull final CommandFactory commandFactory, @Nonnull final CommandExecutor commandExecutor, @Nonnull final String[] strings, final ShellSession shellSession) throws Exception {


        if (strings.length > 0) {

            for (final String string : strings) {
                if ("help".equals(string)) {
                    final HelpFormatter f = new HelpFormatter();
                    f.printHelp("run", options);
                } else {
                    execute(options, commandFactory, commandExecutor, shellSession, string);
                }
            }
            try {
                commandExecutor.waitForCompletion();
                log.info("All commands finished.");
            } catch (InterruptedException e) {
                log.info("Interrupted.");
                Thread.interrupted();
                log.error(e);
                return;
            }

        } else {
            final ConsoleReader consoleReader = new ConsoleReader();
            final Collection<Command> commands = commandFactory.getAll();
            consoleReader.addCompletor(new MyCompletor(commands));
            System.out.println("Cazcade Shell (C) 2011 Cazcade Ltd.");
//            System.out.println(SVN_ID);
            String command;
            do {
                command = consoleReader.readLine(">");
                if (command.length() == 0) {

                } else if ("help".equals(command)) {
                    System.out.println("help: Displays this information.");
                    System.out.println("help <command>: Displays help on a given command.");
                    System.out.println("exit: Exits the shell.");
                    System.out.println("list: List all available commands/commands.");
                } else if ("exit".equals(command)) {
                    break;
                } else if ("quit".equals(command)) {
                    break;
                } else if ("q".equals(command)) {
                    break;
                } else if ("list".equals(command)) {
                    listCommands(commandFactory);
                } else {
                    executeScript(new ByteArrayInputStream((command + ";").getBytes("utf8")), commandFactory, commandExecutor, shellSession, null);
//
//                    execute(options, commandFactory, commandExecutor, shellSession, command);
                }
            }
            while (true);
        }


    }

    private static void listCommands(@Nonnull final CommandFactory commandFactory) {
        final Collection<Command> commands = commandFactory.getAll();
        System.out.printf("%-30s %s%n", "Command Name", "Description");
        System.out.printf("%-30s %s%n", "-----------------------", "------------------------------------------------");
        for (final Command command : commands) {
            if (command.getShortName() != null) {
                System.out.printf("%-30s %s%n", command.getName() + "/" + command.getShortName(), command.getDescription());
            } else {
                System.out.printf("%-30s %s%n", command.getName(), command.getDescription());

            }
        }
    }

    private static void execute(final Options options, @Nonnull final CommandFactory commandFactory, final CommandExecutor commandExecutor, final ShellSession shellSession, @Nonnull final String string) throws Exception {

        final String[] subArgs = string.split("\\s");
        final Command command = commandFactory.getCommandByName(subArgs[0]);
        if (command == null) {
            System.err.println("Could not find a command by the name of " + subArgs[0]);
            return;
        }
        //Pass it on to get any command specific arguments
        final String[] newSubArgs = new String[subArgs.length - 1];
        System.arraycopy(subArgs, 1, newSubArgs, 0, subArgs.length - 1);
        new CommandExecutionContext(command, newSubArgs, commandFactory, commandExecutor, shellSession).execute();

    }

    private static void addShutdownAndTermHandlers() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                log.info("Sociagraph shutdown hook");
                if (!lifecycleManager.isShutdown()) {
                    lifecycleManager.shutdown();
                }
            }
        });

        final SignalHandler handler = new SignalHandler() {
            public void handle(final Signal sig) {
                shutdownCount++;
                if (running) {
                    running = false;
                    log.info("Signal " + sig);
                    log.info("Shutting down LiquidShell...");
                    try {
                        lifecycleManager.shutdown();
                        System.exit(0);
                    } catch (Exception e) {
                        log.error(e);
                    }
                } else {
                    if (shutdownCount == 2) {
                        log.info("Hardstop initiated!");
                        System.exit(-2);
                        return;
                    }
                    if (shutdownCount > 2) {
                        // only on the second attempt do we exit
                        log.info("LiquidShell hardstop interrupted, terminating!");
                        System.exit(-3);
                    }
                }
            }
        };

        Signal.handle(new Signal("INT"), handler);
        Signal.handle(new Signal("TERM"), handler);
    }


    private static class MyCompletor implements Completor {
        private final Collection<Command> commands;

        public MyCompletor(final Collection<Command> commands) {
            this.commands = commands;
        }

        public int complete(@Nonnull final String s, final int i, @Nonnull final List list) {
            final List<String> commandNames = new ArrayList<String>(commands.size() * 2);
            final int pos = 0;
            for (final Command command : commands) {
                commandNames.add(command.getName());
                if (command.getShortName() != null) {
                    commandNames.add(command.getShortName());
                }
            }
            final int firstSpace = s.indexOf(' ');
            if (i < firstSpace) {
                final String toBeCompleted = s.substring(0, i);
                for (final String jobName : commandNames) {
                    if (jobName.startsWith(toBeCompleted)) {
                        list.add(jobName);
                    }
                }
            }
            return 0;
        }
    }
}
