package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CreatePoolRequest extends AbstractCreationRequest {


    public CreatePoolRequest() {
    }

    public CreatePoolRequest(LiquidSessionIdentifier identity, LiquidURI parent, String name, String title, String description, double x, double y) {
        this(LSDDictionaryTypes.POOL2D, null, identity, parent, name, title, description, x, y);
    }


    public CreatePoolRequest(LSDDictionaryTypes type, LiquidURI parent, String name, String title, String description, double x, double y) {
        this(type, null, null, parent, name, title, description, x, y);
    }

    public CreatePoolRequest(LiquidURI parent, String name, String title, String description, double x, double y) {
        this(LSDDictionaryTypes.POOL2D, null, null, parent, name, title, description, x, y);
    }

    public CreatePoolRequest(LSDDictionaryTypes type, LiquidUUID id, LiquidSessionIdentifier identity, LiquidURI parent, String name, String title, String description, double x, double y) {
        this.setTitle(title);
        this.setDescription(description);
        this.setX(x);
        this.setY(y);
        this.setId(id);
        this.setIdentity(identity);
        this.setParent(parent);
        this.setName(name);
        this.setType(type);
    }


    public Collection<LiquidURI> getAffectedEntities() {
        return getStandardAffectedEntitiesInternalPlus(getParent());
    }


    @Override
    public LiquidMessage copy() {
        return new CreatePoolRequest(getType(), getId(), getSessionIdentifier(), getParent(), getName(), getTitle(), getDescription(), getX(), getY());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(getParent(), LiquidPermission.MODIFY));
    }


    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CREATE_POOL;
    }


}
