/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class AdminCommandRequest extends AbstractRequest {
    private String[] args;

    public AdminCommandRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final String... args) {
        super();
        setArgs(args);
        id(id);
        session(identity);
    }


    public AdminCommandRequest() {
        super();
    }

    public AdminCommandRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new AdminCommandRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(session(), new LiquidURI("pool:///"), Permission.SYSTEM_PERM));
    }

    public List<String> notificationLocations() {
        return Arrays.asList(uri().withoutFragment().asReverseDNSString(), uri().asReverseDNSString());
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.ADMIN_COMMAND;
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