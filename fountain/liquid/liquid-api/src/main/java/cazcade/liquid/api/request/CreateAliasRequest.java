package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.LSDEntity;

public class CreateAliasRequest extends AbstractCreationRequest {
    private boolean me;
    private boolean orupdate;

    private boolean claim;

    public CreateAliasRequest() {
    }

    public CreateAliasRequest(LSDEntity alias, boolean me, boolean orupdate, boolean claim) {
        this(null, null, alias, me, orupdate, claim);
    }

    public CreateAliasRequest(LiquidSessionIdentifier identity, LSDEntity alias, boolean me, boolean orupdate, boolean claim) {
        this(null, identity, alias, me, orupdate, claim);
    }

    public CreateAliasRequest(LiquidUUID id, LiquidSessionIdentifier identity, LSDEntity entity, boolean me, boolean orupdate, boolean claim) {
        this.claim = claim;
        this.id = id;
        this.entity = entity;
        this.me = me;
        this.orupdate = orupdate;
        this.identity = identity;
    }

    @Override
    public boolean isAsyncRequest() {
        return false;
    }

    public boolean isMe() {
        return me;
    }

    public boolean isOrupdate() {
        return orupdate;
    }

    @Override
    public LiquidMessage copy() {
        return new CreateAliasRequest(id, identity, entity, me, orupdate, claim);
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CREATE_ALIAS;
    }


    public boolean isClaim() {
        return claim;
    }
}