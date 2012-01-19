package cazcade.fountain.index.common.exceptions;

/**
 * @author neilellis@cazcade.com
 */
public class NotLoggedInException extends RuntimeException {
    public NotLoggedInException(final String s) {
        super(s);
    }

    public NotLoggedInException() {
        super();
    }
}
