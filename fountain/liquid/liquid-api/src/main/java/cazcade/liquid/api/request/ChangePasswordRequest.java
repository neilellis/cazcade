package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ChangePasswordRequest extends AbstractRequest {

    public ChangePasswordRequest() {
    }

    @Override
    public boolean isSecureOperation() {
        return true;
    }

    public ChangePasswordRequest(String password) {
        this(null, null, password);
    }

    public ChangePasswordRequest(LiquidSessionIdentifier identity, String password) {
        this(null, identity, password);
    }

    public ChangePasswordRequest(LiquidUUID id, LiquidSessionIdentifier identity, String password) {
        this.setPassword(password);
        this.setId(id);
        this.setIdentity(identity);
    }


    public Collection<LiquidURI> getAffectedEntities() {
        return java.util.Arrays.asList(getSessionIdentifier().getAliasURL());
    }

    @Override
    public LiquidMessage copy() {
        return new ChangePasswordRequest(getId(), getSessionIdentifier(), super.getPassword());
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

    public List<String> getNotificationLocations() {
        return null;
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CHANGE_PASSWORD;
    }

}