package cazcade.liquid.api.lsd;

import cazcade.fountain.common.error.CazcadeException;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class LSDUnknownAttributeException extends CazcadeException {
    public LSDUnknownAttributeException(Throwable throwable) {
        super(throwable);
    }

    public LSDUnknownAttributeException(@Nonnull String message, Object... params) {
        super(message, params);
    }

    public LSDUnknownAttributeException(Throwable cause, @Nonnull String message, Object... params) {
        super(cause, message, params);
    }
}