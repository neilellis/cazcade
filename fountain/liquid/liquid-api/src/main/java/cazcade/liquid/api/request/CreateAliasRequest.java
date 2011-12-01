package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.LSDEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CreateAliasRequest extends AbstractCreationRequest {

    public CreateAliasRequest() {
        super();
    }

    public CreateAliasRequest(final LSDEntity alias, final boolean me, final boolean orupdate, final boolean claim) {
        this(null, null, alias, me, orupdate, claim);
    }

    public CreateAliasRequest(final LiquidSessionIdentifier identity, final LSDEntity alias, final boolean me, final boolean orupdate, final boolean claim) {
        this(null, identity, alias, me, orupdate, claim);
    }

    public CreateAliasRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, final LSDEntity entity, final boolean me, final boolean orupdate, final boolean claim) {
        super();
        setClaim(claim);
        setId(id);
        setRequestEntity(entity);
        setMe(me);
        setOrCreate(orupdate);
        setSessionId(identity);
    }

    @Override
    public boolean isAsyncRequest() {
        return false;
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new CreateAliasRequest(getId(), getSessionIdentifier(), getRequestEntity(), isMe(), isOrCreate(), isClaim());
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CREATE_ALIAS;
    }


}