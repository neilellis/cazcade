package cazcade.liquid.api.lsd;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class LSDPropertyFormatValidationException extends LSDValidationException {
    public LSDPropertyFormatValidationException(@Nonnull final String message) {
        super(message);
    }

    public LSDPropertyFormatValidationException(@Nonnull final String message, final Throwable cause) {
        super(message, cause);
    }

    public LSDPropertyFormatValidationException(final Throwable cause) {
        super(cause);
    }
}
