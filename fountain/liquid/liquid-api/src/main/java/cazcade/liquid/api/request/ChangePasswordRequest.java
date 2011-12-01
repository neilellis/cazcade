package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ChangePasswordRequest extends AbstractRequest {

    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(LiquidSessionIdentifier identity, String password, String hash) {
        setSessionId(identity);
        setPassword(password);
        setChangePasswordSecurityHash(hash);
    }

    public ChangePasswordRequest(LiquidSessionIdentifier liquidSessionIdentifier) {
        setSessionId(liquidSessionIdentifier);
    }

    public ChangePasswordRequest(@Nonnull LSDEntity entity) {
        setEntity(entity);
    }

    @Override
    public boolean isSecureOperation() {
        return true;
    }

    public ChangePasswordRequest(String password) {
        setPassword(password);
    }

    public ChangePasswordRequest(LiquidSessionIdentifier identity, String password) {
        setSessionId(identity);
        setPassword(password);
    }

    public ChangePasswordRequest(LiquidUUID id, LiquidSessionIdentifier identity, String password) {
        this.setPassword(password);
        this.setId(id);
        this.setSessionId(identity);
    }


    public Collection<LiquidURI> getAffectedEntities() {
        return java.util.Arrays.asList(getSessionIdentifier().getAliasURL());
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