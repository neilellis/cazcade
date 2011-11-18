package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.Arrays;
import java.util.Collection;


public class CreateSessionRequest extends AbstractCreationRequest {

    public CreateSessionRequest() {
    }

    public CreateSessionRequest(ClientApplicationIdentifier client) {
        this(null, null, client);
    }

    public CreateSessionRequest(LiquidURI alias, ClientApplicationIdentifier client) {
        this(null, alias, client);
    }

    public CreateSessionRequest(LiquidUUID id, LiquidURI alias, ClientApplicationIdentifier client) {
        this.setId(id);
        this.setUri(alias);
        this.setClient(client);
    }


    public Collection<LiquidURI> getAffectedEntities() {
        return Arrays.asList(getUri());
    }


    @Override
    public LiquidMessage copy() {
        return new CreateSessionRequest(getId(), getUri(), getClient());
    }


    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CREATE_SESSION;
    }

    @Override
    public boolean isAsyncRequest() {
        return false;
    }

}