package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class RelationshipNotFoundException extends DataStoreException {
    public RelationshipNotFoundException(final Throwable throwable) {
        super(throwable);
    }

    public RelationshipNotFoundException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public RelationshipNotFoundException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }
}