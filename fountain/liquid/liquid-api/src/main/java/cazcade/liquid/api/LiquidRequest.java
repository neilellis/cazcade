package cazcade.liquid.api;

import cazcade.liquid.api.request.AuthorizationRequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author neilelliz@cazcade.com
 */
public interface LiquidRequest extends LiquidMessage {


    boolean isMutationRequest();

    @Nullable
    List<String> getNotificationLocations();

    @Nullable
    LiquidSessionIdentifier getSessionIdentifier();

    /**
     * This can only be called if no identity has yet been set. *
     */
    void setSessionId(LiquidSessionIdentifier identity);


    @Nullable
    String getNotificationSession();

    boolean isAsyncRequest();

    @Nonnull
    LiquidRequestType getRequestType();


    /**
     * Used for pre-authorization, passing the authorization request does not guarantee
     * the action will be allowed by the server. It is simply to allow a quick pre-check.
     *
     * @return
     */
    @Nullable
    List<AuthorizationRequest> getAuthorizationRequests();

    void setId(LiquidUUID uuid);

    boolean shouldNotify();

    long getCacheExpiry();

    /**
     * Returns true if the request should be treated as secure - i.e. we should increase the level of client security around the operation.
     * More security means a reduced user experience so we only make secure the most security sensitive operations (like changing password).
     *
     * @return true if requires a larger degree of security than most operations.
     */
    boolean isSecureOperation();

    /**
     * If true forces synchronous communication.
     */
    void setRpc(Boolean b);


    void adjustTimeStampForServerTime();

    boolean shouldSendProvisional();
}
