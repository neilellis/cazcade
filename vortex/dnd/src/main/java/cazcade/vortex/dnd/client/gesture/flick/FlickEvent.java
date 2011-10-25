package cazcade.vortex.dnd.client.gesture.flick;

import cazcade.vortex.dnd.client.GestureEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author neilellis@cazcade.com
 */
public class FlickEvent extends GestureEvent<FlickHandler> {

    private static final Type<FlickHandler> TYPE = new Type<FlickHandler>();

    public static GwtEvent.Type<FlickHandler> getType() {
        return TYPE;
    }



    public FlickEvent(int deltaX, int deltaY, long duration, long endTime, DomEvent mostRecentDOMEvent, long startTime, int startX, int startY, int x, int y, int offsetX, int offsetY) {
        super(deltaX, deltaY, duration, endTime, mostRecentDOMEvent, startTime, startX, startY, x, y, offsetX, offsetY);
    }

    public final Type<FlickHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(FlickHandler handler) {
        handler.onFlick(this);
    }
}