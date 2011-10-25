package cazcade.fountain.index.model;

import java.io.Serializable;

/**
 * @author neilellis@cazcade.com
 */
public class MessageBase extends CommonBase implements Serializable {

    protected String messageText;
    protected String externalEntryURL;
    protected MessageSource source;

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }


}
