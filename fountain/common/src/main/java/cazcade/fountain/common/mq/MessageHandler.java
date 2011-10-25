package cazcade.fountain.common.mq;

/**
 * An object that wishes to handle messages within the common mq package implements this interface.
 */
public interface MessageHandler {

    /**
     * Handle a message.
     *
     * @param requestBytes the message to be processed.
     */
    public byte[] handleMessage(byte[] requestBytes);
}
