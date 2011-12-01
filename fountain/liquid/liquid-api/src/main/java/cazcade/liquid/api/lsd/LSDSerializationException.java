package cazcade.liquid.api.lsd;

import cazcade.fountain.common.error.CazcadeException;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class LSDSerializationException extends CazcadeException {

    public LSDSerializationException(Throwable throwable) {
        super(throwable);
    }

    public LSDSerializationException(@Nonnull String message, Object... params) {
        super(message, params);
    }

    public LSDSerializationException(Throwable cause, @Nonnull String message, Object... params) {
        super(cause, message, params);
    }
}
