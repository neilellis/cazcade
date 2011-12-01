package cazcade.fountain.datastore.client.validation.entity.api;

/**
 * @author neilelliz@cazcade.com
 */
public class EntityValidationException extends RuntimeException {
    public EntityValidationException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public EntityValidationException(final String message) {
        super(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public EntityValidationException(final String message, final Throwable cause) {
        super(message, cause);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public EntityValidationException(final Throwable cause) {
        super(cause);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
