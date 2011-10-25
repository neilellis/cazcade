package cazcade.fountain.messaging;

import cazcade.common.Logger;
import org.springframework.util.ErrorHandler;

/**
 * @author neilellis@cazcade.com
 */
public class ListenerErrorHandler implements ErrorHandler {
    private final static Logger log = Logger.getLogger(ListenerErrorHandler.class);

    @Override
    public void handleError(Throwable t) {
        log.error(t);
    }
}
