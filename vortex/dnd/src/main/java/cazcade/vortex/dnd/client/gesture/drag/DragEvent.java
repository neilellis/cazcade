package cazcade.vortex.dnd.client.gesture.drag;

import cazcade.vortex.dnd.client.GestureEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.GwtEvent;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class DragEvent extends GestureEvent<DragHandler> {

    @Nonnull
    private static final Type<DragHandler> TYPE = new Type<DragHandler>();

    @Nonnull
    public static GwtEvent.Type<DragHandler> getType() {
        return TYPE;
    }


    public DragEvent(int deltaX, int deltaY, DomEvent mostRecentDOMEvent, long startTime, int startX, int startY, int x, int y, int offsetX, int offsetY) {
        super(deltaX, deltaY, 0, 0, mostRecentDOMEvent, startTime, startX, startY, x, y, offsetX, offsetY);
    }


    @Nonnull
    public final Type<DragHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(@Nonnull DragHandler handler) {
        handler.onDrag(this);
    }
}