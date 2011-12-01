package cazcade.fountain.index.common.exceptions;

/**
 * @author neilellis@cazcade.com
 */
public class PermissionDeniedException extends RuntimeException {
    public PermissionDeniedException() {
        super();
    }

    public PermissionDeniedException(final String s) {
        super(s);
    }
}
