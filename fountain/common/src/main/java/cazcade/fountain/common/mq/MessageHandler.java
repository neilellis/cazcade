package cazcade.fountain.common.mq;

import javax.annotation.Nonnull;

/**
 * An object that wishes to handle messages within the common mq package implements this interface.
 */
public interface MessageHandler {
    /**
     * Handle a message.
     *
     * @param requestBytes the message to be processed.
     */
    @Nonnull
    byte[] handleMessage(byte[] requestBytes);
}
