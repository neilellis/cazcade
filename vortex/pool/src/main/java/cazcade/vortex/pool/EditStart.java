package cazcade.vortex.pool;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author neilellis@cazcade.com
 */
public class EditStart extends GwtEvent<EditStartHandler> {
    public static Type<EditStartHandler> TYPE = new Type<EditStartHandler>();

    public Type<EditStartHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(EditStartHandler handler) {
        handler.onEditStart(this);
    }
}
