package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class AdminCommandRequest extends AbstractRequest {

    private String[] args;

    public final String[] getArgs() {
        return args;
    }

    public final void setArgs(final String[] args) {
        this.args = args;
    }


    public AdminCommandRequest() {
        super();
    }

    public AdminCommandRequest(final String... args) {
        this(null, null, args);
    }

    public AdminCommandRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, final String... args) {
        super();
        setArgs(args);
        setId(id);
        setSessionId(identity);
    }


    @Nonnull
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

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.ADMIN_COMMAND;
    }

    @Override
    public boolean isAsyncRequest() {
        return false;
    }

}