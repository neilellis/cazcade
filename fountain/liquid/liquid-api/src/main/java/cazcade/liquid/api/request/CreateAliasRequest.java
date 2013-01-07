package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CreateAliasRequest extends AbstractCreationRequest {
    public CreateAliasRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity,
                              final LSDTransferEntity entity, final boolean me, final boolean orupdate, final boolean claim) {
        super();
        setClaim(claim);
        setId(id);
        setRequestEntity(entity);
        setMe(me);
        setOrCreate(orupdate);
        setSessionId(identity);
    }

    public CreateAliasRequest(final LiquidSessionIdentifier identity, final LSDTransferEntity alias, final boolean me,
                              final boolean orupdate, final boolean claim) {
        this(null, identity, alias, me, orupdate, claim);
    }

    public CreateAliasRequest(final LSDTransferEntity alias, final boolean me, final boolean orupdate, final boolean claim) {
        this(null, LiquidSessionIdentifier.ANON, alias, me, orupdate, claim);
    }

    public CreateAliasRequest() {
        super();
    }

    public CreateAliasRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new CreateAliasRequest(getEntity());
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CREATE_ALIAS;
    }

    @Override
    public boolean isAsyncRequest() {
        return false;
    }
}