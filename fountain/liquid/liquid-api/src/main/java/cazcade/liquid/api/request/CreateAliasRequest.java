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
    }

    public CreateAliasRequest(LSDEntity alias, boolean me, boolean orupdate, boolean claim) {
        this(null, null, alias, me, orupdate, claim);
    }

    public CreateAliasRequest(LiquidSessionIdentifier identity, LSDEntity alias, boolean me, boolean orupdate, boolean claim) {
        this(null, identity, alias, me, orupdate, claim);
    }

    public CreateAliasRequest(@Nullable LiquidUUID id, @Nullable LiquidSessionIdentifier identity, LSDEntity entity, boolean me, boolean orupdate, boolean claim) {
        this.setClaim(claim);
        this.setId(id);
        this.setRequestEntity(entity);
        this.setMe(me);
        setOrCreate(orupdate);
        this.setSessionId(identity);
    }

    @Override
    public boolean isAsyncRequest() {
        return false;
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new CreateAliasRequest(getId(), getSessionIdentifier(), super.getRequestEntity(), isMe(), isOrCreate(), isClaim());
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CREATE_ALIAS;
    }


}