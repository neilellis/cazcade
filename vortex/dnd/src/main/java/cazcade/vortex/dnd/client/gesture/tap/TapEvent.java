package cazcade.vortex.dnd.client.gesture.tap;

import cazcade.vortex.dnd.client.GestureEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.GwtEvent;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class TapEvent extends GestureEvent<TapHandler> {

    @Nonnull
    private static final GwtEvent.Type<TapHandler> TYPE = new GwtEvent.Type<TapHandler>();

    @Nonnull
    public static GwtEvent.Type<TapHandler> getType() {
        return TYPE;
    }


    public TapEvent(final long duration, final long endTime, final DomEvent event, final long startTime, final int x, final int y) {
        super(0, 0, duration, endTime, event, startTime, 0, 0, x, y, 0, 0);
    }


    @Nonnull
    public final GwtEvent.Type<TapHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(@Nonnull final TapHandler handler) {
        handler.onTap(this);
    }
}
