package cazcade.fountain.index.common.exceptions;

/**
 * @author neilellis@cazcade.com
 */
public class NotAuthorizedException extends RuntimeException {
    public NotAuthorizedException() {
        super();
    }

    public NotAuthorizedException(String s) {
        super(s);
    }
}
