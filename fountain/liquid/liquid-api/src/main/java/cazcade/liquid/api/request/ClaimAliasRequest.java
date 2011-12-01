package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    public ClaimAliasRequest(@Nullable LiquidUUID id, LiquidSessionIdentifier identity) {
        this.setId(id);
        this.setSessionId(identity);
    }


    public Collection<LiquidURI> getAffectedEntities() {
        return java.util.Arrays.asList(getSessionIdentifier().getAliasURL());
    }

    @Nullable
    @Override
    public LiquidMessage copy() {
        return new ClaimAliasRequest(getId(), getSessionIdentifier());
    }

    public boolean isMutationRequest() {
        return true;
    }


    @Override
    public boolean isAsyncRequest() {
        return false;
    }

    @Nullable
    public List<String> getNotificationLocations() {
        return null;
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CLAIM_ALIAS;
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Collections.EMPTY_LIST;
    }

}