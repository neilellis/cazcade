package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidSessionIdentifier;
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
        this.setId(id);
        this.setSessionId(identity);
        this.setTarget(target);
    }


    @Override
    public LiquidMessage copy() {
        return new UnlinkAliasRequest(getId(), getSessionIdentifier(), super.getTarget());
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.DELETE_ALIAS;
    }

}