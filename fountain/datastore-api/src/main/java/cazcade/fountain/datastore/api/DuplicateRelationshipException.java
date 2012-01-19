package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class DuplicateRelationshipException extends DataStoreException {
    public DuplicateRelationshipException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }

    public DuplicateRelationshipException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public DuplicateRelationshipException(final Throwable throwable) {
        super(throwable);
    }
}