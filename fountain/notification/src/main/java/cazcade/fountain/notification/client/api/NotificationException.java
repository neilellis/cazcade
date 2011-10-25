package cazcade.fountain.notification.client.api;

/**
 * @author neilelliz@cazcade.com
 */
public class NotificationException extends RuntimeException {
    public NotificationException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public NotificationException(String message) {
        super(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public NotificationException(String message, Throwable cause) {
        super(message, cause);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public NotificationException(Throwable cause) {
        super(cause);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
