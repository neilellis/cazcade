package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.LSDEntity;

import java.util.List;

public class UpdateSessionRequest extends AbstractUpdateRequest {

    public UpdateSessionRequest() {
    }

    public UpdateSessionRequest(LiquidUUID target, LSDEntity entity, boolean internal) {
        this(null, null, target, entity, internal);
    }

    public UpdateSessionRequest(LiquidSessionIdentifier identity, LiquidUUID target, LSDEntity entity, boolean internal) {
        this(null, identity, target, entity, internal);
    }

    public UpdateSessionRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, LSDEntity entity, boolean internal) {
        this.setId(id);
        this.setSessionId(identity);
        this.setTarget(target);
        this.setRequestEntity(entity);
        this.setInternal(internal);
    }


    @Override
    public LiquidMessage copy() {
        return new UpdateSessionRequest(getId(), getSessionIdentifier(), super.getTarget(), super.getRequestEntity(), isInternal());
    }


    public LiquidRequestType getRequestType() {
        return LiquidRequestType.UPDATE_SESSION;
    }

    @Override
    public List<String> getNotificationLocations() {
        return null;
    }

    @Override
    public String getNotificationSession() {
        //Don't notify anyone of a session update.
        return null;
    }
}