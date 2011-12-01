package cazcade.vortex.dnd.client.gesture.hdrag;

import cazcade.vortex.dnd.client.GestureEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.GwtEvent;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class HoldDragEvent extends GestureEvent<HoldDragHandler> {

    @Nonnull
    private static final Type<HoldDragHandler> TYPE = new Type<HoldDragHandler>();

    @Nonnull
    public static GwtEvent.Type<HoldDragHandler> getType() {
        return TYPE;
    }

    public HoldDragEvent(final int deltaX, final int deltaY, final DomEvent mostRecentDOMEvent, final long startTime, final int startX, final int startY, final int x, final int y, final int offsetX, final int offsetY) {
        super(deltaX, deltaY, 0, 0, mostRecentDOMEvent, startTime, startX, startY, x, y, offsetX, offsetY);
    }


    @Nonnull
    public final Type<HoldDragHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(@Nonnull final HoldDragHandler handler) {
        handler.onHoldDrag(this);
    }


}