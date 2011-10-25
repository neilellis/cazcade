package cazcade.vortex.dnd.client.gesture.lpress;

import cazcade.vortex.dnd.client.GestureEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author neilellis@cazcade.com
 */
public class LongPressEvent extends GestureEvent<LongPressHandler> {

    private static final Type<LongPressHandler> TYPE = new Type<LongPressHandler>();
    public static GwtEvent.Type<LongPressHandler> getType() {
           return TYPE;
       }



    public LongPressEvent(long duration, long endTime, DomEvent event, long startTime, int x, int y) {
        super(0,0,duration, endTime, event, startTime, 0, 0, x, y, 0, 0);
    }


    public final Type<LongPressHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(LongPressHandler handler) {
        handler.onLongPress(this);
    }
}