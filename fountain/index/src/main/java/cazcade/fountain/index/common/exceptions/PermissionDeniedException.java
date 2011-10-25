package cazcade.fountain.index.common.exceptions;

/**
 * @author neilellis@cazcade.com
 */
public class PermissionDeniedException extends RuntimeException {
    public PermissionDeniedException() {
        super();
    }

    public PermissionDeniedException(String s) {
        super(s);
    }
}
