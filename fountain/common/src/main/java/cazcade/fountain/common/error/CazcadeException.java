package cazcade.fountain.common.error;


import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class CazcadeException extends RuntimeException {

    public CazcadeException(Throwable throwable) {
        super(throwable);
    }

    public CazcadeException(@Nonnull String message, Object... params) {
        super(format(message, params));
    }

    public CazcadeException(Throwable cause, @Nonnull String message, Object... params) {
        super(format(message, params));
    }

    private static String format(@Nonnull String message, Object[] params) {
        StringBuilder buffer = new StringBuilder();
        int oldI = 0;
        int paramCount = 0;
        for (int i = message.indexOf("%s"); i > 0 && (oldI < message.length()); i = message.indexOf("%s", oldI)) {
            buffer.append(message.substring(oldI, i));
            buffer.append(params[paramCount++]);
            oldI = i + 2;
        }
        buffer.append(message.substring(oldI));
        return buffer.toString();
    }

    public boolean isClientException() {
        return false;
    }
}