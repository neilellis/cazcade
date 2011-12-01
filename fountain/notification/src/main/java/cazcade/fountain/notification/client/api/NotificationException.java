package cazcade.fountain.notification.client.api;

/**
 * @author neilelliz@cazcade.com
 */
public class NotificationException extends RuntimeException {
    public NotificationException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public NotificationException(final String message) {
        super(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public NotificationException(final String message, final Throwable cause) {
        super(message, cause);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public NotificationException(final Throwable cause) {
        super(cause);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
