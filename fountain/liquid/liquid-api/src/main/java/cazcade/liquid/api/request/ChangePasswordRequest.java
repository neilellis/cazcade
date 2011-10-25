package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ChangePasswordRequest extends AbstractRequest {
    private String password;

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
        this.password = password;
        this.id = id;
        this.identity = identity;
    }


    public Collection<LiquidURI> getAffectedEntities() {
        return java.util.Arrays.asList(identity.getAliasURL());
    }

    @Override
    public LiquidMessage copy() {
        return new ChangePasswordRequest(id, identity, password);
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

    public String getPassword() {
        return password;
    }
}