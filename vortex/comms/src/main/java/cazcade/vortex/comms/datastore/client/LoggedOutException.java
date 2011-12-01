package cazcade.vortex.comms.datastore.client;

/**
 * @author neilellis@cazcade.com
 */
public class LoggedOutException extends RuntimeException {
    public LoggedOutException() {
        super("Logged out.");
    }
}
