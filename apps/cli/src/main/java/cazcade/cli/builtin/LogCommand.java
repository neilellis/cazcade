package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.commands.AbstractShortLivedCommand;
import org.apache.commons.cli.Options;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.varia.LevelMatchFilter;

/**
 * @author Neil Ellis
 */

public class LogCommand extends AbstractShortLivedCommand {
    public Options getOptions() {
        return new Options();
    }

    @Override
    public String getDescription() {
        return "Set the console logging level.";
    }

    public String getName() {
        return "log";
    }

    public long getIntervalSeconds() {
        return 0;
    }

    public String run(final String[] args, ShellSession shellSession) throws Exception {
        if (args.length == 0) {
            System.err.println("You must specify the new logging level, try either debug, info, warn or error.");
            return "";
        }
        final LevelMatchFilter filter = new LevelMatchFilter();
        filter.setLevelToMatch(args[0]);
        final Appender appender = Logger.getRootLogger().getAppender("stdout");
        appender.clearFilters();
        final Level level = Level.toLevel(args[0]);
        appender.addFilter(new Filter() {
            @Override
            public int decide(LoggingEvent loggingEvent) {
                return loggingEvent.getLevel().isGreaterOrEqual(level) ? ACCEPT : DENY;
            }
        });
        return "";
    }
}
