package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ChangePasswordRequest extends AbstractRequest {

    public ChangePasswordRequest() {
        super();
    }

    public ChangePasswordRequest(final LiquidSessionIdentifier identity, final String password, final String hash) {
        super();
        setSessionId(identity);
        setPassword(password);
        setChangePasswordSecurityHash(hash);
    }

    public ChangePasswordRequest(final LiquidSessionIdentifier liquidSessionIdentifier) {
        super();
        setSessionId(liquidSessionIdentifier);
    }

    public ChangePasswordRequest(@Nonnull final LSDTransferEntity entity) {
        super();
        setEntity(entity);
    }

    @Override
    public boolean isSecureOperation() {
        return true;
    }

    public ChangePasswordRequest(final String password) {
        super();
        setPassword(password);
    }

    public ChangePasswordRequest(final LiquidSessionIdentifier identity, final String password) {
        super();
        setSessionId(identity);
        setPassword(password);
    }

    public ChangePasswordRequest(final LiquidUUID id, final LiquidSessionIdentifier identity, final String password) {
        super();
        setPassword(password);
        setId(id);
        setSessionId(identity);
    }


    public Collection<LiquidURI> getAffectedEntities() {
        return Arrays.asList(getSessionIdentifier().getAliasURL());
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new ChangePasswordRequest(getEntity().copy());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Collections.EMPTY_LIST;
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
        return LiquidRequestType.CHANGE_PASSWORD;
    }

}