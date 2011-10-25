package cazcade.vortex.dnd.client.gesture.start;

import cazcade.vortex.dnd.client.GestureEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author neilellis@cazcade.com
 */
public class GestureStartEvent extends GestureEvent<GestureStartHandler> {

    private static final Type<GestureStartHandler> TYPE = new Type<GestureStartHandler>();

    public static GwtEvent.Type<GestureStartHandler> getType() {
        return TYPE;
    }



    public GestureStartEvent(DomEvent event, long startTime, int x, int y) {
        super(0, 0, 0, 0, event, startTime, 0, 0, x, y, 0, 0);
    }


    public final Type<GestureStartHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(GestureStartHandler handler) {
        handler.onGestureStart(this);
    }
}