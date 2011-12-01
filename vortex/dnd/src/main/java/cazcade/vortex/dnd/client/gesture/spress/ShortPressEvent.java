package cazcade.vortex.dnd.client.gesture.spress;

import cazcade.vortex.dnd.client.GestureEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.GwtEvent;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class ShortPressEvent extends GestureEvent<ShortPressHandler> {

    @Nonnull
    private static final Type<ShortPressHandler> TYPE = new Type<ShortPressHandler>();

    @Nonnull
    public static GwtEvent.Type<ShortPressHandler> getType() {
        return TYPE;
    }


    public ShortPressEvent(long duration, long endTime, DomEvent event, long startTime, int x, int y) {
        super(0, 0, duration, endTime, event, startTime, 0, 0, x, y, 0, 0);
    }


    @Nonnull
    public final Type<ShortPressHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(@Nonnull ShortPressHandler handler) {
        handler.onShortPress(this);
    }
}