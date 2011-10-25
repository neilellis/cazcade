package cazcade.fountain.index.common.exceptions;

/**
 * @author neilellis@cazcade.com
 */
public class NotLoggedInException extends RuntimeException {
    public NotLoggedInException() {
        super();
    }

    public NotLoggedInException(String s) {
        super(s);
    }
}
