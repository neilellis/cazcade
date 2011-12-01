package cazcade.cli.script;

import cazcade.common.Logger;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */

public class ScriptHelper {

    @Nonnull
    private final static Logger log = Logger.getLogger(ScriptHelper.class);

    public ScriptHelper() {
    }

    public void start() throws Exception {
    }

    public void stop() throws Exception {
    }

    public void logError(@Nonnull Throwable e) {
        log.error(e);
    }

}
