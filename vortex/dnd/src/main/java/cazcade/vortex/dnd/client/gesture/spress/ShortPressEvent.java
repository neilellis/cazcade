package cazcade.vortex.dnd.client.gesture.spress;

import cazcade.vortex.dnd.client.GestureEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author neilellis@cazcade.com
 */
public class ShortPressEvent extends GestureEvent<ShortPressHandler> {

    private static final Type<ShortPressHandler> TYPE = new Type<ShortPressHandler>();

    public static GwtEvent.Type<ShortPressHandler> getType() {
        return TYPE;
    }



    public ShortPressEvent(long duration, long endTime, DomEvent event, long startTime, int x, int y) {
        super(0,0,duration, endTime, event, startTime, 0, 0, x, y, 0, 0);
    }


    public final Type<ShortPressHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(ShortPressHandler handler) {
        handler.onShortPress(this);
    }
}