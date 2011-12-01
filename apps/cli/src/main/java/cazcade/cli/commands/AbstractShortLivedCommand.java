/*
 * Copyright (c) 2009 Mangala Solutions Ltd, all rights reserved.
 */

package cazcade.cli.commands;

import cazcade.common.Logger;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @author Neil Ellis
 */

public abstract class AbstractShortLivedCommand extends AbstractCommand implements Command {
    @Nonnull
    private final static Logger log = Logger.getLogger(AbstractShortLivedCommand.class);

    public boolean isShortLived() {
        return true;
    }

    public long getIntervalSeconds() {
        return -1;
    }


    protected Process execShellCommand(String command) throws IOException {

        final Process process = Runtime.getRuntime().exec(command);

        Thread stdOutRouter = new Thread() {

            public void run() {
                try {
                    IOUtils.copy(process.getInputStream(), System.out);
                } catch (IOException e) {
                    log.error(e);
                }
            }
        };

        Thread stdErrRouter = new Thread() {

            public void run() {
                try {
                    IOUtils.copy(process.getErrorStream(), System.err);
                } catch (IOException e) {
                    log.error(e);
                }
            }
        };

        stdOutRouter.start();
        stdErrRouter.start();
        return process;

    }

}
