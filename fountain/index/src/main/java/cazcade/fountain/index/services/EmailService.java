package cazcade.fountain.index.services;

/**
 * @author neilellis@cazcade.com
 */
public interface EmailService {

    void sendBoardInvite(String emailAddress, String sender, String boardName, String hash);
}
