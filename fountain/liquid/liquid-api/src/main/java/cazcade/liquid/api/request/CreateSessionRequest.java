package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.Arrays;
import java.util.Collection;


public class CreateSessionRequest extends AbstractCreationRequest {
    private ClientApplicationIdentifier client;

    public CreateSessionRequest() {
    }

    public CreateSessionRequest(ClientApplicationIdentifier client) {
        this(null, null, client);
    }

    public CreateSessionRequest(LiquidURI alias, ClientApplicationIdentifier client) {
        this(null, alias, client);
    }

    public CreateSessionRequest(LiquidUUID id, LiquidURI alias, ClientApplicationIdentifier client) {
        this.id = id;
        this.uri = alias;
        this.client = client;
    }

    public ClientApplicationIdentifier getClient() {
        return client;
    }

    public Collection<LiquidURI> getAffectedEntities() {
        return Arrays.asList(uri);
    }


    @Override
    public LiquidMessage copy() {
        return new CreateSessionRequest(id, uri, client);
    }


    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CREATE_SESSION;
    }

    @Override
    public boolean isAsyncRequest() {
        return false;
    }

}