package cazcade.fountain.datastore.api;

/**
 * @author Neil Ellis
 */


public class RelationshipNotFoundException extends DataStoreException {
    public RelationshipNotFoundException(Throwable throwable) {
        super(throwable);
    }

    public RelationshipNotFoundException(String message, Object... params) {
        super(message, params);
    }

    public RelationshipNotFoundException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }
}