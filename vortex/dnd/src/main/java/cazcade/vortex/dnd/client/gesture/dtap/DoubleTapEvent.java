package cazcade.vortex.dnd.client.gesture.dtap;

import cazcade.vortex.dnd.client.GestureEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author neilellis@cazcade.com
 */
public class DoubleTapEvent extends GestureEvent<DoubleTapHandler> {

    private static final Type<DoubleTapHandler> TYPE = new Type<DoubleTapHandler>();

    public static GwtEvent.Type<DoubleTapHandler> getType() {
        return TYPE;
    }



    public DoubleTapEvent(long duration, long endTime, DomEvent event, long startTime, int x, int y) {
        super(0,0,duration, endTime, event, startTime, 0, 0, x, y, 0, 0);
    }


    public final Type<DoubleTapHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(DoubleTapHandler handlerDouble) {
        handlerDouble.onDoubleTap(this);
    }
}