/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api;

import cazcade.liquid.api.request.AuthorizationRequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author neilelliz@cazcade.com
 */
public interface LiquidRequest extends LiquidMessage {

    void adjustTimeStampForServerTime();

    /**
     * Used for pre-authorization, passing the authorization request does not guarantee
     * the action will be allowed by the server. It is simply to allow a quick pre-check.
     *
     * @return
     */
    @Nonnull List<AuthorizationRequest> authorizationRequests();

    long cacheExpiry();

    @Nullable List<String> notificationLocations();

    @Nullable String notificationSession();

    @Nonnull RequestType requestType();

    @Nonnull SessionIdentifier session();

    boolean isAsyncRequest();

    boolean isMutationRequest();

    /**
     * Returns true if the request should be treated as secure - i.e. we should increase the level of client security around the operation.
     * More security means a reduced user experience so we only make secure the most security sensitive operations (like changing password).
     *
     * @return true if requires a larger degree of security than most operations.
     */
    boolean isSecureOperation();

    void id(LiquidUUID uuid);

    /**
     * If true forces synchronous communication.
     */
    void rpc(Boolean b);

    /**
     * This can only be called if no identity has yet been set. *
     */
    void session(SessionIdentifier identity);

    boolean shouldNotify();

    boolean shouldSendProvisional();

    boolean hasRequestEntity();

    boolean hasId();
}
