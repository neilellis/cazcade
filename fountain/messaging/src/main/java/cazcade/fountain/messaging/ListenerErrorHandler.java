package cazcade.fountain.messaging;

import cazcade.common.Logger;
import org.springframework.util.ErrorHandler;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class ListenerErrorHandler implements ErrorHandler {
    @Nonnull
    private final static Logger log = Logger.getLogger(ListenerErrorHandler.class);

    @Override
    public void handleError(@Nonnull Throwable t) {
        log.error(t);
    }
}
