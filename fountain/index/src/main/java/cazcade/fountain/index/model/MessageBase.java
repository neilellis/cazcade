package cazcade.fountain.index.model;

/**
 * @author neilellis@cazcade.com
 */
public class MessageBase extends CommonBase {
    protected String messageText;
    protected String externalEntryURL;
    protected MessageSource source;

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(final String messageText) {
        this.messageText = messageText;
    }
}
