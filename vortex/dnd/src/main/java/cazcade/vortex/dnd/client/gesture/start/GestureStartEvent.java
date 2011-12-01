package cazcade.vortex.dnd.client.gesture.start;

import cazcade.vortex.dnd.client.GestureEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.GwtEvent;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class GestureStartEvent extends GestureEvent<GestureStartHandler> {

    @Nonnull
    private static final Type<GestureStartHandler> TYPE = new Type<GestureStartHandler>();

    @Nonnull
    public static GwtEvent.Type<GestureStartHandler> getType() {
        return TYPE;
    }


    public GestureStartEvent(final DomEvent event, final long startTime, final int x, final int y) {
        super(0, 0, 0, 0, event, startTime, 0, 0, x, y, 0, 0);
    }


    @Nonnull
    public final Type<GestureStartHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(@Nonnull final GestureStartHandler handler) {
        handler.onGestureStart(this);
    }
}