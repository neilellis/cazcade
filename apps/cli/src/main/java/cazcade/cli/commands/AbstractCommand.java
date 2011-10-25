package cazcade.cli.commands;

import cazcade.common.Logger;
import cazcade.fountain.common.service.AbstractServiceStateMachine;
import org.apache.commons.cli.*;

/**
 * @author Neil Ellis
 */

public abstract class AbstractCommand extends AbstractServiceStateMachine implements Command {
    private final static Logger log = Logger.getLogger(AbstractCommand.class);
    private CommandInitContext context;


    public void init(CommandInitContext context) throws Exception {
        log.info("Initing {0}.", getName());
        this.context = context;
    }


    public String getDescription() {
        return "No description supplied.";
    }

    public String getShortName() {
        return null;
    }

    public long getInitialDelaySeconds() {
        return 0;
    }

    public CommandLine parse(String[] args) throws ParseException {
        CommandLineParser parser = new PosixParser();

        final CommandLine line = parser.parse(getOptions(), args);
        if (line.getArgList().contains("help")) {
            HelpFormatter f = new HelpFormatter();
            f.printHelp(getName(), getOptions());
        }
        return line;
    }

    public CommandInitContext getContext() {
        return context;
    }
}
