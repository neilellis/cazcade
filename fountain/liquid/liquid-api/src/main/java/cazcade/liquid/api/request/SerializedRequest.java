package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.lsd.LSDEntity;

import java.io.Serializable;

/**
 * @author neilellis@cazcade.com
 */
public final class SerializedRequest implements Serializable {
    private LiquidRequestType type;
    private LSDEntity entity;

    public SerializedRequest() {
    }

    public SerializedRequest(final LiquidRequestType type, final LSDEntity entity) {
        this.type = type;
        this.entity = entity;
    }

    public LSDEntity getEntity() {
        return entity;
    }

    public LiquidRequestType getType() {
        return type;
    }
}
