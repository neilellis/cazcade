package cazcade.vortex.dnd.client.gesture.flick;

import cazcade.vortex.dnd.client.GestureEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.GwtEvent;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class FlickEvent extends GestureEvent<FlickHandler> {

    @Nonnull
    private static final Type<FlickHandler> TYPE = new Type<FlickHandler>();

    @Nonnull
    public static GwtEvent.Type<FlickHandler> getType() {
        return TYPE;
    }


    public FlickEvent(final int deltaX, final int deltaY, final long duration, final long endTime, final DomEvent mostRecentDOMEvent, final long startTime, final int startX, final int startY, final int x, final int y, final int offsetX, final int offsetY) {
        super(deltaX, deltaY, duration, endTime, mostRecentDOMEvent, startTime, startX, startY, x, y, offsetX, offsetY);
    }

    @Nonnull
    public final Type<FlickHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(@Nonnull final FlickHandler handler) {
        handler.onFlick(this);
    }
}