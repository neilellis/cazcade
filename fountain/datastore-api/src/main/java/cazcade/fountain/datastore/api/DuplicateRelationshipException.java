package cazcade.fountain.datastore.api;

/**
 * @author Neil Ellis
 */


public class DuplicateRelationshipException extends DataStoreException {
    public DuplicateRelationshipException(Throwable throwable) {
        super(throwable);
    }

    public DuplicateRelationshipException(String message, Object... params) {
        super(message, params);
    }

    public DuplicateRelationshipException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }
}