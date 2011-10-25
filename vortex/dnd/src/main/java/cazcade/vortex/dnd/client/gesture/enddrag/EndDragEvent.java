package cazcade.vortex.dnd.client.gesture.enddrag;

import cazcade.vortex.dnd.client.GestureEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author neilellis@cazcade.com
 */
public class EndDragEvent extends GestureEvent<EndDragHandler> {

    private static final Type<EndDragHandler> TYPE = new Type<EndDragHandler>();

    public static GwtEvent.Type<EndDragHandler> getType() {
        return TYPE;
    }



    public EndDragEvent(int deltaX, int deltaY, long duration, long endTime, DomEvent mostRecentDOMEvent, long startTime, int startX, int startY, int x, int y, int offsetX, int offsetY) {
        super(deltaX, deltaY, duration, endTime, mostRecentDOMEvent, startTime, startX, startY, x, y, offsetX, offsetY);
    }

  

    public final Type<EndDragHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(EndDragHandler handlerEnd) {
        handlerEnd.onEndDrag(this);
    }
}