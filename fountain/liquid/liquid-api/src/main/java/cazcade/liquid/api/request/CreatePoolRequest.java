package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CreatePoolRequest extends AbstractCreationRequest {
    private LiquidURI parent;
    private LSDDictionaryTypes type;
    private String name;
    private String title;
    private String description;
    private double x;
    private double y;
    private boolean listed;


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
        this.title = title;
        this.description = description;
        this.x = x;
        this.y = y;
        this.id = id;
        this.identity = identity;
        this.parent = parent;
        this.name = name;
        this.type = type;
    }


    public Collection<LiquidURI> getAffectedEntities() {
        return getStandardAffectedEntitiesInternalPlus(parent);
    }


    public String getName() {
        return name;
    }


    public LiquidURI getParent() {
        return parent;
    }

    @Override
    public LiquidMessage copy() {
        return new CreatePoolRequest(type, id, identity, parent, name, title, description, x, y);
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(parent, LiquidPermission.MODIFY));
    }


    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CREATE_POOL;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public LSDDictionaryTypes getType() {
        return type;
    }


    public boolean isListed() {
        return listed;
    }
}
