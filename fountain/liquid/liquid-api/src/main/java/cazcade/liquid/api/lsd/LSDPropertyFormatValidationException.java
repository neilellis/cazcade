package cazcade.liquid.api.lsd;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class LSDPropertyFormatValidationException extends LSDValidationException {
    public LSDPropertyFormatValidationException(@Nonnull String message) {
        super(message);
    }

    public LSDPropertyFormatValidationException(@Nonnull String message, Throwable cause) {
        super(message, cause);
    }

    public LSDPropertyFormatValidationException(Throwable cause) {
        super(cause);
    }
}
