package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class RelationshipNotFoundException extends DataStoreException {
    public RelationshipNotFoundException(Throwable throwable) {
        super(throwable);
    }

    public RelationshipNotFoundException(@Nonnull String message, Object... params) {
        super(message, params);
    }

    public RelationshipNotFoundException(Throwable cause, @Nonnull String message, Object... params) {
        super(cause, message, params);
    }
}