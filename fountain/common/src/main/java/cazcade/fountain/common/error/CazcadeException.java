package cazcade.fountain.common.error;


import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class CazcadeException extends RuntimeException {
    public CazcadeException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(format(message, params));
    }

    public CazcadeException(@Nonnull final String message, final Object... params) {
        super(format(message, params));
    }

    private static String format(@Nonnull final String message, final Object[] params) {
        final StringBuilder buffer = new StringBuilder();
        int oldI = 0;
        int paramCount = 0;
        for (int i = message.indexOf("%s"); i > 0 && oldI < message.length(); i = message.indexOf("%s", oldI)) {
            buffer.append(message.substring(oldI, i));
            buffer.append(params[paramCount++]);
            oldI = i + 2;
        }
        buffer.append(message.substring(oldI));
        return buffer.toString();
    }

    public CazcadeException(final Throwable throwable) {
        super(throwable);
    }

    public boolean isClientException() {
        return false;
    }
}