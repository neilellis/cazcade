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
        super();
    }

    public CreatePoolRequest(final LiquidSessionIdentifier identity, final LiquidURI parent, final String name, final String title, final String description, final double x, final double y) {
        this(LSDDictionaryTypes.POOL2D, null, identity, parent, name, title, description, x, y);
    }


    public CreatePoolRequest(@Nonnull final LSDDictionaryTypes type, final LiquidURI parent, final String name, final String title, final String description, final double x, final double y) {
        this(type, null, null, parent, name, title, description, x, y);
    }

    public CreatePoolRequest(final LiquidURI parent, final String name, final String title, final String description, final double x, final double y) {
        this(LSDDictionaryTypes.POOL2D, null, null, parent, name, title, description, x, y);
    }

    public CreatePoolRequest(@Nonnull final LSDDictionaryTypes type, @Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, final LiquidURI parent, final String name, final String title, final String description, final double x, final double y) {
        super();
        setTitle(title);
        setDescription(description);
        setX(x);
        setY(y);
        setId(id);
        setSessionId(identity);
        setParent(parent);
        setName(name);
        setType(type);
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
