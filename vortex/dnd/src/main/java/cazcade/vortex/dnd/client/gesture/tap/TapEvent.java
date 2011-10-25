package cazcade.vortex.dnd.client.gesture.tap;

import cazcade.vortex.dnd.client.GestureEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author neilellis@cazcade.com
 */
public class TapEvent extends GestureEvent<TapHandler> {

    private static final GwtEvent.Type<TapHandler> TYPE = new GwtEvent.Type<TapHandler>();

    public static GwtEvent.Type<TapHandler> getType() {
        return TYPE;
    }



    public TapEvent(long duration, long endTime, DomEvent event, long startTime, int x, int y) {
        super(0,0,duration, endTime, event, startTime, 0, 0, x, y, 0, 0);
    }


    public final GwtEvent.Type<TapHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(TapHandler handler) {
        handler.onTap(this);
    }
}
