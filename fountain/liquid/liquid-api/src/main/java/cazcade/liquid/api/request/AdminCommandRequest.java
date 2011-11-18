package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.Arrays;
import java.util.List;

public class AdminCommandRequest extends AbstractRequest {

    private String[] args;

    public final String[] getArgs() {
        return args;
    }

    public final void setArgs(String[] args) {
        this.args = args;
    }


    public AdminCommandRequest() {
    }

    public AdminCommandRequest(String... args) {
        this(null, null, args);
    }

    public AdminCommandRequest(LiquidUUID id, LiquidSessionIdentifier identity, String... args) {
        this.setArgs(args);
        this.setId(id);
        this.setIdentity(identity);
    }


    @Override
    public LiquidMessage copy() {
        return new AdminCommandRequest(getId(), getSessionIdentifier(), getArgs());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(new LiquidURI("pool:///"), LiquidPermission.SYSTEM));
    }

    @Override
    public boolean isMutationRequest() {
        return true;
    }

    public List<String> getNotificationLocations() {
        return Arrays.asList(getUri().getWithoutFragment().asReverseDNSString(), getUri().asReverseDNSString());
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.ADMIN_COMMAND;
    }

    @Override
    public boolean isAsyncRequest() {
        return false;
    }

}