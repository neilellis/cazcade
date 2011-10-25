package cazcade.cli.script;

import cazcade.common.Logger;

/**
 * @author Neil Ellis
 */

public class ScriptHelper {

    private final static Logger log = Logger.getLogger(ScriptHelper.class);

    public ScriptHelper() {
    }

    public void start() throws Exception {
    }

    public void stop() throws Exception {
    }

    public void logError(Throwable e) {
        log.error(e);
    }

}
