package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class DuplicateRelationshipException extends DataStoreException {
    public DuplicateRelationshipException(Throwable throwable) {
        super(throwable);
    }

    public DuplicateRelationshipException(@Nonnull String message, Object... params) {
        super(message, params);
    }

    public DuplicateRelationshipException(Throwable cause, @Nonnull String message, Object... params) {
        super(cause, message, params);
    }
}