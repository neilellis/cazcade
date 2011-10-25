package cazcade.vortex.dnd.client.gesture.hdrag;

import cazcade.vortex.dnd.client.GestureEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author neilellis@cazcade.com
 */
public class HoldDragEvent extends GestureEvent<HoldDragHandler> {

    private static final Type<HoldDragHandler> TYPE = new Type<HoldDragHandler>();

    public static GwtEvent.Type<HoldDragHandler> getType() {
        return TYPE;
    }

    public HoldDragEvent(int deltaX, int deltaY, DomEvent mostRecentDOMEvent, long startTime, int startX, int startY, int x, int y, int offsetX, int offsetY) {
        super(deltaX, deltaY, 0, 0, mostRecentDOMEvent, startTime, startX, startY, x, y, offsetX, offsetY);
    }



    public final Type<HoldDragHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(HoldDragHandler handler) {
        handler.onHoldDrag(this);
    }


}