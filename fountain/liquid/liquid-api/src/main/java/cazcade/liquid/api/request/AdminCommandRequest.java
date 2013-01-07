package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class AdminCommandRequest extends AbstractRequest {
    private String[] args;

    public AdminCommandRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity,
                               final String... args) {
        super();
        setArgs(args);
        setId(id);
        setSessionId(identity);
    }


    public AdminCommandRequest() {
        super();
    }

    public AdminCommandRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new AdminCommandRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(getSessionIdentifier(), new LiquidURI("pool:///"), LiquidPermission.SYSTEM));
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

    @Override
    public boolean isMutationRequest() {
        return true;
    }

    public final String[] getArgs() {
        return args;
    }

    public final void setArgs(final String[] args) {
        this.args = args;
    }
}