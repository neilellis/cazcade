package cazcade.fountain.index.common.exceptions;

/**
 * @author neilellis@cazcade.com
 */
public class NotAuthorizedException extends RuntimeException {
    public NotAuthorizedException(final String s) {
        super(s);
    }

    public NotAuthorizedException() {
        super();
    }
}
