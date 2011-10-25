/*
 * Copyright (c) 2009 Mangala Solutions Ltd, all rights reserved.
 */

package cazcade.cli;

import cazcade.cli.commands.*;
import cazcade.cli.script.LiquidScriptParser;
import cazcade.fountain.common.app.ApplicationLifecycleListener;
import cazcade.fountain.common.app.ApplicationLifecycleManager;
import cazcade.fountain.datastore.api.FountainDataStore;
import jline.Completor;
import jline.ConsoleReader;
import org.apache.commons.cli.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sun.misc.Signal;
import sun.misc.SignalHandler;

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
    private final static cazcade.common.Logger log = cazcade.common.Logger.getLogger(CLI.class);

    //private static final String CONTEXT_FILE = "applicationContext.xml";
//    private static final String SVN_ID = "SVN Identification : $Id$";
    private static boolean running = true;
    private static int shutdownCount = 0;

    private static ApplicationLifecycleManager lifecycleManager = new ApplicationLifecycleManager();

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("shell-spring-config.xml");
        CommandFactory commandFactory = (CommandFactory) applicationContext.getBean("commandFactory");
        FountainDataStore dataStore = (FountainDataStore) applicationContext.getBean("syncRemoteDataStore");
        dataStore.startIfNotStarted();
        lifecycleManager = (ApplicationLifecycleManager) applicationContext.getBean("shellLifecycleManager");
        addShutdownAndTermHandlers();
        try {

            //ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(CONTEXT_FILE);


            CommandLineParser parser = new PosixParser();
            Options options = new Options();
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
            ShellSession shellSession = new ShellSession();
            shellSession.setDataStore(dataStore);

            if (line.hasOption("file")) {
                final String filename = line.getOptionValue("file");
                File file = new File(filename);
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

    private static void executeScript(InputStream stream, CommandFactory commandFactory, CommandExecutor commandExecutor, ShellSession shellSession, File file) {
        final LiquidScriptParser LiquidScriptParser = new LiquidScriptParser(stream);
        LiquidScriptParser.commandFactory = commandFactory;
        LiquidScriptParser.executor = commandExecutor;
        LiquidScriptParser.shellSession = shellSession;
        try {
            LiquidScriptParser.Script();
        } catch (cazcade.cli.script.ParseException pe) {
            if (file != null) {
                System.err.println(file.getName() + ": " + pe.getMessage());
            } else {
                System.err.println(pe.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private static void executeCommands(Options options, CommandFactory commandFactory, CommandExecutor commandExecutor, String[] strings, ShellSession shellSession) throws Exception {


        if (strings.length > 0) {

            for (String string : strings) {
                if (string.equals("help")) {
                    HelpFormatter f = new HelpFormatter();
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

                } else if (command.equals("help")) {
                    System.out.println("help: Displays this information.");
                    System.out.println("help <command>: Displays help on a given command.");
                    System.out.println("exit: Exits the shell.");
                    System.out.println("list: List all available commands/commands.");
                } else if (command.equals("exit")) {
                    break;
                } else if (command.equals("quit")) {
                    break;
                } else if (command.equals("q")) {
                    break;
                } else if (command.equals("list")) {
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

    private static void listCommands(CommandFactory commandFactory) {
        Collection<Command> commands = commandFactory.getAll();
        System.out.printf("%-30s %s%n", "Command Name", "Description");
        System.out.printf("%-30s %s%n", "-----------------------", "------------------------------------------------");
        for (Command command : commands) {
            if (command.getShortName() != null) {
                System.out.printf("%-30s %s%n", command.getName() + "/" + command.getShortName(), command.getDescription());
            } else {
                System.out.printf("%-30s %s%n", command.getName(), command.getDescription());

            }
        }
    }

    private static void execute(Options options, CommandFactory commandFactory, CommandExecutor commandExecutor, ShellSession shellSession, String string) throws Exception {

        final String[] subArgs = string.split("\\s");
        final Command command = commandFactory.getCommandByName(subArgs[0]);
        if (command == null) {
            System.err.println("Could not find a command by the name of " + subArgs[0]);
            return;
        }
        //Pass it on to get any command specific arguments
        String[] newSubArgs = new String[subArgs.length - 1];
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

        SignalHandler handler = new SignalHandler() {
            public void handle(Signal sig) {
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

        public MyCompletor(Collection<Command> commands) {
            this.commands = commands;
        }

        public int complete(String s, int i, List list) {
            final List<String> commandNames = new ArrayList<String>(commands.size() * 2);
            int pos = 0;
            for (Command command : commands) {
                commandNames.add(command.getName());
                if (command.getShortName() != null) {
                    commandNames.add(command.getShortName());
                }
            }
            int firstSpace = s.indexOf(" ");
            if (i < firstSpace) {
                String toBeCompleted = s.substring(0, i);
                for (String jobName : commandNames) {
                    if (jobName.startsWith(toBeCompleted)) {
                        list.add(jobName);
                    }
                }
            }
            return 0;
        }
    }
}
