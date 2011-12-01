package cazcade.cli.commands;

import cazcade.common.Logger;
import cazcade.fountain.common.service.AbstractServiceStateMachine;
import org.apache.commons.cli.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Neil Ellis
 */

public abstract class AbstractCommand extends AbstractServiceStateMachine implements Command {
    @Nonnull
    private static final Logger log = Logger.getLogger(AbstractCommand.class);
    private CommandInitContext context;


    public void init(final CommandInitContext context) throws Exception {
        log.info("Initing {0}.", getName());
        this.context = context;
    }


    @Nonnull
    public String getDescription() {
        return "No description supplied.";
    }

    @Nullable
    public String getShortName() {
        return null;
    }

    public long getInitialDelaySeconds() {
        return 0;
    }

    public CommandLine parse(final String[] args) throws ParseException {
        final CommandLineParser parser = new PosixParser();

        final CommandLine line = parser.parse(getOptions(), args);
        if (line.getArgList().contains("help")) {
            final HelpFormatter f = new HelpFormatter();
            f.printHelp(getName(), getOptions());
        }
        return line;
    }

    public CommandInitContext getContext() {
        return context;
    }
}
