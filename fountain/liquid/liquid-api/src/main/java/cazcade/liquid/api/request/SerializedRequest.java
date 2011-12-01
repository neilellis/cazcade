package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import java.io.Serializable;

/**
 * @author neilellis@cazcade.com
 */
public final class SerializedRequest implements Serializable {
    private LiquidRequestType type;
    private LSDTransferEntity entity;

    public SerializedRequest() {
    }

    public SerializedRequest(final LiquidRequestType type, final LSDTransferEntity entity) {
        this.type = type;
        this.entity = entity;
    }

    public LSDTransferEntity getEntity() {
        return entity;
    }

    public LiquidRequestType getType() {
        return type;
    }
}
