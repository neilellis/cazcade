package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;


public class CreateSessionRequest extends AbstractCreationRequest {

    public CreateSessionRequest() {
    }

    public CreateSessionRequest(@Nonnull ClientApplicationIdentifier client) {
        this(null, null, client);
    }

    public CreateSessionRequest(LiquidURI alias, @Nonnull ClientApplicationIdentifier client) {
        this(null, alias, client);
    }

    public CreateSessionRequest(@Nullable LiquidUUID id, @Nullable LiquidURI alias, @Nonnull ClientApplicationIdentifier client) {
        this.setId(id);
        this.setUri(alias);
        this.setClient(client);
    }


    public Collection<LiquidURI> getAffectedEntities() {
        return Arrays.asList(getUri());
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new CreateSessionRequest(getId(), getUri(), getClient());
    }


    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CREATE_SESSION;
    }

    @Override
    public boolean isAsyncRequest() {
        return false;
    }

}