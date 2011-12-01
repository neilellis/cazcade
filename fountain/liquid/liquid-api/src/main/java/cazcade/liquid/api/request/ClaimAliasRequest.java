package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ClaimAliasRequest extends AbstractRequest {

    public ClaimAliasRequest() {
        super();
    }

    @Override
    public boolean isSecureOperation() {
        return true;
    }


    public ClaimAliasRequest(final LiquidSessionIdentifier identity) {
        this(null, identity);
    }

    public ClaimAliasRequest(@Nullable final LiquidUUID id, final LiquidSessionIdentifier identity) {
        super();
        setId(id);
        setSessionId(identity);
    }


    public Collection<LiquidURI> getAffectedEntities() {
        return Arrays.asList(getSessionIdentifier().getAliasURL());
    }

    @Nonnull
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