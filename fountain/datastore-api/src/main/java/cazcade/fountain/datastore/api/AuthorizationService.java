package cazcade.fountain.datastore.api;

import cazcade.liquid.api.*;
import cazcade.liquid.api.request.AbstractRetrievalRequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilelliz@cazcade.com
 */
public interface AuthorizationService {

    @Nonnull
    AuthorizationStatus authorize(LiquidSessionIdentifier identity, LiquidUUID resource, LiquidPermission permission) throws Exception;

    @Nullable
    LiquidRequest authorize(LiquidRequest liquidRequest) throws Exception;

    /**
     * Used to authorize requests after the fact, using the entity returned. Obviously this should
     * only be done for queries!
     * <br/>
     * If the entity passed in is a system entity, it is returned without any processing.
     * <br/>
     * If the user is authorized then the entity passed in is returned, otherwise an authorization
     * failure entity is returned.
     * <br/>
     * One could say this is a bit of a hack really ;-)
     *
     * @param identity   the user we are authorizing.
     * @param request    the request to be authorized.
     * @param permission the action which we wish to perform.
     * @return the entity passed in or an authorization failure entity.
     */
    @Nonnull
    LiquidMessage postAuthorize(LiquidSessionIdentifier identity, AbstractRetrievalRequest request, LiquidPermission permission) throws Exception;
}
