package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidUUID;

public class UnlinkAliasRequest extends AbstractDeletionRequest {

    public UnlinkAliasRequest() {
    }

    public UnlinkAliasRequest(LiquidUUID target) {
        this(null, null, target);
    }

    public UnlinkAliasRequest(LiquidSessionIdentifier identity, LiquidUUID target) {
        this(null, identity, target);
    }

    public UnlinkAliasRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target) {
        this.id = id;
        this.identity = identity;
        this.target = target;
    }


    @Override
    public LiquidMessage copy() {
        return new UnlinkAliasRequest(id, identity, target);
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.DELETE_ALIAS;
    }

}