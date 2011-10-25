package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.Arrays;
import java.util.List;

public class AdminCommandRequest extends AbstractRequest {


    private String[] args;

    public AdminCommandRequest() {
    }

    public AdminCommandRequest(String ... args) {
        this(null, null, args);
    }

    public AdminCommandRequest(LiquidUUID id, LiquidSessionIdentifier identity, String ... args) {
        this.args = args;
        this.id = id;
        this.identity = identity;
    }


    @Override
    public LiquidMessage copy() {
        return new AdminCommandRequest(id, identity, args);
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(new LiquidURI("pool:///"), LiquidPermission.SYSTEM));
    }

    @Override
    public boolean isMutationRequest() {
        return true;
    }

    public List<String> getNotificationLocations() {
        return Arrays.asList(uri.getWithoutFragment().asReverseDNSString(), uri.asReverseDNSString());
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.ADMIN_COMMAND;
    }

    @Override
    public boolean isAsyncRequest() {
        return false;
    }

    public String[] getArgs() {
        return args;
    }
}