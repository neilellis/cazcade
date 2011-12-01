package cazcade.vortex.dnd.client.gesture.lpress;

import cazcade.vortex.dnd.client.GestureEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.GwtEvent;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class LongPressEvent extends GestureEvent<LongPressHandler> {

    @Nonnull
    private static final Type<LongPressHandler> TYPE = new Type<LongPressHandler>();

    @Nonnull
    public static GwtEvent.Type<LongPressHandler> getType() {
        return TYPE;
    }


    public LongPressEvent(long duration, long endTime, DomEvent event, long startTime, int x, int y) {
        super(0, 0, duration, endTime, event, startTime, 0, 0, x, y, 0, 0);
    }


    @Nonnull
    public final Type<LongPressHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(@Nonnull LongPressHandler handler) {
        handler.onLongPress(this);
    }
}