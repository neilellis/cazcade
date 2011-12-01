package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CreatePoolRequest extends AbstractCreationRequest {


    public CreatePoolRequest() {
    }

    public CreatePoolRequest(LiquidSessionIdentifier identity, LiquidURI parent, String name, String title, String description, double x, double y) {
        this(LSDDictionaryTypes.POOL2D, null, identity, parent, name, title, description, x, y);
    }


    public CreatePoolRequest(@Nonnull LSDDictionaryTypes type, LiquidURI parent, String name, String title, String description, double x, double y) {
        this(type, null, null, parent, name, title, description, x, y);
    }

    public CreatePoolRequest(LiquidURI parent, String name, String title, String description, double x, double y) {
        this(LSDDictionaryTypes.POOL2D, null, null, parent, name, title, description, x, y);
    }

    public CreatePoolRequest(@Nonnull LSDDictionaryTypes type, @Nullable LiquidUUID id, @Nullable LiquidSessionIdentifier identity, LiquidURI parent, String name, String title, String description, double x, double y) {
        this.setTitle(title);
        this.setDescription(description);
        this.setX(x);
        this.setY(y);
        this.setId(id);
        this.setSessionId(identity);
        this.setParent(parent);
        this.setName(name);
        this.setType(type);
    }


    @Nonnull
    public Collection<LiquidURI> getAffectedEntities() {
        return getStandardAffectedEntitiesInternalPlus(getParent());
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new CreatePoolRequest(getType(), getId(), getSessionIdentifier(), getParent(), getName(), getTitle(), getDescription(), getX(), getY());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(getParent(), LiquidPermission.MODIFY));
    }


    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CREATE_POOL;
    }


}
