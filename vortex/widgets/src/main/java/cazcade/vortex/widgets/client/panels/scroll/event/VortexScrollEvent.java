package cazcade.vortex.widgets.client.panels.scroll.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author neilellis@cazcade.com
 */
public class VortexScrollEvent extends GwtEvent<VortexScrollHandler> {
    public static Type<VortexScrollHandler> TYPE = new Type<VortexScrollHandler>();

    public Type<VortexScrollHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(VortexScrollHandler handler) {
        handler.onVortexScroll(this);
    }
}
