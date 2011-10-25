package cazcade.vortex.pool;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author neilellis@cazcade.com
 */
public class EditFinish extends GwtEvent<EditFinishHandler> {
    public static Type<EditFinishHandler> TYPE = new Type<EditFinishHandler>();

    public Type<EditFinishHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(EditFinishHandler handler) {
        handler.onEditFinish(this);
    }
}
