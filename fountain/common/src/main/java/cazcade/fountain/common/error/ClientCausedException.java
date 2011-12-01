package cazcade.fountain.common.error;

import javax.annotation.Nonnull;

/**
 * This exception and subclasses should be thrown if an error occurred that is
 * because of the Client behaving badly.
 *
 * @author neilellis@cazcade.com
 */
public class ClientCausedException extends CazcadeException {
    public ClientCausedException(Throwable throwable) {
        super(throwable);
    }

    public ClientCausedException(@Nonnull String message, Object... params) {
        super(message, params);
    }

    public ClientCausedException(Throwable cause, @Nonnull String message, Object... params) {
        super(cause, message, params);
    }

    public boolean isClientException() {
        return true;
    }

}
