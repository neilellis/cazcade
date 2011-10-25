package cazcade.fountain.datastore.client.validation.entity.api;

/**
 * @author neilelliz@cazcade.com
 */
public class EntityValidationException extends RuntimeException {
    public EntityValidationException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public EntityValidationException(String message) {
        super(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public EntityValidationException(String message, Throwable cause) {
        super(message, cause);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public EntityValidationException(Throwable cause) {
        super(cause);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
