package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ClaimAliasRequest extends AbstractRequest {

    public ClaimAliasRequest() {
    }

    @Override
    public boolean isSecureOperation() {
        return true;
    }


    public ClaimAliasRequest(LiquidSessionIdentifier identity) {
        this(null, identity);
    }

    public ClaimAliasRequest(LiquidUUID id, LiquidSessionIdentifier identity) {
        this.id = id;
        this.identity = identity;
    }


    public Collection<LiquidURI> getAffectedEntities() {
        return java.util.Arrays.asList(identity.getAliasURL());
    }

    @Override
    public LiquidMessage copy() {
        return new ClaimAliasRequest(id, identity);
    }

    public boolean isMutationRequest() {
        return true;
    }


    @Override
    public boolean isAsyncRequest() {
        return false;
    }

    public List<String> getNotificationLocations() {
        return null;
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CLAIM_ALIAS;
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Collections.EMPTY_LIST;
    }

}