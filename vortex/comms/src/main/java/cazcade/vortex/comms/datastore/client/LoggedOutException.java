package cazcade.vortex.comms.datastore.client;

import java.io.Serializable;

/**
 * @author neilellis@cazcade.com
 */
public class LoggedOutException extends RuntimeException implements Serializable {
    public LoggedOutException() {
        super("Logged out.");
    }
}
