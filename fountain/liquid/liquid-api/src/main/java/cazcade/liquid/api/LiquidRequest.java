package cazcade.liquid.api;

import cazcade.liquid.api.request.AuthorizationRequest;

import java.util.List;

/**
 * @author neilelliz@cazcade.com
 */
public interface LiquidRequest extends LiquidMessage {


    boolean isMutationRequest();

    List<String> getNotificationLocations();

    public LiquidSessionIdentifier getSessionIdentifier();

    /**
     * This can only be called if no identity has yet been set. *
     */
    public void setIdentity(LiquidSessionIdentifier identity);


    String getNotificationSession();

    boolean isAsyncRequest();

    LiquidRequestType getRequestType();



    /**
     * Used for pre-authorization, passing the authorization request does not guarantee
     * the action will be allowed by the server. It is simply to allow a quick pre-check.
     *
     * @return
     */
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

    void setReplyTo(String name);

    void adjustTimeStampForServerTime();

    boolean shouldSendProvisional();
}
