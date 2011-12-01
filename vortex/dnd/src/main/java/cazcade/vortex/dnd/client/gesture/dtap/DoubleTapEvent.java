package cazcade.vortex.dnd.client.gesture.dtap;

import cazcade.vortex.dnd.client.GestureEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.GwtEvent;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class DoubleTapEvent extends GestureEvent<DoubleTapHandler> {

    @Nonnull
    private static final Type<DoubleTapHandler> TYPE = new Type<DoubleTapHandler>();

    @Nonnull
    public static GwtEvent.Type<DoubleTapHandler> getType() {
        return TYPE;
    }


    public DoubleTapEvent(final long duration, final long endTime, final DomEvent event, final long startTime, final int x, final int y) {
        super(0, 0, duration, endTime, event, startTime, 0, 0, x, y, 0, 0);
    }


    @Nonnull
    public final Type<DoubleTapHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(@Nonnull final DoubleTapHandler handlerDouble) {
        handlerDouble.onDoubleTap(this);
    }
}